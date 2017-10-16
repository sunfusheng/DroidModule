package com.sunfusheng.base.DataStream.RefreshStrategy;

import com.sunfusheng.infostream.RefreshStrategy.AbsRefreshStrategy;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

public class PullToReplaceAndNoLoadMoreStrategy extends AbsRefreshStrategy {

    @Override
    public boolean isPullRefreshEnabled() {
        return true;
    }

    @Override
    public List<ViewObject> onLoad(int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        return newItems;
    }

    @Override
    public List<ViewObject> onLoadMore(int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        return currentItems;
    }
}
