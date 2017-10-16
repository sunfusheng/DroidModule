package com.sunfusheng.viewobject.delegate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterDelegatesManager {

    private int viewTypeVal = 1;
    private Map<Class, AdapterDelegate> adapterDelegateMap = new HashMap<>();
    private SparseArray<Class> viewType2ClassMap = new SparseArray<>();
    private Map<Class, Integer> class2ViewTypeMap = new HashMap<>();

    protected AdapterDelegatesManager addDelegate(@NonNull Class clazz, @NonNull AdapterDelegate delegate) {
        if (adapterDelegateMap.containsKey(clazz)) {
            return this;
        }
        return addDelegate(viewTypeVal++, clazz, delegate);
    }

    protected AdapterDelegatesManager addDelegate(int viewType, @NonNull Class clazz, @NonNull AdapterDelegate delegate) {
        adapterDelegateMap.put(clazz, delegate);
        viewType2ClassMap.put(viewType, clazz);
        class2ViewTypeMap.put(clazz, viewType);
        return this;
    }

    protected boolean isDelegateRegistered(Class<? extends ViewObject> clazz) {
        return adapterDelegateMap.containsKey(clazz);
    }

    public boolean registerDelegate(ViewObject viewObject) {
        if (isDelegateRegistered(viewObject.getClass())) {
            return true;
        }
        addDelegate(viewObject.getClass(), viewObject.getAdapterDelegate());
        return isDelegateRegistered(viewObject.getClass());
    }

    public int getItemViewType(@NonNull List<ViewObject> items, int position) {
        if (!adapterDelegateMap.containsKey(items.get(position).getClass())) {
            return -1;
        }
        return class2ViewTypeMap.get(items.get(position).getClass());
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterDelegate delegate = adapterDelegateMap.get(viewType2ClassMap.get(viewType));
        if (delegate == null) {
            throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);
        }

        RecyclerView.ViewHolder vh = delegate.onCreateViewHolder(parent);
        if (vh == null) {
            throw new NullPointerException("ViewHolder returned from AdapterDelegate " + delegate + " for ViewType =" + viewType + " is null!");
        }
        return vh;
    }

    public void onBindViewHolder(@NonNull List<ViewObject> items, int position, @NonNull RecyclerView.ViewHolder viewHolder) {
        int itemViewType = getItemViewType(items, position);
        AdapterDelegate delegate = adapterDelegateMap.get(viewType2ClassMap.get(itemViewType));
        if (delegate == null) {
            throw new NullPointerException("No AdapterDelegate added for ViewType " + viewHolder.getItemViewType());
        }
        delegate.onBindViewHolder(items, position, viewHolder);
    }
}
