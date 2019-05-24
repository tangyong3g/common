package com.sny.tangyong.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <br>
 * 类描述: <br>
 * 功能详细描述:生成文件的MD5码
 *
 * @author yuanzhibiao
 * @date [2013-1-22]
 */
public class MD5Util {
    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
//    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', //CHECKSTYLE IGNORE
//            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}; // CHECKSTYLE IGNORE

    protected static MessageDigest messagedigest = null; // CHECKSTYLE IGNORE

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            nsaex.printStackTrace();
        }
    }

    /**
     * 生成字符串的md5校验值
     *
     * @param s
     * @return
     */
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    /**
     * 判断字符串的md5校验码是否与一个已知的md5码相匹配
     *
     * @param password  要校验的字符串
     * @param md5PwdStr 已知的md5校验码
     * @return
     */
    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }

    /**
     * 生成文件的md5校验值
     *
     * @param file
     * @return
     * @throws
     */
    public static String getFileMD5String(File file) {
        try {
            InputStream fis;
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                messagedigest.update(buffer, 0, numRead);
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bufferToHex(messagedigest.digest());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        String c0 = hexDigits[(bt & 0xf0) >> 4]; // 取字节中高 4 位的数字转换, >>>
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        String c1 = hexDigits[bt & 0xf]; // 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }


    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal() {
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var2) {
                throw new IllegalStateException("no md5 algorythm found");
            }
        }
    };

    public MD5Util() {
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();

        for (int i = 0; i < b.length; ++i) {
            resultSb.append(byteToHexString(b[i], true));
        }

        return resultSb.toString();
    }

    public static String byteArrayToHexStringLittleEnding(byte[] b) {
        StringBuffer resultSb = new StringBuffer();

        for (int i = 0; i < b.length; ++i) {
            resultSb.append(byteToHexString(b[i], false));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b, boolean bigEnding) {
        int n = b;
        if (b < 0) {
            n = 256 + b;
        }

        int d1 = n / 16;
        int d2 = n % 16;
        return bigEnding ? hexDigits[d1] + hexDigits[d2] : hexDigits[d2] + hexDigits[d1];
    }

    public static String MD5Encode(String origin) {
        return MD5Encode(origin, (String) null);
    }

    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            throw new RuntimeException("Error Hex String length");
        } else {
            byte[] result = new byte[s.length() / 2];

            int bytepos;
            char c;
            char c2;
            for (int i = 0; i < s.length(); result[bytepos] = Integer.decode("0x" + c + c2).byteValue()) {
                bytepos = i / 2;
                c = s.charAt(i++);
                c2 = s.charAt(i++);
            }

            return result;
        }
    }

    public static String MD5Encode(String origin, String encoding) {
        String resultString = null;

        try {
            resultString = new String(origin);
            MessageDigest e = (MessageDigest) MD5.get();
            if (encoding == null) {
                resultString = byteArrayToHexString(e.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(e.digest(resultString.getBytes(encoding)));
            }

            return resultString;
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static MessageDigest getMd5Digest() {
        return (MessageDigest) MD5.get();
    }

    public static byte[] MD5Encode(byte[] origin) {
        try {
            MessageDigest e = (MessageDigest) MD5.get();
            return e.digest(origin);
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }
}

