package shop.linyh.generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.linyh.generator.entity.ftl.DataModel;
import shop.linyh.generator.service.GeneratorService;
import shop.linyh.generator.utils.UserUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class GeneratorController {

    @Autowired
    private GeneratorService generatorService;

    /**
     * 根据传入参数创建对应模板文件，然后返回zip包
     *
     * @param dataModel
     * @return
     */
    @PostMapping("/generator")
    public void generator(@RequestBody DataModel dataModel, HttpServletRequest request, HttpServletResponse response) {
        Long userId = UserUtils.getLoginUserId(request);
        System.out.println(dataModel);
        String dirPath = generatorService.generate(dataModel, userId);
//        获取文件夹里面的内容，然后转为zip返回给前端
        File file = new File(dirPath);
        String zipFileName = file.getName() + ".zip";
        Path foldToZipPath = Paths.get(file.getParent());
        // 创建一个字节流来保存ZIP数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            // 遍历文件夹添加文件到ZIP
            Files.walk(foldToZipPath)
                    .filter(path -> !Files.isDirectory(path)) // 过滤掉目录，只添加文件
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(foldToZipPath.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            System.out.println(e);

        }
        // 设置响应头
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);

        // 响应字节流并关闭资源
        byte[] zipBytes = byteArrayOutputStream.toByteArray();

        try {
            // 获取输出流
            ServletOutputStream outputStream = response.getOutputStream();
            // 写入数据
            outputStream.write(zipBytes);
            // 刷新并关闭流
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
