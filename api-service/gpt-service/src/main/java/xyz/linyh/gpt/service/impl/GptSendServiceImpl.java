package xyz.linyh.gpt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import xyz.linyh.gpt.config.GPTProperties;
import xyz.linyh.gpt.service.GptSendService;
import xyz.linyh.model.gpt.eneitys.GPTBody;
import xyz.linyh.model.gpt.eneitys.GPTMessage;
import xyz.linyh.model.gpt.eneitys.GPTResponse;

import javax.annotation.Resource;

import java.util.*;

@Service
public class GptSendServiceImpl implements GptSendService {

    private final String ADDRESS_PRE = "https://";

    private final String ADDRESS_AFTER = "/v1/chat/completions";

    @Autowired
    private GPTProperties properties;


    @Autowired
    RestTemplate restTemplate;

//    请求如果出现错误返回优化 todo
    @Override
    public List<GPTMessage> sendRequest(List<GPTMessage> messages) {

//        插入初始化配置
        GPTBody gptBody = new GPTBody.Builder()
                .messages(messages)
                .model(properties.getModel())
                .temperature(properties.getTemperature())
                .stream(properties.getIsStream())
                .build();

        HttpEntity<GPTBody> httpEntity = new HttpEntity<>(gptBody);
//        在配置里面配置了通过resTemplate发送请求可以添加apikey请求头
        try {
            ResponseEntity<GPTResponse> responseEntity = restTemplate.postForEntity(requestURL(), httpEntity, GPTResponse.class);

            HttpStatus statusCode = responseEntity.getStatusCode();

            if(statusCode == HttpStatus.OK){
                GPTResponse response = responseEntity.getBody();
                GPTMessage message = response.getChoices().get(0).getMessage();
                System.out.println(message);

//                将请求后的结果保存到messages中，然后返回
            messages.add(message);
            return messages;
            }else{
                System.out.println("请求相应出现错误");
            }
        } catch (RestClientException e) {
            System.out.println(e);
            System.out.println("发送请求出错");
        }
//        如果发送异常，那么就直接将gpt回复设置为空的然后返回给前端
        GPTMessage message = new GPTMessage();
        message.setRole("assistant");
        message.setContent("");
        messages.add(message);
        return messages;
    }


    public String requestURL(){
        String host = properties.getHost();

        String requestAddress = ADDRESS_PRE+host+ADDRESS_AFTER;

        return requestAddress;
    }


}
