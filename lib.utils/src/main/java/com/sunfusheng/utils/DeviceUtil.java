package com.sunfusheng.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class DeviceUtil {

    public static String getDeviceSerial() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    public static String getDeviceToken() {
        try {
            Context context = ApplicationContextHolder.getContext();

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String imei = tm.getDeviceId();
            String androidId = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
            String serialNo = getDeviceSerial();

            return CryptoUtil.MD5Hash(imei + androidId + serialNo);
        } catch (Throwable e) {
            e.printStackTrace();
            return CryptoUtil.MD5Hash("Unknown Device");
        }
    }
}
