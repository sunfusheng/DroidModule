package com.sunfusheng.viewobject.delegate.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class DefaultViewHolderFactory implements ViewHolderFactory {
    private final static int POOL_SIZE = 2;
    private int layoutId;
    private Class viewHolderClass;
    private List<RecyclerView.ViewHolder> preCreateViewHolderList = new ArrayList<>();
    private int asyncCreateCount = 0;
    private boolean usePreCreate;

    public DefaultViewHolderFactory(int layoutId, Class viewHolderClass, boolean usePreCreate) {
        this.layoutId = layoutId;
        this.viewHolderClass = viewHolderClass;
        this.usePreCreate = usePreCreate;
        if (viewHolderClass == null || Modifier.isAbstract(viewHolderClass.getModifiers())) {
            this.viewHolderClass = AdapterDelegate.DefaultViewHolder.class;
        }
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent) {
        RecyclerView.ViewHolder viewHolder = null;

        if (usePreCreate) {
            if (preCreateViewHolderList.size() < POOL_SIZE) {
                createViewHolderAsync(parent, POOL_SIZE);
            }

            if (preCreateViewHolderList.size() > 0) {
                viewHolder = preCreateViewHolderList.remove(0);
            }
        }

        return viewHolder == null ? createViewHolderSync(parent) : viewHolder;
    }

    private RecyclerView.ViewHolder createViewHolderSync(@NonNull ViewGroup parent) {
        return bindToViewItem(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    private void createViewHolderAsync(@NonNull ViewGroup parent, int count) {
        if (asyncCreateCount != 0) {
            return;
        }

        asyncCreateCount = count;
        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(parent.getContext());
        for (int i = 0; i < count; i++) {
            asyncLayoutInflater.inflate(layoutId, parent, (view, _1, _2) -> {
                RecyclerView.ViewHolder viewHolder = bindToViewItem(view);
                if (viewHolder != null) {
                    preCreateViewHolderList.add(viewHolder);
                }

                asyncCreateCount--;
            });
        }
    }

    @SuppressWarnings("unchecked")
    private RecyclerView.ViewHolder bindToViewItem(View viewItem) {
        try {
            Constructor constructor = viewHolderClass.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            return (RecyclerView.ViewHolder) constructor.newInstance(viewItem);
        } catch (Throwable e1) {
            e1.printStackTrace();
            try {
                return new AdapterDelegate.DefaultViewHolder(viewItem);
            } catch (Throwable e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }
}
