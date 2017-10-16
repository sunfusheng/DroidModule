package com.sunfusheng.base.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunfusheng on 2017/5/22.
 */
public class NewsModel {

    private String gid;
    private String uuid;
    private String parentGid;
    private long index;
    private String channel;
    private Long time;

    private Long readTime;
    private int type;
    private String open_url;
    private int module;
    private String module_id;
    private String zm_json;
    private String last;

    private NewsData data;

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getZm_json() {
        return zm_json;
    }

    public void setZm_json(String zm_json) {
        this.zm_json = zm_json;
    }

    public NewsData getData() {
        return data;
    }

    public void setData(NewsData data) {
        this.data = data;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOpen_url() {
        return open_url;
    }

    public void setOpen_url(String open_url) {
        this.open_url = open_url;
    }


    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getReadTime() {
        return readTime;
    }

    public void setReadTime(Long readTime) {
        this.readTime = readTime;
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

    private Map<String, String> splitQuery(String url) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        if (TextUtils.isEmpty(url)) {
            return query_pairs;
        }
        String query = url.substring(url.indexOf("?") + 1);
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (TextUtils.isEmpty(pair)) continue;
            int idx = pair.indexOf("=");
            if (idx < 0) continue;
            try {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return query_pairs;
    }

    public static class NewsData implements Parcelable {

        private int is_stick;
        private String title;
        private String color;
        private String pdate;
        private String source;
        private List<String> covers;
        private String author_uid;
        private String author_name;
        private String author_img;
        private String comments;
        private int is_book;
        private String duration;
        private String pics_count;
        private String subject_name;
        private List<NewsModel> news;
        private String template_url;
        private String template_url_sign;
        private String template_data;
        private String template_common_url;
        private String template_common_url_sign;
        private String url;
        private String footer;
        private String readers;
        private String c_id;
        private String name;
        private String icon;
        private String summary;
        private int is_my;
        private int news_num;
        private int sub_num;
        private int auth_type;
        private int identify_status;
        private boolean is_sub;
        private String relation_id;
        private long tdate;
        private String group_label;
        private boolean deleteFlag;
        private boolean moreFlag = true;
        private String share;
        private String wemedia_url;//自频道URL
        private String extra;
        public String group_index;
        public String group_index_color;
        private String watches;//资讯用
        private String watches_str;//直播用
        private int live_stats;//资讯用直播状态
        private String status;//直播用直播状态
        private String news_data;//直播用
        private String meta;
        private NewsMoreModel more;

        public NewsData() {
        }

        public String getMeta() {
            return meta;
        }

        public void setMeta(String meta) {
            this.meta = meta;
        }

        public NewsMoreModel getMore() {
            return more;
        }

        public void setMore(NewsMoreModel more) {
            this.more = more;
        }

        public int getIs_my() {
            return is_my;
        }

        public void setIs_my(int is_my) {
            this.is_my = is_my;
        }

        public String getShare() {
            return share;
        }

        public void setShare(String share) {
            this.share = share;
        }

        public boolean isDeleteFlag() {
            return deleteFlag;
        }

        public void setDeleteFlag(boolean deleteFlag) {
            this.deleteFlag = deleteFlag;
        }

        public boolean isMoreFlag() {
            return moreFlag;
        }

        public void setMoreFlag(boolean moreFlag) {
            this.moreFlag = moreFlag;
        }

        public String getGroup_label() {
            return group_label;
        }

        public void setGroup_label(String group_label) {
            this.group_label = group_label;
        }

        public void setTdate(long tdate) {
            this.tdate = tdate;
        }

        public long getTdate() {
            return tdate;
        }

        public int getIs_stick() {
            return is_stick;
        }

        public void setIs_stick(int is_stick) {
            this.is_stick = is_stick;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getPdate() {
            return pdate;
        }

        public void setPdate(String pdate) {
            this.pdate = pdate;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public List<String> getCovers() {
            return covers;
        }

        public void setCovers(List<String> covers) {
            this.covers = covers;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAuthor_uid() {
            return author_uid;
        }

        public void setAuthor_uid(String author_uid) {
            this.author_uid = author_uid;
        }

        public String getAuthor_img() {
            return author_img;
        }

        public void setAuthor_img(String author_img) {
            this.author_img = author_img;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getWatches() {
            return watches;
        }

        public void setWatches(String watches) {
            this.watches = watches;
        }

        public int getLive_stats() {
            return live_stats;
        }

        public void setLive_stats(int live_stats) {
            this.live_stats = live_stats;
        }

        public int getIs_book() {
            return is_book;
        }

        public void setIs_book(int is_book) {
            this.is_book = is_book;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getPics_count() {
            return pics_count;
        }

        public void setPics_count(String pics_count) {
            this.pics_count = pics_count;
        }

        public String getSubject_name() {
            return subject_name;
        }

        public void setSubject_name(String subject_name) {
            this.subject_name = subject_name;
        }

        public List<NewsModel> getNews() {
            return news;
        }

        public void setNews(List<NewsModel> news) {
            this.news = news;
        }

        public String getTemplate_url() {
            return template_url;
        }

        public void setTemplate_url(String template_url) {
            this.template_url = template_url;
        }

        public String getTemplate_url_sign() {
            return template_url_sign;
        }

        public void setTemplate_url_sign(String template_url_sign) {
            this.template_url_sign = template_url_sign;
        }

        public String getTemplate_data() {
            return template_data;
        }

        public void setTemplate_data(String template_data) {
            this.template_data = template_data;
        }

        public String getTemplate_common_url() {
            return template_common_url;
        }

        public void setTemplate_common_url(String template_common_url) {
            this.template_common_url = template_common_url;
        }

        public String getTemplate_common_url_sign() {
            return template_common_url_sign;
        }

        public void setTemplate_common_url_sign(String template_common_url_sign) {
            this.template_common_url_sign = template_common_url_sign;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFooter() {
            return footer;
        }

        public void setFooter(String footer) {
            this.footer = footer;
        }

        public String getReaders() {
            return readers;
        }

        public void setReaders(String readers) {
            this.readers = readers;
        }

        public String getC_id() {
            return c_id;
        }

        public void setC_id(String c_id) {
            this.c_id = c_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public int getNews_num() {
            return news_num;
        }

        public void setNews_num(int news_num) {
            this.news_num = news_num;
        }

        public int getSub_num() {
            return sub_num;
        }

        public void setSub_num(int sub_num) {
            this.sub_num = sub_num;
        }

        public int getAuth_type() {
            return auth_type;
        }

        public void setAuth_type(int auth_type) {
            this.auth_type = auth_type;
        }

        public int getIdentify_status() {
            return identify_status;
        }

        public void setIdentify_status(int identify_status) {
            this.identify_status = identify_status;
        }

        public boolean getIs_sub() {
            return is_sub;
        }

        public void setIs_sub(boolean is_sub) {
            this.is_sub = is_sub;
        }

        public String getRelation_id() {
            return relation_id;
        }

        public void setRelation_id(String relation_id) {
            this.relation_id = relation_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getWatches_str() {
            return watches_str;
        }

        public void setWatches_str(String watches_str) {
            this.watches_str = watches_str;
        }


        public String getNews_data() {
            return news_data;
        }

        public void setNews_data(String news_data) {
            this.news_data = news_data;
        }


        public String getWemedia_url() {
            return wemedia_url;
        }

        public void setWemedia_url(String wemedia_url) {
            this.wemedia_url = wemedia_url;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getGroup_index() {
            return group_index;
        }

        public void setGroup_index(String group_index) {
            this.group_index = group_index;
        }

        public String getGroup_index_color() {
            return group_index_color;
        }

        public void setGroup_index_color(String group_index_color) {
            this.group_index_color = group_index_color;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.is_stick);
            dest.writeString(this.title);
            dest.writeString(this.color);
            dest.writeString(this.pdate);
            dest.writeString(this.source);
            dest.writeStringList(this.covers);
            dest.writeString(this.author_uid);
            dest.writeString(this.author_name);
            dest.writeString(this.author_img);
            dest.writeString(this.comments);
            dest.writeInt(this.is_book);
            dest.writeString(this.duration);
            dest.writeString(this.pics_count);
            dest.writeString(this.subject_name);
            dest.writeList(this.news);
            dest.writeString(this.template_url);
            dest.writeString(this.template_url_sign);
            dest.writeString(this.template_data);
            dest.writeString(this.template_common_url);
            dest.writeString(this.template_common_url_sign);
            dest.writeString(this.url);
            dest.writeString(this.footer);
            dest.writeString(this.readers);
            dest.writeString(this.c_id);
            dest.writeString(this.name);
            dest.writeString(this.icon);
            dest.writeString(this.summary);
            dest.writeInt(this.is_my);
            dest.writeInt(this.news_num);
            dest.writeInt(this.sub_num);
            dest.writeInt(this.auth_type);
            dest.writeInt(this.identify_status);
            dest.writeByte(this.is_sub ? (byte) 1 : (byte) 0);
            dest.writeString(this.relation_id);
            dest.writeLong(this.tdate);
            dest.writeString(this.group_label);
            dest.writeByte(this.deleteFlag ? (byte) 1 : (byte) 0);
            dest.writeByte(this.moreFlag ? (byte) 1 : (byte) 0);
            dest.writeString(this.share);
            dest.writeString(this.wemedia_url);
            dest.writeString(this.extra);
            dest.writeString(this.group_index);
            dest.writeString(this.group_index_color);
            dest.writeString(this.watches);
            dest.writeString(this.watches_str);
            dest.writeInt(this.live_stats);
            dest.writeString(this.status);
            dest.writeString(this.news_data);
            dest.writeString(this.meta);
            dest.writeParcelable(this.more, flags);
        }

        protected NewsData(Parcel in) {
            this.is_stick = in.readInt();
            this.title = in.readString();
            this.color = in.readString();
            this.pdate = in.readString();
            this.source = in.readString();
            this.covers = in.createStringArrayList();
            this.author_uid = in.readString();
            this.author_name = in.readString();
            this.author_img = in.readString();
            this.comments = in.readString();
            this.is_book = in.readInt();
            this.duration = in.readString();
            this.pics_count = in.readString();
            this.subject_name = in.readString();
            this.news = new ArrayList<NewsModel>();
            in.readList(this.news, NewsModel.class.getClassLoader());
            this.template_url = in.readString();
            this.template_url_sign = in.readString();
            this.template_data = in.readString();
            this.template_common_url = in.readString();
            this.template_common_url_sign = in.readString();
            this.url = in.readString();
            this.footer = in.readString();
            this.readers = in.readString();
            this.c_id = in.readString();
            this.name = in.readString();
            this.icon = in.readString();
            this.summary = in.readString();
            this.is_my = in.readInt();
            this.news_num = in.readInt();
            this.sub_num = in.readInt();
            this.auth_type = in.readInt();
            this.identify_status = in.readInt();
            this.is_sub = in.readByte() != 0;
            this.relation_id = in.readString();
            this.tdate = in.readLong();
            this.group_label = in.readString();
            this.deleteFlag = in.readByte() != 0;
            this.moreFlag = in.readByte() != 0;
            this.share = in.readString();
            this.wemedia_url = in.readString();
            this.extra = in.readString();
            this.group_index = in.readString();
            this.group_index_color = in.readString();
            this.watches = in.readString();
            this.watches_str = in.readString();
            this.live_stats = in.readInt();
            this.status = in.readString();
            this.news_data = in.readString();
            this.meta = in.readString();
            this.more = in.readParcelable(NewsModel.class.getClassLoader());
        }

        public static final Creator<NewsData> CREATOR = new Creator<NewsData>() {
            @Override
            public NewsData createFromParcel(Parcel source) {
                return new NewsData(source);
            }

            @Override
            public NewsData[] newArray(int size) {
                return new NewsData[size];
            }
        };
    }
}
