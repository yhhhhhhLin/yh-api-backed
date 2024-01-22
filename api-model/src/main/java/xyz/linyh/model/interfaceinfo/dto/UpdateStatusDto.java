package xyz.linyh.model.interfaceinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long interfaceId;
    private Integer status;
}
