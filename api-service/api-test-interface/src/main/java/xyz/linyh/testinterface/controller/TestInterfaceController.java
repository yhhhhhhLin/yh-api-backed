package xyz.linyh.testinterface.controller;

import cn.hutool.core.date.DateUtil;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.model.testinterface.entitys.ThisUser;
import xyz.linyh.testinterface.utils.MyDigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class TestInterfaceController {

//    没有任何校验接收
//    @GetMapping("/g")
//    public String testGet(@RequestParam String name){
//        String result = "Get "+name;
//        System.out.println(result);
//        return result;
//    }

    //    包含认证签名过程
    @GetMapping("/g")
    public String testGet(@RequestParam String name,
                          HttpServletRequest request, HttpServletResponse response) {
        String result = "Get " + name;

//        gateway统一认证了
//        String sign = request.getHeader("sign");
//        String timeS = request.getHeader("timeS");
//        String accessKey = request.getHeader("accessKey");
//        if(StrUtil.hasBlank(sign,timeS,accessKey)){
//            throw new RuntimeException("对应请求头数据不能为空");
//        }
//
//        try {
////            api签名认证
//            signAuth(sign,timeS,accessKey,name);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        System.out.println(result);

        return result;
    }


    @PostMapping("/p")
    public String testPost(@RequestBody ThisUser thisUser) {
        if (thisUser == null) {
            return "null";
        }
        String result = "POST " + thisUser.getName();
        System.out.println(result);
        return "名字:" + thisUser.getName() + ",年龄:" + thisUser.getAge();
    }
//
//    @GetMapping("/")
//    public String test(@RequestParam String name){
//        String result = "Get "+name;
//        System.out.println(result);
//        return result
//    }

    Boolean signAuth(String sign, String timeS, String ak, String body) {
        //        认证签名
//        从根据accessKey从数据库获取secretKet
        String secretKey = "testsk";
//        通过加密算法加密生成然后和sign比较
//        认证生成签名
        if (!sign.equals(MyDigestUtils.getDigest(secretKey, body))) {
            throw new RuntimeException("签名认证不通过");
        }

//        判断时间是否超出 获取5分钟后的时间戳
        Long nowTime = DateUtil.date().toTimestamp().getTime();
        Long time = Long.valueOf(timeS);
        if ((nowTime - time) > 300000) {
            throw new RuntimeException("超出时间");
        }
        return true;
    }


}
