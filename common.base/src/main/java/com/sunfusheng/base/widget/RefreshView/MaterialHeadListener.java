package com.sunfusheng.base.widget.RefreshView;

public interface MaterialHeadListener {

    void onComplete(MaterialRefreshLayout materialRefreshLayout);

    void onBegin(MaterialRefreshLayout materialRefreshLayout);

    void onPull(MaterialRefreshLayout materialRefreshLayout, float fraction);

    void onRelease(MaterialRefreshLayout materialRefreshLayout, float fraction);

    void onRefreshing(MaterialRefreshLayout materialRefreshLayout);

    void onCanRelease(MaterialRefreshLayout materialRefreshLayout, boolean can);
}
