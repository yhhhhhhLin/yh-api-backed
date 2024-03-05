package xyz.linyh.yhapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.interfaceinfo.dto.DebugParamsDto;
import xyz.linyh.yhapi.service.InterfaceDebugService;

/**
 * 用户接口在线调试
 * @author lin
 */
@RestController
@RequestMapping("/debug")
@Slf4j
public class InterfaceDebugController {

    @Autowired
    private InterfaceDebugService interfaceDebugService;

    /**
     * 执行某一个接口调试
     * @param dto 数据
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<String> debug(@RequestBody DebugParamsDto dto) {
        if (dto == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        log.info("debug:{}", dto);
        String result = interfaceDebugService.invokeDebug(dto.getGetRequestParams(), dto.getHeaderRequestParams(), dto.getPreUrl(), dto.getSuffUrl(), dto.getMethod(), dto.getPostValue());

        return ResultUtils.success(result);
    }


}
