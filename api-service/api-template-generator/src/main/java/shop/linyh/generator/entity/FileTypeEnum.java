package shop.linyh.generator.entity;

import lombok.Getter;

/**
 * 文件类型枚举
 */
@Getter
public enum FileTypeEnum {
    GROUP("文件组","group"),
    FILE("文件","file"),
    DIR("目录","dir");

    private final String text;
    private final String value;

    FileTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
