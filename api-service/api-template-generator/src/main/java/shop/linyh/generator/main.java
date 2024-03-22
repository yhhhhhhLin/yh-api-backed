package shop.linyh.generator;

import shop.linyh.generator.entity.Meta;
import shop.linyh.generator.entity.ftl.DataModel;
import shop.linyh.generator.maker.entity.EntityMaker;
import shop.linyh.generator.meta.MetaManager;
import shop.linyh.generator.project.ProjectMarker;

import java.io.File;

public class main {
    public static void main(String[] args) throws Exception {
//        EntityMaker entityMaker = new EntityMaker();
//        创建对应的dataModel的entity
        String tempPath = "F:/AllIdeaProject/yh-api-backed/api-service/api-template-generator/template/6/yhapi-backed";
//        entityMaker.doGenerate("F:/AllIdeaProject/yh-api-backed/api-service/api-template-generator/template/6/yhapi-backed","F:/AllIdeaProject/yh-api-backed/api-service/api-template-generator/src/main/java/shop/linyh/generator/entity/ftl");
        ProjectMarker projectMarker = new ProjectMarker();
        Meta meta = MetaManager.getMeta(tempPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "meta.json");
//        判断不同的操作系统，重新设定输入地址和输出地址
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            meta.getFileConfig().setInputRootPath("/template/boot/yhapi-backed");
            meta.getFileConfig().setOutputRootPath("/generated/boot/#{projectName}");
        }
        DataModel dataModel = new DataModel();
        dataModel.setNeedPostExample(false);
        dataModel.setNeedUserExample(true);
        dataModel.setGroupName("shop.linyh");
        dataModel.setArtifactName("template");
        dataModel.setProjectName("backedTemplate");
        dataModel.setNeedCors(false);
        dataModel.setNeedBootWeb(false);
        dataModel.setNeedBootAop(false);
        dataModel.setNeedMybatisPlus(false);
        dataModel.setNeedMybatis(false);
        dataModel.setNeedMysql(false);
        dataModel.setNeedRedis(false);
        dataModel.setNeedGson(false);
        dataModel.setNeedFastJson(false);
        dataModel.setNeedHutool(false);
        dataModel.setNeedKnife4j(false);
        dataModel.setNeedLombok(false);

        projectMarker.genProject(meta,dataModel);
    }
}
