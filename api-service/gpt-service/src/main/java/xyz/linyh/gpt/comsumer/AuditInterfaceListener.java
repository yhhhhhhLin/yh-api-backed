package xyz.linyh.gpt.comsumer;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.linyh.dubboapi.service.DubboAuditInterfaceService;
import xyz.linyh.ducommon.constant.AuditMQTopicConstant;
import xyz.linyh.gpt.service.GptSendService;
import xyz.linyh.model.gpt.dtos.InterfaceResult;
import xyz.linyh.model.gpt.eneitys.GPTMessage;

import java.util.List;
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

    //限制一次只能处理消息队列里面2个
    private final Semaphore semaphore = new Semaphore(1);

    @Override
    public void onMessage(String message) {
        log.info("接收到要审核的信息:{}", message);
//        限制只能一次处理2个，不能一次处理处理过多

//        开一个线程池，规定发给gpt的请求只能有5个同时进行
        List<GPTMessage> messages = JSONUtil.toList(JSONUtil.parseArray(message), GPTMessage.class);


////        开一个大小只有5的线程池，然后发送消息给gpt 等待结果返回，判断是否符合要求的返回格式，如果不符合，需要重新放到线程池里面
//        ExecutorService executorService = Executors.newFixedThreadPool(5);

//        先一次只能处理一个请求
        processMessage(messages);

//        todo 改为线程池异步执行相关任务，在指定线程池里面
//        CompletableFuture.runAsync(() -> processMessage(messages));

    }


    public void processMessage(List<GPTMessage> messages) {


        try {

            semaphore.acquire();
            Thread.sleep(1000);
            log.info("获取一个信号量，开始处理消息..................");
            int maxTry = 5;
            int nowTry = 0;
            Boolean isOk = false;

//       发送信息到gpt,如果不对会重试五次
            InterfaceResult interfaceResult = null;
            while (nowTry < maxTry) {
                List<GPTMessage> returnMessage = gptSendService.sendRequest(messages);
                interfaceResult = check(returnMessage);

                if (interfaceResult == null) {
                    log.info("重试次数为:{}", nowTry);
                    nowTry++;
//                    限制发送的速度
                    Thread.sleep(5000);
                } else {
                    isOk = true;
                    break;
                }

            }

            if (isOk) {
//            todo 刷新对应数据库信息
                log.info("审核完成，开始去调用audit模块的相关服务..................");
                System.out.println(interfaceResult);
                try {
                    dubboAuditInterfaceService.updateAuditInterfaceCodeAndMsg(interfaceResult.getId(), Integer.valueOf(interfaceResult.getCode()), interfaceResult.getMsg());
                } catch (NumberFormatException e) {
                    log.info("更新审核接口的code和msg出现异常: {}", e.getMessage());
                }
            }

        }catch (InterruptedException e){
            log.info("接口处理异常{}",e);
        }finally {
            semaphore.release();
        }
    }

    /**
     * 判断是否还需要继续问gpt
     *
     * @param returnMessage
     * @return
     */

    private InterfaceResult check(List<GPTMessage> returnMessage) {
        if (returnMessage == null) {
            return null;
        }
        if (returnMessage.size() >= 8) {
            return null;
        }
//        如果他回复的最后一个为空也需要重试
        if(StringUtils.isBlank(returnMessage.get(returnMessage.size()-1).getContent().toString())){
            return null;
        }
        try {

            InterfaceResult interfaceResult = JSONUtil.toBean(returnMessage.get(returnMessage.size() - 1).getContent().toString(), InterfaceResult.class);
            return interfaceResult;
        } catch (Exception e) {
            log.info("返回的结果不符合要求，需要重新发送请求");
            return null;
        }

    }


}
