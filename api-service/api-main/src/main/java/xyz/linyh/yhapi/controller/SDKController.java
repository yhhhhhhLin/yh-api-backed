package xyz.linyh.yhapi.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.sdkfile.entitys.Sdkfile;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.annotation.AuthCheck;
import xyz.linyh.yhapi.service.SdkfileService;
import xyz.linyh.yhapi.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 对sdk进行上传和下载操作
 */
@RestController
@RequestMapping("/sdk")
@Slf4j
public class SDKController {


    @Autowired
    private SdkfileService sdkfileService;
    @Autowired
    private UserService userService;


    //TODO 保存文件到磁盘，然后保存路径到数据库
    @PostMapping("/upload")
    @AuthCheck(mustRole = "admin")
    public BaseResponse handleFileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "description", required = false) String description) {

//            获取用户的id
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        String fileType = file.getContentType();
        log.info("文件类型为:{}", fileType);
//            获取文件初始信息
        String originalFilename = file.getOriginalFilename();
        log.info("文件原先名字为{}", originalFilename);

//            获取原件输入流
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取文件输入流失败");
        }

//            给文件生成一个不会重复的文件名，保存到磁盘中
        Boolean save = sdkfileService.saveFile(inputStream, originalFilename, loginUser.getId(), description);

        System.out.println("文件保存成功");

        // 保存文件并获取保存的路径
        return ResultUtils.success("文件上传成功");
    }

    /**
     * 下载对应名称的sdk TODO 下载有问题，后面改为将sdk文件上传到maven仓库，不用jar包
     *
     * @param name
     * @param response
     * @return
     */

    @GetMapping("/install")
    public BaseResponse<byte[]> installSdk(@RequestParam("name") String name, HttpServletRequest request, HttpServletResponse response) {
        // 根据文件名称去查询数据库，获取对应文件保存到磁盘中的地址
        User loginUser = userService.getLoginUser(request);
        File file = sdkfileService.installSdk(name, loginUser.getId());

        // 获取文件输出流
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);

            // 设置 Content-Disposition 头
            response.addHeader("Content-Disposition", "attachment;filename=" + name);

            // 设置 Content-Type 头
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResultUtils.success(bytes);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取文件输出流失败");
        }
    }

    /**
     * 获取最近10个版本的sdk
     *
     * @return
     */
    @GetMapping("/get")
    @ResponseBody
    public BaseResponse<List<Sdkfile>> getAllSdk() {
        List<Sdkfile> list = sdkfileService.list(Wrappers.<Sdkfile>lambdaQuery().orderByDesc(Sdkfile::getUpdateTime));
        if (list != null && list.size() > 10) {
            list = list.subList(0, 10);
        }
        return ResultUtils.success(list);
    }

}
