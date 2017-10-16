package com.sunfusheng.base.widget.CollectionView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sunfusheng.viewobject.RecyclerViewVOAdapter;
import com.sunfusheng.viewobject.delegate.adapter.AdapterDelegatesManager;

@SuppressWarnings("unused")
class RecyclerViewFooterAdapter extends RecyclerViewVOAdapter implements IFooterAdapter {

    public static final int VIEW_TYPE_FOOTER = Integer.MAX_VALUE;
    private boolean showFooterAtTop = false;
    private View footerView = null;

    public RecyclerViewFooterAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public RecyclerViewFooterAdapter(RecyclerView recyclerView, AdapterDelegatesManager adapterDelegatesManager) {
        super(recyclerView, adapterDelegatesManager);
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && showFooterAtTop && position == 0) {
            return VIEW_TYPE_FOOTER;
        } else if (footerView != null && !showFooterAtTop && position == super.getItemCount()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return super.getItemViewType(position - (footerView != null && showFooterAtTop ? 1 : 0));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterViewVH(footerView);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_FOOTER) {
            super.onBindViewHolder(holder, position - (footerView != null && showFooterAtTop ? 1 : 0));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasFooter() ? 1 : 0);
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
        return super.getItemCount();
    }

    @Override
    public void onInserted(int position, int count) {
        super.onInserted(position + (footerView != null && showFooterAtTop ? 1 : 0), count);
    }

    @Override
    public void onRemoved(int position, int count) {
        super.onRemoved(position + (footerView != null && showFooterAtTop ? 1 : 0), count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        super.onMoved(fromPosition + (footerView != null && showFooterAtTop ? 1 : 0), toPosition + (footerView != null && showFooterAtTop ? 1 : 0));
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        super.onChanged(position + (footerView != null && showFooterAtTop ? 1 : 0), count, payload);
    }

    private static class FooterViewVH extends RecyclerView.ViewHolder {
        public FooterViewVH(View itemView) {
            super(itemView);
        }
    }
}
