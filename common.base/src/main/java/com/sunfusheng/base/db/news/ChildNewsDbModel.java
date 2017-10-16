package com.sunfusheng.base.db.news;

import io.realm.RealmObject;

/**
 * @author sunfusheng on 2017/8/24.
 */
public class ChildNewsDbModel extends RealmObject {

    private String gid;
    private String parentGid;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getParentGid() {
        return parentGid;
    }

    public void setParentGid(String parentGid) {
        this.parentGid = parentGid;
    }
}
