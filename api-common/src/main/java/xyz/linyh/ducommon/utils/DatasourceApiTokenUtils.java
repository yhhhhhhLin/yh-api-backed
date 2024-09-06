package xyz.linyh.ducommon.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * @author linzz
 */
public class DatasourceApiTokenUtils {

    private static final String SECRET = "HUIWHUJGOIOSJOI1JIOFSDIO";
//    private static final long EXPIRATION_TIME = 86400000;

    // 生成JWT
    public static String generateToken(String id) {
        Date now = new Date();
//        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return JWT.create()
                .withSubject(id)
                .withIssuedAt(now)
//                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC256(SECRET));
    }

    // 解析JWT
    public static DecodedJWT parseToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        return verifier.verify(token);
    }

    // 验证JWT
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            // 处理验证失败的异常，例如过期或篡改等
            return false;
        }
    }

}
