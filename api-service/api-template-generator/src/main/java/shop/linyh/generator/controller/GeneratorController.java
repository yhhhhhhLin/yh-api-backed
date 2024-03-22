package shop.linyh.generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.linyh.generator.entity.ftl.DataModel;
import shop.linyh.generator.service.GeneratorService;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;

@RestController
public class GeneratorController {

    @Autowired
    private GeneratorService generatorService;

    /**
     * 根据传入参数创建对应模板文件，然后返回zip包
     * @param dataModel
     * @return
     */
    @PostMapping("/generator")
    public BaseResponse generator(@RequestBody DataModel dataModel){

        generatorService.generate(dataModel);

        return ResultUtils.success("生成成功");
    }
}
