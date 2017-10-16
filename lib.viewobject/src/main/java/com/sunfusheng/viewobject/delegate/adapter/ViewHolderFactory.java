package com.sunfusheng.viewobject.delegate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface ViewHolderFactory {

    RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent);
}
