package com.sunfusheng.base.widget.CollectionView;

import android.view.View;

import com.sunfusheng.viewobject.helper.IViewObjectAdapter;

public interface IFooterAdapter extends IViewObjectAdapter {

    boolean hasFooter();

    void setFooterView(View footerView, boolean showFooterAtTop);

    View getFooterView();

    int getContentItemCount();
}
