package com.sunfusheng.news.viewobject;

import android.text.TextUtils;

import com.sunfusheng.base.Constants;
import com.sunfusheng.base.model.NewsModel;
import com.sunfusheng.utils.DateTimeUtil;

/**
 * Created by sunfusheng on 2017/5/25.
 */
public class BaseNewsViewObjectCreator {

    public static void initVo(NewsModel model, com.sunfusheng.news.viewobject.BaseNewsViewObject viewObject) {
        if (model == null) return;
        NewsModel.NewsData data = model.getData();
        if (data == null) return;

        viewObject.title = data.getTitle();
        if (data.getCovers() != null && data.getCovers().size() > 0) {
            viewObject.cover_img = data.getCovers().get(0);
        }
        viewObject.time = DateTimeUtil.formatTime(data.getPdate());
        viewObject.source = data.getSource();
        viewObject.source_img = data.getAuthor_img();
        viewObject.pics = data.getCovers();
        if (!TextUtils.isEmpty(data.getComments()) && !data.getComments().equals("0")) {
            viewObject.comments = handleCommentCountText(Integer.valueOf(data.getComments()));
        }
        viewObject.duration = data.getDuration();

        switch (model.getType()) {
            case Constants.TAG_TYPE_GALLERY:
                if (!TextUtils.isEmpty(data.getPics_count()) && !data.getPics_count().equals("0")) {
                    viewObject.tag_name = "图集";
                } else {
                    viewObject.tag_name = "";
                }
                viewObject.tag_name = "";
                viewObject.tag_color = "#66000000";
                break;
            case Constants.TAG_TYPE_VIDEO:
                viewObject.is_video = true;
                viewObject.tag_name = "视频";
                viewObject.tag_color = "#66000000";
                break;
            case Constants.TAG_TYPE_SPECIAL:
                if (TextUtils.isEmpty(data.getSubject_name())) {
                    viewObject.tag_name = "专题";
                } else {
                    viewObject.tag_name = data.getSubject_name();
                }
                viewObject.tag_color = "#FF9800";
                break;
            case Constants.TAG_TYPE_NORMAL:
            default:
                viewObject.tag_name = null;
                viewObject.tag_color = null;
                break;
        }
    }

    private static String handleCommentCountText(int count) {
        StringBuilder sb = new StringBuilder();
        if (count > 10000) {
            return sb.append(Integer.toString((count / 10000))).append("万").append("评论").toString();
        }
        return sb.append(Integer.toString(count)).append("评论").toString();
    }
}
