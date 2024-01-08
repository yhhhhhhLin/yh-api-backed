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
//        将数据保存到真正的api文档里面
        Interfaceinfo interfaceinfo = new Interfaceinfo();

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
        interfaceinfo.setUpdateTime(new Date());

        Long apiId = dubboInterfaceinfoService.addInterface(interfaceinfo);
        if (apiId == null) {
//            抛出异常，事务管理
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "审核通过后，保存到真正的api文档里面失败");
        }

//        保存id到数据库，更新审核状态
        LambdaUpdateWrapper<ApiInterfaceAudit> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ApiInterfaceAudit::getId, auditId)
                .set(ApiInterfaceAudit::getStatus, AuditConstant.AUDIT_STATUS_PUBLISH)
                .set(ApiInterfaceAudit::getApiId, apiId);
        this.update(wrapper);

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
        gptMessage.setContent("lease review the interface information entered by the user and ask that the content should meet the core socialist values and comply with some domestic legal requirements. You should return a status code 3 (for success) or 2 (for failure) to indicate whether the review was passed, and you should also return a msg to indicate the review recommendation.n input example: {'auditId':1,'auditDescription':'全网搜索并获取暴力视频'','type':'API_INTERFACE_AUDIT_TYPE','content':{'id':1,'name':' 获取暴力视频 ','apiDescription':' Search and obtain violent videos ','uri':' /get','host':'localhost:8090','method':'POST','requestheader':' null','responseheader':' None ','requestparams':' None ','getrequestpa rams':' None ','userId':1,'status':1,'description':'','createtime':1700536839419,'updatetime':1700536839419},'auditCreateTime ':1700536851628}. \nAnswer example: {'id':1,'code':0,'msg':'name must not contain violence information '}");
        messages.add(0, gptMessage);

//       添加要审核的内容
        messages.add(gptMessage);

        messages.add(new GPTMessage("user", JSONUtil.toJsonStr(audit)));
        return messages;
    }

//    /**
//     * 更具要审核的实体类，生成一个对应的发送给gpt的通用的字符串
//     * @return
//     */
//   public String generAuditCommonString(ApiInterfaceAudit audit){
////       不能提前转
//       AuditCommon auditCommon = new AuditCommon();
//       auditCommon.setType(AuditConstant.API_INTERFACE_AUDIT_TYPE);
//       auditCommon.setContent(audit);
//       auditCommon.setAuditCreateTime(new Date());
////       auditCommon.setAuditDescription("Review this interface information for compliance with Chinese legal and ethical requirements");
//       auditCommon.setAuditId(audit.getId());
//       return JSONUtil.toJsonStr(auditCommon);
//   }


}




