package com.sunfusheng.base.net.ServiceInfo;

import android.os.Build;

import com.sunfusheng.utils.DeviceUtil;
import com.sunfusheng.utils.NetworkUtil;
import com.sunfusheng.utils.PackageUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by sunfusheng on 2017/5/22.
 */
abstract public class ServiceInfo {

    abstract public String getBaseUrl();

    abstract public Map<String, String> getCommonParameters();

    abstract public Map<String, String> getExtraParameters(Request request);

    abstract public Interceptor getExtraInterceptor();

    static Map<String, String> getBaseCommonParameters() {
        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("ver", String.valueOf(PackageUtil.getVersionCode()));
        mapParams.put("os", Build.DISPLAY);
        mapParams.put("os_ver", String.valueOf(Build.VERSION.SDK_INT));
        mapParams.put("os_type", "Android");
        mapParams.put("carrier", NetworkUtil.getCarrierName());
        mapParams.put("token", DeviceUtil.getDeviceToken());
        mapParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        mapParams.put("src", "lx_android");
        mapParams.put("net", NetworkUtil.getNetworkTypeName());
//        mapParams.put("sid", Session.getSid());
//        IUserConfigService service = Router.getService("settings", "config", IUserConfigService.class);
//        mapParams.put("browse_mode", service == null ? "2" : String.valueOf(service.getDownloadPicSetting()));
//        String push_id = BTPushManager.getClientid(ApplicationStatus.getApplicationContext());
//        mapParams.put("push_id", TextUtils.isEmpty(push_id) ? "" : push_id);
        return mapParams;
    }

}
