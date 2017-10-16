package com.sunfusheng.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PackageUtil {
    private static PackageManager packageManager;
    private static PackageInfo packageInfo;

    private static PackageManager getPackageManager() {
        if (packageManager == null) {
            packageManager = ApplicationContextHolder.getContext().getPackageManager();
        }

        return packageManager;
    }

    public static String getVersionName() {
        try {
            if (packageInfo == null) {
                packageInfo = getPackageManager().getPackageInfo(ApplicationContextHolder.getContext().getPackageName(), 0);
            }

            return packageInfo.versionName;
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode() {
        try {
            if (packageInfo == null) {
                packageInfo = getPackageManager().getPackageInfo(ApplicationContextHolder.getContext().getPackageName(), 0);
            }

            return packageInfo.versionCode;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<String> enumAppInstalled() {
        List<String> list = new ArrayList<>();

        try {
            List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo info = packageInfos.get(i);
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    list.add(info.packageName);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean isApkPackageOfCurrentApp(String apkPath) {
        try {
            String currentPackageName = ApplicationContextHolder.getContext().getPackageName();
            String apkPackageName = getPackageNameOfApk(apkPath);
            if (apkPackageName != null) {
                return apkPackageName.equals(currentPackageName);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getPackageNameOfApk(String apkPath) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
