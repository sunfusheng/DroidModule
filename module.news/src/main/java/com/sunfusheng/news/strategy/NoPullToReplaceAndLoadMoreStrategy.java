package com.sunfusheng.news.strategy;

import android.text.TextUtils;

import com.sunfusheng.infostream.RefreshStrategy.AbsRefreshStrategy;
import com.sunfusheng.base.model.NewsModel;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

public class NoPullToReplaceAndLoadMoreStrategy extends AbsRefreshStrategy {
    @Override
    public boolean isPullRefreshEnabled() {
        return false;
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
    public List<ViewObject> onLoadMore(int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        return appendList(currentItems, newItems);
    }

    public List<ViewObject> appendList(List<ViewObject> currentList, List<ViewObject> newItems) {
        for (ViewObject viewObject : newItems) {
            if (viewObject.getParent() != null) {
                continue;
            }
            boolean flag = true;
            for (ViewObject vo : currentList) {
                if (isEqual(vo, viewObject)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                currentList.add(viewObject);
            }
        }
        return currentList;
    }

    private boolean isEqual(ViewObject ob1, ViewObject ob2) {
        if (ob1.getData() != null && ob2.getData() != null && ob1.getData() instanceof NewsModel
                && ob2.getData() instanceof NewsModel) {
            NewsModel model1 = (NewsModel) ob1.getData();
            NewsModel model2 = (NewsModel) ob2.getData();
            if (TextUtils.isEmpty(model1.getGid())) {
                return false;
            }
            return model1.getGid().equals(model2.getGid());
        } else {
            return ob1.equals(ob2);
        }
    }

}