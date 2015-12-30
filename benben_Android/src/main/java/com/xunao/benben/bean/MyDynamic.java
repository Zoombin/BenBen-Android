package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MyDynamic extends BaseBean {

	@com.lidroid.xutils.db.annotation.Id @NoAutoIncrement
	private int Id;
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
	private int singImageW=150;
	private int singImageH=150;
	private String status;
	
	private String laudList;

	private ArrayList<MyDynamicDataComment> mFriendDataComments;


	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

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

	public ArrayList<MyDynamicDataComment> getmFriendDataComments() {
		return mFriendDataComments;
	}

	public String getLaudList() {
		return laudList;
	}

	public void setLaudList(String laudList) {
		this.laudList = laudList;
	}

	public void setmFriendDataComments(
			ArrayList<MyDynamicDataComment> mFriendDataComments) {
		this.mFriendDataComments = mFriendDataComments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	// Comment
	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		MemberId = jsonObj.optString("MemberId");
		Id = jsonObj.optInt("Id");
		Name = jsonObj.optString("Name");
		Poster = jsonObj.optString("Poster");
		laud = jsonObj.optInt("Laud");
		type = jsonObj.optInt("type");
		Description = jsonObj.optString("Description");
		CreatedTime = jsonObj.optString("CreatedTime");
		singImageW = jsonObj.optInt("Width");
		singImageH = jsonObj.optInt("Height");
		Views = jsonObj.optInt("Views");
		
		status = jsonObj.optString("status");
		laudList = jsonObj.optString("laud_list");
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
			mFriendDataComments = new ArrayList<MyDynamicDataComment>();
			MyDynamicDataComment comment;
			for (int i = 0; i < length; i++) {
				comment = new MyDynamicDataComment();
				comment.parseJSON(CommentArray.optJSONObject(i));
				mFriendDataComments.add(comment);
			}
		}

		return this;
	}
}
