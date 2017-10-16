package com.sunfusheng.news.preference;

import com.sunfusheng.base.preference.BaseSharedPreferences;
import com.sunfusheng.utils.ApplicationContextHolder;

/**
 * Created by sunfusheng on 2017/5/25.
 */
public class NewsSharedPreferences extends BaseSharedPreferences {

    private static final int VERSION = 1;
    private static final String PREF_NEWS_MODULE = "pref_news_module";
    private static NewsSharedPreferences mInstance = new NewsSharedPreferences();

    public static NewsSharedPreferences getInstance() {
        return mInstance;
    }

    private NewsSharedPreferences() {
        super(ApplicationContextHolder.getContext(), PREF_NEWS_MODULE, VERSION);
    }

    public void setLastRefreshTime(String key, boolean isLoadMore, Long value) {
        put("refreshTime_" + key + (isLoadMore ? "loadMore" : "load"), value);
    }

    public Long getLastRefreshTime(String key, boolean isLoadMore) {
        return getLong("refreshTime_" + key + (isLoadMore ? "loadMore" : "load"), 0L);
    }
}
