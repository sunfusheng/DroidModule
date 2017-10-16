package com.sunfusheng.base.net;

import android.support.annotation.IntDef;

import com.sunfusheng.base.Constants;
import com.sunfusheng.base.net.ServiceInfo.CommentServiceInfo;
import com.sunfusheng.base.net.ServiceInfo.GeneralServiceInfo;
import com.sunfusheng.base.net.ServiceInfo.H5SearchServiceInfo;
import com.sunfusheng.base.net.ServiceInfo.ServiceInfo;
import com.sunfusheng.base.net.ServiceInfo.UserCenterServiceInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sunfusheng on 2017/5/22.
 */
public class Api {

    @IntDef({ServiceType.GENERAL, ServiceType.COMMENT, ServiceType.H5_SEARCH, ServiceType.USER_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceType {
        int GENERAL = 1;
        int COMMENT = 2;
        int H5_SEARCH = 3;
        int USER_CENTER = 4;
    }

    private static final int CONNECT_TIMEOUT_SECONDS = 10;
    private static final int READ_TIMEOUT_SECONDS = 10;

    private static final HashMap<Integer, ServiceInfo> serviceInfoMap = new HashMap<>();
    private static final HashMap<Integer, HashMap<Class, Object>> serviceMap = new HashMap<>();
    private static final Object lock = new Object();

    static {
        registerServiceInfo(ServiceType.GENERAL, new GeneralServiceInfo());
        registerServiceInfo(ServiceType.COMMENT, new CommentServiceInfo());
        registerServiceInfo(ServiceType.H5_SEARCH, new H5SearchServiceInfo());
        registerServiceInfo(ServiceType.USER_CENTER, new UserCenterServiceInfo());
    }

    private static void registerServiceInfo(@ServiceType int serviceType, ServiceInfo serviceInfo) {
        synchronized (lock) {
            serviceInfoMap.put(serviceType, serviceInfo);
        }
    }

    private static ServiceInfo getServiceInfo(@ServiceType int serviceType) {
        synchronized (lock) {
            return serviceInfoMap.get(serviceType);
        }
    }

    public static ApiService getService() {
        return getService(ApiService.class);
    }

    public static <T> T getService(Class<T> apiClass) {
        return getService(ServiceType.GENERAL, apiClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(@ServiceType int type, Class<T> apiClass) {
        synchronized (lock) {
            putIfAbsent(serviceMap, type, new HashMap<>());
            putIfAbsent(serviceMap.get(type), apiClass, Api.createService(type, apiClass));
            return (T) serviceMap.get(type).get(apiClass);
        }
    }

    // 有些手机系统Map里没有这个方法（另复制一份）
    private static <K, V> void putIfAbsent(Map<K, V> map, K key, V value) {
        V v = map.get(key);
        if (v == null) {
            map.put(key, value);
        }
    }

    private static <T> T createService(@ServiceType int type, Class<T> apiClass) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptorChain ->
                proceedRequest(interceptorChain, chain -> {
                    HttpUrl.Builder builder = chain.request().url().newBuilder();
                    Map<String, String> params = getServiceInfo(type).getCommonParameters();
                    if (params != null) {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            builder.addQueryParameter(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder.build();
                }));

        clientBuilder.addInterceptor(interceptorChain ->
                proceedRequest(interceptorChain, chain -> {
                    HttpUrl.Builder builder = chain.request().url().newBuilder();
                    Map<String, String> extraParams = getServiceInfo(type).getExtraParameters(chain.request());
                    if (extraParams != null) {
                        for (Map.Entry<String, String> entry : extraParams.entrySet()) {
                            builder.addQueryParameter(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder.build();
                }));

        clientBuilder.cookieJar(BTimeCookieJar.getInstance());
        clientBuilder.connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        clientBuilder.readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        Interceptor extraInterceptor = getServiceInfo(type).getExtraInterceptor();
        if (extraInterceptor != null) {
            clientBuilder.addInterceptor(extraInterceptor);
        }

        if (Constants.isDebug) {
            clientBuilder.addInterceptor(new LogInterceptor());
        }

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(clientBuilder.build())
                .baseUrl(getServiceInfo(type).getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        return retrofitBuilder.build().create(apiClass);
    }

    private static Response proceedRequest(Interceptor.Chain chain, Function<Interceptor.Chain, HttpUrl> function) {
        try {
            HttpUrl url = function.apply(chain);
            Request request = chain.request().newBuilder().url(url).build();
            return chain.proceed(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
