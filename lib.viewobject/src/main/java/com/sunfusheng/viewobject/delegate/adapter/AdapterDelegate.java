package com.sunfusheng.viewobject.delegate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

@SuppressWarnings("unused")
public class AdapterDelegate<T extends RecyclerView.ViewHolder> {

    private ViewHolderFactory viewHolderFactory;

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        public DefaultViewHolder(View itemView) {
            super(itemView);
        }
    }

    public AdapterDelegate(ViewObject viewObject, ViewHolderFactory viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
    }

    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull List<ViewObject> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        onBindViewHolder(items.get(position), (T) holder);
    }

    @SuppressWarnings("unchecked")
    protected void onBindViewHolder(@NonNull ViewObject item, @NonNull T viewHolder) {
        try {
            item.onBindViewHolder(viewHolder);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return viewHolderFactory.createViewHolder(parent);
    }
}
