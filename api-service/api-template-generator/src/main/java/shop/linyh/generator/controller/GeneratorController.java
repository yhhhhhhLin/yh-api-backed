package shop.linyh.generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.linyh.generator.entity.ftl.DataModel;
import shop.linyh.generator.service.GeneratorService;
import shop.linyh.generator.utils.UserUtils;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

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
    public ResponseEntity<byte[]> generator(@RequestBody DataModel dataModel, HttpServletRequest request){
        Long userId = UserUtils.getLoginUserId(request);
        System.out.println(dataModel);
        String dirPath = generatorService.generate(dataModel,userId);
//        获取文件夹里面的内容，然后转为zip返回给前端
        File file = new File(dirPath);
        System.out.println(file.getName());
        return null;

    }
}
