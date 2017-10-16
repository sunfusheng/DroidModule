package com.sunfusheng.base.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sunfusheng on 2017/4/10.
 */
public class NewsMoreModel implements Parcelable {

    private String name;
    private String str_color;
    private String open_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStr_color() {
        return str_color;
    }

    public void setStr_color(String str_color) {
        this.str_color = str_color;
    }

    public String getOpen_url() {
        return open_url;
    }

    public void setOpen_url(String open_url) {
        this.open_url = open_url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.str_color);
        dest.writeString(this.open_url);
    }

    public NewsMoreModel() {
    }

    protected NewsMoreModel(Parcel in) {
        this.name = in.readString();
        this.str_color = in.readString();
        this.open_url = in.readString();
    }

    public static final Creator<NewsMoreModel> CREATOR = new Creator<NewsMoreModel>() {
        @Override
        public NewsMoreModel createFromParcel(Parcel source) {
            return new NewsMoreModel(source);
        }

        @Override
        public NewsMoreModel[] newArray(int size) {
            return new NewsMoreModel[size];
        }
    };
}
