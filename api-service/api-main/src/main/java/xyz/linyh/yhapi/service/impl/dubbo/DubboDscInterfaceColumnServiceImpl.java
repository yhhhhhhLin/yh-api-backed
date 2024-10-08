package xyz.linyh.yhapi.service.impl.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.dubboapi.service.DubboDscInterfaceColumnService;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;
import xyz.linyh.yhapi.service.DscInterfaceColumnService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * dubbo dscInterfaceColumn
 *
 * @author linzz
 */
@DubboService
public class DubboDscInterfaceColumnServiceImpl implements DubboDscInterfaceColumnService {


    @Autowired
    private DscInterfaceColumnService dscInterfaceColumnService;

    @Override
    public List<DscInterfaceColumn> getDscInterfaceColumnByInterfaceId(Long interfaceId) {
        return dscInterfaceColumnService.listByInterfaceInfoIds(Collections.singletonList(interfaceId));
    }
}
