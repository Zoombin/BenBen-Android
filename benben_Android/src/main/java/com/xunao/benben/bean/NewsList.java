package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class NewsList extends BaseBean {

	private ArrayList<News> mNewsList;

	public ArrayList<News> getmNewsList() {
		return mNewsList;
	}

	public void setmNewsList(ArrayList<News> mNewsList) {
		this.mNewsList = mNewsList;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("news");
		if (optJSONArray != null && optJSONArray.length() > 0) {
			int length = optJSONArray.length();
			mNewsList = new ArrayList<News>();
			News news;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					news = new News();
					news.parseJSON(optJSONObject);
					mNewsList.add(news);
				}
			}
		}

		return this;
	}

}
