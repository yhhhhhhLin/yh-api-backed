package xyz.linyh.apiweatherinterface.Controller;

import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.model.apiweatherInterface.entitys.JsonRootBean;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/weather")
public class WeatcherAnalyzeController {

    private String requestRUL = "http://t.weather.itboy.net/api/weather/city/";

    @GetMapping("/analyze")
    public String analyze(String code, HttpServletRequest request){
        request.getParameterMap();
//        获取天气
        String json = HttpUtil.get(requestRUL + code);
        JsonRootBean jsonRootBean  = JSONUtil.toBean(json, JsonRootBean.class);

        String returnMessage="";
//        判断请求是否成功
        if(jsonRootBean.getStatus()== HttpStatus.HTTP_OK){
            returnMessage+=jsonRootBean.getCityInfo().getCity()+",气温："+
                    jsonRootBean.getData().getForecast().get(0).getHigh()+","+jsonRootBean.getData().getForecast().get(0).getNotice();
        }else if(jsonRootBean.getStatus()==HttpStatus.HTTP_NOT_FOUND){
            returnMessage="找不到这个城市";
        }else{
            returnMessage = "请求错误";
        }

        System.out.println(jsonRootBean);
        return returnMessage;
    }
}
