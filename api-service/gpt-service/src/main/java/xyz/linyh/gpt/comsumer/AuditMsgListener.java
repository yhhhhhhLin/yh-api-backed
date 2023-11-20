package xyz.linyh.gpt.comsumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 *
 通用的一个接收审核消息的接口
 */
@Service
@RocketMQMessageListener(topic = "test-topic-1", consumerGroup = "my-consumer_test-topic-1")
@Slf4j
public class AuditMsgListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
//        返回给用户对应状态码

        log.info("接收到要审核的信息: {}", message);
    }
}
