package com.xmopay.common.utils;

import com.xmopay.common.exceptions.ApiException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public class AESUtils {

    private static final String AES_ALG = "AES";

    /**
     * AES算法
     */
    private static final String AES_CBC_PCK_ALG = "AES/CBC/PKCS5Padding";

//    private static final byte[] AES_IV = initIv(AES_CBC_PCK_ALG);
    private static final byte[] AES_IV = "0000000000000000".getBytes(); //固定向量，请勿改动

    /**
     * 加密
     *
     * @param content
     * @param encryptType
     * @param encryptKey
     * @param charset
     * @return
     */
    public static String encryptContent(String content, String encryptType, String encryptKey, String charset) throws ApiException {
        if (AES_ALG.equals(encryptType)) {
            return aesEncrypt(content, encryptKey, charset);
        } else {
            throw new ApiException("当前不支持该算法类型：encrypeType=" + encryptType);
        }
    }

    /**
     * 解密
     *
     * @param content
     * @param encryptType
     * @param encryptKey
     * @param charset
     * @return
     */
    public static String decryptContent(String content, String encryptType, String encryptKey, String charset) throws ApiException {
        if (AES_ALG.equals(encryptType)) {
            return aesDecrypt(content, encryptKey, charset);
        } else {
            throw new ApiException("当前不支持该算法类型：encrypeType=" + encryptType);
        }
    }

    /**
     * AES加密
     *
     * @param content
     * @param aesKey
     * @param charset
     * @return
     */
    private static String aesEncrypt(String content, String aesKey, String charset) throws ApiException {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PCK_ALG);

            IvParameterSpec iv = new IvParameterSpec(AES_IV);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(aesKey.getBytes()), AES_ALG), iv);
            byte[] encryptBytes = cipher.doFinal(content.getBytes(charset));
            return new String(Base64.encodeBase64(encryptBytes));
        } catch (Exception e) {
            throw new ApiException("AES加密失败：Aescontent = " + content + "; charset = "+ charset, e);
        }
    }

    /**
     * AES解密
     *
     * @param content
     * @param key
     * @param charset
     * @return
     */
    private static String aesDecrypt(String content, String key, String charset) throws ApiException {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PCK_ALG);
            IvParameterSpec iv = new IvParameterSpec(AES_IV);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key.getBytes()), AES_ALG), iv);

            byte[] cleanBytes = cipher.doFinal(Base64.decodeBase64(content.getBytes()));
            return new String(cleanBytes, charset);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("AES解密失败：Aescontent = " + content + "; charset = " + charset, e);
        }
    }

    /**
     * 初始向量的方法, 全部为0. 这里的写法适合于其它算法,针对AES算法的话,IV值一定是128位的(16字节).
     *
     * @param fullAlg
     * @return
     * @throws GeneralSecurityException
     */
    private static byte[] initIv(String fullAlg) {
        try {
            Cipher cipher = Cipher.getInstance(fullAlg);
            int blockSize = cipher.getBlockSize();
            byte[] iv = new byte[blockSize];
            for (int i = 0; i < blockSize; ++i) {
                iv[i] = 0;
            }
            return iv;
        } catch (Exception e) {

            int blockSize = 16;
            byte[] iv = new byte[blockSize];
            for (int i = 0; i < blockSize; ++i) {
                iv[i] = 0;
            }
            return iv;
        }
    }
}
