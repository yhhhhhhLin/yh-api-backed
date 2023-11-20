package xyz.linyh.audit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import xyz.linyh.audit.mapper.ApiinterfaceauditMapper;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.AuditConstant;
import xyz.linyh.ducommon.constant.AuditMQTopicConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.apiaudit.eneitys.ApiInterfaceAudit;
import xyz.linyh.model.apiaudit.eneitys.AuditCommon;

import java.util.Date;

/**
* @author lin
* @description 针对表【apiinterfaceaudit(api接口审核表)】的数据库操作Service实现
* @createDate 2023-11-20 09:33:12
*/
@Service
@Slf4j
public class ApiInterfaceAuditServiceImpl extends ServiceImpl<ApiinterfaceauditMapper, ApiInterfaceAudit>
    implements ApiinterfaceauditService{


    @Autowired
    private RocketMQTemplate rocketMqTemplate;


    /**
     * 发送消息到消息队列中，gpt模块会将消息队列取出然后审核
     * 发送整个消息审核的结构给gpt
     */
   public void sendAuditInterfaceMsgToGpt(ApiInterfaceAudit audit){
       if(audit== null || audit.getId()==null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR,"审核内容或审核的内容id不能为空参数错误");
       }

//       将要审核的内容封装为发送给gpt的类型
       String auditJson = generAuditCommonString(audit);

       try {
           rocketMqTemplate.convertAndSend(AuditMQTopicConstant.API_INTERFACE_AUDIT_TOPIC, auditJson);
       } catch (MessagingException e) {
//           如果实在发送不过去，就先不管了
           log.info("生产者消息发送失败：{}",audit);
           throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息发送出问题了");
       }
   }

    /**
     * 更具要审核的实体类，生成一个对应的发送给gpt的通用的字符串
     * @return
     */
   public String generAuditCommonString(ApiInterfaceAudit audit){
//       不能提前转
       AuditCommon auditCommon = new AuditCommon();
       auditCommon.setType(AuditConstant.API_INTERFACE_AUDIT_TYPE);
       auditCommon.setContent(audit);
       auditCommon.setAuditCreateTime(new Date());
       auditCommon.setAuditDescription("Review this interface information for compliance with Chinese legal and ethical requirements");
       auditCommon.setAuditId(audit.getId());
       return JSONUtil.toJsonStr(auditCommon);
   }


}




