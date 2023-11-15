import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;
import xyz.linyh.ducommon.utils.JwtUtils;


public class TestJwtToken {

    @Test
    public void test() {
        String token = JwtUtils.generateToken("123");
        System.out.println(token);
        DecodedJWT decodedJWT = JwtUtils.parseToken(token);
        System.out.println(decodedJWT.getSubject());
    }
}
