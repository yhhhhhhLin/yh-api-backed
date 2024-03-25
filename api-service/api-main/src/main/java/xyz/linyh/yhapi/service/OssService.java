package xyz.linyh.yhapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface OssService {

    public String uploadImage(MultipartFile file,Long userId,String type);

    public InputStream getImage(String filePath);

    public String delImage(String filePath);
}
