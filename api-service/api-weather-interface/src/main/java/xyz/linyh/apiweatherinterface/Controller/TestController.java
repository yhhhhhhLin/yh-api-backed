package xyz.linyh.apiweatherinterface.Controller;

import org.springframework.web.bind.annotation.*;
import xyz.linyh.model.user.vo.UserVO;

@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping("/test2")
    public Object test2(@RequestBody UserVO userVO){
        return userVO;
    }

    @GetMapping("/test3")
    public String test3(String userName){
        return userName;
    }
}
