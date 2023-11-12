package xyz.linyh.testinterface.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

public class MyDigestUtils {

    public static String getDigest(String sk,String body){
        Digester SHA256 = new Digester(DigestAlgorithm.SHA256);
        String str = SHA256.digestHex(body + sk);
        return str;
    }
}
