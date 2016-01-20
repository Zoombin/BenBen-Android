package com.xunao.test.bean;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ltf on 2015/12/4.
 */
public class Album extends BaseBean {
    private int id;
    private int member_id;
    private String title;
    private long time;
    private int poster_num;
    private String small_poster_cover;
    private String poster_cover;
    private int is_close;

    private ArrayList<AlbumPic> albumPics = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPoster_num() {
        return poster_num;
    }

    public void setPoster_num(int poster_num) {
        this.poster_num = poster_num;
    }

    public String getPoster_cover() {
        return poster_cover;
    }

    public void setPoster_cover(String poster_cover) {
        this.poster_cover = poster_cover;
    }

    public int getIs_close() {
        return is_close;
    }

    public void setIs_close(int is_close) {
        this.is_close = is_close;
    }

    public String getSmall_poster_cover() {
        return small_poster_cover;
    }

    public void setSmall_poster_cover(String small_poster_cover) {
        this.small_poster_cover = small_poster_cover;
    }

    public ArrayList<AlbumPic> getAlbumPics() {
        return albumPics;
    }

    public void setAlbumPics(ArrayList<AlbumPic> albumPics) {
        this.albumPics = albumPics;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        id = jsonObj.optInt("id");
        member_id = jsonObj.optInt("member_id");
        title = jsonObj.optString("title");
        poster_num = jsonObj.optInt("poster_num");
        time = jsonObj.optLong("time");
        small_poster_cover = jsonObj.optString("small_poster_cover");
        poster_cover = jsonObj.optString("poster_cover");
        is_close = jsonObj.optInt("is_close");

        JSONArray picArray = jsonObj.optJSONArray("pic");
        if (picArray != null && picArray.length() > 0) {
            for (int i = 0; i < picArray.length(); i++) {
                JSONObject jsonObject = picArray.optJSONObject(i);
                AlbumPic albumPic = new AlbumPic();
                albumPic.parseJSON(jsonObject);
                albumPics.add(albumPic);
            }
        }
        return this;
    }
}
