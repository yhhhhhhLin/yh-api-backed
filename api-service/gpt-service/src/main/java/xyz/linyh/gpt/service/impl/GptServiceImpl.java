package xyz.linyh.gpt.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import xyz.linyh.gpt.service.GptService;
import xyz.linyh.model.apiaudit.eneitys.AuditCommon;
import xyz.linyh.model.gpt.dtos.InterfaceResult;
import xyz.linyh.model.gpt.eneitys.GPTMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * 用来发送消息到gpt
 */
@Service
@Slf4j
public class GptServiceImpl implements GptService {

    @Autowired
    private   GptSendServiceImpl gptSendService;

    /**
     * 用来发送审核api接口是否符合规范
     */
    @Override
   public Boolean auditApiInterface(String auditContext) {
      log.info("准备开始发送Interface接口消息给gpt审核:{}",auditContext);
      List<GPTMessage> messages = new ArrayList<>();
       GPTMessage gptMessage = new GPTMessage();
//       添加请求条件
       gptMessage.setRole("system");
       gptMessage.setContent("lease review the interface information entered by the user and ask that the content should meet the core socialist values and comply with some domestic legal requirements. You should return a status code 200 (for success) or 0 (for failure) to indicate whether the review was passed, and you should also return a msg to indicate the review recommendation.n input example: {'auditId':1,'auditDescription':'全网搜索并获取暴力视频'','type':'API_INTERFACE_AUDIT_TYPE','content':{'id':1,'name':' 获取暴力视频 ','apiDescription':' Search and obtain violent videos ','uri':' /get','host':'localhost:8090','method':'POST','requestheader':' null','responseheader':' None ','requestparams':' None ','getrequestpa rams':' None ','userId':1,'status':1,'description':'','createtime':1700536839419,'updatetime':1700536839419},'auditCreateTime ':1700536851628}. \nAnswer example: {'code':0,'msg':'name must not contain violence information '}");
       messages.add(0,gptMessage);
       AuditCommon auditCommon = JSONUtil.toBean(auditContext, AuditCommon.class);
       String content = (String) auditCommon.getContent();
       messages.add(new GPTMessage("user",content));
       log.info("准备开始发送Interface接口消息给gpt审核:{}",messages);

//       发送消息到gpt
       List<GPTMessage> returnMessages = gptSendService.sendRequest(messages);

//       获取返回格式
//       判断是否符合格式
       GPTMessage lastMessage = returnMessages.get(returnMessages.size() - 1);

       try {
           InterfaceResult bean = JSONUtil.toBean(lastMessage.getContent().toString(), InterfaceResult.class);
//           todo 如果格式正确，那么调用audit服务的对应的方法，更新audit表里面对应字段的code和msg

       } catch (Exception e) {
//           如果强转失败，那么重新放到线程池然后重试
           log.info("有消息返回结果类型错误: {} ",lastMessage.getContent());
           throw new RuntimeException(e);
       }



       log.info("审核完成一个Interface的gpt审核:{}",auditContext);
      return true;
   }




    public Boolean audit(){

        return true;
    }

}
