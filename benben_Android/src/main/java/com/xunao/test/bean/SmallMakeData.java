package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class SmallMakeData extends BaseBean {

	@Transient
	public static final int DOWNLOADED = 0;
	@Transient
	public static final int UNDOWNLOAD = 1;
	@Transient
	public static final int DOWNLOADBAD = 2;

	@Id @NoAutoIncrement
	private int id;
	private String MemberId;
	private String Name;
	private String Poster;
	private int type;
	private int laud;
	private int Attention;
	private String Description;
	private int Views;
	private String CreatedTime;
	private String Images;// 使用||分割
	private String Thumb;// 使用||分割

	private int singImageW = 150;
	private int singImageH = 150;
	private String laudList;
	
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

	public int getAttention() {
		return Attention;
	}

	public void setAttention(int attention) {
		Attention = attention;
	}

	public void setSingImageH(int singImageH) {
		this.singImageH = singImageH;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public String getLaudList() {
		return laudList;
	}

	public void setLaudList(String laudList) {
		this.laudList = laudList;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	// Comment
	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		MemberId = jsonObj.optString("MemberId");
		id = jsonObj.optInt("Id");
		Name = jsonObj.optString("Name");
		Poster = jsonObj.optString("Poster");
		laud = jsonObj.optInt("Laud");
		Attention = jsonObj.optInt("Attention");
		type = jsonObj.optInt("Type");
		Description = jsonObj.optString("Description");
		CreatedTime = jsonObj.optString("CreatedTime");
		Views = jsonObj.optInt("Views");
		singImageW = jsonObj.optInt("Width");
		singImageH = jsonObj.optInt("Height");
		laudList = jsonObj.optString("laud_list");
		statu = UNDOWNLOAD;
		JSONArray ImagesArray = jsonObj.optJSONArray("Images");
		JSONArray ThumbArray = jsonObj.optJSONArray("Thumb");
		JSONArray CommentArray = jsonObj.optJSONArray("Comment");

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
