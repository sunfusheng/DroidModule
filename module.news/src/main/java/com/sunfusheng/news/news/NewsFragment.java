package com.sunfusheng.news.news;

import android.os.Bundle;

import com.sunfusheng.base.DataStream.BaseDataStreamFragment;
import com.sunfusheng.base.DataStream.RefreshStrategy.PullToReplaceAndLoadMoreStrategy;
import com.sunfusheng.infostream.InfoStreamContract;
import com.sunfusheng.router.annotation.RouterExport;

/**
 * Created by sunfusheng on 2017/5/24.
 */
@RouterExport
public class NewsFragment extends BaseDataStreamFragment {

    protected String cid;
    private String is_paging = "1";
    private String city_code = "";
    private boolean is_persist = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cid = getArguments().getString("cid");
        is_paging = getArguments().getString("is_paging");
        city_code = getArguments().getString("city_code");
        is_persist = getArguments().getBoolean("is_persist", true);
        setLazyLoad(getArguments().getBoolean("is_lazy_load", false));
    }

    @Override
    public InfoStreamContract.Presenter createPresenter(InfoStreamContract.View view) {
        return new NewsPresenter(view, NewsDataSource.createInstance(cid, is_paging, city_code, is_persist), null, new PullToReplaceAndLoadMoreStrategy());
    }
}
