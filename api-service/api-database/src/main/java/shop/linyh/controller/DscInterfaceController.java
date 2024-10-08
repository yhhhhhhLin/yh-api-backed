package shop.linyh.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.linyh.helper.DscInterfaceHelper;
import xyz.linyh.ducommon.common.BaseResponse;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DscInterfaceController {

    private DscInterfaceHelper dscInterfaceHelper;

    /**
     * 处理数据源接口
     * @param request
     * @return
     */
    @PostMapping("/yhapiBatabase")
    public BaseResponse processDatasourceInterface(HttpServletRequest request){
        String result = dscInterfaceHelper.queryData(request);

        return null;
    }
}
