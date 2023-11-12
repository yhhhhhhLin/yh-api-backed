package xyz.linyh.yhapi.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
* @author lin
* @description 针对表【sdkfile(sdk版本管理表)】的数据库操作Service实现
* @createDate 2023-10-03 13:51:46
*/
@Service
@Transactional
public class SdkfileServiceImpl extends ServiceImpl<SdkfileMapper, Sdkfile>
    implements SdkfileService{


    /**
     * 将sdk保存到服务器中和数据库中
     *
     * @param file
     * @param user
     * @return
     */
    @Override
    public Boolean saveSDK(MultipartFile file, User user) throws IOException {
        if(file==null || user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"file和user不能为空");
        }
//        1. 保存到服务器中
        byte[] bytes = file.getBytes();
//            获取用户传递过来的文件名称
        String originalFilename = file.getOriginalFilename();
        File fileName = FileUtil.file(SdkFileConstant.SDK_DIR_PRE + originalFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.write(bytes);

//        1. 保存到数据库中
        Sdkfile sdkfile = new Sdkfile();
        sdkfile.setUserId(user.getId());
        sdkfile.setName(originalFilename);
        sdkfile.setNum(0);
        sdkfile.setStatus(1);
        sdkfile.setDescription("无");

//        查看数据库中是否有已经存在的对应文件
        Sdkfile one = this.getOne(Wrappers.<Sdkfile>lambdaQuery().eq(Sdkfile::getName, originalFilename));
        if(one==null){
            this.save(sdkfile);
        }else{
//            更新将原先的数据都恢复默认
            sdkfile.setId(one.getId());
            this.updateById(sdkfile);
        }

        return true;
    }

    /**
     * 根据文件名获取sdk
     *
     * @param name
     * @return
     */
    @Override
    public Resource getSdkById(String name) {
            if(name==null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"filename不能为空");
            }

            Sdkfile one = this.getOne(Wrappers.<Sdkfile>lambdaQuery().eq(Sdkfile::getName, name));
            if(one==null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"找不到这个文件名的sdk");
            }

            Path filePath = Paths.get(SdkFileConstant.SDK_DIR_PRE).resolve(one.getName());
            Resource resource = new PathResource(filePath);
            return resource;
    }
}




