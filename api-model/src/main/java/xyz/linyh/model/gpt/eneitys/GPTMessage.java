package xyz.linyh.model.gpt.eneitys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * gpt请求message单个实体类
 * @author lin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPTMessage {

    private String role;

    private Object content;
}
