package xyz.linyh.ducommon.common;

public enum InterfaceTypeEnum{

    DEFAULT_INTERFACE(1,"默认api"),
    DATABASE_INTERFACE(2,"数据库api");


    private final Integer code;

    private final String name;

    InterfaceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
