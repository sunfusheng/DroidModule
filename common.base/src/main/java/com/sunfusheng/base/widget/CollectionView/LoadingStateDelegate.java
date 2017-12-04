package com.sunfusheng.base.widget.CollectionView;

import android.view.View;
import android.view.ViewStub;

import com.sunfusheng.infostream.anotations.LoadingStatus;

public class LoadingStateDelegate {

    private View viewHolder[] = new View[4];
    private ViewStub viewStubHolder[] = new ViewStub[4];

    public LoadingStateDelegate(View normalView, View loadingView, View failedView, View emptyView) {
        this(normalView, null, loadingView, null, failedView, null, emptyView, null);
    }

    public LoadingStateDelegate(View normalView, ViewStub normalViewStub,
                                View loadingView, ViewStub loadingViewStub,
                                View failedView, ViewStub failedViewStub,
                                View emptyView, ViewStub emptyViewStub) {
        viewHolder[0] = loadingView;
        viewHolder[1] = normalView;
        viewHolder[2] = failedView;
        viewHolder[3] = emptyView;

        viewStubHolder[0] = loadingViewStub;
        viewStubHolder[1] = normalViewStub;
        viewStubHolder[2] = failedViewStub;
        viewStubHolder[3] = emptyViewStub;
    }

    public View setViewState(@LoadingStatus int state) {
        if (state < 0 || state >= viewHolder.length) {
            return null;
        }

        for (View v : viewHolder) {
            if (v == null) {
                continue;
            }
            v.setVisibility(View.GONE);
        }

        if (viewHolder[state] == null) {
            if (viewStubHolder[state] != null && viewStubHolder[state].getParent() != null) {
                viewHolder[state] = viewStubHolder[state].inflate();
            }
        }

        if (viewHolder[state] != null) {
            viewHolder[state].setVisibility(View.VISIBLE);
        }

        return viewHolder[state];
    }

    public void setEmptyView(View view) {
        viewHolder[3] = view;
    }
}
