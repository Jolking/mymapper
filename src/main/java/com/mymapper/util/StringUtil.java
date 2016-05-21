package com.mymapper.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huang on 4/1/16.
 */
public class StringUtil {
    public static final char UNDERLINE = '_';

    public static boolean isNotEmpty(String param) {
        return null != param && !"".equals(param.trim());
    }

    public static boolean isEmpty(String param) {
        return null == param || "".equals(param.trim());
    }

    public static boolean contains(String[] origin, String target) {
        if (origin == null || origin.length == 0)
            return false;

        List<String> strList = Arrays.asList(origin);
        return strList
            .stream()
            .filter(str -> str.equalsIgnoreCase(target))
            .count() > 0;
    }

    public static String underline2Camel(String str) {
        if (!isNotEmpty(str)) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String camel2Underline(String str) {
        if (!isNotEmpty(str)) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == '_') {
                i++;
                if (i < len) {
                    c = str.charAt(i);
                    sb.append(Character.toUpperCase(c));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static int countOfChar(String str, char c) {
        if (!isNotEmpty(str)) {
            return 0;
        }
        int len = str.length();
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(camel2Underline("user_id_asdasd"));
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] md5Byte(String encryptStr) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptStr.getBytes("UTF-8"));
        return md.digest();
    }
}
