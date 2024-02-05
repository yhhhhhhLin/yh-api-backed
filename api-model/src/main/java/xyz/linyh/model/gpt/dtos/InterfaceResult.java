package xyz.linyh.model.gpt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceResult {

    private Long id;

    private String code;

    private String msg;
}
