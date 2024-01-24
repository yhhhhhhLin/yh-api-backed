package xyz.linyh.gpt.comsumer;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import xyz.linyh.dubboapi.service.DubboAuditInterfaceService;
import xyz.linyh.ducommon.constant.AuditMQTopicConstant;
import xyz.linyh.gpt.service.GptSendService;
import xyz.linyh.model.gpt.dtos.InterfaceResult;
import xyz.linyh.model.gpt.eneitys.GPTBody;
import xyz.linyh.model.gpt.eneitys.GPTMessage;
import xyz.linyh.model.gpt.eneitys.GPTResponse;
import xyz.linyh.tokenpool.client.TokenPoolClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 用来接收相关的接口审核信息
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = AuditMQTopicConstant.API_INTERFACE_AUDIT_TOPIC, consumerGroup = "audit-interface-consumer-topic-1")
public class AuditInterfaceListener implements RocketMQListener<String> {

    @Autowired
    private GptSendService gptSendService;

    @DubboReference
    private DubboAuditInterfaceService dubboAuditInterfaceService;

    @Autowired
    private TokenPoolClient tokenPoolClient;


    @Override
    public void onMessage(String message) {
        log.info("接收到要审核的信息:{}", message);

        List<GPTMessage> messages = JSONUtil.toList(JSONUtil.parseArray(message), GPTMessage.class);

        processMessage(messages);

    }

    /**
     * 发送消息给gpt 如果返回结果不对，会进行重试发送，如果超过最大次数不对，那么就不会发送给gpt审核了
     * @param messages 发送给gpt的消息
     */
    public void processMessage(List<GPTMessage> messages) {

        try {
            Thread.sleep(1000);
            log.info("开始准备发送要审核的数据给gpt");
            int maxTry = 5;
            int nowTry = 0;
            Boolean isOk = false;

//       发送信息到gpt,如果不对会重试五次
            InterfaceResult interfaceResult = null;
            while (nowTry < maxTry) {
                GPTMessage returnMessage = gptSendService.sendRequest(messages);
                log.info("返回的消息为:{}", returnMessage);
                interfaceResult = check(returnMessage);

                if (interfaceResult == null) {
                    log.info("重试次数为:{}", nowTry);
                    nowTry++;
//                    限制重试的速度
                    Thread.sleep(5000);
                } else {
                    isOk = true;
                    break;
                }

            }

            if (isOk) {
                log.info("审核完成，开始去调用audit模块的相关服务..................,结果为:{}", interfaceResult);
                try {
                    dubboAuditInterfaceService.updateAuditInterfaceCodeAndMsg(interfaceResult.getId(), Integer.valueOf(interfaceResult.getCode()), interfaceResult.getMsg());
                } catch (NumberFormatException e) {
                    log.info("更新审核接口的code和msg出现异常: {}", e.getMessage());
                }
            }

        } catch (InterruptedException e) {
            log.info("接口处理异常{}", e.getMessage());
        }
    }

    /**
     * 判断是否还需要继续问gpt
     *
     * @param returnMessage
     * @return
     */

    private InterfaceResult check(GPTMessage returnMessage) {
        if (returnMessage == null) {
            return null;
        }

//        如果他回复的最后一个为空也需要重试
        if (StringUtils.isBlank(returnMessage.getContent())) {
            return null;
        }

        try {
            return JSONUtil.toBean(returnMessage.getContent(), InterfaceResult.class);
        } catch (Exception e) {
            log.info("返回的结果不符合要求，需要重新发送请求");
            return null;
        }

    }


}
