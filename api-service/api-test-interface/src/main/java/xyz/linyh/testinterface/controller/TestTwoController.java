package xyz.linyh.testinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestTwoController {

    @RequestMapping(value = "/get/get")
    public String test2(){
        return "ok";
    }

    @RequestMapping(value = "/interface/get/get")
    public String test5(){
        return "ok";
    }

    @RequestMapping
    public String test3(){
        return "not ok";
    }

    @PostMapping("/testPost")
    public String ping(){
        return "post ping成功";
    }

    @GetMapping("/testGet")
    public String ping2(){
        return "get ping成功";
    }

}
