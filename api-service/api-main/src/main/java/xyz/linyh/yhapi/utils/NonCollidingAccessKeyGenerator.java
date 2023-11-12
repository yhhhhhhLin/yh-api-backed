package xyz.linyh.yhapi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class NonCollidingAccessKeyGenerator {
    public static Map generAkAndSk()throws Exception{
        // 生成一个随机的盐值
        byte[] salt = generateSalt();

        // 使用盐值和当前时间戳生成Access Key
        String accessKey = generateAccessKey(salt, System.currentTimeMillis());

        // 使用盐值和当前时间戳生成Secret Key
        String secretKey = generateSecretKey(salt, System.currentTimeMillis());
        HashMap<String, String> map = new HashMap<>();
        // 打印生成的AK和SK
        map.put("accessKey",accessKey);
        map.put("secretKey",secretKey);
        return map;
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    private static String generateAccessKey(byte[] salt, long timestamp) throws NoSuchAlgorithmException {
        // 使用盐值和时间戳生成哈希值
        String input = bytesToHex(salt) + Long.toString(timestamp);
        return hashString(input);
    }

    private static String generateSecretKey(byte[] salt, long timestamp) throws NoSuchAlgorithmException {
        // 使用不同的输入数据生成另一个哈希值
        String input = Long.toString(timestamp) + bytesToHex(salt);
        return hashString(input);
    }

    private static String hashString(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(input.getBytes());

        // 转换为十六进制字符串
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
