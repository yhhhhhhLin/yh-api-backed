package xyz.linyh.testinterface.controller;

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
}
