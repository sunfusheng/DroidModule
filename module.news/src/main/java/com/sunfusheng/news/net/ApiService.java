package com.sunfusheng.news.net;

import com.sunfusheng.base.model.BaseModel;
import com.sunfusheng.infostream.DataSource.InfoStreamDataList;
import com.sunfusheng.base.model.NewsModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sunfusheng on 2017/5/22.
 */
public interface ApiService {

    @GET("/news/listv2?protocol=1")
    Observable<BaseModel<InfoStreamDataList<NewsModel>>> getNewsList(@Query("cid") String cid,
                                                                     @Query("is_paging") String is_paging,
                                                                     @Query("offset") String offset,
                                                                     @Query("refresh_type") String refresh_type,
                                                                     @Query("refresh_count") String refresh_count,
                                                                     @Query("citycode") String city_code,
                                                                     @Query("last") String last);
}
