package xyz.linyh.model.datasource.dtos;

import lombok.Data;

@Data
public class ListColumnsDto {

    private Long dscId;

    private String schemaName;

    private String tableName;
}
