package com.sunfusheng.base.db.news;

import android.text.TextUtils;

import com.sunfusheng.base.db.DbHelper;
import com.sunfusheng.base.db.DbQueryParams;
import com.sunfusheng.base.model.NewsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunfusheng on 2017/8/24.
 */
public class ChildNewsDbHelper extends DbHelper<ChildNewsDbModel> {

    public void saveChildNewsList(List<NewsModel> list) {
        for (NewsModel model : list) {
            saveChildNewsList(model);
        }
    }

    public void saveChildNewsList(NewsModel model) {
        List<ChildNewsDbModel> childNewsItemList = new ArrayList<>();
        String parentGid = model.getGid();
        if (TextUtils.isEmpty(parentGid)) {
            return;
        }
        if (model.getData() == null || model.getData().getNews() == null || model.getData().getNews().size() == 0) {
            return;
        }

        for (NewsModel subModle : model.getData().getNews()) {
            String gid = subModle.getGid();
            if (TextUtils.isEmpty(gid)) {
                continue;
            }
            ChildNewsDbModel childNewsItem = new ChildNewsDbModel();
            childNewsItem.setGid(gid);
            childNewsItem.setParentGid(parentGid);
            childNewsItemList.add(childNewsItem);
        }
        insertOrUpdate(childNewsItemList);
    }

    public String getParentGid(String gid) {
        DbQueryParams params = new DbQueryParams(ChildNewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "gid", gid));
        ChildNewsDbModel childNewsItem = queryFirst(params);
        if (childNewsItem != null) {
            return childNewsItem.getParentGid();
        } else {
            return null;
        }
    }

    public void delByParentGid(String parentGid) {
        DbQueryParams params = new DbQueryParams(ChildNewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "parentGid", parentGid));
        deleteAll(params);
    }

    public void updateChildItem(NewsModel model) {
        String parentGid = getParentGid(model.getGid());
        if (TextUtils.isEmpty(parentGid)) {
            return;
        }
    }
}
