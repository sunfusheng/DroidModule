package com.sunfusheng.app.main;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.sunfusheng.base.Constants;
import com.sunfusheng.base.adapter.FragmentPagerItemAdapter;
import com.sunfusheng.base.base.BaseActivity;
import com.sunfusheng.base.widget.SmartTabLayout.SmartTabLayout;
import com.sunfusheng.utils.ImageUtil;
import com.sunfusheng.utils.ToastUtil;
import com.sunfusheng.utils.Utils;
import com.sunfusheng.router.Router;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.smartTabLayout)
    SmartTabLayout smartTabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.iv_mine)
    ImageView ivMine;

    private static final String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkPermission(PERMISSIONS, failedPermissions -> {
            if (Utils.isEmpty(failedPermissions)) {
                initView();
                initListener();
            } else {
                ToastUtil.toast(getString(R.string.check_permission_phone_state));
            }
        });
    }

    private void initView() {
        ImageUtil.colorImageViewDrawable(ivSearch, R.color.transparent60_white);
        ImageUtil.colorImageViewDrawable(ivMine, R.color.transparent60_white);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter.Builder(this, getSupportFragmentManager())
                .add(R.string.news, getNewsFragment())
                .add(R.string.video, getNewsFragment())
                .add(R.string.image, getNewsFragment())
                .add(R.string.music, getNewsFragment())
                .build();
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        smartTabLayout.setViewPager(viewPager);
    }

    private void initListener() {
        ivSearch.setOnClickListener(v -> {
            ToastUtil.toast("ToDo ...");
        });

        ivMine.setOnClickListener(v -> {
            ToastUtil.toast("ToDo ...");
        });
    }

    private Fragment getNewsFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("cid", Constants.CHANNEL_ID_RECOMMEND_NEWS);
        bundle.putString("is_paging", "2");
        bundle.putString("city_code", "");
        bundle.putBoolean("is_persist", true);
        bundle.putBoolean("is_lazy_load", true);
        return Router.createFragment(this, "news", "NewsFragment", bundle);
    }
}
