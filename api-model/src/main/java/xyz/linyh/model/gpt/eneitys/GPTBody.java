package xyz.linyh.model.gpt.eneitys;

import lombok.Data;

import java.util.List;

/**
 * gpt请求体内容
 */
@Data
public class GPTBody {

    private List<GPTMessage> messages;

    private double temperature;

    private boolean stream;

    private String model;


    private GPTBody(Builder builder) {
        this.messages = builder.messages;
        this.stream=builder.stream;
        this.model = builder.model;
        this.temperature = builder.temperature;
    }

    /**
     * 利用构建者模式快速选择创建对应body信息
     */
    public static class Builder {

//        @Autowired
//        private GPTProperties properties;
        private double temperature;

        private boolean stream;

        private String model;

        private List<GPTMessage> messages;

        public Builder stream(boolean stream){
            this.stream=stream;
            return this;
        }

        public Builder messages(List<GPTMessage> messages){
            this.messages=messages;
            return this;
        }

        public Builder temperature(double temperature){
            this.temperature=temperature;
            return this;
        }

        public Builder model(String model){
            this.model=model;
            return this;
        }

        public GPTBody build(){
            return new GPTBody(this);
        }
    }
}
