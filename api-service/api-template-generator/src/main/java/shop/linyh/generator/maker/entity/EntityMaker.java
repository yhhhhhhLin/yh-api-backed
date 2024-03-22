package shop.linyh.generator.maker.entity;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import shop.linyh.generator.entity.Meta;
import shop.linyh.generator.meta.MetaManager;

import java.io.File;
import java.io.FileWriter;

/**
 * 用来生成meta里面modal对应的实体类
 */
@Slf4j
public class EntityMaker {

    public void doGenerate(String tempPath,String outputPath) throws Exception{
        String metaPath = tempPath+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"meta.json";
//        获取meta单例对象
        Meta meta = MetaManager.getMeta(metaPath);

//        获取dataModal的ftl文件
        String entityFtlPath = tempPath+File.separator+"ftl"+File.separator+"DataModel.java.ftl";
        File ftlParentFile = new File(entityFtlPath);

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);


//        加载模板文件
        configuration.setDirectoryForTemplateLoading(ftlParentFile.getParentFile());
        configuration.setDefaultEncoding("UTF-8");

//        读出模板文件
        Template template = configuration.getTemplate(new File(entityFtlPath).getName());

        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }


        try (FileWriter out = new FileWriter(outputPath+File.separator+"DataModel.java")) {
            template.process(meta, out);
        } catch (TemplateException e) {
            log.info("模板文件异常,{}", e.getMessage(), e);
        }
//        生成对应的实体类到指定目录
    }
}
