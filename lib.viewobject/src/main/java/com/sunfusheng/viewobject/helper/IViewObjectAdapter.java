package com.sunfusheng.viewobject.helper;

import android.support.v7.util.ListUpdateCallback;

import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

public interface IViewObjectAdapter {

    interface Callback extends ListUpdateCallback {
        int getLayoutSpanCount();
    }

    int getItemCount();

    int getFirstVisibleItemIndex();

    int getLastVisibleItemIndex();

    int add(ViewObject viewObject);

    int add(int position, ViewObject viewObject);

    int add(int position, ViewObject viewObject, boolean notifyDataChanged);

    int addAll(List<ViewObject> list);

    int addAll(int position, List<ViewObject> list);

    int addAll(int position, List<ViewObject> list, boolean notifyDataChanged);

    int remove(int position);

    int remove(int position, boolean notifyDataChanged);

    int remove(ViewObject viewObject);

    int remove(ViewObject viewObject, boolean notifyDataChanged);

    int remove(int startPosition, int endPosition);

    int remove(int startPosition, int endPosition, boolean notifyDataChanged);

    int removeFrom(int startPosition);

    int removeFrom(int startPosition, boolean notifyDataChanged);

    int removeAll();

    int removeAll(boolean notifyDataChanged);

    void replace(ViewObject oldViewObject, ViewObject newViewObject);

    void replace(ViewObject oldViewObject, ViewObject newViewObject, boolean notifyDataChanged);

    void replace(int position, ViewObject viewObject);

    void replace(int position, ViewObject viewObject, boolean notifyDataChanged);

    void setList(List<ViewObject> list);

    void setList(List<ViewObject> list, boolean notifyDataChanged);

    List<ViewObject> getList();

    List<Object> getDataList();

    ViewObject getViewObject(int position);

    ViewObject getViewObject(ViewObjectComparator comparator);

    <T> T getData(Class<T> clazz, int position);

    Object getData(int position);

    int getLayoutSpanCount();

    void onContextPause();

    void onContextResume();

    void notifyDataSetChanged();
}
