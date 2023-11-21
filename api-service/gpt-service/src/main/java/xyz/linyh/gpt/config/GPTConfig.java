package xyz.linyh.gpt.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;



/**
 * 会在启动的时候将配置文件里面的配置注入到这个类里面，可以通过配置文件信息配置apiKey
 * @author lin
 */
@Configuration
@EnableConfigurationProperties(GPTProperties.class)
@Slf4j
public class GPTConfig {

    private final GPTProperties gptProperties;

    public GPTConfig(GPTProperties gptProperties){
        this.gptProperties = gptProperties;
        System.out.println(gptProperties.getModel());
    }


    /**
     * 在restTemplate发送请求前都会增加一个gpt认证的请求头信息
     * @return
     */
    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(45000);
        requestFactory.setReadTimeout(45000);
        restTemplate.setRequestFactory(requestFactory);
//        添加拦截器，每次用这个restTemplate发送请求都会携带上这些请求头
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization",  gptProperties.getApiKey());
            request.getHeaders().remove("Content-Type");
            request.getHeaders().add("Content-Type","application/json");
            return execution.execute(request, body);
        });

        return restTemplate;
    }

}
