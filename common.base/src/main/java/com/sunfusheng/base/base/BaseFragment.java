package com.sunfusheng.base.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.sunfusheng.utils.Utils;
import com.sunfusheng.utils.permission.IPermission;
import com.sunfusheng.utils.permission.IPermissionCallback;
import com.sunfusheng.utils.permission.PermissionHelper;
import com.trello.rxlifecycle2.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment implements IPermission {

    private boolean isViewCreated = false;
    private boolean isPagerVisible = false;
    private boolean isLoaded = false;

    private PermissionHelper permissionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("log-fragment", getClass().getSimpleName() + ".java");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isPagerVisible = isVisibleToUser;
        if (isVisibleToUser) {
            lazyLoad();
        }
    }

    public boolean isVisibleNow() {
        return isViewCreated && isPagerVisible;
    }

    private void lazyLoad() {
        if (isVisibleNow() && !isLoaded) {
            isLoaded = true;
            onLazyLoad();
        }
    }

    protected void onLazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isPagerVisible = false;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void checkPermission(@NonNull String[] permissions, IPermissionCallback callback) {
        if (Utils.isEmpty(permissions)) {
            return;
        }

        if (permissionHelper == null) {
            permissionHelper = new PermissionHelper(this);
        }
        permissionHelper.checkPermission(permissions, callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

