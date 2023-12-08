package xyz.linyh.model.gpt.eneitys;

import lombok.Data;

import java.util.List;

/**
 * gpt流式返回body结构体
 */
@Data
public class GPTStreamResponse {

    private String id;

    private String object;

    private int created;

    private String model;

    private List<StreamChoice> choices;

    private Usage usage;


}
