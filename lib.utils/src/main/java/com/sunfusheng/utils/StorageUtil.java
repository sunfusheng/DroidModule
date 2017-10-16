package com.sunfusheng.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

@SuppressWarnings("unused")
public class StorageUtil {

    public static boolean isMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getAppSdRootPath(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            packageName = ApplicationContextHolder.getContext().getPackageName();
        }

        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + packageName + File.separator;
    }
}
