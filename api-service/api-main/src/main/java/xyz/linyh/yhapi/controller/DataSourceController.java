package xyz.linyh.yhapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.model.datasource.dtos.AddOrUpdateDscInfoDto;
import xyz.linyh.model.datasource.dtos.ListDscInfoDto;
import xyz.linyh.yhapi.service.DscInfoService;

@RestController
@RequestMapping("/dsc")
public class DataSourceController {

    @Autowired
    private DscInfoService dscInfoService;

    @PostMapping
    public BaseResponse addDsc(@RequestBody AddOrUpdateDscInfoDto dto) {

        return null;
    }

    @DeleteMapping("/{id}")
    public BaseResponse deleteDsc(@PathVariable Long id) {
        return null;
    }

    @GetMapping
    public BaseResponse getDsc() {
        return null;
    }

    @PutMapping
    public BaseResponse updateDsc(@RequestBody AddOrUpdateDscInfoDto dto) {
        return null;
    }

    @GetMapping("/list")
    public BaseResponse listDsc(@RequestBody ListDscInfoDto dto) {
        return null;
    }
}
