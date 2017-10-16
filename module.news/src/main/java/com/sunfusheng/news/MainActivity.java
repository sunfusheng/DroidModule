package com.sunfusheng.news;

import android.os.Bundle;

import com.sunfusheng.base.Constants;
import com.sunfusheng.base.base.BaseActivity;
import com.sunfusheng.news.news.NewsFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Initializer().onInit(getApplicationContext());
        initFragment();
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("cid", Constants.CHANNEL_ID_RECOMMEND_NEWS);
        bundle.putString("is_paging", "2");
        bundle.putString("city_code", "");
        bundle.putBoolean("is_persist", false);
        bundle.putBoolean("is_lazy_load", false);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment_container, fragment)
                .commitAllowingStateLoss();
    }
}
