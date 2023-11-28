package xyz.linyh.model.interfaceinfo.dto;

import lombok.Data;

@Data
public class RequestHeaderParamsDto {

    private Long id;

    private String headerKey;

    private String headerValue;
}
