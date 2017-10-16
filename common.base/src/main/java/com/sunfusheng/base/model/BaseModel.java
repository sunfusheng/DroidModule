package com.sunfusheng.base.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseModel<T> {

    @SerializedName("errno")
    @Expose
    private Integer errno;
    @SerializedName("errmsg")
    @Expose
    private String errmsg;
    @SerializedName("data")
    @Expose
    private T data;

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
