package com.sunfusheng.news.viewobject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.sunfusheng.news.R;
import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;
import com.sunfusheng.glideimageview.GlideImageLoader;

import java.util.List;

/**
 * Created by sunfusheng on 2017/5/25.
 */
public abstract class BaseNewsViewObject<T extends RecyclerView.ViewHolder> extends ViewObject<T> {

    public String title;
    public String cover_img;
    public String time;
    public String source;
    public String source_img;
    public List<String> pics;
    public String comments;
    public String tag_name;
    public String tag_color;
    public boolean is_video;
    public String duration;

    public BaseNewsViewObject(Context context, Object data, ActionDelegateFactory actionDelegateFactory, ViewObjectFactory viewObjectFactory) {
        super(context, data, actionDelegateFactory, viewObjectFactory);
    }

    public void loadImage(ImageView imageView, String imageUrl) {
        if (imageView == null) return;
        GlideImageLoader imageLoader = GlideImageLoader.create(imageView);
        RequestOptions requestOptions = imageLoader.requestOptions(R.color.placeholder_color);
        imageLoader.requestBuilder(imageUrl, requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

}
