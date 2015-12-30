package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MySmallCreationData extends BaseBean {

	@Transient
	public static final int DOWNLOADED = 0;
	@Transient
	public static final int UNDOWNLOAD = 1;
	@Transient
	public static final int DOWNLOADBAD = 2;

	@Id @NoAutoIncrement
	private int id;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int MemberId;
	private String Name;
	private String Poster;
	private int type;
	private int laud;
	private String Description;
	private int Views;
	private String CreatedTime;
	private String Images;// 使用||分割
	private String Thumb;// 使用||分割

	private int statu;// 音频的状态
	private ArrayList<MySmallCreationComment> mySmallMakeCreationComments;

	private int singImageW = 150;
	private int singImageH = 150;
	private String laudList;
	
	private String status;

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

	public int getMemberId() {
		return MemberId;
	}

	public void setMemberId(int memberId) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<MySmallCreationComment> getMySmallMakeCreationComments() {
		return mySmallMakeCreationComments;
	}

	public void setMySmallMakeCreationComments(
			ArrayList<MySmallCreationComment> mySmallMakeCreationComments) {
		this.mySmallMakeCreationComments = mySmallMakeCreationComments;
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
		MemberId = jsonObj.optInt("MemberId");
		id = jsonObj.optInt("Id");
		Name = jsonObj.optString("Name");
		Poster = jsonObj.optString("Poster");
		laud = jsonObj.optInt("Laud");
		type = jsonObj.optInt("Type");
		Description = jsonObj.optString("Description");
		CreatedTime = jsonObj.optString("CreatedTime");
		Views = jsonObj.optInt("Views");
		singImageW = jsonObj.optInt("Width");
		singImageH = jsonObj.optInt("Height");
		laudList = jsonObj.optString("laud_list");
		status = jsonObj.optString("status");
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
			mySmallMakeCreationComments = new ArrayList<MySmallCreationComment>();
			MySmallCreationComment comment;
			for (int i = 0; i < length; i++) {
				comment = new MySmallCreationComment();
				comment.parseJSON(CommentArray.optJSONObject(i));
				mySmallMakeCreationComments.add(comment);
			}
		}

		return this;
	}
}
