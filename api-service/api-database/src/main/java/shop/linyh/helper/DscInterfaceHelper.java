package shop.linyh.helper;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import xyz.linyh.dubboapi.service.DubboUserService;
import xyz.linyh.model.user.entitys.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author linzz
 */
@Component
public class DscInterfaceHelper {


    @DubboReference
    private DubboUserService dubboUserService;

    /**
     * 拼接和执行sql，返回查询结果
     * @param request
     * @return
     */
    public Object queryData(HttpServletRequest request) {

//        TODO 根据获取的用户id和接口地址查询获取接口，然后获取查询的数据库地址和查询的字段拼接sql
        String accessKey = request.getHeader("accessKey");
        User user = dubboUserService.getUserByAk(accessKey);
        if(user == null) {
//            TODO
            System.out.println("错误");
            return null;
        }
        Long userId = user.getId();


//        TODO 返回格式
        return null;
    }
}
