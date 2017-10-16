package com.sunfusheng.utils;

import android.text.TextUtils;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UrlUtil {

    public static String AddOrModifyQueryByKey(String url, String key, String newValue) {
        try {
            URI uri;
            URL webUrl = new URL(url);
            String webHost = webUrl.getHost();
            String query = webUrl.getQuery();
            if (TextUtils.isEmpty(query)) {
                query = key + "=" + newValue;
                uri = new URI(webUrl.getProtocol(), webUrl.getUserInfo(), webHost, webUrl.getPort(), webUrl.getPath(), query, webUrl.getRef());
                return uri.toString();
            } else {
                String[] params = query.split("[&]");
                boolean flag = false;
                Map<String, String> paramsMap = new HashMap<>();
                for (String str : params) {
                    String[] split = str.split("[=]");
                    if (split.length == 2) {
                        if (split[0].equals(key)) {
                            flag = true;
                            paramsMap.put(split[0], newValue);
                        } else {
                            paramsMap.put(split[0], split[1]);
                        }
                    }
                }
                if (!flag) {
                    paramsMap.put(key, newValue);
                }
                StringBuilder buffer = new StringBuilder();
                Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    buffer.append(next.getKey());
                    buffer.append('=');
                    buffer.append(next.getValue());
                    buffer.append('&');
                }
                buffer.deleteCharAt(buffer.length() - 1);
                String newQuery = buffer.toString();
                return url.replace(query, newQuery);
            }
        } catch (Throwable e) {
            return url;
        }
    }
}
