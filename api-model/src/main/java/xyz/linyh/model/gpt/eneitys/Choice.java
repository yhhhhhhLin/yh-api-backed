package xyz.linyh.model.gpt.eneitys;

import lombok.Data;

/**
 * 返回的信息内容
 */
@Data
public class Choice {
    private int index;

    /**
     * gpt返回的信息
     */
    private GPTMessage message;

    private String finish_reason;


}
