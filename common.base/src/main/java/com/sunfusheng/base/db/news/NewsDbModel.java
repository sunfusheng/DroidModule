package com.sunfusheng.base.db.news;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunfusheng.base.model.NewsModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class NewsDbModel extends RealmObject {

    @PrimaryKey
    private String id;
    @Required
    private String gid;
    private String parentGid;
    private long index;
    private String channel;
    private Long time;
    private long readTime;
    private String readNewsType;
    private Integer isStick;
    private String extraJson;
    private String zm_json;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public String getReadNewsType() {
        return readNewsType;
    }

    public void setReadNewsType(String readNewsType) {
        this.readNewsType = readNewsType;
    }

    public Integer getIsStick() {
        return isStick;
    }

    public void setIsStick(Integer isStick) {
        this.isStick = isStick;
    }

    public String getExtraJson() {
        return extraJson;
    }

    public void setExtraJson(String extraJson) {
        this.extraJson = extraJson;
    }

    public String getZm_json() {
        return zm_json;
    }

    public void setZm_json(String zm_json) {
        this.zm_json = zm_json;
    }

    public static NewsDbModel model2DbModel(NewsModel model) {
        if (model == null) {
            return null;
        }
        NewsDbModel dbModel = new NewsDbModel();
        dbModel.setId(model.getGid() + model.getModule_id());
        dbModel.setGid(model.getGid());
        dbModel.setParentGid(model.getParentGid());
        dbModel.setIndex(model.getIndex());
        dbModel.setChannel(model.getChannel());
        dbModel.setTime(model.getTime());
        dbModel.setReadTime(model.getReadTime() != null ? model.getReadTime() : 0L);
        dbModel.setReadNewsType(String.valueOf(model.getType()));
        dbModel.setExtraJson(new Gson().toJson(model));
        dbModel.setIsStick(model.getData() != null ? model.getData().getIs_stick() : 0);
        dbModel.setZm_json(model.getZm_json());
        return dbModel;
    }

    public static NewsModel dbModel2Model(NewsDbModel dbModel) {
        if (dbModel == null || TextUtils.isEmpty(dbModel.getExtraJson())) {
            return null;
        }
        return new Gson().fromJson(dbModel.getExtraJson(), NewsModel.class);
    }
}
