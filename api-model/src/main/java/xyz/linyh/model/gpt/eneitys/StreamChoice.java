package xyz.linyh.model.gpt.eneitys;

import lombok.Data;

@Data
public class StreamChoice {
    private int index;

    /**
     * gpt返回的信息
     */
    private DeltaContent delta;

    private String finish_reason;

}
