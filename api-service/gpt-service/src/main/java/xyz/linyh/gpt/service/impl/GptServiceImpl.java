package xyz.linyh.gpt.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.linyh.gpt.service.GptService;
import xyz.linyh.model.apiaudit.eneitys.MsgRoleAndContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用来发送消息到gpt
 */
@Service
@Slf4j
public class GptServiceImpl implements GptService {

    /**
     * 用来发送审核api接口是否符合规范
     */
   public Boolean auditApiInterface(String auditContext) {
      log.info("准备开始发送Interface接口消息给gpt审核:{}",auditContext);
       List<MsgRoleAndContent> systems = new ArrayList<>();
       systems.add(new MsgRoleAndContent("system", "You are an audit assistant on the api open platform. Your responsibility is to conduct a preliminary review of the api interface information, which must be conducted in strict accordance with Chinese laws and regulations, and return the processing results in strict accordance with the requirements of the return format."));
       systems.add(new MsgRoleAndContent("system","You can only return content in this format: {\"code\": 0, \"msg\": \"\"}"));

       log.info("审核完成一个Interface的gpt审核:{}",auditContext);
      return true;
   }


    /**
     * 发送审核相关的信息给gpt
      * @param systems 给gpt设定的角色和相关返回结果码
     * @param reqDescription 审核的要求
     * @param auditMsg 真正要审核的内容
     * @return
     */
   public String sendAuditMsgToGpt(List<MsgRoleAndContent> systems, Map<String,Object> reqDescription, Map<String,String> auditMsg){

       return null;
   }

}
