package xyz.linyh.gpt.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.linyh.gpt.config.GPTProperties;
import xyz.linyh.gpt.service.GptSendService;
import xyz.linyh.model.gpt.eneitys.GPTBody;
import xyz.linyh.model.gpt.eneitys.GPTMessage;
import xyz.linyh.model.gpt.eneitys.GPTResponse;
import xyz.linyh.model.gpt.eneitys.GPTStreamResponse;
import xyz.linyh.tokenpool.client.TokenPoolClient;

import javax.annotation.Resource;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.*;

@Service
@Slf4j
public class GptSendServiceImpl implements GptSendService {

    private final String ADDRESS_PRE = "https://";

    private final String ADDRESS_AFTER = "/v1/chat/completions";

    @Autowired
    private GPTProperties properties;


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private TokenPoolClient tokenPoolClient;


    //    请求如果出现错误返回优化 todo
    @Override
    public List<GPTMessage> sendRequest(List<GPTMessage> messages) {

//        插入初始化配置
        GPTBody gptBody = new GPTBody.Builder()
                .messages(messages)
                .model(properties.getModel())
                .temperature(properties.getTemperature())
                .stream(false)
                .build();

//        在配置里面配置了通过resTemplate发送请求可以添加apikey请求头
        try {
            HttpHeaders headers = new HttpHeaders();

            ResponseEntity<GPTResponse> responseEntity = tokenPoolClient.<ResponseEntity<GPTResponse>>addTask((token) -> {
                System.out.println("获取到的token为:" + token);
                headers.add("Authorization", token);
                HttpEntity<GPTBody> httpEntity = new HttpEntity<>(gptBody, headers);
                ResponseEntity<GPTResponse> gptResponseResponseEntity = restTemplate.postForEntity(requestURL(), httpEntity, GPTResponse.class);
                System.out.println("请求成功" + gptResponseResponseEntity);
//                Log.info("请求成功:{}");
                return gptResponseResponseEntity;
            });
//            ResponseEntity<GPTResponse> responseEntity = restTemplate.postForEntity(requestURL(), httpEntity, GPTResponse.class);

            HttpStatus statusCode = responseEntity.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                GPTResponse response = responseEntity.getBody();
                GPTMessage message = response.getChoices().get(0).getMessage();
                System.out.println(message);

//                将请求后的结果保存到messages中，然后返回
                messages.add(message);
                return messages;
            } else {
                System.out.println("请求相应出现错误");
            }
        } catch (RestClientException e) {
            System.out.println(e);
            System.out.println("发送请求出错");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        如果发送异常，那么就直接将gpt回复设置为空的然后返回给前端
        GPTMessage message = new GPTMessage();
        message.setRole("assistant");
        message.setContent("");
        messages.add(message);
        return messages;
    }


    @Override
    public Flux<String> sendChatRequest(List<GPTMessage> messages) {

//        插入初始化配置
        GPTBody gptBody = new GPTBody.Builder()
                .messages(messages)
                .model(properties.getModel())
                .temperature(properties.getTemperature())
                .stream(true)
                .build();

//        在配置里面配置了通过resTemplate发送请求可以添加apikey请求头
        try {
            Flux<String> stringFlux = tokenPoolClient.<Flux<String>>addTask((token) -> {
                WebClient webClient = WebClient.create();
//            返回的流式结果
                Flux<String> fluxResponse = webClient.post()
                        .uri(requestURL())
                        .header("Authorization", token)
                        .header("Content-Type", "application/json")
                        .bodyValue(gptBody)
                        .retrieve()
                        .bodyToFlux(String.class)
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            HttpStatus status = ex.getStatusCode();
                            String res = ex.getResponseBodyAsString();
                            log.error("OpenAI API error: {} {}", status, res);
                            return Mono.error(new RuntimeException(res));
                        });
                System.out.println("发送请求结束");

                return fluxResponse;
            });
            return stringFlux;


        } catch (RestClientException e) {
            log.info("发送请求出错,{}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        如果发送异常，那么就直接将gpt回复设置为空的然后返回给前端
        GPTMessage message = new GPTMessage();
        message.setRole("assistant");
        message.setContent("");
        messages.add(message);
//        将messages返回
        return null;
    }

    public String requestURL() {
        String host = properties.getHost();

        String requestAddress = ADDRESS_PRE + host + ADDRESS_AFTER;

        return requestAddress;
    }


}
