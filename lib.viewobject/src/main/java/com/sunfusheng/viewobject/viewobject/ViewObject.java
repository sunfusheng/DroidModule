package com.sunfusheng.viewobject.viewobject;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;

import com.sunfusheng.viewobject.delegate.action.OnActionRaisedListener;
import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.delegate.adapter.AdapterDelegate;
import com.sunfusheng.viewobject.delegate.adapter.DefaultViewHolderFactory;
import com.sunfusheng.viewobject.delegate.adapter.ViewHolderFactory;
import com.sunfusheng.viewobject.helper.IViewObjectAdapter;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class ViewObject<T extends RecyclerView.ViewHolder> {

    private final WeakReference<Context> contextWeakReference;
    private final ActionDelegateFactory actionDelegateFactory;
    private final ViewObjectFactory viewObjectFactory;
    private ViewHolderFactory viewHolderFactory;
    private IViewObjectAdapter adapter;
    private ViewObject parent;
    private Object data;
    private boolean dataUpdated = false;
    private List<ViewObject.LifeCycleNotify> lifeCycleNotifyList = new ArrayList<>();

    public interface LifeCycleNotify {
        void onLifeCycleNotify(ViewObject from, LifeCycleNotifyType type);
    }

    public enum LifeCycleNotifyType {
        onContextPause,
        onContextResume,
        onRecyclerViewDetached,
        onRecyclerViewAttached,
        onViewObjectRecycled,
        onScrollIn,
        onScrollOut
    }

    public ViewObject(Context context, Object data, ActionDelegateFactory actionDelegateFactory, ViewObjectFactory viewObjectFactory) {
        this.contextWeakReference = new WeakReference<>(context);
        this.data = data;
        this.actionDelegateFactory = actionDelegateFactory;
        this.viewObjectFactory = viewObjectFactory;
    }

    public abstract int getLayoutId();

    public abstract void onBindViewHolder(T viewHolder);

    public void setViewHolderFactory(ViewHolderFactory viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
    }

    public final AdapterDelegate getAdapterDelegate() {
        if (viewHolderFactory == null) {
            viewHolderFactory = new DefaultViewHolderFactory(getLayoutId(), getViewHolderClass(), usePreCreate());
        }
        return new AdapterDelegate<T>(this, viewHolderFactory);
    }

    protected boolean usePreCreate() {
        // FixMe: pre-create逻辑还有些问题，暂时默认禁用
        return false;
    }

    @SuppressWarnings("unchecked")
    public final Class<T> getViewHolderClass() {
        try {
            Class clazz = this.getClass();
            while (clazz != null && !(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
                clazz = clazz.getSuperclass();
            }

            if (clazz != null) {
                ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();
                return ((Class<T>) pt.getActualTypeArguments()[0]);
            } else {
                throw new RuntimeException("No view holder class found.");
            }
        } catch (Throwable e) {
            return null;
        }
    }

    public final void setAdapter(IViewObjectAdapter adapter) {
        if (this.adapter != null && this.adapter instanceof LifeCycleNotifySource) {
            ((LifeCycleNotifySource) this.adapter).unregisterLifeCycleNotify(this);
        }

        this.adapter = adapter;

        if (this.adapter != null && this.adapter instanceof LifeCycleNotifySource && lifeCycleNotifyList.size() > 0) {
            ((LifeCycleNotifySource) this.adapter).registerLifeCycleNotify(this);
        }
    }

    public final Context getContext() {
        return contextWeakReference.get();
    }

    public void setData(Object data) {
        this.data = data;
        dataUpdated = true;
    }

    public Object getData() {
        return data;
    }

    protected void setParent(ViewObject viewObject) {
        this.parent = viewObject;
    }

    public ViewObject getParent() {
        return this.parent;
    }

    public final ViewObject getPrevSibling() {
        if (adapter == null) {
            return null;
        }

        List<ViewObject> viewObjectList = adapter.getList();
        if (viewObjectList == null) {
            return null;
        }

        int index = viewObjectList.indexOf(this);
        return index <= 0 ? null : viewObjectList.get(index - 1);
    }

    public final ViewObject getNextSibling() {
        if (adapter == null) {
            return null;
        }

        List<ViewObject> viewObjectList = adapter.getList();
        if (viewObjectList == null) {
            return null;
        }

        int index = viewObjectList.indexOf(this);
        return index >= viewObjectList.size() - 1 ? null : viewObjectList.get(index + 1);
    }

    public ActionDelegateFactory getActionDelegateFactory() {
        return this.actionDelegateFactory;
    }

    public void raiseAction(@IdRes int actionId) {
        raiseAction(actionId, data);
    }

    public void raiseAction(@IdRes int actionId, Object data) {
        if (actionDelegateFactory == null) {
            return;
        }
        OnActionRaisedListener actionListener = actionDelegateFactory.createActionDelegate(data);
        if (actionListener != null) {
            try {
                actionListener.onActionRaised(getContext(), getClass(), actionId, data, this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void replaceBy(ViewObject viewObject) {
        if (viewObject.dataUpdated && viewObjectFactory != null) {
            ViewObject newViewObject = viewObjectFactory.model2ViewObject(viewObject.getData(), viewObject.getContext(), viewObject.getActionDelegateFactory());
            if (newViewObject != null) {
                viewObject = newViewObject;
            }
        }

        ViewObjectGroup parent = (ViewObjectGroup) getParent();
        if (parent != null) {
            viewObject.setParent(parent);
            int index = parent.removeViewObject(this);
            if (index != -1) {
                parent.addViewObject(index, viewObject);
            }
        }
        if (adapter != null) adapter.replace(this, viewObject);
    }

    public void registerLifeCycleNotify(ViewObject.LifeCycleNotify notify) {
        if (lifeCycleNotifyList.contains(notify)) {
            return;
        }

        lifeCycleNotifyList.add(notify);

        if (adapter != null && adapter instanceof LifeCycleNotifySource) {
            ((LifeCycleNotifySource) this.adapter).registerLifeCycleNotify(this);
        }
    }

    public void unregisterLifeCycleNotify(ViewObject.LifeCycleNotify notify) {
        if (!lifeCycleNotifyList.contains(notify)) {
            return;
        }

        lifeCycleNotifyList.remove(notify);

        if (adapter != null && adapter instanceof LifeCycleNotifySource && lifeCycleNotifyList.size() == 0) {
            ((LifeCycleNotifySource) this.adapter).unregisterLifeCycleNotify(this);
        }
    }

    private void clearLifeCycleNotify() {
        for (int i = lifeCycleNotifyList.size() - 1; i >= 0; i--) {
            unregisterLifeCycleNotify(lifeCycleNotifyList.get(i));
        }
    }

    public void dispatchLifeCycleNotify(ViewObject.LifeCycleNotifyType notifyType) {
        for (int i = lifeCycleNotifyList.size() - 1; i >= 0; i--) {
            try {
                lifeCycleNotifyList.get(i).onLifeCycleNotify(this, notifyType);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyChanged() {
        replaceBy(this);
    }

    public void onViewRecycled() {
        dispatchLifeCycleNotify(LifeCycleNotifyType.onViewObjectRecycled);
        clearLifeCycleNotify();
    }

    public int getSpanSize() {
        if (adapter == null) {
            return 1;
        }

        return adapter.getLayoutSpanCount();
    }

    public int getSideMarginForMultiColumn() {
        return 0;
    }
}
