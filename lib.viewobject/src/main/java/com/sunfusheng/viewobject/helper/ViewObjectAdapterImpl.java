package com.sunfusheng.viewobject.helper;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sunfusheng.viewobject.delegate.adapter.AdapterDelegatesManager;
import com.sunfusheng.viewobject.viewobject.LifeCycleNotifySource;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.ViewObjectGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

@SuppressWarnings("UnusedParameters")
public class ViewObjectAdapterImpl implements IViewObjectAdapter, LifeCycleNotifySource {

    private List<ViewObject> viewObjectList = new ArrayList<>();
    private static AdapterDelegatesManager defaultAdapterDelegatesManager = new AdapterDelegatesManager();
    private AdapterDelegatesManager adapterDelegatesManager;
    private Callback callback;
    private List<ViewObject> lifeCycleNotifyList = new ArrayList<>();

    public ViewObjectAdapterImpl(Callback viewObjectAdapterCallback) {
        this(defaultAdapterDelegatesManager, viewObjectAdapterCallback);
    }

    public ViewObjectAdapterImpl(AdapterDelegatesManager adapterDelegatesManager, Callback viewObjectAdapterCallback) {
        this.adapterDelegatesManager = adapterDelegatesManager;
        this.callback = viewObjectAdapterCallback;
    }

    public int getItemViewType(int position) {
        return adapterDelegatesManager.getItemViewType(viewObjectList, position);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapterDelegatesManager.onCreateViewHolder(parent, viewType);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        adapterDelegatesManager.onBindViewHolder(viewObjectList, position, holder);
    }

    public int getItemCount() {
        return viewObjectList == null ? 0 : viewObjectList.size();
    }

    @Override
    public int getFirstVisibleItemIndex() {
        return RecyclerView.NO_POSITION;
    }

    @Override
    public int getLastVisibleItemIndex() {
        return RecyclerView.NO_POSITION;
    }

    public int add(ViewObject viewObject) {
        return add(viewObjectList.size(), viewObject);
    }

    public int add(int position, ViewObject viewObject) {
        return add(position, viewObject, true);
    }

    public int add(int position, ViewObject viewObject, boolean notifyDataChanged) {
        return add(position, viewObject, notifyDataChanged, true);
    }

    private int add(int position, ViewObject viewObject, boolean notifyDataChanged, boolean recursive) {
        int count = 0;
        if (viewObject instanceof ViewObjectGroup && recursive) {
            ViewObjectGroup viewObjectGroup = (ViewObjectGroup) viewObject;
            int positionToAdd = position;
            for (int i = 0; i < viewObjectGroup.getViewObjectCount(); i++) {
                count += add(positionToAdd, viewObjectGroup.getViewObject(i), false, viewObjectGroup.getViewObject(i) != viewObjectGroup);
                positionToAdd = position + count;
            }
        } else {
            viewObject.setAdapter(this);
            if (adapterDelegatesManager.registerDelegate(viewObject) && !viewObjectList.contains(viewObject)) {
                viewObjectList.add(position, viewObject);
                count = 1;
            } else {
                count = 0;
            }
        }

        if (notifyDataChanged) {
            callback.onInserted(position, count);
        }
        return count;
    }

    public int addAll(List<ViewObject> list) {
        return addAll(viewObjectList.size(), list);
    }

    public int addAll(int position, List<ViewObject> list) {
        return addAll(position, list, true);
    }

    public int addAll(int position, List<ViewObject> list, boolean notifyDataChanged) {
        int totalCount = 0;
        int deltaPosition = position;

        if (list == null || list.size() == 0) {
            return totalCount;
        }

        for (ViewObject viewObject : list) {
            int count = add(deltaPosition, viewObject, false, true);
            deltaPosition += count;
            totalCount += count;
        }

        if (notifyDataChanged) {
            callback.onInserted(position, totalCount);
        }
        return totalCount;
    }

    public int remove(int position) {
        return remove(position, true);
    }

    public int remove(int position, boolean notifyDataChanged) {
        if (!validPosition(position)) {
            return 0;
        }

        return remove(viewObjectList.get(position), notifyDataChanged);
    }

    public int remove(ViewObject viewObject) {
        return remove(viewObject, true);
    }

    public int remove(ViewObject viewObject, boolean notifyDataChanged) {
        return remove(viewObject, notifyDataChanged, true);
    }

    private int remove(ViewObject viewObject, boolean notifyDataChanged, boolean recursive) {
        int count = 0;
        int position = viewObjectList.indexOf(viewObject);

        if (viewObject instanceof ViewObjectGroup && recursive) {
            ViewObjectGroup viewObjectGroup = (ViewObjectGroup) viewObject;

            while (position > 0) {
                ViewObject childViewObject = viewObjectList.get(position - 1);
                if (childViewObject.getParent() != viewObjectGroup) {
                    break;
                }

                viewObject = childViewObject;
                position--;
            }

            while (viewObject != null && (viewObject.getParent() == viewObjectGroup || viewObject == viewObjectGroup)) {
                count += remove(viewObject, false, viewObject != viewObjectGroup);
                viewObject = position < viewObjectList.size() ? viewObjectList.get(position) : null;
            }
        } else {
            viewObject.setAdapter(null);
            viewObjectList.remove(viewObject);
            count = 1;
        }

        if (notifyDataChanged) {
            callback.onRemoved(position, count);
        }

        return count;
    }

    public int remove(int startPosition, int endPosition) {
        return remove(startPosition, endPosition, true);
    }

    public int remove(int startPosition, int endPosition, boolean notifyDataChanged) {
        int count = 0;
        for (int i = endPosition; i >= startPosition; i--) {
            count += remove(i, false);
        }

        if (notifyDataChanged) {
            callback.onRemoved(startPosition, endPosition - startPosition + 1);
        }
        return count;
    }

    public int removeFrom(int startPosition) {
        return removeFrom(startPosition, true);
    }

    public int removeFrom(int startPosition, boolean notifyDataChanged) {
        if (startPosition == 0) {
            return removeAll(notifyDataChanged);
        } else {
            return remove(startPosition, viewObjectList.size() - 1, notifyDataChanged);
        }
    }

    public int removeAll() {
        return removeAll(true);
    }

    public int removeAll(boolean notifyDataChanged) {
        int count = viewObjectList.size();
        for (ViewObject viewObject : viewObjectList) {
            viewObject.setAdapter(null);
        }
        viewObjectList.clear();

        if (notifyDataChanged) {
            callback.onChanged(0, count, null);
        }

        return count;
    }

    public void replace(ViewObject oldViewObject, ViewObject newViewObject) {
        replace(oldViewObject, newViewObject, true);
    }

    public void replace(ViewObject oldViewObject, ViewObject newViewObject, boolean notifyDataChanged) {
        if (oldViewObject == null || newViewObject == null || viewObjectList == null) {
            return;
        }

        replace(viewObjectList.indexOf(oldViewObject), newViewObject, notifyDataChanged);
    }

    public void replace(int position, ViewObject viewObject) {
        replace(position, viewObject, true);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void replace(int position, ViewObject viewObject, boolean notifyDataChanged) {
        List<ViewObject> originalList = new ArrayList<>(viewObjectList);

        ViewObject oldViewObject = viewObjectList.get(position);
        int addPosition = position;
        if (oldViewObject instanceof ViewObjectGroup) {
            ViewObjectGroup viewObjectGroup = (ViewObjectGroup) oldViewObject;

            while (addPosition > 0) {
                ViewObject childViewObject = viewObjectList.get(addPosition - 1);
                if (childViewObject.getParent() != viewObjectGroup) {
                    break;
                }

                addPosition--;
            }
        }

        int removeCount = remove(viewObjectList.get(position), false, true);
        int addCount = add(addPosition, viewObject, false, true);

        if (notifyDataChanged) {
            if (removeCount != addCount) {
                DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return originalList.size();
                    }

                    @Override
                    public int getNewListSize() {
                        return viewObjectList.size();
                    }

                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return originalList.get(oldItemPosition).equals(viewObjectList.get(newItemPosition));
                    }

                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        return originalList.get(oldItemPosition).equals(viewObjectList.get(newItemPosition));
                    }
                }, false).dispatchUpdatesTo(callback);
            } else {
                callback.onChanged(addPosition, addCount, null);
            }
        }
    }

    public void setList(List<ViewObject> list) {
        setList(list, true);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void setList(List<ViewObject> list, boolean notifyDataChanged) {
        List<ViewObject> originalList = new ArrayList<>(viewObjectList);
        removeAll(false);
        addAll(0, list, false);

        if (notifyDataChanged) {
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return originalList.size();
                }

                @Override
                public int getNewListSize() {
                    return viewObjectList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return originalList.get(oldItemPosition).equals(viewObjectList.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return originalList.get(oldItemPosition).equals(viewObjectList.get(newItemPosition));
                }
            }, false).dispatchUpdatesTo(callback);
        }
    }

    public List<ViewObject> getList() {
        return new ListWrapper<>(viewObjectList);
    }

    public List<ViewObject> getRawList() {
        return viewObjectList;
    }

    public List<Object> getDataList() {
        return Observable.fromIterable(viewObjectList)
                .map(ViewObject::getData)
                .toList().blockingGet();
    }

    public ViewObject getViewObject(int position) {
        if (!validPosition(position)) {
            return null;
        }

        return viewObjectList.get(position);
    }

    public ViewObject getViewObject(ViewObjectComparator comparator) {
        if (comparator == null) {
            return null;
        }

        for (ViewObject viewObject : viewObjectList) {
            if (comparator.isEquals(viewObject)) {
                return viewObject;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clazz, int position) {
        Object data = getData(position);
        if (data != null && data.getClass().equals(clazz)) {
            return (T) data;
        } else {
            return null;
        }
    }

    public Object getData(int position) {
        if (!validPosition(position)) {
            return null;
        }

        return viewObjectList.get(position).getData();
    }

    private boolean validPosition(int position) {
        return position >= 0 && position < viewObjectList.size();
    }

    public int getLayoutSpanCount() {
        return callback.getLayoutSpanCount();
    }

    @Override
    public void registerLifeCycleNotify(ViewObject notify) {
        if (lifeCycleNotifyList.contains(notify)) {
            return;
        }

        lifeCycleNotifyList.add(notify);
    }

    @Override
    public void unregisterLifeCycleNotify(ViewObject notify) {
        if (!lifeCycleNotifyList.contains(notify)) {
            return;
        }

        lifeCycleNotifyList.remove(notify);
    }

    private void dispatchLifeCycleNotify(ViewObject.LifeCycleNotifyType notifyType) {
        for (int i = lifeCycleNotifyList.size() - 1; i >= 0; i--) {
            try {
                lifeCycleNotifyList.get(i).dispatchLifeCycleNotify(notifyType);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasLifeCycleObserver() {
        return lifeCycleNotifyList.size() != 0;
    }

    public void onViewAttachedToWindow() {
        dispatchLifeCycleNotify(ViewObject.LifeCycleNotifyType.onRecyclerViewAttached);
    }

    public void onViewDetachedFromWindow() {
        dispatchLifeCycleNotify(ViewObject.LifeCycleNotifyType.onRecyclerViewDetached);
    }

    public void onContextPause() {
        dispatchLifeCycleNotify(ViewObject.LifeCycleNotifyType.onContextPause);
    }

    public void onContextResume() {
        dispatchLifeCycleNotify(ViewObject.LifeCycleNotifyType.onContextResume);
    }

    @Override
    public void notifyDataSetChanged() {
        throw new RuntimeException("This should not be invoked.");
    }
}
