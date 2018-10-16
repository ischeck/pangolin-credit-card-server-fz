package cn.fintecher.pangolin.common.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    public static String ALGORITHM = "RSA";
    public static String CHAR_SET = "UTF-8";

    /**
     * RSA签名
     *
     * @param content       待签名数据
     * @param privateKey    商户私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String input_charset) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(input_charset));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * RSA验签名检查
     *
     * @param content        待签名数据
     * @param sign           签名值
     * @param ali_public_key 支付宝公钥
     * @param input_charset  编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String ali_public_key, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            byte[] encodedKey = Base64.decode(ali_public_key);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));

            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 公钥加密
     *
     * @return Base64之后的数据
     */
    public static String encryptToB64ByPublicKey(String content, String publicKey) {
        try {
            byte[] bytes = content.getBytes(CHAR_SET);
            byte[] secret = encryptByPublicKey(bytes, publicKey);
            return Base64.encode(secret);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 私钥加密
     *
     * @return Base64之后的数据
     */
    public static String encryptToB64ByPrivateKey(String content, String privateKey) {
        try {
            byte[] bytes = content.getBytes(CHAR_SET);
            byte[] secretText = encryptByPrivateKey(bytes, privateKey);
            return Base64.encode(secretText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 私钥解密
     */
    public static String decryptB64ByPrivateKey(String content, String privateKey) {
        try {
            byte[] secretText = Base64.decode(content);
            byte[] planText = decryptByPrivateKey(secretText, privateKey);
            return new String(planText, CHAR_SET);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥解密
     */
    public static String decryptB64ByPublicKey(String content, String publicKey) {
        try {
            byte[] secretText = Base64.decode(content);
            byte[] planText = decryptByPublicKey(secretText, publicKey);
            return new String(planText, CHAR_SET);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥加密
     */
    public static byte[] encryptByPublicKey(byte[] content, String publicKey) throws Exception {

        PublicKey pk = getPublicKey(publicKey);
        return encrypt(content, pk);
    }

    /**
     * 私钥加密
     */
    public static byte[] encryptByPrivateKey(byte[] content, String privateKey) throws Exception {

        PrivateKey pk = getPrivateKey(privateKey);
        return encrypt(content, pk);
    }

    /**
     * 私钥解密
     */
    public static byte[] decryptByPrivateKey(byte[] content, String privateKey) throws Exception {
        PrivateKey key = getPrivateKey(privateKey);
        return decrypt(content, key);
    }

    /**
     * 公钥解密
     */
    public static byte[] decryptByPublicKey(byte[] content, String publicKey) throws Exception {
        PublicKey key = getPublicKey(publicKey);
        return decrypt(content, key);
    }


    /**
     * 解密
     */
    protected static byte[] decrypt(byte[] content, Key key) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        InputStream ins = new ByteArrayInputStream(content);
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        //rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
        byte[] buf = new byte[128];
        int bufl;

        while ((bufl = ins.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(cipher.doFinal(block));
        }

        return writer.toByteArray();
    }

    /**
     * 加密
     */
    protected static byte[] encrypt(byte[] content, Key key) throws Exception {
        Cipher ch = Cipher.getInstance(ALGORITHM);
        ch.init(Cipher.ENCRYPT_MODE, key);

        InputStream ins = new ByteArrayInputStream(content);
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        //rsa加密在1024 bit密钥下面 长度为 1024/8 -11 的字节大小最多是117，按117位拆开进行加密
        byte[] buf = new byte[117];
        int bufl;

        while ((bufl = ins.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(ch.doFinal(block));
        }

        return writer.toByteArray();
    }


    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {

        byte[] keyBytes;

        keyBytes = Base64.decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }


    /**
     * 获取公钥对象
     *
     * @param publicKey 密钥字符串（经过base64编码秘钥字节)
     * @throws Exception
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {

        byte[] keyBytes;

        keyBytes = Base64.decode(publicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        PublicKey publickey = keyFactory.generatePublic(keySpec);

        return publickey;
    }

}
