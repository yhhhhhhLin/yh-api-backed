package xyz.linyh.gpt.service;


import xyz.linyh.model.gpt.eneitys.GPTMessage;

import java.util.List;

/**
 * gpt的一些操作处理
 */
public interface GptSendService {


    public List<GPTMessage> sendRequest(List<GPTMessage> messages);



}
