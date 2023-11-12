package xyz.linyh.yapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

public class MyDigestUtils {
    public static final String SALT = "yhapiDigestSalt";

    public static String getDigest(String sk){
        Digester SHA256 = new Digester(DigestAlgorithm.SHA256);
        String str = SHA256.digestHex(SALT+ sk);
        return str;
    }
}
