package com.sunfusheng.infostream.DataSource;

import android.text.TextUtils;

import java.util.List;

public class InfoStreamDataList<T> {

    private String tips;

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public List<T> getData() {

        return data;
    }

    public String getTips() {
        return tips;
    }

    private List<T> data;

    public InfoStreamDataList(String tips, List data) {
        this.tips = TextUtils.isEmpty(tips) ? "" : tips;
        this.data = data;
    }

    public InfoStreamDataList(List data) {
        this.tips = "";
        this.data = data;
    }
}
