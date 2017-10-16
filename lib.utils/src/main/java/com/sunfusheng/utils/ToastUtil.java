package com.sunfusheng.utils;

import android.content.Context;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;

@SuppressWarnings("unused")
public class ToastUtil {

    private Toast toast = null;

    private static class ToastHelperHandler {
        public static final ToastUtil instance = new ToastUtil();
    }

    protected ToastUtil() {
    }

    public static void toast(int resourceId) {
        toast(resourceId, Toast.LENGTH_SHORT);
    }

    public static void toast(int resourceId, int duration) {
        Context context = ApplicationContextHolder.getContext();
        if (context == null) {
            return;
        }

        String message = context.getString(resourceId);
        toast(message, duration);
    }

    public static void toast(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    public static void toast(String message, int duration) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> ToastHelperHandler.instance.showToast(message, duration));
    }

    private void showToast(String message, int duration) {
        if (message == null) {
            return;
        }

        Context context = ApplicationContextHolder.getContext();
        if (context == null) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, message, duration);
            toast.show();
        } else {
            toast.setText(message);
            toast.setDuration(duration);
            toast.show();
        }
    }
}