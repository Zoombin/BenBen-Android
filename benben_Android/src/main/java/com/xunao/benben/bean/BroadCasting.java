package com.xunao.benben.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import java.util.ArrayList;

public class BroadCasting extends BaseBean<BroadCasting> {
	private String id;
	private String createdTime;
	private String content;
    private String contentdetail;
	private String description;
	private String shortDescription;
	private String league_id;
	private String phone;
	private String is_benben;
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private String Images;// 使用||分割
    private String Thumb;// 使用||分割

    private int singImageW = 150;
    private int singImageH = 150;
    private int type=1;
    private int statu;// 音频的状态

    @Transient
    public static final int DOWNLOADED = 0;
    @Transient
    public static final int UNDOWNLOAD = 1;
    @Transient
    public static final int DOWNLOADBAD = 2;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BroadCasting parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		createdTime = jsonObj.optString("created_time");
		content = jsonObj.optString("content");
        contentdetail = jsonObj.optString("contentdetail");
		description = jsonObj.optString("description");
		shortDescription = jsonObj.optString("short_description");
		league_id = jsonObj.optString("league_id");
		phone = jsonObj.optString("phone");
		is_benben = jsonObj.optString("is_benben");
        statu = UNDOWNLOAD;

//        JSONArray ImagesArray = jsonObj.optJSONArray("Images");
//        JSONArray ThumbArray = jsonObj.optJSONArray("Thumb");
//        if (ImagesArray != null) {
//            int length = ImagesArray.length();
//            for (int i = 0; i < length; i++) {
//                if (Images != null) {
//                    Images += "^" + ImagesArray.optString(i);
//                } else {
//                    Images = ImagesArray.optString(i);
//                }
//            }
//        }
//        if (ThumbArray != null) {
//            int length = ThumbArray.length();
//            for (int i = 0; i < length; i++) {
//                if (Thumb != null) {
//                    Thumb += "^" + ThumbArray.optString(i);
//                } else {
//                    Thumb = ThumbArray.optString(i);
//                }
//            }
//        }
//
//
        JSONArray array = jsonObj.optJSONArray("attachment");
        if (array != null && array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                Attachment attachment = new Attachment();
                attachment.parseJSON(jsonObject);
                if (Images != null) {
                    Images += "^" + attachment.getAttachment();
                } else {
                    Images = attachment.getAttachment();
                }
                if (Thumb != null) {
                    Thumb += "^" + attachment.getThumb();
                } else {
                    Thumb = attachment.getThumb();
                }
                type = attachment.getType();
                if(type==1){
                    if(attachment.getWidth()!=null && !attachment.getWidth().equals("")){
                        singImageW = Integer.parseInt(attachment.getWidth());
                    }
                    if(attachment.getHeight()!=null && !attachment.getHeight().equals("")){
                        singImageH = Integer.parseInt(attachment.getHeight());
                    }


                }

                attachments.add(attachment);
            }
        }
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLeague_id() {
		return league_id;
	}

	public void setLeague_id(String league_id) {
		this.league_id = league_id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIs_benben() {
		return is_benben;
	}

	public void setIs_benben(String is_benben) {
		this.is_benben = is_benben;
	}

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
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

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public String getContentdetail() {
        return contentdetail;
    }

    public void setContentdetail(String contentdetail) {
        this.contentdetail = contentdetail;
    }
}
