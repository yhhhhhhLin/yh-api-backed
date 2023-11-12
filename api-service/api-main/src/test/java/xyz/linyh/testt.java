package xyz.linyh;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.linyh.yhapi.MyApplication;

@SpringBootTest(classes = MyApplication.class)
public class testt {

    @Test
    public String testt1(){

        HttpResponse execute = HttpRequest.get("http://139.9.65.13:8000/api/submit/?problem_id=4").execute();
        return "1234";
    }
}
