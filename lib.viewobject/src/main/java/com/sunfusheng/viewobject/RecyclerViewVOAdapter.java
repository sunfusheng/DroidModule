package com.sunfusheng.viewobject;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sunfusheng.viewobject.delegate.adapter.AdapterDelegatesManager;
import com.sunfusheng.viewobject.helper.IViewObjectAdapter;
import com.sunfusheng.viewobject.helper.ViewObjectAdapterImpl;
import com.sunfusheng.viewobject.helper.ViewObjectComparator;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

@SuppressWarnings("unused")
public class RecyclerViewVOAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IViewObjectAdapter, IViewObjectAdapter.Callback {

    private RecyclerView recyclerView;
    private final ViewObjectAdapterImpl viewObjectAdapterImpl;
    private int prevFirstVisibleItem = RecyclerView.NO_POSITION;
    private int prevLastVisibleItem = RecyclerView.NO_POSITION;
    private int firstVisibleItem = RecyclerView.NO_POSITION;
    private int lastVisibleItem = RecyclerView.NO_POSITION;

    public RecyclerViewVOAdapter(RecyclerView recyclerView) {
        this(recyclerView, null);
    }

    public RecyclerViewVOAdapter(RecyclerView recyclerView, AdapterDelegatesManager adapterDelegatesManager) {
        this.recyclerView = recyclerView;
        this.viewObjectAdapterImpl = (adapterDelegatesManager == null) ? new ViewObjectAdapterImpl(this) : new ViewObjectAdapterImpl(adapterDelegatesManager, this);

        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                viewObjectAdapterImpl.onViewAttachedToWindow();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                viewObjectAdapterImpl.onViewDetachedFromWindow();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (viewObjectAdapterImpl.hasLifeCycleObserver() && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    raiseViewObjectScrollNotify();
                }
            }
        });
    }

    private void raiseViewObjectScrollNotify() {
        if (firstVisibleItem == RecyclerView.NO_POSITION && lastVisibleItem == RecyclerView.NO_POSITION) {
            return;
        }

        for (int i = Math.min(firstVisibleItem, prevFirstVisibleItem); i < Math.max(firstVisibleItem, prevFirstVisibleItem); i++) {
            ViewObject vo = getViewObject(i);
            if (vo != null) {
                vo.dispatchLifeCycleNotify(firstVisibleItem < prevFirstVisibleItem ? ViewObject.LifeCycleNotifyType.onScrollIn : ViewObject.LifeCycleNotifyType.onScrollOut);
            }
        }

        for (int i = Math.min(lastVisibleItem, prevLastVisibleItem); i < Math.max(lastVisibleItem, prevLastVisibleItem); i++) {
            ViewObject vo = getViewObject(i);
            if (vo != null) {
                vo.dispatchLifeCycleNotify(lastVisibleItem > prevLastVisibleItem ? ViewObject.LifeCycleNotifyType.onScrollIn : ViewObject.LifeCycleNotifyType.onScrollOut);
            }
        }

        prevFirstVisibleItem = firstVisibleItem;
        prevLastVisibleItem = lastVisibleItem;
    }

    @Override
    public int getItemViewType(int position) {
        return viewObjectAdapterImpl.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewObjectAdapterImpl.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        viewObjectAdapterImpl.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return viewObjectAdapterImpl.getItemCount();
    }

    @Override
    public int getFirstVisibleItemIndex() {
        if (firstVisibleItem != RecyclerView.NO_POSITION) {
            return firstVisibleItem;
        }

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            return layoutManager.findFirstVisibleItemPosition();
        }

        return RecyclerView.NO_POSITION;
    }

    @Override
    public int getLastVisibleItemIndex() {
        if (lastVisibleItem != RecyclerView.NO_POSITION) {
            return lastVisibleItem;
        }

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            return layoutManager.findLastVisibleItemPosition();
        }

        return RecyclerView.NO_POSITION;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder != null) {
            ViewObject viewObject = viewObjectAdapterImpl.getViewObject(holder.getAdapterPosition());
            if (viewObject != null) {
                viewObject.onViewRecycled();
            }
        }
        super.onViewRecycled(holder);
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
        notifyItemRangeInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        notifyItemRangeChanged(position, count, payload);
    }

    @Override
    public int getLayoutSpanCount() {
        if (recyclerView == null) {
            return 1;
        }

        if (!(recyclerView.getLayoutManager() instanceof GridLayoutManager)) {
            return 1;
        }

        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        return gridLayoutManager.getSpanCount();
    }

    @Override
    public void onContextPause() {
        viewObjectAdapterImpl.onContextPause();
    }

    @Override
    public void onContextResume() {
        viewObjectAdapterImpl.onContextResume();
    }
}
