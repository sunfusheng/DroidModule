package com.sunfusheng.viewobject;

import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.sunfusheng.viewobject.delegate.adapter.AdapterDelegatesManager;
import com.sunfusheng.viewobject.helper.IViewObjectAdapter;
import com.sunfusheng.viewobject.helper.ViewObjectAdapterImpl;
import com.sunfusheng.viewobject.helper.ViewObjectComparator;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ListViewVOAdapter extends BaseAdapter implements IViewObjectAdapter, IViewObjectAdapter.Callback {

    private final ViewObjectAdapterImpl viewObjectAdapterImpl;
    private final ListView listView;
    private final int maxViewTypeCount;
    private int oldListSize = -1;
    private int oldListHash = -1;
    private LruCache<ViewObject, RecyclerView.ViewHolder> viewHolderLruCache = new LruCache<>(10);

    public ListViewVOAdapter(ListView listView, int maxViewTypeCount) {
        this(listView, maxViewTypeCount, null);
    }

    public ListViewVOAdapter(ListView listView, int maxViewTypeCount, AdapterDelegatesManager adapterDelegatesManager) {
        this.maxViewTypeCount = maxViewTypeCount;
        this.viewObjectAdapterImpl = adapterDelegatesManager == null ? new ViewObjectAdapterImpl(this) : new ViewObjectAdapterImpl(adapterDelegatesManager, this);
        this.listView = listView;

        listView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                viewObjectAdapterImpl.onViewAttachedToWindow();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                viewObjectAdapterImpl.onViewDetachedFromWindow();
            }
        });
    }

    @Override
    public int getCount() {
        return viewObjectAdapterImpl.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return viewObjectAdapterImpl.getViewObject(position);
    }

    @Override
    public long getItemId(int position) {
        return viewObjectAdapterImpl.getViewObject(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecyclerView.ViewHolder holder;
        if (convertView == null) {
            holder = viewObjectAdapterImpl.onCreateViewHolder(parent, getItemViewType(position));
            convertView = holder.itemView;
            convertView.setTag(R.id.view_object_tag, getViewObject(position));
            convertView.setTag(holder);
            if (!holder.isRecyclable()) {
                viewHolderLruCache.put(getViewObject(position), holder);
            }
        } else {
            holder = (RecyclerView.ViewHolder) convertView.getTag();
            if (holder != null && holder.isRecyclable()) {
                ViewObject viewObject = (ViewObject) convertView.getTag(R.id.view_object_tag);
                if (viewObject != null) {
                    viewObject.onViewRecycled();
                }
            } else if (holder != null && !holder.isRecyclable() && viewHolderLruCache.get(getViewObject(position)) != null) {
                holder = viewHolderLruCache.get(getViewObject(position));
                convertView = holder.itemView;
            } else {
                holder = viewObjectAdapterImpl.onCreateViewHolder(parent, getItemViewType(position));
                convertView = holder.itemView;
                convertView.setTag(R.id.view_object_tag, getViewObject(position));
                convertView.setTag(holder);
                if (!holder.isRecyclable()) {
                    viewHolderLruCache.put(getViewObject(position), holder);
                }
            }
        }

        viewObjectAdapterImpl.onBindViewHolder(holder, position);
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return maxViewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        return viewObjectAdapterImpl.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return viewObjectAdapterImpl.getItemCount();
    }

    @Override
    public int getFirstVisibleItemIndex() {
        return listView.getFirstVisiblePosition();
    }

    @Override
    public int getLastVisibleItemIndex() {
        return listView.getLastVisiblePosition();
    }

    @Override
    public int add(ViewObject viewObject) {
        return viewObjectAdapterImpl.add(viewObject);
    }

    @Override
    public int add(int position, ViewObject viewObject) {
        return viewObjectAdapterImpl.add(position, viewObject);
    }

    @Override
    public int add(int position, ViewObject viewObject, boolean notifyDataChanged) {
        return viewObjectAdapterImpl.add(position, viewObject, notifyDataChanged);
    }

    @Override
    public int addAll(List<ViewObject> list) {
        return viewObjectAdapterImpl.addAll(list);
    }

    @Override
    public int addAll(int position, List<ViewObject> list) {
        return viewObjectAdapterImpl.addAll(position, list);
    }

    @Override
    public int addAll(int position, List<ViewObject> list, boolean notifyDataChanged) {
        return viewObjectAdapterImpl.addAll(position, list, notifyDataChanged);
    }

    @Override
    public int remove(int position) {
        return viewObjectAdapterImpl.remove(position);
    }

    @Override
    public int remove(int position, boolean notifyDataChanged) {
        return viewObjectAdapterImpl.remove(position, notifyDataChanged);
    }

    @Override
    public int remove(ViewObject viewObject) {
        return viewObjectAdapterImpl.remove(viewObject);
    }

    @Override
    public int remove(ViewObject viewObject, boolean notifyDataChanged) {
        return viewObjectAdapterImpl.remove(viewObject, notifyDataChanged);
    }

    @Override
    public int remove(int startPosition, int endPosition) {
        return viewObjectAdapterImpl.remove(startPosition, endPosition);
    }

    @Override
    public int remove(int startPosition, int endPosition, boolean notifyDataChanged) {
        return viewObjectAdapterImpl.remove(startPosition, endPosition, notifyDataChanged);
    }

    @Override
    public int removeFrom(int startPosition) {
        return viewObjectAdapterImpl.removeFrom(startPosition);
    }

    @Override
    public int removeFrom(int startPosition, boolean notifyDataChanged) {
        return viewObjectAdapterImpl.removeFrom(startPosition, notifyDataChanged);
    }

    @Override
    public int removeAll() {
        return viewObjectAdapterImpl.removeAll();
    }

    @Override
    public int removeAll(boolean notifyDataChanged) {
        return viewObjectAdapterImpl.removeAll(notifyDataChanged);
    }

    @Override
    public void replace(ViewObject oldViewObject, ViewObject newViewObject) {
        viewObjectAdapterImpl.replace(oldViewObject, newViewObject);
    }

    @Override
    public void replace(ViewObject oldViewObject, ViewObject newViewObject, boolean notifyDataChanged) {
        viewObjectAdapterImpl.replace(oldViewObject, newViewObject, notifyDataChanged);
    }

    @Override
    public void replace(int position, ViewObject viewObject) {
        viewObjectAdapterImpl.replace(position, viewObject);
    }

    @Override
    public void replace(int position, ViewObject viewObject, boolean notifyDataChanged) {
        viewObjectAdapterImpl.replace(position, viewObject, notifyDataChanged);
    }

    @Override
    public void setList(List<ViewObject> list) {
        viewObjectAdapterImpl.setList(list);
    }

    @Override
    public void setList(List<ViewObject> list, boolean notifyDataChanged) {
        viewObjectAdapterImpl.setList(list, notifyDataChanged);
    }

    @Override
    public List<ViewObject> getList() {
        return viewObjectAdapterImpl.getList();
    }

    @Override
    public List<Object> getDataList() {
        return viewObjectAdapterImpl.getDataList();
    }

    @Override
    public ViewObject getViewObject(int position) {
        return viewObjectAdapterImpl.getViewObject(position);
    }

    @Override
    public ViewObject getViewObject(ViewObjectComparator comparator) {
        return viewObjectAdapterImpl.getViewObject(comparator);
    }

    @Override
    public <T> T getData(Class<T> clazz, int position) {
        return viewObjectAdapterImpl.getData(clazz, position);
    }

    @Override
    public Object getData(int position) {
        return viewObjectAdapterImpl.getData(position);
    }

    @Override
    public void onInserted(int position, int count) {
        notifyDataSetChanged();
    }

    @Override
    public void onRemoved(int position, int count) {
        notifyDataSetChanged();
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        notifyDataSetChanged();
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutSpanCount() {
        return 1;
    }

    @Override
    public void onContextPause() {
        viewObjectAdapterImpl.onContextPause();
    }

    @Override
    public void onContextResume() {
        viewObjectAdapterImpl.onContextResume();
    }

    @Override
    public void notifyDataSetChanged() {
        if (oldListSize != viewObjectAdapterImpl.getRawList().size() || oldListHash != viewObjectAdapterImpl.getRawList().hashCode()) {
            oldListSize = viewObjectAdapterImpl.getRawList().size();
            oldListHash = viewObjectAdapterImpl.getRawList().hashCode();

            List<ViewObject> viewObjectToRemove = new ArrayList<>();
            for (ViewObject viewObject : viewHolderLruCache.snapshot().keySet()) {
                if (!viewObjectAdapterImpl.getList().contains(viewObject)) {
                    viewObjectToRemove.add(viewObject);
                }
            }

            for (ViewObject viewObject : viewObjectToRemove) {
                viewHolderLruCache.remove(viewObject);
            }

            super.notifyDataSetChanged();
        }
    }
}
