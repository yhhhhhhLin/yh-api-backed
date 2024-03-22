package shop.linyh.generator.project.file;


import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class DynamicFileGenerator {

    /**
     * @param inputPath  模板文件的路径
     * @param outputPath 用户制作完成后想要输出的路径
     * @param model      需要动态传入的参数
     */
    public static void doGenerate(String inputPath, String outputPath, Object model) throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        File parentFile = new File(inputPath).getParentFile();

//        加载模板文件
        configuration.setDirectoryForTemplateLoading(parentFile);
        configuration.setDefaultEncoding("UTF-8");

//        读出模板文件
        Template template = configuration.getTemplate(new File(inputPath).getName());

        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }


        try (FileWriter out = new FileWriter(outputPath)) {
            template.process(model, out);
        } catch (TemplateException e) {
            log.info("模板文件异常,{}", e.getMessage(), e);
        }

    }


}
