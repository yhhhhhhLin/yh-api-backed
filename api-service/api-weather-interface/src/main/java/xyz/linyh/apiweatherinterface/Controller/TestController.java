package xyz.linyh.apiweatherinterface.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.model.user.vo.UserVO;

/**
 * 测试接口调用测试是否可用
 */
@RestController
public class TestController {

    /**
     * get 请求测试
     * @param username
     * @return
     */
    @GetMapping("/testGet")
    public String test(String username){
        return username;
    }

    /**
     * post 请求测试
     * @param userVO
     * @return
     */
    @PostMapping("/testPost")
    public Object test2(@RequestBody UserVO userVO){
        return userVO;
    }

    /**
     * 最简单请求测试
     * @return
     */
    @GetMapping("/testPing")
    public String test3(){
        return "ping 通";
    }
}
