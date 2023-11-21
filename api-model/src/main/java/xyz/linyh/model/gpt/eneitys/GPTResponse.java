package xyz.linyh.model.gpt.eneitys;

import lombok.Data;

import java.util.List;

/**
 * gpt返回body结构体
 */
@Data
public class GPTResponse {

    private String id;

    private String object;

    private int created;

    private String model;

    private List<Choice> choices;

    private Usage usage;


}
