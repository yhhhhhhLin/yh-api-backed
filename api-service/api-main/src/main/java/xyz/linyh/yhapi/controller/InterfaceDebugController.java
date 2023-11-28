package xyz.linyh.yhapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.model.interfaceinfo.dto.DebugParamsDto;

/**
 * @author lin
 */
@RestController
@RequestMapping("/debug")
@Slf4j
public class InterfaceDebugController {

//    todo 传递参数错误
    @PostMapping("/invoke")
    public BaseResponse debug(@RequestBody DebugParamsDto dto){
        log.info("debug:{}",dto);
        return null;
    }


}
