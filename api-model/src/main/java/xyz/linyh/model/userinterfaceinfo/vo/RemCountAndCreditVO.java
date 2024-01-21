package xyz.linyh.model.userinterfaceinfo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemCountAndCreditVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer remNum;
    private Integer credits;
}
