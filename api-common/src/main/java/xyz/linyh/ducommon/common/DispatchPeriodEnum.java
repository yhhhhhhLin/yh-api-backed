package xyz.linyh.ducommon.common;

public enum DispatchPeriodEnum {

    DAY(1,"day"),

    WEEK(1, "week");


    private final Integer code;

    private final String name;

    DispatchPeriodEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public static DispatchPeriodEnum getDispatchPeriodEnum(Integer code) {
        return DispatchPeriodEnum.values()[code];
    }
}
