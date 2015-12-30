package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class SmallMakeSingleData extends BaseBean {

	@Transient
	public static final int DOWNLOADED = 0;
	@Transient
	public static final int UNDOWNLOAD = 1;
	@Transient
	public static final int DOWNLOADBAD = 2;

	@com.lidroid.xutils.db.annotation.Id
	private String id;
	private String MemberId;
	private String Name;
	private String Poster;
	private int type;
	private int laud;
	private String Description;
	private int Views;
	private String CreatedTime;
	private String Images;// 使用||分割
	private String Thumb;// 使用||分割

	private int singImageW = 150;
	private int singImageH = 150;

	private int statu;// 音频的状态

	private ArrayList<SmallMakeDataComment> mFriendDataComments;

	public int getSingImageW() {
		return singImageW;
	}

	public void setSingImageW(int singImageW) {
		this.singImageW = singImageW;
	}

	public int getSingImageH() {
		return singImageH;
	}

	public void setSingImageH(int singImageH) {
		this.singImageH = singImageH;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLaud() {
		return laud;
	}

	public void setLaud(int laud) {
		this.laud = laud;
	}

	public String getMemberId() {
		return MemberId;
	}

	public void setMemberId(String memberId) {
		MemberId = memberId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPoster() {
		return Poster;
	}

	public void setPoster(String poster) {
		Poster = poster;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public int getViews() {
		return Views;
	}

	public void setViews(int views) {
		Views = views;
	}

	public int getStatu() {
		return statu;
	}

	public void setStatu(int statu) {
		this.statu = statu;
	}

	public String getCreatedTime() {
		return CreatedTime;
	}

	public void setCreatedTime(String createdTime) {
		CreatedTime = createdTime;
	}

	public String getImages() {
		return Images;
	}

	public void setImages(String images) {
		Images = images;
	}

	public String getThumb() {
		return Thumb;
	}

	public void setThumb(String thumb) {
		Thumb = thumb;
	}

	public ArrayList<SmallMakeDataComment> getmFriendDataComments() {
		return mFriendDataComments;
	}

	public void setmFriendDataComments(
			ArrayList<SmallMakeDataComment> mFriendDataComments) {
		this.mFriendDataComments = mFriendDataComments;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	// Comment
	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);
		JSONObject optJSONObject = jsonObj.optJSONObject("number_info");

		MemberId = optJSONObject.optString("MemberId");
		id = optJSONObject.optString("Id");
		Name = optJSONObject.optString("Name");
		Poster = optJSONObject.optString("Poster");
		laud = optJSONObject.optInt("Laud");
		type = optJSONObject.optInt("Type");
		Description = optJSONObject.optString("Description");
		CreatedTime = optJSONObject.optString("CreatedTime");
		Views = optJSONObject.optInt("Views");
		singImageW = optJSONObject.optInt("Width");
		singImageH = optJSONObject.optInt("Height");
		statu = UNDOWNLOAD;
		JSONArray ImagesArray = optJSONObject.optJSONArray("Images");
		JSONArray ThumbArray = optJSONObject.optJSONArray("Thumb");
		JSONArray CommentArray = optJSONObject.optJSONArray("Comment");

		if (ImagesArray != null) {
			int length = ImagesArray.length();
			for (int i = 0; i < length; i++) {
				if (Images != null) {
					Images += "^" + ImagesArray.optString(i);
				} else {
					Images = ImagesArray.optString(i);
				}
			}
		}
		if (ThumbArray != null) {
			int length = ThumbArray.length();
			for (int i = 0; i < length; i++) {
				if (Thumb != null) {
					Thumb += "^" + ThumbArray.optString(i);
				} else {
					Thumb = ThumbArray.optString(i);
				}
			}
		}
		if (CommentArray != null) {
			int length = CommentArray.length();
			mFriendDataComments = new ArrayList<SmallMakeDataComment>();
			SmallMakeDataComment comment;
			for (int i = 0; i < length; i++) {
				comment = new SmallMakeDataComment();
				comment.parseJSON(CommentArray.optJSONObject(i));
				mFriendDataComments.add(comment);
			}
		}

		return this;
	}
}
