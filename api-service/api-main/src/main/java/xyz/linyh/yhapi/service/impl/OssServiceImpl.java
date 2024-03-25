package xyz.linyh.yhapi.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.linyh.yhapi.config.OssConfig;
import xyz.linyh.yhapi.service.OssService;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service("ossService")
public class OssServiceImpl implements OssService {

    @Override
    public String uploadImage(MultipartFile file,Long userId,String type) {
        String endpoint = OssConfig.END_POINT;
        String accessKeyId = OssConfig.ACCESS_KEY_ID;
        String accessKeySecret = OssConfig.ACCESS_KEY_SECRET;
        String bucketName = OssConfig.BUCKET_NAME;
        try {
            //创建oss实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //获取上传文件的输入流
            String filename = file.getOriginalFilename();
            String ossPath = "oss/"+type+"/"+userId+filename;
            InputStream fileInputStream = file.getInputStream();
            //获取原文件名
            if (StrUtil.isBlank(filename)) {
                return null;
            }
            /*
             * 调用OSS实现文件的上传
             * 第一个参数:Bucket名称
             * 第二个参数:上传到oss的指定路径和文件名称, oss/image/xx.png
             * 第三个参数:上传文件输入流
             */
            ossClient.putObject(bucketName, ossPath, fileInputStream);
            //关闭OSSClient
            ossClient.shutdown();
            //返回上传到OSS的文件的URL，这里使用自定义域名，因此自己手动拼接路径
            return ossPath;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return null;
        }
    }


    @Override
    public InputStream getImage(String filePath) {
        String endpoint = OssConfig.END_POINT;
        String accessKeyId = OssConfig.ACCESS_KEY_ID;
        String accessKeySecret = OssConfig.ACCESS_KEY_SECRET;
        String bucketName = OssConfig.BUCKET_NAME;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        OSSObject ossObject = ossClient.getObject(bucketName, filePath);
        return ossObject.getObjectContent();
    }

    @Override
    public String delImage(String filePath) {
        String endpoint = OssConfig.END_POINT;
        String accessKeyId = OssConfig.ACCESS_KEY_ID;
        String accessKeySecret = OssConfig.ACCESS_KEY_SECRET;
        String bucketName = OssConfig.BUCKET_NAME;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName, filePath);
        return null;
    }
}