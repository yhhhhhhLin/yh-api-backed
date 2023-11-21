package xyz.linyh.gpt.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 获取配置文件里面配置
 */
@ConfigurationProperties(prefix = "gpt.service")
@Data
@ToString
public class GPTProperties {

    /**
     * 平台上面创建的appId
     */
    private String appId;

    /**
     * 用户apiKey
     */
    private String apiKey;

    /**
     * 用户密钥
     */
    private String apiSecret;

    /**
     * 那个gpt版本模型
     */
    private String model="gpt-3.5-turbo";

    /**
     * 代理地址
     */
    private String host="api.openai.com";

    /**
     * 是否流式返回
     */
    private Boolean isStream=false;

    /**
     * 温度
     */
    private double temperature = 0.7;


    /**
     * 给这个助手的定义，如果没有就是通用的
     */
    private String systemRole;
}
