package com.sunfusheng.base.DataStream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunfusheng.base.R;
import com.sunfusheng.base.R2;
import com.sunfusheng.base.widget.CollectionView.CollectionView;
import com.sunfusheng.infostream.InfoStreamContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseDataStreamFragment extends BaseRefreshFragment {

    @BindView(R2.id.collection_view)
    CollectionView collectionView;

    private InfoStreamContract.Presenter presenter;
    private DataStreamViewDefaultImpl view;
    private boolean isLazyLoad = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_collection_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLazyLoad) {
            init();
        }
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        if (isLazyLoad) {
            init();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (view != null) {
            view.setPagerVisible(isVisibleNow());
        }
    }

    protected void init() {
        view = new DataStreamViewDefaultImpl(collectionView);
        view.setPagerVisible(isVisibleNow());

        presenter = createPresenter(view);
        presenter.init();
    }

    public abstract InfoStreamContract.Presenter createPresenter(InfoStreamContract.View view);

    public InfoStreamContract.Presenter getPresenter() {
        return presenter;
    }

    public CollectionView getCollectionView() {
        return collectionView;
    }

    public void setLazyLoad(boolean isLazy) {
        this.isLazyLoad = isLazy;
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.unInit();
        }
        if (view != null) {
            view.unInit();
        }
        super.onDestroyView();
    }

    @Override
    public void refresh(boolean force) {
        if (presenter != null) {
            presenter.refresh(force);
        }
    }
}
