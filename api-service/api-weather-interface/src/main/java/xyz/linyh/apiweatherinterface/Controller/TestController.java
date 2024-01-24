package xyz.linyh.apiweatherinterface.Controller;

import org.springframework.web.bind.annotation.*;
import xyz.linyh.model.user.vo.UserVO;

@RestController
public class TestController {

    @GetMapping("/testGet")
    public String test(String username){
        return username;
    }

    @PostMapping("/testPost")
    public Object test2(@RequestBody UserVO userVO){
        return userVO;
    }

    @GetMapping("/testPing")
    public String test3(){
        return "ping 通";
    }
}
