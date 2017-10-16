package com.sunfusheng.utils;

@SuppressWarnings("unused")
public class CodecUtil {

    private static final char HEX_DIGITS[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String toHexString(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder(bytes.length * 2);
        for (byte perByte : bytes) {
            hexStringBuilder.append(HEX_DIGITS[(perByte & 0xf0) >>> 4]);
            hexStringBuilder.append(HEX_DIGITS[perByte & 0x0f]);
        }
        return hexStringBuilder.toString();
    }

    public static String toUnicode(String string) {
        StringBuilder unicodeStringBuilder = new StringBuilder(string.length() * 2);

        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if ((int) ch > 128) {
                unicodeStringBuilder.append("\\u").append(Integer.toHexString((int) ch));
            } else {
                unicodeStringBuilder.append(ch);
            }
        }

        return unicodeStringBuilder.toString();
    }
}
