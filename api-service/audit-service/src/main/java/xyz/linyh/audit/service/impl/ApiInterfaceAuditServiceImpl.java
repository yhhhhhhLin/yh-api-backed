package xyz.linyh.audit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.linyh.audit.mapper.ApiinterfaceauditMapper;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import xyz.linyh.dubboapi.service.DubboInterfaceinfoService;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.AuditConstant;
import xyz.linyh.ducommon.constant.AuditMQTopicConstant;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.apiaudit.eneitys.ApiInterfaceAudit;
import xyz.linyh.model.gpt.eneitys.GPTMessage;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lin
 * @description 针对表【apiinterfaceaudit(api接口审核表)】的数据库操作Service实现
 * @createDate 2023-11-20 09:33:12
 */
@Service
@Slf4j
public class ApiInterfaceAuditServiceImpl extends ServiceImpl<ApiinterfaceauditMapper, ApiInterfaceAudit>
        implements ApiinterfaceauditService {


    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @DubboReference
    private DubboInterfaceinfoService dubboInterfaceinfoService;


    /**
     * 发送消息到消息队列中，gpt模块会将消息队列取出然后审核
     * 发送整个消息审核的结构给gpt
     */
    @Override
    public void sendAuditInterfaceMsgToGpt(ApiInterfaceAudit audit) {
        if (audit == null || audit.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核内容或审核的内容id不能为空参数错误");
        }

//       封装好发送给gpt的messages的list类型
        List<GPTMessage> messages = getMessageList(audit);

        try {
            log.info("生产者发送消息：{}", messages);
            rocketMqTemplate.convertAndSend(AuditMQTopicConstant.API_INTERFACE_AUDIT_TOPIC, JSONUtil.toJsonStr(messages));
        } catch (MessagingException e) {
//           如果实在发送不过去，就先不管了
            log.info("生产者消息发送失败：{}", audit);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息发送出问题了");
        }
    }


    /**
     * 更新审核的接口的code和msg
     */
    @Override
    public void updateAuditInterfaceCodeAndMsg(Long auditId, Integer code, String msg) {
        if (auditId == null || code == null || msg == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        LambdaUpdateWrapper<ApiInterfaceAudit> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ApiInterfaceAudit::getId, auditId);
        wrapper.set(ApiInterfaceAudit::getDescription, msg);
//        code = Integer.valueOf((code==200? AuditConstant.AUDIT_STATUS_GPT_SUCCESS:AuditConstant.AUDIT_STATUS_GPT_FAIL));
//        TODO 可能还需要判断对应的code是否符合规范
        wrapper.set(ApiInterfaceAudit::getStatus, code);
        wrapper.set(ApiInterfaceAudit::getUpdateTime, new Date());
        this.update(wrapper);

    }

    /**
     * 保存对应接口审核的数据到数据库
     *
     * @param audit
     * @return
     */
    @Override
    public ApiInterfaceAudit saveAuditInterface(ApiInterfaceAudit audit) {
//        参数校验
        if (audit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

//        还需要校验uri只能唯一目前
        Interfaceinfo interfaceByUri = dubboInterfaceinfoService.getInterfaceByURI(audit.getUri(), audit.getMethod());
        if (interfaceByUri != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称已经存在");
        }

        audit.setCreateTime(new Date());
        audit.setStatus(Integer.valueOf(AuditConstant.AUDIT_STATUS_SUMMIT));
        this.save(audit);

        return audit;
    }

    /**
     * @param auditId     要修改的审核id
     * @param status      修改后的状态
     * @param description 修改建议
     * @return
     */
    @Override
    public Boolean updateStatusAndDescription(Long auditId, Integer status, String description) {

        return null;
    }

    /**
     * 接口审核通过
     *
     * @param auditId
     * @param status
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passInterfaceAudit(Long auditId, Integer status) {
        this.updateAuditInterfaceCodeAndMsg(auditId, status, "审核通过");

        ApiInterfaceAudit apiInterfaceAudit = this.getById(auditId);
//        将数据保存到真正的接口数据库里面
        Interfaceinfo interfaceinfo = new Interfaceinfo();
        if (apiInterfaceAudit.getApiId() != null && apiInterfaceAudit.getApiId() != 0) {
            interfaceinfo.setId(apiInterfaceAudit.getApiId());
        }
        interfaceinfo.setName(apiInterfaceAudit.getName());
        interfaceinfo.setMethod(apiInterfaceAudit.getMethod());
        interfaceinfo.setRequestParams(apiInterfaceAudit.getRequestParams());
        interfaceinfo.setGetRequestParams(apiInterfaceAudit.getGetRequestParams());
        interfaceinfo.setDescription(apiInterfaceAudit.getApiDescription());
        interfaceinfo.setUri(apiInterfaceAudit.getUri());
        interfaceinfo.setHost(apiInterfaceAudit.getHost());
        interfaceinfo.setRequestHeader(apiInterfaceAudit.getRequestHeader());
        interfaceinfo.setResponseHeader(apiInterfaceAudit.getResponseHeader());
        interfaceinfo.setStatus(1);
        interfaceinfo.setUserId(apiInterfaceAudit.getUserId());
        interfaceinfo.setCreateTime(apiInterfaceAudit.getCreateTime());
        interfaceinfo.setPointsRequired(apiInterfaceAudit.getPointsRequired());
        interfaceinfo.setUpdateTime(new Date());

        Long apiId = dubboInterfaceinfoService.addOrUpdateInterface(interfaceinfo);
        if (apiId == null) {
//            抛出异常，事务管理
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "审核通过后，保存到真正的api文档里面失败");
        }
        if (apiId == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存失败，可能有重复uri");
        }

//        保存id到数据库，更新审核状态
        LambdaUpdateWrapper<ApiInterfaceAudit> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ApiInterfaceAudit::getId, auditId)
                .set(ApiInterfaceAudit::getStatus, AuditConstant.AUDIT_STATUS_PUBLISH)
                .set(ApiInterfaceAudit::getApiId, apiId);
        this.update(wrapper);

    }

    @Override
    @Transactional
    public void rejectInterfaceAudit(Long auditId, Integer status, String reason) {
//        判断拒绝的那个是否有apiId，如果没有，说明没有审核通过的，直接更新状态即可
        ApiInterfaceAudit apiInterfaceAudit = this.getById(auditId);

        if (apiInterfaceAudit.getApiId() == null) {
            this.updateAuditInterfaceCodeAndMsg(auditId, status, reason);
        } else {
            Boolean update = dubboInterfaceinfoService.updateInterfaceStatusById(apiInterfaceAudit.getApiId(), InterfaceInfoConstant.STATIC_SHOULD_RE_AUDIT);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改接口状态失败,系统出错");
            }
            this.updateAuditInterfaceCodeAndMsg(auditId, status, reason);
        }


    }

    @Override
    @Transactional
    public boolean updateAuditInterface(ApiInterfaceAudit apiInterfaceAudit, Long userId) {
//        判断接口是否存在
        ApiInterfaceAudit dbInterfaceAudit = this.getOne(Wrappers.<ApiInterfaceAudit>lambdaQuery().eq(ApiInterfaceAudit::getApiId, apiInterfaceAudit.getApiId()));
        if (dbInterfaceAudit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }

//        判断uri是否重复
        Interfaceinfo interfaceInfo = dubboInterfaceinfoService.getInterfaceByURI(apiInterfaceAudit.getUri(), apiInterfaceAudit.getMethod());
        if (interfaceInfo != null && !apiInterfaceAudit.getApiId().equals(interfaceInfo.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称已经存在");
        }

//        修改数据库对应数据
        apiInterfaceAudit.setUserId(userId);
        apiInterfaceAudit.setId(dbInterfaceAudit.getId());
        apiInterfaceAudit.setUpdateTime(new Date());
        boolean updateResult = this.update(apiInterfaceAudit, Wrappers.<ApiInterfaceAudit>lambdaUpdate().eq(ApiInterfaceAudit::getApiId, apiInterfaceAudit.getApiId()));
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败");
        }

//        修改接口状态
        Boolean updateStatus = dubboInterfaceinfoService.updateInterfaceStatusById(apiInterfaceAudit.getApiId(), InterfaceInfoConstant.STATIC_AUDITING);
        if (!updateStatus) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改接口状态失败");
        }

//        重新进行审核
        this.sendAuditInterfaceMsgToGpt(apiInterfaceAudit);

        return true;
    }

    /**
     * 封装好发送到gpt的list<> messages数据类型
     *
     * @return
     */
    private List<GPTMessage> getMessageList(ApiInterfaceAudit audit) {
        ArrayList<GPTMessage> messages = new ArrayList<>();
        GPTMessage gptMessage = new GPTMessage();
//       添加请求条件
        gptMessage.setRole("system");
        gptMessage.setContent("The interface information submitted by users has been reviewed and its content must conform to the core socialist values and comply with specific domestic legal requirements. You need to return status code 3 (requirements met) or 2 (requirements not met) to indicate whether the review met the requirements. Additionally, please provide a message (msg) indicating any review suggestions. Please reply in Chinese.\n" +
                "        Input example:\n" +
                "        {'id':1749814169059741698,'apiId':55,'name':'获取暴力视频','apiDescription':'获取暴力视频','uri':'/violence','host':'http://localhost:9000','method':'POST','pointsRequired':199,'requestHeader':'无','responseHeader':'无','requestParams':'无','getRequestParams':'无','userId':1,'status':1,'updateTime':1706095125811}\n" +
                "        Answer example:\n" +
                "        {'id':1,'code':2,'msg':'接口不能包含暴力信息'}");
        messages.add(gptMessage);

//       添加要审核的内容
        messages.add(new GPTMessage("user", JSONUtil.toJsonStr(audit)));
        return messages;
    }

}




