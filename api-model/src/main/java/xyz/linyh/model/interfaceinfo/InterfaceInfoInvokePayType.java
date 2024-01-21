package xyz.linyh.model.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInfoInvokePayType implements Serializable {
    private static final long serialVersionUID = 1L;
    private String payType;
    private Integer payAmount;
}
