package shop.linyh.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DscInterfaceController {

    /**
     * 处理数据源接口
     * @param request
     * @return
     */
    @PostMapping("/yhapiBatabase")
    public BaseResponse processDatasourceInterface(HttpServletRequest request){
//        TODO 根据获取的用户id和接口地址查询获取接口，然后获取查询的数据库地址和查询的字段拼接sql

        return null;
    }
}
