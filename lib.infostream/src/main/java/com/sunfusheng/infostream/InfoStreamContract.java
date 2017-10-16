package com.sunfusheng.infostream;

import com.sunfusheng.infostream.anotations.FooterStatus;
import com.sunfusheng.infostream.anotations.LoadingStatus;
import com.sunfusheng.infostream.base.BasePresenterWithLifecycle;
import com.sunfusheng.infostream.base.BaseViewWithLifecycle;
import com.sunfusheng.viewobject.helper.ViewObjectComparator;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

public interface InfoStreamContract {

    enum LoadType {
        TYPE_LOCAL,
        TYPE_REMOTE,
        TYPE_BOTH
    }

    interface View extends BaseViewWithLifecycle<Presenter> {

        void init();

        void unInit();

        void setList(List<ViewObject> viewObjectList, boolean animation);

        List<ViewObject> getList();

        void showLoadIndicator(boolean show);

        void showLoadingTopToast(String text);

        void setLoadingStatus(@LoadingStatus int status);

        void setFooterStatus(@FooterStatus int status);

        void scrollList(int scrollTo);

        void enablePullRefresh();

        void enableLoadMore();

        void enableLoadMoreFromTop();

        void addData(int position, List<ViewObject> viewObjectList, boolean animation);

        void remove(ViewObject viewObject);

        void showEmptyView(boolean show);

        void setEmptyView(int layoutResourceId);

        void setErrorView(int layoutResourceId);

        ViewObject getViewObject(ViewObjectComparator viewObjectComparator);

        void setColumnNum(int columnNum);
    }

    interface Presenter extends BasePresenterWithLifecycle {

        void load(LoadType loadType);

        void loadMore();

        void refresh(boolean force);

        void remove(ViewObjectComparator viewObjectComparator);

        void removeAll(ViewObjectComparator viewObjectComparator);
    }
}
