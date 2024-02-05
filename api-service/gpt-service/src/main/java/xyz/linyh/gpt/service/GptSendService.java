package xyz.linyh.gpt.service;


import reactor.core.publisher.Flux;
import xyz.linyh.model.gpt.eneitys.GPTMessage;

import java.util.List;

/**
 * gpt的一些操作处理
 */
public interface GptSendService {


    public GPTMessage sendRequest(List<GPTMessage> messages);


    public Flux<String> sendChatRequest(List<GPTMessage> messages);


}
