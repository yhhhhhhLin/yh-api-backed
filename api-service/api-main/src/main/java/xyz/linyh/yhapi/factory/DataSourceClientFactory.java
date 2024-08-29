package xyz.linyh.yhapi.factory;

import io.swagger.models.auth.In;
import org.aspectj.weaver.ast.Var;
import xyz.linyh.ducommon.common.DscTypeEnum;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.yhapi.datasource.DataSourceClient;
import xyz.linyh.yhapi.datasource.MysqlDataSrouceClient;

import java.util.HashMap;
import java.util.Map;

public class DataSourceClientFactory {

    private static final Map<Integer, DataSourceClient> clients = new HashMap<>();


    public static DataSourceClient getClient(Integer dscTypeCode, String url, String username, String password) {
//        如果已经在缓存的话直接返回
        if (clients.containsKey(dscTypeCode)) {
            return clients.get(dscTypeCode);
        }

        DataSourceClient client = null;
        DscTypeEnum dscType = DscTypeEnum.getDscTypeEnum(dscTypeCode);
        switch (dscType) {
            case MYSQL:
                client = new MysqlDataSrouceClient(url, username, password);
                break;
            default:
                throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "没有定义的数据类型");
        }

        clients.put(dscTypeCode, client);


        return client;
    }
}
