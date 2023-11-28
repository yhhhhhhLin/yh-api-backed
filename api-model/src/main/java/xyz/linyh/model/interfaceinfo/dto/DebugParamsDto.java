package xyz.linyh.model.interfaceinfo.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DebugParamsDto {

    /**
     * url前缀
     */
    private String preUrl;
    /**
     * url后缀
     */
    private String  suffUrl;
    /**
     * 请求方式
     */
    private String method;
    /**
     * 请求get参数 todo 封成一个类，转递数组  [
  {
    "id": 1701158991692,
    "requestParmK": "",
    "requestParmV": "",
    "getKey": "iuyvb",
    "getValue": "loiuhh"
  }
]
     */
    private Map<String,String> getRequestParams;
    /**
     * 请求头参数
     */
    private Map<String,String> headerRequestParams;
    /**
     * 请求体参数
     */
    private String  postValue;
}
