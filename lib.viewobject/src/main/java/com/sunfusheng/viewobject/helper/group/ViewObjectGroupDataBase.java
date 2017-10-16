package com.sunfusheng.viewobject.helper.group;

import java.util.ArrayList;
import java.util.List;

public class ViewObjectGroupDataBase {

    final private String key;

    private List<GroupableItem> dataList = new ArrayList<>();

    public ViewObjectGroupDataBase(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public List<GroupableItem> getDataList() {
        return dataList;
    }
}
