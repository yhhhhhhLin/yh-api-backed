package xyz.linyh.model.gpt.eneitys;

import lombok.Data;

/**
 * gpt返回的信息(存储了用了多少token信息)
 */
@Data
public class Usage {
    private Integer prompt_tokens;

    private Integer completion_tokens;

    private Integer total_tokens;
}
