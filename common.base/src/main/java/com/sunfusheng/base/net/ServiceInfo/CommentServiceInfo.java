package com.sunfusheng.base.net.ServiceInfo;

import android.text.TextUtils;

import com.sunfusheng.utils.CryptoUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;

public class CommentServiceInfo extends ServiceInfo {

    private static final String BASE_URL = "http://gcs.so.com/";
    private static final String COMMENT_CLIENT_ID = "25";

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public Map<String, String> getCommonParameters() {
        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("client_id", COMMENT_CLIENT_ID);
        mapParams.put("src", "lx_android");
        return mapParams;
    }

    @Override
    public Map<String, String> getExtraParameters(Request request) {
        if ("/comment/post".equals(request.url().uri().getPath())) {
            String message = request.url().queryParameter("message");
            String url = request.url().queryParameter("url");
            String uid = request.url().queryParameter("uid");
            if (message == null || url == null || uid == null) {
                return null;
            }

            if (!TextUtils.isEmpty(message)) {
                message = message.trim();
            }

            String salt = "!@#salt#&*_";
            String client_id = "25";
            String sign = CryptoUtil.MD5Hash(url + "_" + uid + "_" + client_id + "_" + message + "_" + salt);
            int len = !TextUtils.isEmpty(sign) ? sign.length() : 0;
            int start = 8;
            int end = len - 8;
            sign = !TextUtils.isEmpty(sign) && end > start ? sign.substring(start, end) : null;

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("sign", sign);
            return resultMap;
        }

        return null;
    }

    @Override
    public Interceptor getExtraInterceptor() {
        return null;
    }
}
