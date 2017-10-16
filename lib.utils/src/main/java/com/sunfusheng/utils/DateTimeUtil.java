package com.sunfusheng.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unused")
public class DateTimeUtil {
    public static final SimpleDateFormat DateFormat_yMdHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat DateFormat_MdHm = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    public static boolean isJavaTimeStamp(long time) {
        return time > 10000000000L;
    }

    public static boolean isJavaTimeStamp(String timeString) {
        return isJavaTimeStamp(NumberUtil.longValueOf(timeString));
    }

    public static long convertToJavaTimeStamp(long time) {
        if (isJavaTimeStamp(time)) {
            return time;
        }

        return time * 1000;
    }

    public static long convertToJavaTimeStamp(String timeString) {
        return convertToJavaTimeStamp(NumberUtil.longValueOf(timeString));
    }

    public static long parseTime(String timeString, SimpleDateFormat dateFormat) {
        long timestamp = 0;
        if (TextUtils.isEmpty(timeString))
            return timestamp;
        try {
            Date date = dateFormat.parse(timeString);
            timestamp = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static String formatTime(long time, SimpleDateFormat format) {
        if (format == null) {
            format = DateFormat_yMdHms;
        }

        return format.format(new Date(convertToJavaTimeStamp(time)));
    }

    public static String formatTime(String timeString, SimpleDateFormat format) {
        return formatTime(TextUtils.isEmpty(timeString) ? System.currentTimeMillis() : NumberUtil.longValueOf(timeString), format);
    }

    public static String formatTime(long time, long currentTime) {
        if (time == 0 || currentTime == 0) {
            return "";
        }

        time = convertToJavaTimeStamp(time);
        currentTime = convertToJavaTimeStamp(currentTime);

        if (currentTime >= time) {
            // 如果时间差 <= 0秒，则修正为1秒钟前
            int deltaSeconds = Math.max(1, (int) ((currentTime - time) / 1000));

            if (deltaSeconds <= 60) {
                return "刚刚";
            }
            // 规则2: 小于1小时的，显示xx分钟前
            if (deltaSeconds < 3600 && deltaSeconds > 60) {
                return (int) Math.max(1, Math.floor(deltaSeconds / 60)) + "分钟前";
            }
            // 规则3: 大于1小时的，显示xx小时前
            if (deltaSeconds < 86400) {
                return (int) Math.floor(deltaSeconds / 3600) + "小时前";
            }
            // 规则4: 大于1天的，显示xx天前
            if (deltaSeconds > 86400 && deltaSeconds < 3 * 86400) {
                return (int) Math.floor(deltaSeconds / 86400) + "天前";
            }
            // 规则4: 大于3天的，显示x月x日
            String template = "MM月dd日";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template, Locale.getDefault());
            return simpleDateFormat.format(time);
        } else {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(time);
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTimeInMillis(currentTime);

            String format = "M月dd日HH:mm";
            if (startCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)
                    && startCalendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH)) {
                int delta = startCalendar.get(Calendar.DAY_OF_YEAR) - nowCalendar.get(Calendar.DAY_OF_YEAR);
                if (delta == 0) {
                    format = "今天" + "HH:mm";
                } else if (delta == 1) {
                    format = "明天" + "HH:mm";
                }
            }

            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            return df.format(time);
        }
    }

    public static String formatTime(long time) {
        return formatTime(time, System.currentTimeMillis());
    }

    public static String formatTime(String timeString) {
        return formatTime(NumberUtil.longValueOf(timeString), System.currentTimeMillis());
    }

    public static String formatSeconds(int seconds) {
        int hour = seconds / (60 * 60);
        seconds %= 60 * 60;
        int minute = seconds / 60;
        seconds %= 60;
        int second = seconds;

        if (hour > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minute, second);
        }
    }
}
