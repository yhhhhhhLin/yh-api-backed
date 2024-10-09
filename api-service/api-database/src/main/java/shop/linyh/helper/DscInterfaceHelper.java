package shop.linyh.helper;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import xyz.linyh.dubboapi.service.DubboDscInterfaceColumnService;
import xyz.linyh.dubboapi.service.DubboInterfaceinfoService;
import xyz.linyh.dubboapi.service.DubboUserService;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author linzz
 */
@Component
public class DscInterfaceHelper {


    @DubboReference
    private DubboUserService dubboUserService;

    @DubboReference
    private DubboInterfaceinfoService dubboInterfaceinfoService;

    @DubboReference
    private DubboDscInterfaceColumnService dscInterfaceColumnService;

    private final String TABLE_NAME_FIX = "yh_api";

    private final String SELECT_SQL_TEMPLATE = "select %s from %s.%s";


    /**
     * 拼接和执行sql，返回查询结果
     * 返回结果json
     *
     * @param request
     * @return
     */
    public String queryData(HttpServletRequest request) {

//        TODO 根据获取的用户id和接口地址查询获取接口，然后获取查询的数据库地址和查询的字段拼接sql
        String accessKey = request.getHeader("accessKey");
        String uri = request.getHeader("uri");
//        请求体获取查询时间，如果没有就为当天时间
        String selectTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
//        TODO 校验一下为为空什么的
        User user = dubboUserService.getUserByAk(accessKey);
        if (user == null) {
//            TODO
            System.out.println("错误");
            return null;
        }
        Long userId = user.getId();
//        根据用户id和接口地址获取数据源接口信息
        Interfaceinfo interfaceinfo = dubboInterfaceinfoService.queryInterfaceByURIAndUserId(uri, userId);
        if (interfaceinfo == null) {
//            TODO
            return null;
        }


        Long interfaceInfoId = interfaceinfo.getId();
        List<DscInterfaceColumn> dscInterfaceColumns = dscInterfaceColumnService.getDscInterfaceColumnByInterfaceId(interfaceInfoId);
        if (dscInterfaceColumns == null) {
//            TODO
            return null;
        }

        String selectSql = createSelectSql(dscInterfaceColumns, selectTime);

        return executeSelectSql(selectSql);
    }

    /**
     * 根据数据源地址查询数据库获取信息
     *
     * @param selectSql
     * @return
     */
    private String executeSelectSql(String selectSql) {


//        转为json返回结果
        return null;
    }

    private String createSelectSql(List<DscInterfaceColumn> dscInterfaceColumns, String time) {
        if (dscInterfaceColumns == null || dscInterfaceColumns.isEmpty()) {
            return null;
        }
//       TODO 暂时单表查询先
        DscInterfaceColumn firstDscInterfaceCloumn = dscInterfaceColumns.get(0);
        String schemaName = firstDscInterfaceCloumn.getSchemaName();
        String tableName = firstDscInterfaceCloumn.getTableName();
        String resultTableName = String.format("%s.%s", schemaName, tableName + TABLE_NAME_FIX + "_" + time);


        StringBuffer selectField = new StringBuffer();
        dscInterfaceColumns.forEach(column -> {
            String columnAlias = column.getColumnAlias();
            selectField.append(columnAlias).append(",");
        });

        selectField.deleteCharAt(selectField.length() - 1);
        return String.format(SELECT_SQL_TEMPLATE, selectField, schemaName, resultTableName);
    }
}
