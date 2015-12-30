package com.xunao.benben.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.base.Error;
import com.xunao.benben.exception.NetRequestException;

public class AndriodVersion extends BaseBean<AndriodVersion> {

	private int AndriodVersion;// 版本号
	private String updateContent;// 版本更新内容
	private String url;// 版本更新内容

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUpdateContent() {
		return updateContent;
	}

	public void setUpdateContent(String updateContent) {
		this.updateContent = updateContent;
	}

	public int getAndriodVersion() {
		return AndriodVersion;
	}

	public void setAndriodVersion(int andriodVersion) {
		AndriodVersion = andriodVersion;
	}

	@Override
	public JSONObject toJSON() {

		return null;
	}

	@Override
	public AndriodVersion parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		JSONObject optJSONObject = jsonObj.optJSONObject("version");
		if (optJSONObject != null) {
			AndriodVersion = optJSONObject.optInt("version");
			updateContent = optJSONObject.optString("info");
			url = optJSONObject.optString("path");
		}
		return this;
	}

}
