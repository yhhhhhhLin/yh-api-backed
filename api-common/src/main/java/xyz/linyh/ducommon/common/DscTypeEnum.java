package xyz.linyh.ducommon.common;


/**
 * @author linzz
 */

public enum DscTypeEnum {

    MYSQL(1,"mysql");


    private final Integer code;

    private final String dscTypeName;

    DscTypeEnum(Integer code, String dscTypeName) {
        this.code = code;
        this.dscTypeName = dscTypeName;
    }

    public Integer getCode() {
        return code;
    }

    public String getDscTypeName() {
        return dscTypeName;
    }
}
