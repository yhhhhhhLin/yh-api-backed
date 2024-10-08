package xyz.linyh.dubboapi.service;

import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;

import java.util.List;

public interface DubboDscInterfaceColumnService {

    List<DscInterfaceColumn> getDscInterfaceColumnByInterfaceId(Long interfaceId);
}
