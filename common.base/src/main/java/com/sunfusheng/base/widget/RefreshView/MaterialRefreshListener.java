package com.sunfusheng.base.widget.RefreshView;

public abstract class MaterialRefreshListener {

    public void onFinish() {
    }

    public abstract void onRefresh(MaterialRefreshLayout materialRefreshLayout);

    public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
    }
}
