package com.sunfusheng.base.net.ServiceInfo;

import com.sunfusheng.base.Constants;
import com.sunfusheng.utils.CryptoUtil;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;

public class UserCenterServiceInfo extends ServiceInfo {

    public static final String BASE_URL = "http://api.btime.com/";

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public Map<String, String> getCommonParameters() {
        Map<String, String> mapParams = ServiceInfo.getBaseCommonParameters();
        mapParams.put("u_time", String.valueOf(System.currentTimeMillis() / 1000L));
        return mapParams;
    }

    @Override
    public Map<String, String> getExtraParameters(Request request) {
        if (request == null) {
            return null;
        }

        TreeMap<String, String> paramsMap = new TreeMap<>();
        if (request.body() != null && request.body() instanceof FormBody) {
            FormBody formBody = (FormBody) request.body();
            for (int i = 0; i < formBody.size(); i++) {
                paramsMap.put(URLDecoder.decode(formBody.encodedName(i)), URLDecoder.decode(formBody.encodedValue(i)));
            }
        }
        if (request.url() != null) {
            for (int i = 0; i < request.url().querySize(); i++) {
                paramsMap.put(request.url().queryParameterName(i), request.url().queryParameterValue(i));
            }
        }

        String u_salt = CryptoUtil.generateSalt();
        paramsMap.put("u_salt", u_salt);

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
            stringBuilder.append("&");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("&");
        stringBuilder.append(Constants.APP_SECRET);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("u_sign", CryptoUtil.MD5Hash(stringBuilder.toString()) + u_salt);
        resultMap.put("u_salt", u_salt);
        return resultMap;
    }

    @Override
    public Interceptor getExtraInterceptor() {
        return null;
    }
}
