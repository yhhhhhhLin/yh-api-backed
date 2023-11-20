package xyz.linyh.yhapi.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.SdkFileConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.sdkfile.entitys.Sdkfile;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.mapper.SdkfileMapper;
import xyz.linyh.yhapi.service.SdkfileService;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @author lin
 * @description 针对表【sdkfile(sdk版本管理表)】的数据库操作Service实现
 * @createDate 2023-10-03 13:51:46
 */
@Service
@Transactional
@Slf4j
public class SdkfileServiceImpl extends ServiceImpl<SdkfileMapper, Sdkfile>
        implements SdkfileService {


    /**
     * 将sdk保存到服务器中和数据库中
     *
     * @param file
     * @param user
     * @return
     */
    @Override
    public Boolean saveSDK(MultipartFile file, User user) throws IOException {
//        if(file==null || user==null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"file和user不能为空");
//        }
////        1. 保存到服务器中
//        byte[] bytes = file.getBytes();
////            获取用户传递过来的文件名称
//        String originalFilename = file.getOriginalFilename();
//        File fileName = FileUtil.file(SdkFileConstant.SDK_DIR_PRE + originalFilename);
//        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
//        fileOutputStream.write(bytes);
//
////        1. 保存到数据库中
//        Sdkfile sdkfile = new Sdkfile();
//        sdkfile.setUserId(user.getId());
//        sdkfile.setName(originalFilename);
//        sdkfile.setNum(0);
//        sdkfile.setStatus(1);
//        sdkfile.setDescription("无");
//
////        查看数据库中是否有已经存在的对应文件
//        Sdkfile one = this.getOne(Wrappers.<Sdkfile>lambdaQuery().eq(Sdkfile::getName, originalFilename));
//        if(one==null){
//            this.save(sdkfile);
//        }else{
////            更新将原先的数据都恢复默认
//            sdkfile.setId(one.getId());
//            this.updateById(sdkfile);
//        }
//
//        return true;
        return null;
    }

    /**
     * 根据文件名获取sdk
     *
     * @param name
     * @return
     */
    @Override
    public Resource getSdkById(String name) {
        if (name == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "filename不能为空");
        }

        Sdkfile one = this.getOne(Wrappers.<Sdkfile>lambdaQuery().eq(Sdkfile::getName, name));
        if (one == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "找不到这个文件名的sdk");
        }

        Path filePath = Paths.get(getFilPrePath()).resolve(one.getName());
        Resource resource = new PathResource(filePath);
        return resource;
    }

    /**
     * 生成一个随机的名字，将文件保存到磁盘中，然后将路径保存到数据库中
     *
     * @param inputStream
     * @param originalFilename
     * @return
     */
    @Override
    @Transactional
    public Boolean saveFile(InputStream inputStream, String originalFilename, Long userId, String description) {
//        为文件生成一个随机名字,并保存到磁盘的对应目录下
        String saveFileName = IdUtil.randomUUID();
        String pathPre = getFilPrePath();
        if(originalFilename==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件名不能为空");
        }
//        获取文件名后缀
        String[] split = originalFilename.split("\\.");
        String suffix = split[ArrayUtil.length(split) - 1];

        File file = new File(pathPre + saveFileName+"."+suffix);
        log.info("保存的文件名字为：{}",file.getName());

//        防止生成的文件名字重复
        while (file.exists()) {
            saveFileName = IdUtil.randomUUID();
            file = new File(pathPre + saveFileName);
        }



//        将文件路径保存到数据库中
        Sdkfile sdkfile = new Sdkfile();
        sdkfile.setUserId(userId);
        sdkfile.setName(originalFilename);
        sdkfile.setFilePath(pathPre + file.getName());
        sdkfile.setDescription(description);
        sdkfile.setNum(0);
        sdkfile.setStatus(1);
        sdkfile.setCreateTime(new Date());
        sdkfile.setUpdateTime(new Date());
        this.save(sdkfile);

//        将文件保存到磁盘中
        try (FileOutputStream fileOutputStream = new FileOutputStream((file))) {
//            判断是否有对应文件夹，如果没有那么就直接创建
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

//            创建对应文件
            file.createNewFile();

            byte[] bytes = new byte[1023];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes,0,len);
            }

            fileOutputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存文件失败");
        }

        return true;
    }

    /**
     * 根据文件名字去数据库里面查询对应文件保存磁盘文章，返回输出流
     *
     * @param name
     * @param id
     * @return
     */
    @Override
    public File installSdk(String name, Long id) {
//        1. 查询数据获取文件保存到磁盘的位置
        Sdkfile sdkfile = this.getOne(Wrappers.<Sdkfile>lambdaQuery().eq(Sdkfile::getName, name));
        if(sdkfile==null && sdkfile.getFilePath()==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"找不到对应sdk文件");
        }

//        找到磁盘对应地址的文件
        String filePath = sdkfile.getFilePath();
//        判断对应位置是否有对应文件
        File file = new File(filePath);
        if(!file.exists()){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"磁盘中没有对应文件");
        }

//        对应文件下载次数加1
        sdkfile.setNum(sdkfile.getNum()+1);
        sdkfile.setUpdateTime(new Date());
        this.updateById(sdkfile);

        return file;



    }


    /**
     * 根据电脑系统获取文件保存的路径
     *
     * @return
     */
    private String getFilPrePath() {

        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().startsWith("win")) {
            return SdkFileConstant.SDK_WIN_DIR_PRE;
        } else {
            return SdkFileConstant.SDK_LINUX_DIR_PRE;
        }

    }
}




