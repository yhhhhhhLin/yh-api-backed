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

    @Override
    public void generate(DataModel dataModel) {
//        获取当前操作系统
        String osName = System.getProperty("os.name");
        String tempPath = "F:/AllIdeaProject/yh-api-backed/api-service/api-template-generator/template/6/yhapi-backed";
        if (!osName.toLowerCase().startsWith("win")) {
            tempPath = "/template/boot/yhapi-backed";
        }

        Meta meta = MetaManager.getMeta(tempPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "meta.json");
        if (!osName.toLowerCase().startsWith("win")) {
            meta.getFileConfig().setInputRootPath("/template/boot/yhapi-backed");
            meta.getFileConfig().setOutputRootPath("/generated/boot/#{projectName}");
        }

        try {
            ProjectMarker.genProject(meta, dataModel);
            log.info("生成文件成功");
        } catch (Exception e) {
            log.error("生成文件失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成文件失败");
        }

    }
}
