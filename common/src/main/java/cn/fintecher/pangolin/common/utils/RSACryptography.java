package cn.fintecher.pangolin.common.utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * RSA加密工具类
 * Created by ChenChang on 2017/8/24.
 */
public class RSACryptography {


    public static void main(String[] args) throws Exception {
//        String data = "account=13720588720&amount=10000&outTedid=111122223333111144&";
//        String data = "account=59b77f9bf5129a9e5d54ff32&orderSerialNumber=6313343537140125696&totalFee=300&random=688108&paymentPassword=123456";
        String data = "a=59b77f9bf5129a9e5d54ff32&o=6313343537140125696&t=300&d=688108&p=123456";

        String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPvMrILlr9UJoqndDTJ4we qVOMD2Fu/DjIz+jsiK/RxrUzidZgu7bJWZRIdOxQP0W/JhNFycaQjPPfT01G IQvVMFT0cGveDOQujA/Cmv0AkUop+5zlKh7vKj38UOaQrIRP0a7nqz7mtaHF hsSlR8b7EcMuZY90YAGPATIJhWTupQIDAQAB";
        String privateKeyStr = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI+8ysguWv1Q miqd0NMnjB6pU4wPYW78OMjP6OyIr9HGtTOJ1mC7tslZlEh07FA/Rb8mE0XJ xpCM899PTUYhC9UwVPRwa94M5C6MD8Ka/QCRSin7nOUqHu8qPfxQ5pCshE/R ruerPua1ocWGxKVHxvsRwy5lj3RgAY8BMgmFZO6lAgMBAAECgYBWk94E878E w+8IamSlOzXwJgfX3k/O3QeLFYh0KOTWU31DnOvO+mJgJ5/kgODyeRoWx+Vl 7OK4oN4oDQk7i54nDs64XuwJtAsxjbrfwCTzl7ac/dRunX38CPUAlWi01Iph zXGm1ptnhwR+9g9bgj7dgxNWzFTczKGFVxYDS1QuhQJBAPMHDYlVZrpQJpZR 3dQcXWkFyZ/TIqd0qIdMKzac2TNkgXGbFlEr3hBx8PLF029bJTC+AFEB6Hg7 Ey1EGXOj1kMCQQCXaPNCV/svFfuD9s93dnH7mk5dr34AizpukGZ9veizN2+J v3HPucw1LP1zP/3Pgh0IuRxC5dPaw5xpYG4XqLz3AkBAy1OXlvZhnDb9guwy vjFFa+6atw8ZOGgqUmt/Dwh1xEkiweeMUrbNCaPlBktd+B4NRcsNzgVt8Mon Gm3yv+7DAkAtzv7qL5w7Xrc98cpLJnbN3J2hEptYpscC9IVNvctNjZQdZOKW KmxyGi6EU/QCeCc4oGjAhLBYG56S8y3b0p15AkEAn/zABws9+EoJpiJFpPp1 qN3P5Fu7jPGdvD+IJYM8QfE/YzrOFqJtpvOfQ6PLAFhaTl2CIcLGOoCeCDXt AneqAQ==";
//        KeyPair keyPair=genKeyPair(1024);
//        String content="ZecKoyK75aOqMsEny2xrXLLiD7yFoVJSMrwVUTNS6aWc2bQSadyKLFy9QuGl2UFGTftAvAet7sVaZ5VKK9DjXFzixajdveQW7bQ7gGkfli7qtsR8NTkPR83SrXlbsM1+x80s2lAQCDkUTBmavom7WDhaM39Afmn8TSrviDaG03hPL4m4zbShygPMvuOm1L8RDn28cZnnhZnCwns0sV/kS9CV06wnB7fDOpLMjjJnfE1XMGqWJ+uLyQk8ZuxYVerR6C2+yz6rpesXbYYBtPjsUdGMJ8MOvzoD4yqZg0DNY8wirQe7rczl5D5/AAlwSwwRYfMnbdpcOiRTLHM5ThdKKw==";
        //获取公钥，并以base64格式打印出来

        PublicKey publicKey = getPublicKey(publicKeyStr);
        System.out.println("公钥：" + new String(Base64.encode(publicKey.getEncoded())));

        //获取私钥，并以base64格式打印出来
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        System.out.println("私钥：" + new String(Base64.encode(privateKey.getEncoded())));

        //公钥加密
        byte[] encryptedBytes = encrypt(data.getBytes(), publicKey);
        String str = Base64.encode(encryptedBytes);
        System.out.println("加密后：" + str);

        //私钥解密
        byte[] decryptedBytes = decrypt(Base64.decode(str), privateKey);
        System.out.println("解密后：" + new String(decryptedBytes));
        Map<String, String> map = URLUtils.getUrlParams(new String(decryptedBytes));
        for (String key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }
    }

    //将base64编码后的公钥字符串转成PublicKey实例
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    //将base64编码后的私钥字符串转成PrivateKey实例
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


    //生成密钥对
    public static KeyPair genKeyPair(int keyLength) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.generateKeyPair();
    }

    //公钥加密
    public static byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    //私钥解密
    public static byte[] decrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

}