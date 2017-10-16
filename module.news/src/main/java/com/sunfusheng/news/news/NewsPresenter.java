package com.sunfusheng.news.news;

import com.sunfusheng.infostream.DataSource.InfoStreamDataSource;
import com.sunfusheng.infostream.HeaderProvider.HeaderProvider;
import com.sunfusheng.infostream.InfoStreamContract;
import com.sunfusheng.infostream.InfoStreamPresenter;
import com.sunfusheng.infostream.RefreshStrategy.AbsRefreshStrategy;

/**
 * Created by sunfusheng on 2017/5/24.
 */
public class NewsPresenter extends InfoStreamPresenter {

    public NewsPresenter(InfoStreamContract.View view, InfoStreamDataSource repository, HeaderProvider headerProvider, AbsRefreshStrategy refreshStrategy) {
        super(view, repository, headerProvider, refreshStrategy);
        setRefreshInterval(0);
    }

    @Override
    public void init() {
        super.init();
    }

}
