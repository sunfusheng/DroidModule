package com.sunfusheng.infostream.RefreshStrategy;

import com.sunfusheng.infostream.DataSource.InfoStreamDataSource;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsRefreshStrategy {

    public boolean isPullRefreshEnabled() {
        return false;
    }

    public boolean isLoadMore() {
        return false;
    }

    public boolean isLoadMoreFromTop() {
        return false;
    }

    public boolean isShowNewDataToast() {
        return false;
    }

    public List<ViewObject> onLoad(@InfoStreamDataSource.SourceType int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        return new ArrayList<>();
    }

    public List<ViewObject> onLoadMore(@InfoStreamDataSource.SourceType int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        return new ArrayList<>();
    }
}
