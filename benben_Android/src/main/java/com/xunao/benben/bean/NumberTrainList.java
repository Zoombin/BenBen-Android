package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class NumberTrainList extends BaseBean{
	private ArrayList<NumberTrain> numberTrains;
	
	public ArrayList<NumberTrain> getNumberTrains() {
		return numberTrains;
	}

	public void setNumberTrains(ArrayList<NumberTrain> numberTrains) {
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
			numberTrains = new ArrayList<NumberTrain>();
			NumberTrain numberTrain;
			
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					numberTrain = new NumberTrain();
					numberTrain.parseJSON(optJSONObject);
					
					numberTrains.add(numberTrain);
				}
			}

		}

		return this;
	}

}
