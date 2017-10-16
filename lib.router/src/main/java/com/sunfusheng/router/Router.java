package com.sunfusheng.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.sunfusheng.router.bundle.ModuleManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class Router {

    public interface Initializer {
        void onInit(Context applicationContext);
    }

    public interface HttpSchemeDelegate {
        void onRequest(Context context, String url);
    }

    public static final int UNKNOWN_SCHEME = -1;
    public static final int BTIME_URL_SCHEME = 1;
    public static final int HTTP_URL_SCHEME = 2;
    public static final int HTTPS_URL_SCHEME = 3;

    private static Map<String, Object> sServiceMap = new HashMap<>();
    private static boolean isInitialized = false;
    private static HttpSchemeDelegate httpSchemeDelegate;

    static public void init(final Context applicationContext) {
        ModuleManager.getInstance().loadBundles(applicationContext);
        isInitialized = true;
    }

    static private void check() {
        if (!isInitialized) {
            throw new RuntimeException("Rounter Uninitialized!!");
        }
    }

    @SuppressWarnings("unchecked")
    static public <T> T getService(String moduleName, String itemName, Class<T> apiClass) {
        check();

        Object service = sServiceMap.get(moduleName + "_" + itemName);
        if (service == null) {
            String serviceClassName = ModuleManager.getInstance().getClass(moduleName, "service", itemName);
            if (serviceClassName == null) {
                return null;
            }
            try {
                Class clazz = Class.forName(serviceClassName);
                service = clazz.newInstance();
                sServiceMap.put(moduleName + "_" + itemName, service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!apiClass.isInstance(service)) {
            return null;
        }

        return (T) service;
    }

    static public Fragment createFragment(Context context, String moduleName, String itemName, Bundle args) {
        check();
        String fragmentClassName = ModuleManager.getInstance().getClass(moduleName, "fragment", itemName);
        if (fragmentClassName == null) {
            return null;
        }

        try {
            if (args == null) {
                return Fragment.instantiate(context, fragmentClassName);
            } else {
                ModuleManager.getInstance().checkRequiredArgs(moduleName, "fragment", itemName, args);
                return Fragment.instantiate(context, fragmentClassName, args);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    static public void startActivity(Context context, String moduleName, String itemName, Bundle args) {
        check();
        String activityClassName = ModuleManager.getInstance().getClass(moduleName, "activity", itemName);
        if (activityClassName == null) {
            return;
        }

        try {
            Intent intent = getIntent(new ComponentName(context, activityClassName), args);

            if (args != null) {
                ModuleManager.getInstance().checkRequiredArgs(moduleName, "activity", itemName, args);
                intent.putExtras(args);
            }

            context.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static public void invokeUrl(Context context, String openUrl) {
        invokeUrl(context, null, openUrl);
    }

    static public Observable<String> invokeUrl(Context context, Object handlerInstance, String openUrl) {
        check();

        if (context == null || TextUtils.isEmpty(openUrl)) {
            return Observable.just(null);
        }

        try {
            int scheme = getScheme(openUrl);

            if (scheme == BTIME_URL_SCHEME) {
                return ModuleManager.getInstance().invokeUrl(context, handlerInstance, openUrl);
            } else if (scheme == HTTP_URL_SCHEME || scheme == HTTPS_URL_SCHEME) {
                if (httpSchemeDelegate != null) {
                    httpSchemeDelegate.onRequest(context, openUrl);
                }
                return Observable.just(null);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return Observable.just(null);
    }

    static public void startActivityForResult(Activity activity, int requestCode, String moduleName, String itemName, Bundle args) {
        check();
        String activityClassName = ModuleManager.getInstance().getClass(moduleName, "activity", itemName);
        if (activityClassName == null) {
            return;
        }

        try {
            Intent intent = getIntent(new ComponentName(activity, activityClassName), args);
            activity.startActivityForResult(intent, requestCode);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private static Intent getIntent(ComponentName component, Bundle args) {
        Intent intent = new Intent();
        intent.setComponent(component);

        if (args != null) {
            intent.putExtras(args);
        }
        return intent;
    }

    public static int getScheme(String openUrl) {
        if (TextUtils.isEmpty(openUrl)) {
            return UNKNOWN_SCHEME;
        }

        Uri requestURI = Uri.parse(openUrl);
        String scheme = !TextUtils.isEmpty(requestURI.getScheme()) ? requestURI.getScheme().toLowerCase() : "";
        if ("btime".equals(scheme)) {
            return BTIME_URL_SCHEME;
        } else if ("http".equals(scheme)) {
            return HTTP_URL_SCHEME;
        } else if ("https".equals(scheme)) {
            return HTTPS_URL_SCHEME;
        } else {
            return UNKNOWN_SCHEME;
        }
    }

    public static void registerHttpSchemeDelegate(HttpSchemeDelegate delegate) {
        httpSchemeDelegate = delegate;
    }
}
