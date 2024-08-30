package xyz.linyh.yhapi.factory;

import xyz.linyh.ducommon.common.DscTypeEnum;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.yhapi.helper.GenSql;
import xyz.linyh.yhapi.helper.MysqlGenSql;

import java.util.HashMap;
import java.util.Map;

public class GenSqlFactory {

    private static final Map<Integer, GenSql> clients = new HashMap<>();


    public static GenSql getGensql(Integer dscTypeCode) {
//        如果已经在缓存的话直接返回
        if (clients.containsKey(dscTypeCode)) {
            return clients.get(dscTypeCode);
        }

        GenSql client = null;
        DscTypeEnum dscType = DscTypeEnum.getDscTypeEnum(dscTypeCode);
        switch (dscType) {
            case MYSQL:
                client = new MysqlGenSql();
                break;
            default:
                throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "没有定义的数据类型");
        }

        clients.put(dscTypeCode, client);

        return client;
    }

}
