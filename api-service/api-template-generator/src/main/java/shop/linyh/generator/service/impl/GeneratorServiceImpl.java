package shop.linyh.generator.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.linyh.generator.entity.Meta;
import shop.linyh.generator.entity.ftl.DataModel;
import shop.linyh.generator.meta.MetaManager;
import shop.linyh.generator.project.ProjectMarker;
import shop.linyh.generator.service.GeneratorService;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.exception.BusinessException;

import java.io.File;

@Service
@Slf4j
public class GeneratorServiceImpl implements GeneratorService {

    String winProjectOutputPathPrefix = "D:/test";

    String linuxProjectOutputPathPrefix = "/generated/boot";

    @Override
    public String generate(DataModel dataModel, Long userId) {
//        获取当前操作系统
        String osName = System.getProperty("os.name");
        String tempPath = "F:/AllIdeaProject/yh-api-backed/api-service/api-template-generator/template/6/yhapi-backed";
        if (!osName.toLowerCase().startsWith("win")) {
            tempPath = "/template/boot/yhapi-backed";
        }

        Meta meta = MetaManager.getMeta(tempPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "meta.json");
        String outputPath = null;
        if (!osName.toLowerCase().startsWith("win")) {
            meta.getFileConfig().setInputRootPath("/template/boot/yhapi-backed");
            outputPath = linuxProjectOutputPathPrefix + File.separator + userId + File.separator + "#{projectName}";
        } else {
            outputPath = winProjectOutputPathPrefix + File.separator + userId + File.separator + "#{projectName}";
        }
        meta.getFileConfig().setOutputRootPath(outputPath);

        try {
            String fileOutputRootPath = ProjectMarker.genProject(meta, dataModel);
            log.info("生成文件成功");
//            返回保存后的文件地址
            return fileOutputRootPath;
        } catch (Exception e) {
            log.error("生成文件失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成文件失败");
        }

    }
}
