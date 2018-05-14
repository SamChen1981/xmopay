package com.xmopay.common.utils;

import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.exceptions.ApiException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * RSA工具类
 */
public class SignUtils {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成RequestUrl地址用于 同步
     *
     * @param requestUrl
     * @param params
     * @return
     */
    public static String bulidRequestUrl(String requestUrl, Map<String, String> params) {
        if (requestUrl.indexOf("?") > -1) {
            requestUrl = requestUrl + "&" + getSignCheckContent(params, null) + "&sign=" + params.get("sign").toString();
        } else {
            requestUrl = requestUrl + "?" + getSignCheckContent(params, null) + "&sign=" + params.get("sign").toString();
        }
        return requestUrl;
    }

    /**
     * MD5/RSA 加签
     *
     * @param params
     * @param secretKey
     * @return
     * @throws ApiException
     */
    public static String signString(Map<String, String> params, String secretKey) throws ApiException {
        String signType = params.get("sign_type");
        if (signType.equals(XmoPayConstants.SIGN_METHOD_MD5)) {
            return md5Sign(params, secretKey, XmoPayConstants.CHARSET_UTF8);
        } else if (signType.equals(XmoPayConstants.SIGN_METHOD_RSA)) {
            return rsaSign(params, secretKey, XmoPayConstants.CHARSET_UTF8);
        } else {
            throw new ApiException("signString Error signType = " + signType);
        }
    }

    /**
     * MD5/RSA 验签
     *
     * @param params
     * @param secretKey
     * @return
     * @throws ApiException
     */
    public static boolean signCheck(Map<String, String> params, String secretKey) throws ApiException {
        String signType = params.get("sign_type");
        if (signType.equals(XmoPayConstants.SIGN_METHOD_MD5)) {
            return md5SignCheck(params, secretKey, XmoPayConstants.CHARSET_UTF8);
        } else if (signType.equals(XmoPayConstants.SIGN_METHOD_RSA)) {
            return rsaSignCheck(params, secretKey, XmoPayConstants.CHARSET_UTF8);
        } else {
            throw new ApiException("signCheck Error  signType = " + signType);
        }
    }


    /**
     * 验签字符串内容
     *
     * @param params
     * @return
     */
    public static String getSignCheckContent(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        params.remove("sign"); //除sign不参与加签，其他参数均参与(为空或null参数不参与)

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (XmoPayUtils.areNotEmpty(key, value)) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }
        }

        return content.toString();
    }

    /**
     * 验签字符串内容
     *
     * @param params
     * @param excludeKey
     * @return
     */
    public static String getSignCheckContent(Map params, List<String> excludeKey) {
        if (params == null) {
            return null;
        }

        StringBuffer content = new StringBuffer();
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i).toString();
            String value = params.get(key).toString();

            if (excludeKey != null
                    && excludeKey.contains(key)) {
                continue;
            }

            if (XmoPayUtils.areNotEmpty(key, value)) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }
        }

        return content.toString();
    }

    /**
     * MD5 加签
     *
     * @param params
     * @param secretKey
     * @param charset
     * @return
     * @throws ApiException
     */
    public static String md5Sign(Map<String, String> params, String secretKey, String charset) throws ApiException {
        String content = getSignCheckContent(params);
        try {
            return DigestUtils.md5Hex(content + "&key=" + secretKey).toUpperCase();
        } catch (Exception e) {
            throw new ApiException("MD5content = " + content + "; charset = " + charset, e);
        }
    }

    /**
     * MD5 验签
     *
     * @param params
     * @param secretKey
     * @param charset
     * @return
     * @throws ApiException
     */
    public static boolean md5SignCheck(Map<String, String> params, String secretKey, String charset) throws ApiException {
        String sign = params.get("sign");
        String content = getSignCheckContent(params);

        try {
            return sign.equals(md5Sign(params, secretKey, charset));
        } catch (Exception e) {
            throw new ApiException("MD5content = " + content + "; charset = " + charset, e);
        }
    }

    /**
     * sha256WithRsa 加签
     *
     * @param params
     * @param privateKey
     * @param charset
     * @return
     * @throws ApiException
     */
    public static String rsaSign(Map<String, String> params, String privateKey,
                                 String charset) throws ApiException {
        String content = getSignCheckContent(params);
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(XmoPayConstants.SIGN_METHOD_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));
            return rsaSign(content, priKey,
                    XmoPayConstants.SIGN_SHA256RSA_ALGORITHMS, charset);
        } catch (Exception e) {
            throw new ApiException("RSAcontent = " + content + "; charset = " + charset, e);
        }
    }

    /**
     * 加签
     *
     * @param content
     * @param privateKey
     * @param algorithm
     * @param charset
     * @return
     * @throws ApiException
     */
    public static String rsaSign(String content, PrivateKey privateKey, String algorithm,
                                 String charset) throws ApiException {
        try {
            Signature signature = Signature
                    .getInstance(algorithm);

            signature.initSign(privateKey);

            if (XmoPayUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
            throw new ApiException("RSAcontent = " + content + "; charset = " + charset, e);
        }

    }

    /**
     * sha256WithRsa 签名验证
     *
     * @param params
     * @param publicKey
     * @param charset
     * @return
     * @throws ApiException
     */
    public static boolean rsaSignCheck(Map<String, String> params, String publicKey,
                                       String charset) throws ApiException {
        String sign = params.get("sign");
        String content = getSignCheckContent(params);
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA",
                    new ByteArrayInputStream(publicKey.getBytes()));
            return rsaSignCheck(content, pubKey, sign,
                    XmoPayConstants.SIGN_SHA256RSA_ALGORITHMS, charset);
        } catch (Exception e) {
            throw new ApiException(
                    "RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }

    /**
     * RSA 签名验证
     *
     * @param content
     * @param publicKey
     * @param sign
     * @param algorithm
     * @param charset
     * @return
     * @throws ApiException
     */
    public static boolean rsaSignCheck(String content, PublicKey publicKey, String sign,
                                       String algorithm, String charset) throws ApiException {
        try {
            Signature signature = Signature
                    .getInstance(algorithm);

            signature.initVerify(publicKey);

            if (XmoPayUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new ApiException(
                    "RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }

    /**
     * AES密钥生成
     *
     * @return
     */
    public static String generatorAESKey() {
        String keyStr;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();
            keyStr = Base64.encodeBase64String(key.getEncoded());
            if (XmoPayUtils.isEmpty(keyStr)) {
                throw new NullPointerException("AES 密钥生成错误");
            }
            return keyStr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5密钥生成
     *
     * @param salt
     * @return
     */
    public static String generatorMD5Key(String salt) {
        return DigestUtils.md5Hex(salt + RandomStringUtils.randomAlphanumeric(6));
    }

    /**
     * RSA密钥对生成
     *
     * @return
     */
    public static Map<String, String> generateRSAKeyPair() {
        Map<String, String> keyPairMap = new HashMap<>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            keyPairMap.put("public_key", Base64.encodeBase64String(publicKey.getEncoded()));
            keyPairMap.put("private_key", Base64.encodeBase64String(privateKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPairMap;
    }

    /**
     * 公钥加密
     *
     * @param content   待加密内容
     * @param publicKey 公钥
     * @param charset   字符集，如UTF-8, GBK, GB2312
     * @return 密文内容
     * @throws ApiException
     */
    public static String rsaEncrypt(String content, String publicKey,
                                    String charset) throws ApiException {
        try {
            PublicKey pubKey = getPublicKeyFromX509(XmoPayConstants.SIGN_METHOD_RSA,
                    new ByteArrayInputStream(publicKey.getBytes()));
            Cipher cipher = Cipher.getInstance(XmoPayConstants.SIGN_METHOD_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] data = XmoPayUtils.isEmpty(charset) ? content.getBytes()
                    : content.getBytes(charset);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
            out.close();

            return XmoPayUtils.isEmpty(charset) ? new String(encryptedData)
                    : new String(encryptedData, charset);
        } catch (Exception e) {
            throw new ApiException("EncryptContent = " + content + ",charset = " + charset,
                    e);
        }
    }

    /**
     * 私钥解密
     *
     * @param content    待解密内容
     * @param privateKey 私钥
     * @param charset    字符集，如UTF-8, GBK, GB2312
     * @return 明文内容
     * @throws ApiException
     */
    public static String rsaDecrypt(String content, String privateKey,
                                    String charset) throws ApiException {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(XmoPayConstants.SIGN_METHOD_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));
            Cipher cipher = Cipher.getInstance(XmoPayConstants.SIGN_METHOD_RSA);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedData = XmoPayUtils.isEmpty(charset)
                    ? Base64.decodeBase64(content.getBytes())
                    : Base64.decodeBase64(content.getBytes(charset));
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();

            return XmoPayUtils.isEmpty(charset) ? new String(decryptedData)
                    : new String(decryptedData, charset);
        } catch (Exception e) {
            throw new ApiException("EncodeContent = " + content + ",charset = " + charset, e);
        }
    }

    public static PublicKey getPublicKeyFromX509(String algorithm,
                                                 InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString().getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    /**
     * 获取私钥
     *
     * @param algorithm
     * @param ins
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    InputStream ins) throws Exception {
        if (ins == null || XmoPayUtils.isEmpty(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = StreamUtil.readText(ins).getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

}
