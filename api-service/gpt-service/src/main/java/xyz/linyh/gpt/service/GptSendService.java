package xyz.linyh.gpt.service;


import reactor.core.publisher.Flux;
import xyz.linyh.model.gpt.eneitys.GPTMessage;
import xyz.linyh.model.gpt.eneitys.GPTResponse;
import xyz.linyh.model.gpt.eneitys.GPTStreamResponse;

import java.util.List;

/**
 * gpt的一些操作处理
 */
public interface GptSendService {


    public List<GPTMessage> sendRequest(List<GPTMessage> messages);


    public Flux<String> sendChatRequest(List<GPTMessage> messages);


}
