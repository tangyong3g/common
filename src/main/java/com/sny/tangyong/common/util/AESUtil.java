package com.sny.tangyong.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 *
 * 加密工具类
 *
 *  涵盖加密和解密方法
 *
 *
 */
public class AESUtil {

    public static String AES_KEY = "AES7654321!#@tcl";
    private static String IV = "1234567812345678";
    private static String ALGORITHM = "AES/CBC/NoPadding";
    public static String AES_DECRYPT_KEY = "cqgf971sp394@!#0";


    /**
     * 加密方法
     *
     * @param dataBytes
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt2(byte[] dataBytes, String key) throws Exception {
        if (dataBytes == null || dataBytes.length == 0) return null;

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            int blockSize = cipher.getBlockSize();

            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            byte[] keybytes = key.getBytes("utf-8");
            SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("utf-8"));

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return encrypted;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 揭秘方法
     *
     * @param encrypted
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt2(byte[] encrypted, String key) throws Exception {
        if (encrypted == null || encrypted.length == 0) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            byte[] keybytes = key.getBytes("utf-8");
            SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("utf-8"));

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted);
            return original;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
