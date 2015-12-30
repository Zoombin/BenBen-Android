package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class NumberTrainCollectionList extends BaseBean{
	private ArrayList<NUmberTrainCollection> numberTrains;
	
	public ArrayList<NUmberTrainCollection> getNumberTrains() {
		return numberTrains;
	}

	public void setNumberTrains(ArrayList<NUmberTrainCollection> numberTrains) {
		this.numberTrains = numberTrains;
	}

	@Override
	public JSONObject toJSON() {
		
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("number_info");
		
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			numberTrains = new ArrayList<NUmberTrainCollection>();
			NUmberTrainCollection numberTrain;
			
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					numberTrain = new NUmberTrainCollection();
					numberTrain.parseJSON(optJSONObject);
					
					numberTrains.add(numberTrain);
				}
			}

		}

		return this;
	}

}
