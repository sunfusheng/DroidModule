package com.sunfusheng.news;

import android.content.Context;

import com.sunfusheng.base.model.NewsModel;
import com.sunfusheng.news.viewobject.NoImageViewObject;
import com.sunfusheng.news.viewobject.RightImageViewObject;
import com.sunfusheng.news.viewobject.ThreeImagesViewObject;
import com.sunfusheng.news.viewobject.TopImageViewObject;
import com.sunfusheng.router.Router;
import com.sunfusheng.viewobject.ViewObjectRegister;

/**
 * Created by sunfusheng on 2017/5/8.
 */
public class Initializer implements Router.Initializer {

    public static final int TYPE_NO_IMAGE = 2; // 无图
    public static final int TYPE_RIGHT_IMAGE = 1; // 单张小图
    public static final int TYPE_TOP_IMAGE = 4; // 单张大图
    public static final int TYPE_TOP_THREE_IMAGES = 5; // 三张小图

    @Override
    public void onInit(Context applicationContext) {
        ViewObjectRegister.getInstance().registerViewObjectCreator(NewsModel.class, NewsModel::getModule, TYPE_NO_IMAGE, NoImageViewObject::createViewObject);
        ViewObjectRegister.getInstance().registerViewObjectCreator(NewsModel.class, NewsModel::getModule, TYPE_RIGHT_IMAGE, RightImageViewObject::createViewObject);
        ViewObjectRegister.getInstance().registerViewObjectCreator(NewsModel.class, NewsModel::getModule, TYPE_TOP_IMAGE, TopImageViewObject::createViewObject);
        ViewObjectRegister.getInstance().registerViewObjectCreator(NewsModel.class, NewsModel::getModule, TYPE_TOP_THREE_IMAGES, ThreeImagesViewObject::createViewObject);
    }
}
