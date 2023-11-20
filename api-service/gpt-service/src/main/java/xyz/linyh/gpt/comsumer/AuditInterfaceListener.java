package xyz.linyh.gpt.comsumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.constant.AuditMQTopicConstant;

/**
 * 用来接收相关的接口审核信息
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = AuditMQTopicConstant.API_INTERFACE_AUDIT_TOPIC, consumerGroup = "audit-interface-consumer-topic-1")
public class AuditInterfaceListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        log.info("接收到要审核的信息:{}",message);
//        调用对应发送到gpt的方法，将消息发送到gpt

    }
}
