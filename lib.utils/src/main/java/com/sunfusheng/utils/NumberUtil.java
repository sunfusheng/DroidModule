package com.sunfusheng.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class NumberUtil {
    public static long parseLong(String str) {
        return parseLong(str, 10);
    }

    public static long parseLong(String str, int radix) {
        try {
            return Long.parseLong(str, radix);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static int parseInt(String str) {
        return parseInt(str, 10);
    }

    public static int parseInt(String str, int radix) {
        try {
            return Integer.parseInt(str, radix);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long longValueOf(String str) {
        return longValueOf(str, 10);
    }

    public static long longValueOf(String str, int radix) {
        try {
            return Long.valueOf(str, radix);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int intValueOf(String str) {
        return intValueOf(str, 10);
    }

    public static int intValueOf(String str, int radix) {
        try {
            return Integer.valueOf(str, radix);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String stringify(int number) {
        if (number < 9999) {
            return String.valueOf(number);
        } else if (number < 9999999) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            return df.format((float) number / 10000) + "万";
        } else {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            return df.format((float) number / 1000000) + "百万";
        }
    }

    public static String formatNumber(String number) {
        if (number == null) {
            return "0";
        }
        return formatNumber(parseInt(number));
    }

    public static String formatNumber(Integer number) {
        if (number == null || number < 0) {
            return "0";
        } else if (number >= 0 && number < 10000) {
            return Integer.toString(number);
        } else {
            int temp = (int) (number / 10000.0 * 10);
            return (temp % 10) == 0 ? temp / 10 + "万" : temp / 10.0 + "万";
        }
    }
}
