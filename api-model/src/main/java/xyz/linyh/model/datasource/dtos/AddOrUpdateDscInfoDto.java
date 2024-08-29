package xyz.linyh.model.datasource.dtos;

import lombok.Data;

@Data
public class AddOrUpdateDscInfoDto{

    private Long id;

    /**
     * 数据源类型
     */
    private Integer dscType;

    private String url;

    private String username;

    private String password;

    private String schemaName;

    private Long userId;

    private Integer status;
}
