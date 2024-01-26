package xyz.linyh.model.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceAllCountAndCallCount implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 可用接口数量
     */
    private long canUseInterfaceNum;

    /**
     * 接口调用总次数
     */
    private long interfaceTransferNum;
}
