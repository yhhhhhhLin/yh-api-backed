package xyz.linyh.model.apiaudit.eneitys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgRoleAndContent implements Serializable {

    private String role;

    private String content;

}
