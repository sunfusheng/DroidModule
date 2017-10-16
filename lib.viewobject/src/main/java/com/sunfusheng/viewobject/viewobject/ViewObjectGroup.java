package com.sunfusheng.viewobject.viewobject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewObjectGroup<T extends RecyclerView.ViewHolder> extends ViewObject<T> {
    private List<ViewObject> viewObjectList = new ArrayList<>();

    public ViewObjectGroup(Context context, Object extraData, ActionDelegateFactory actionDelegateFactory, ViewObjectFactory viewObjectFactory) {
        super(context, extraData, actionDelegateFactory, viewObjectFactory);
    }

    public void addViewObject(ViewObject viewObject) {
        addViewObject(viewObjectList.size(), viewObject);
    }

    public void addViewObject(int position, ViewObject viewObject) {
        if (viewObject != this) {
            viewObject.setParent(this);
        }
        viewObjectList.add(position, viewObject);
    }

    public int removeViewObject(ViewObject viewObject) {
        int index = viewObjectList.indexOf(viewObject);
        if (index != -1) {
            viewObjectList.remove(viewObject);
        }
        return index;
    }

    public void removeAll() {
        viewObjectList.clear();
    }

    public ViewObject getViewObject(int position) {
        return viewObjectList.get(position);
    }

    public int getViewObjectCount() {
        return viewObjectList.size();
    }

    public List<ViewObject> getViewObjectList() {
        return viewObjectList;
    }
}
