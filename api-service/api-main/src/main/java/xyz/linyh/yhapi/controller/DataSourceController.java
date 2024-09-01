package xyz.linyh.yhapi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.UserConstant;
import xyz.linyh.model.base.dtos.IdDto;
import xyz.linyh.model.datasource.dtos.AddOrUpdateDscInfoDto;
import xyz.linyh.model.datasource.dtos.ListColumnsDto;
import xyz.linyh.model.datasource.dtos.ListDscInfoDto;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;
import xyz.linyh.model.datasource.vos.DscInfoVo;
import xyz.linyh.yhapi.service.DscInfoService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dsc")
public class DataSourceController {

    @Autowired
    private DscInfoService dscInfoService;

    @PostMapping
    public BaseResponse<Boolean> addOrUpdateDsc(@RequestBody AddOrUpdateDscInfoDto dto,
                                                HttpServletRequest request) {
//        TODO 参数校验
        Long userId = Long.parseLong(request.getHeader(UserConstant.USER_Id));
        dto.setUserId(userId);
        return dscInfoService.addOrUpdateDataSource(dto);
    }

    @PostMapping("/testConnect")
    public BaseResponse<Boolean> testDsc(@RequestBody AddOrUpdateDscInfoDto dto) {
//        TODO 校验参数
        return dscInfoService.testConnect(dto);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteDsc(@PathVariable Long id) {
        return dscInfoService.deleteDataSource(id);
    }

    @GetMapping
    public BaseResponse<DscInfoVo> getDsc(@RequestBody IdDto dto) {
        return dscInfoService.getDscInfoById(dto);
    }

    @GetMapping("/list")
    public BaseResponse<Page<DscInfoVo>> listDsc(@RequestBody ListDscInfoDto dto) {
        return dscInfoService.listPage(dto);
    }

    @GetMapping("/columns")
    public BaseResponse<List<ColumnBriefVO>> listColumns(@RequestBody ListColumnsDto dto) {
        List<ColumnBriefVO> columnBriefVO = dscInfoService.listColumns(dto);
        return ResultUtils.success(columnBriefVO);

    }
}
