package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

import java.io.Serializable;

public class ChatImages extends BaseBean<ChatImages> implements Serializable{

	private String msgId;
	private String thumbnailPath;
	private String filePath;
	private String remotePath;
    private String secret;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
	public JSONObject toJSON() {
		return null;
	}


    @Override
	public ChatImages parseJSON(JSONObject jsonObj) throws NetRequestException {

        msgId = jsonObj.optString("msgId");
        thumbnailPath = jsonObj.optString("thumbnailPath");
        filePath = jsonObj.optString("filePath");
        remotePath = jsonObj.optString("remotePath");

		return this;
	}

}
