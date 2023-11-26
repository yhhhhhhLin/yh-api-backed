import cn.hutool.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class myTest {

    @Test
    public void test1(){

        for(int i = 0;i<10000;i++){
            
        }
        HttpRequest httpRequest = HttpRequest.get("http://43.139.227.202/api/ticket-service/ticket/query?fromStation=BJP&toStation=HZH&departureDate=2023-11-26");
    }
}
