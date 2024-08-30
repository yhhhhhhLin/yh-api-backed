package xyz.linyh.model.datasource.vos;

import lombok.Data;

@Data
public class ColumnBriefVO {
    private String schemaName;
    private String tableName;
    private String columnName;
    private String columnAlias;
    private String columnType;
    private String columnComment;

}
