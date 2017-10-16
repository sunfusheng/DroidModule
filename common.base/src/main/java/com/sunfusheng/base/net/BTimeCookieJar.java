package com.sunfusheng.base.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class BTimeCookieJar implements CookieJar {

    private final Map<String, ConcurrentHashMap<String, Cookie>> memCookies;

    private static BTimeCookieJar ourInstance = new BTimeCookieJar();

    public static BTimeCookieJar getInstance() {
        return ourInstance;
    }

    private BTimeCookieJar() {
        memCookies = new HashMap<>();
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            String hostKey = url.host();
            ConcurrentHashMap<String, Cookie> mapCookie;
            if (memCookies.containsKey(hostKey)) {
                mapCookie = memCookies.get(hostKey);
            } else {
                mapCookie = new ConcurrentHashMap<>();
                memCookies.put(hostKey, mapCookie);
            }

            for (Cookie cookie : cookies) {
                String cookieKey = cookie.name();
                mapCookie.put(cookieKey, cookie);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = new ArrayList<>();
        String hostKey = url.host();
        if (memCookies.containsKey(hostKey)) {
            ConcurrentHashMap<String, Cookie> mapCookie = memCookies.get(hostKey);
            for (Map.Entry<String, Cookie> entry : mapCookie.entrySet()) {
                Cookie cookie = entry.getValue();
                cookies.add(cookie);
            }
        }
        return cookies;
    }
}
