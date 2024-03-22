package shop.linyh.generator.project;

import cn.hutool.core.util.StrUtil;
import shop.linyh.generator.entity.Meta;
import shop.linyh.generator.entity.ftl.DataModel;
import shop.linyh.generator.project.file.DynamicFileGenerator;
import shop.linyh.generator.project.file.StaticFileGenerator;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 用来将ftl文件转为java文件
 */
public class ProjectMarker {

    public static void genProject(Meta meta, DataModel dataModel) throws Exception {
        String resourcePath = meta.getFileConfig().getInputRootPath();
        String fileOutputRootPath = StrUtil.replace(meta.getFileConfig().getOutputRootPath(), "#{projectName}", String.format("%s", dataModel.getProjectName()));
//        将meta所有文件生成出来
        List<Meta.FileConfig.Files> files = meta.getFileConfig().getFiles();
        System.out.println(files);
        for (Meta.FileConfig.Files file : files) {
//            判断里面是那种生成方式
//            将file里面output路径给改了
            String groupFormat = String.format("%s", dataModel.getGroupName().replace(".", File.separator));
            String artifactFormat = String.format("%s", dataModel.getArtifactName());
            String outPathBack = StrUtil.replace(StrUtil.replace(file.getOutputPath(), "#{basePackage}", groupFormat), "#{baseArtifact}", artifactFormat);
            String fileInputPath = resourcePath + File.separator + file.getInputPath();
            String fileOutputPath = fileOutputRootPath + File.separator + outPathBack;
//                通过反射获取dataModel中的对应属性，判断是否需要生成
            String fileCondition = file.getCondition();
            boolean needFile = false;
            if(StrUtil.isBlank(fileCondition)){
                needFile = true;
            }else{
                Field field = dataModel.getClass().getDeclaredField(fileCondition);
                field.setAccessible(true);
                needFile= (boolean) field.get(dataModel);
            }
            if (file.getGenerateType().equals("dynamic")) {
//                使用动态生成
//                判断文件是否需要被生成
                if (needFile) {
                    DynamicFileGenerator.doGenerate(fileInputPath, fileOutputPath, dataModel);
                }
            } else if (file.getGenerateType().equals("static")) {
//                使用静态生成
                if (needFile) {
                    StaticFileGenerator.doGenerate(fileInputPath, fileOutputPath);
                }
            }
        }
    }

}
