package xyz.linyh.yhapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import xyz.linyh.model.sdkfile.entitys.Sdkfile;
import xyz.linyh.model.user.entitys.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lin
 * @description 针对表【sdkfile(sdk版本管理表)】的数据库操作Service
 * @createDate 2023-10-03 13:51:46
 */
public interface SdkfileService extends IService<Sdkfile> {

    /**
     * 将sdk保存到服务器中和数据库中
     *
     * @param file
     * @param user
     * @return
     */
    Boolean saveSDK(MultipartFile file, User user) throws IOException;

    /**
     * 根据文件名获取sdk
     *
     * @param name
     * @return
     */
    Resource getSdkById(String name);

    /**
     * 生成一个随机的名字，将文件保存到磁盘中，然后将路径保存到数据库中
     *
     * @param inputStream
     * @param originalFilename
     * @return
     */
    Boolean saveFile(InputStream inputStream, String originalFilename, Long userId, String description);

    /**
     * 根据文件名字去数据库里面查询对应文件保存磁盘文章，返回输出流
     *
     * @param name
     * @param id
     * @return
     */
    File installSdk(String name, Long id);
}
