package com.sny.tangyong.common.util;

import android.util.Base64;

/**
 * @author xinyizhang
 * @date 2015年5月21日 下午4:22:29
 */
public class Base64Utils {


    /**
     * 加密方法
     *
     * @param b
     * @return
     */
    public static String encodeBase64(byte[] b) {
        if (b == null) return null;
        //return new String((new Base64()).encode(b));
        return new String(Base64.encode(b, Base64.NO_PADDING));
    }

    /**
     * 解密方法
     *
     * @param encodeStr
     * @return
     */
    public static byte[] decodeBase64(String encodeStr) {
        if (encodeStr == null) {
            return null;
        }
        //Base64 base64 = new Base64();
        try {
            byte[] encodeByte = encodeStr.getBytes("utf-8");
            return Base64.decode(encodeByte, Base64.NO_PADDING);
        } catch (Exception e) {

        }
        return null;
    }
}
