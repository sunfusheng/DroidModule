package com.sunfusheng.base.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.orhanobut.logger.Logger;
import com.sunfusheng.utils.Utils;
import com.sunfusheng.utils.permission.IPermission;
import com.sunfusheng.utils.permission.IPermissionCallback;
import com.sunfusheng.utils.permission.PermissionHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

public abstract class BaseActivity extends RxAppCompatActivity implements IPermission {

    private BaseFragment mBackPressedFragment;
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("log-activity", getClass().getSimpleName() + ".java");
    }

    protected void initToolbar(Toolbar toolbar, @StringRes int resId, boolean showHomeAsUp) {
        initToolbar(toolbar, getString(resId), showHomeAsUp);
    }

    protected void initToolbar(Toolbar toolbar, String title, boolean showHomeAsUp) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedFragment == null || !mBackPressedFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void setBackPressedFragment(Fragment fragment) {
        if (fragment instanceof BaseFragment) {
            this.mBackPressedFragment = (BaseFragment) fragment;
        }
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
