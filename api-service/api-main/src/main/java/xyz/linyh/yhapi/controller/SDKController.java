package xyz.linyh.yhapi.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import java.util.List;

/**
 * 对sdk进行上传和下载操作
 */
@Controller
@RequestMapping("/sdk")
@Slf4j
public class SDKController {

    /**
     * 上传file格式
     * {
     *     uid: 'uid',      // 文件唯一标识，建议设置为负数，防止和内部产生的 id 冲突
     *     name: 'xx.png',   // 文件名
     *     status: 'done', // 状态有：uploading done error removed
     *     response: '{"status": "success"}', // 服务端响应内容
     *     linkProps: '{"download": "image"}', // 下载链接额外的 HTML 属性
     *     xhr: 'XMLHttpRequest{ ... }', // XMLHttpRequest Header
     * }
     * @param file
     * @return
     */


    @Autowired
    private SdkfileService sdkfileService;
    @Autowired
    private UserService userService;

    private String SDK_DIR_PRE = "/usr/share/apiProject/";
    @PostMapping("/upload")
    @AuthCheck(mustRole="admin")
    @ResponseBody
    public BaseResponse<String> sdkUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        if(file==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件内容不能为空");
        }

        User user = userService.getLoginUser(request);
//        将文件保存到服务器中
        try {
            Boolean isSave = sdkfileService.saveSDK(file,user);
            return ResultUtils.success("保存成功");
        }catch(Exception e){
            log.info("文件保存出现错误");
            e.printStackTrace();
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,e.getMessage());
        }

    }

    /**
     * 下载对应名称的sdk
     * @param name
     * @param response
     * @return
     */
    @GetMapping("/install")
    public ResponseEntity<Resource> installSdk(@RequestParam("name") String name, HttpServletResponse response){
        Resource resource = sdkfileService.getSdkById(name);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

//        下载次数加1 锁不锁无所谓
        LambdaUpdateWrapper<Sdkfile> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Sdkfile::getName,name);
        wrapper.setSql("num = num+1");
        sdkfileService.update(wrapper);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * 获取最近5个版本的sdk
     * @return
     */
    @GetMapping("/get")
    @ResponseBody
    public BaseResponse<List<Sdkfile>> getAllSdk(){
        List<Sdkfile> list = sdkfileService.list(Wrappers.<Sdkfile>lambdaQuery().orderByDesc(Sdkfile::getUpdateTime));
        return ResultUtils.success(list);
    }

}
