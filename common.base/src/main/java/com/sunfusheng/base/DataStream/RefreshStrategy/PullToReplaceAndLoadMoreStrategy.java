package com.sunfusheng.base.DataStream.RefreshStrategy;

import com.sunfusheng.infostream.RefreshStrategy.AbsRefreshStrategy;
import com.sunfusheng.viewobject.helper.group.GroupableItem;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.ViewObjectGroup;

import java.util.List;

public class PullToReplaceAndLoadMoreStrategy extends AbsRefreshStrategy {

    @Override
    public boolean isPullRefreshEnabled() {
        return true;
    }

    @Override
    public boolean isLoadMore() {
        return true;
    }

    @Override
    public List<ViewObject> onLoad(int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        return newItems;
    }

    @Override
    public boolean isShowNewDataToast() {
        return true;
    }

    @Override
    public List<ViewObject> onLoadMore(int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            return currentItems;
        }

        if (currentItems == null || currentItems.isEmpty()) {
            return newItems;
        }

        ViewObjectGroup lastViewObjectGroup = getViewObjectGroupOfVO(currentItems.get(currentItems.size() - 1));
        ViewObjectGroup newViewObjectGroup = getViewObjectGroupOfVO(newItems.get(0));
        if (lastViewObjectGroup == null ||
                newViewObjectGroup == null ||
                lastViewObjectGroup.getViewObjectCount() == 0 ||
                newViewObjectGroup.getViewObjectCount() == 0) {

            currentItems.addAll(newItems);
            return currentItems;
        }

        Object lastGroupKey = getKey(lastViewObjectGroup);
        Object newGroupKey = getKey(newViewObjectGroup);
        if (lastGroupKey == null || newGroupKey == null || !lastGroupKey.equals(newGroupKey)) {
            currentItems.addAll(newItems);
            return currentItems;
        }

        for (int i = 0; i < newViewObjectGroup.getViewObjectCount(); i++) {
            ViewObject viewObject = newViewObjectGroup.getViewObject(i);
            if (viewObject instanceof ViewObjectGroup || viewObject.getData() == null || !(viewObject.getData() instanceof GroupableItem)) {
                continue;
            }

            lastViewObjectGroup.addViewObject(viewObject);
        }

        newItems.remove(newViewObjectGroup);
        currentItems.addAll(newItems);
        return currentItems;
    }

    private ViewObjectGroup getViewObjectGroupOfVO(ViewObject viewObject) {
        if (viewObject instanceof ViewObjectGroup) {
            return (ViewObjectGroup) viewObject;
        } else {
            return (ViewObjectGroup) viewObject.getParent();
        }
    }

    private String getKey(ViewObjectGroup viewObjectGroup) {
        for (int i = 0; i < viewObjectGroup.getViewObjectCount(); i++) {
            ViewObject viewObject = viewObjectGroup.getViewObject(i);
            if (viewObject instanceof ViewObjectGroup) {
                continue;
            }

            if (!(viewObject.getData() instanceof GroupableItem)) {
                continue;
            }

            return ((GroupableItem) viewObject.getData()).generateKey();
        }

        return null;
    }
}