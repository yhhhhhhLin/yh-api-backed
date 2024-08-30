package xyz.linyh.model.interfaceInfoDispatchInfo.dtos;

import lombok.Data;

/**
 * @author linzz
 */
@Data
public class DispatchInfoDto {

//    /**
//     * 是否重试 0-否 1-是
//     */
//    private Integer isRetry;
//
//    /**
//     * 重试次数
//     */
//    private Integer retryTimes;
//
//    /**
//     * 重试间隔时间（分钟）
//     */
//    private Integer intervalTime;

    /**
     * 调度周期,-1-手动调度,2 天
     */
    private Integer dispatchPeriod;

    /**
     * 具体时间 时分 HH:mm
     */
    private String specTime;
}
