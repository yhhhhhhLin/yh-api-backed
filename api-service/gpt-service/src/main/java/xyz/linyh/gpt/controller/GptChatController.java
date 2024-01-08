package xyz.linyh.gpt.controller;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.utils.JwtUtils;
import xyz.linyh.gpt.service.GptSendService;
import xyz.linyh.model.gpt.eneitys.GPTMessage;
import xyz.linyh.model.gpt.eneitys.GPTResponse;
import xyz.linyh.model.gpt.eneitys.GPTStreamResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author lin
 */
@RestController
@Slf4j
public class GptChatController {


    @Autowired
    private GptSendService gptSendService;

    /**
     * 只能先管理员用而已
     *
     * @param messages
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/gptChat")
    public Flux<ServerSentEvent> chat(@RequestBody List<GPTMessage> messages, HttpServletRequest request) {
//        messages = this.SplitMessage(messages);
        Flux<String> stringFlux = gptSendService.sendChatRequest(messages);


        return stringFlux.mapNotNull(message -> {
//            System.out.println(message);
            if ("[DONE]".equals(message)) {
                return null;
            }
//            System.out.println();
            GPTStreamResponse response = JSONUtil.toBean(message, GPTStreamResponse.class);
            if (response.getChoices().get(0).getFinish_reason() == null) {
                System.out.println(response.getChoices().get(0));
                return ServerSentEvent.builder(response.getChoices().get(0)).build();
            } else if (response.getChoices().get(0).getFinish_reason().equals("stop")) {
                System.out.print("stop");
                return ServerSentEvent.builder(response.getChoices().get(0)).build();
            }
            return null;
        });

    }
//分析需求:\n分析个个月数的销售额\n原始数据\n月数 销售额\n1,1000\n2,2000 \n3,1500\n4,1800\n5,1200\n6,2500
//7,1700
//8,1900

    /**
     * 只能先管理员用而已
     *
     * @param messages
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/gptChatNoStream")
    public BaseResponse<GPTMessage> chatNoStream(@RequestBody List<GPTMessage> messages, HttpServletRequest request) {
//        判断gpt的字数长度是否超过长度限制，如果超过长度限制，那么就把一个message拆分成多个message
//        messages = SplitMessage(messages);
        List<GPTMessage> returnMessages = gptSendService.sendRequest(messages);
        if (returnMessages == null || returnMessages.size() == 0) {
            return ResultUtils.error(500, "服务器错误");
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println(returnMessages.get(returnMessages.size() - 1));
        return ResultUtils.success(returnMessages.get(returnMessages.size() - 1));
    }

    private List<GPTMessage> SplitMessage(List<GPTMessage> messages) {
//        判断最后一个问题长度是否超过长度限制
        GPTMessage gptMessage = messages.get(messages.size() - 1);
        Integer maxLength = 1000;
        String content = gptMessage.getContent();
//        长
//        度最长只能有1000
        if (content.length() <= maxLength) {
            return messages;
        } else {
            messages.remove(messages.size() - 1);
        }
        for (int i = 0; i <= content.length() / maxLength; i++) {
            GPTMessage newGptMessage = new GPTMessage();
            newGptMessage.setRole("user");
            newGptMessage.setContent(content.substring(i * maxLength, i * maxLength + Math.min(content.length() - i * maxLength, maxLength)));
            messages.add(newGptMessage);

        }
        return messages;
    }


}
