package xyz.linyh.testinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interface")
public class TestThreeController {

    @GetMapping("/api/g")
    public String g2(String name){
        System.out.println(name);
        return name;
    }
}
