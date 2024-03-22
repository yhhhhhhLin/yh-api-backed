package shop.linyh.generator.project.file;

import cn.hutool.core.io.FileUtil;

// 静态文件生成器
public class StaticFileGenerator {

    /**
     * 生成静态文件
     *
     * @param inputPath  静态文件原始位置
     * @param outputPath 要生成后的路径
     */
    public static void doGenerate(String inputPath, String outputPath) {
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }
        FileUtil.copy(inputPath, outputPath, true);
    }

}
