package com.sunfusheng.base.widget.CollectionView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sunfusheng.viewobject.ListViewVOAdapter;
import com.sunfusheng.viewobject.delegate.adapter.AdapterDelegatesManager;

@SuppressWarnings("unused")
class ListViewFooterAdapter extends ListViewVOAdapter implements IFooterAdapter {

    private int VIEW_TYPE_FOOTER;
    private boolean showFooterAtTop = false;
    private View footerView = null;
    private static final int MAX_VIEW_TYPE_COUNT = 255;

    public ListViewFooterAdapter(ListView listView) {
        this(listView, null);
    }

    public ListViewFooterAdapter(ListView listView, AdapterDelegatesManager adapterDelegatesManager) {
        super(listView, MAX_VIEW_TYPE_COUNT, adapterDelegatesManager);
        VIEW_TYPE_FOOTER = MAX_VIEW_TYPE_COUNT - 1;
    }

    @Override
    public Object getItem(int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_FOOTER) {
            return null;
        }

        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_FOOTER) {
            return 0;
        }
        return super.getItemId(position - (footerView != null && showFooterAtTop ? 1 : 0));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_FOOTER) {
            return footerView;
        } else {
            return super.getView(position - (footerView != null && showFooterAtTop ? 1 : 0), convertView, parent);
        }
    }

    @Override
    public int getCount() {
        return footerView != null ? super.getCount() + 1 : super.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && showFooterAtTop && position == 0) {
            return VIEW_TYPE_FOOTER;
        } else if (footerView != null && !showFooterAtTop && position == super.getCount()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return super.getItemViewType(position - (footerView != null && showFooterAtTop ? 1 : 0));
        }
    }

    public boolean hasFooter() {
        return footerView != null;
    }

    public void setFooterView(View footerView, boolean showFooterAtTop) {
        this.footerView = footerView;
        this.showFooterAtTop = showFooterAtTop;
    }

    public View getFooterView() {
        return footerView;
    }

    public int getContentItemCount() {
        return super.getCount();
    }

}
