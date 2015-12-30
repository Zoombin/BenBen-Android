package com.xunao.benben.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.utils.CommonUtils;

import java.util.ArrayList;

public class NumberTrainDetail extends BaseBean {
	private String id;
	private String poster;
	private String name;
	private String industry;
	private String address;
	private String tag;
	private String phone;
	private String description;
	private String views;
	private String collection;
	private String area;
	private String shortName;
	private String numberProvince;
	private String numberCity;
	private String numberArea;
	private String kil;
	private String numberStreet;
	private double numberLat;
	private double numberLng;
	private String industryId;
	private int is_close;
	private String telPhone;
	private String collecitonNumber;
	private String huanxinUsername;
	private String haveRight;
	private String telNumber;
	private String distance_kilometers;
	private int numberStatus;
    private String shop;

    private int auth_status;
    private int no_auth;
    private int is_promotion;
    private int is_overtime;
    private long vip_time;
    private int type;
    private int vip_type;
    private int shopnum;
    private int auth_grade;
    private int rank;

    @Transient
    private ArrayList<Promotion> promotions = new ArrayList<Promotion>();
    @Transient
    private ArrayList<NumberTrainPoster> numberTrainPosters = new ArrayList<NumberTrainPoster>();

	public int getIs_close() {
		return is_close;
	}

	public void setIs_close(int is_close) {
		this.is_close = is_close;
	}

	public String getIndustryId() {
		return industryId;
	}

	public String getDistance_kilometers() {
		return distance_kilometers;
	}

	public void setDistance_kilometers(String distance_kilometers) {
		this.distance_kilometers = distance_kilometers;
	}

	public void setIndustryId(String industryId) {
		this.industryId = industryId;
	}

	public double getNumberLat() {
		return numberLat;
	}

	public String getKil() {
		return kil;
	}

	public void setKil(String kil) {
		this.kil = kil;
	}

	public void setNumberLat(double numberLat) {
		this.numberLat = numberLat;
	}

	public double getNumberLng() {
		return numberLng;
	}

	public void setNumberLng(double numberLng) {
		this.numberLng = numberLng;
	}

	public String getNumberProvince() {
		return numberProvince;
	}

	public void setNumberProvince(String numberProvince) {
		this.numberProvince = numberProvince;
	}

	public String getNumberCity() {
		return numberCity;
	}

	public void setNumberCity(String numberCity) {
		this.numberCity = numberCity;
	}

	public String getNumberArea() {
		return numberArea;
	}

	public void setNumberArea(String numberArea) {
		this.numberArea = numberArea;
	}

	public String getNumberStreet() {
		return numberStreet;
	}

	public void setNumberStreet(String numberStreet) {
		this.numberStreet = numberStreet;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public String getTelPhone() {
		return telPhone;
	}

	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCollecitonNumber() {
		return collecitonNumber;
	}

	public void setCollecitonNumber(String collecitonNumber) {
		this.collecitonNumber = collecitonNumber;
	}

	public String getHuanxinUsername() {
		return huanxinUsername;
	}

	public void setHuanxinUsername(String huanxinUsername) {
		this.huanxinUsername = huanxinUsername;
	}

	public String getHaveRight() {
		return haveRight;
	}

	public void setHaveRight(String haveRight) {
		this.haveRight = haveRight;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public int getNumberStatus() {
		return numberStatus;
	}

	public void setNumberStatus(int numberStatus) {
		this.numberStatus = numberStatus;
	}

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public int getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(int auth_status) {
        this.auth_status = auth_status;
    }

    public int getNo_auth() {
        return no_auth;
    }

    public void setNo_auth(int no_auth) {
        this.no_auth = no_auth;
    }

    public int getIs_promotion() {
        return is_promotion;
    }

    public void setIs_promotion(int is_promotion) {
        this.is_promotion = is_promotion;
    }

    public ArrayList<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<Promotion> promotions) {
        this.promotions = promotions;
    }

    public int getIs_overtime() {
        return is_overtime;
    }

    public void setIs_overtime(int is_overtime) {
        this.is_overtime = is_overtime;
    }

    public long getVip_time() {
        return vip_time;
    }

    public void setVip_time(long vip_time) {
        this.vip_time = vip_time;
    }

    public ArrayList<NumberTrainPoster> getNumberTrainPosters() {
        return numberTrainPosters;
    }

    public void setNumberTrainPosters(ArrayList<NumberTrainPoster> numberTrainPosters) {
        this.numberTrainPosters = numberTrainPosters;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVip_type() {
        return vip_type;
    }

    public void setVip_type(int vip_type) {
        this.vip_type = vip_type;
    }

    public int getShopnum() {
        return shopnum;
    }

    public void setShopnum(int shopnum) {
        this.shopnum = shopnum;
    }

    public int getAuth_grade() {
        return auth_grade;
    }

    public void setAuth_grade(int auth_grade) {
        this.auth_grade = auth_grade;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
	public JSONObject toJSON() {

		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		if (jsonObj != null) {

			id = jsonObj.optString("NumberId");
			poster = jsonObj.optString("NumberPoster");
			name = jsonObj.optString("NumberName");
			industry = jsonObj.optString("NumberIndustry");
			address = jsonObj.optString("NumberAddress");
			phone = jsonObj.optString("NumberPhone");
			tag = jsonObj.optString("Numbertag");
			area = jsonObj.optString("NumberPro_city");
			description = jsonObj.optString("NumberDescription");
			views = jsonObj.optString("NumberViews");
			collection = jsonObj.optString("NumberCollection");
			shortName = jsonObj.optString("NumberShortName");
			if (CommonUtils.isEmpty(shortName))
				shortName = jsonObj.optString("NumberShort_name");
			numberProvince = jsonObj.optString("NumberProvince");
			numberCity = jsonObj.optString("NumberCity");
			numberArea = jsonObj.optString("NumberArea");
			numberStreet = jsonObj.optString("NumberStreet");
			numberLat = jsonObj.optDouble("NumberLat");
			numberLng = jsonObj.optDouble("NumberLng");
			is_close = jsonObj.optInt("is_close");
			industryId = jsonObj.optString("NumberInd");
			telPhone = jsonObj.optString("NumberTelephone");
			collecitonNumber = jsonObj.optString("CollectionNumber");
			huanxinUsername = jsonObj.optString("huanxin_username");
			haveRight = jsonObj.optString("HaveRight");
			telNumber = jsonObj.optString("NumberTel");
			kil = jsonObj.optString("distance_kilometers");
			numberStatus = jsonObj.optInt("NumberStatus");
            shop = jsonObj.optString("shop");
		}
		return this;
	}

    public Object parseJSONMyNumberTrain(JSONObject jsonObj) throws NetRequestException {
        if (jsonObj != null) {

            id = jsonObj.optString("NumberId");
            poster = jsonObj.optString("NumberPoster");
            name = jsonObj.optString("NumberName");
            industry = jsonObj.optString("NumberIndustry");
            address = jsonObj.optString("NumberAddress");
            phone = jsonObj.optString("NumberPhone");
            tag = jsonObj.optString("Numbertag");
            area = jsonObj.optString("NumberPro_city");
            description = jsonObj.optString("NumberDescription");
            views = jsonObj.optString("NumberViews");
            collection = jsonObj.optString("NumberCollection");
            shortName = jsonObj.optString("NumberShortName");
            if (CommonUtils.isEmpty(shortName))
                shortName = jsonObj.optString("NumberShort_name");
            numberProvince = jsonObj.optString("NumberProvince");
            numberCity = jsonObj.optString("NumberCity");
            numberArea = jsonObj.optString("NumberArea");
            numberStreet = jsonObj.optString("NumberStreet");
            numberLat = jsonObj.optDouble("NumberLat");
            numberLng = jsonObj.optDouble("NumberLng");
            is_close = jsonObj.optInt("is_close");
            industryId = jsonObj.optString("NumberInd");
            telPhone = jsonObj.optString("NumberTelephone");
            collecitonNumber = jsonObj.optString("CollectionNumber");
            huanxinUsername = jsonObj.optString("huanxin_username");
            haveRight = jsonObj.optString("HaveRight");
            telNumber = jsonObj.optString("NumberTel");
            kil = jsonObj.optString("distance_kilometers");
            numberStatus = jsonObj.optInt("NumberStatus");
            shop = jsonObj.optString("shop");

            auth_status = jsonObj.optInt("auth_status");
            no_auth = jsonObj.optInt("no_auth");
            is_promotion = jsonObj.optInt("is_promotion");
            is_overtime = jsonObj.optInt("is_overtime");
            vip_time = jsonObj.optLong("vip_time");
            type = jsonObj.optInt("type");
            vip_type = jsonObj.optInt("vip_type");
            shopnum = jsonObj.optInt("shopnum");
            auth_grade = jsonObj.optInt("auth_grade");
            rank = jsonObj.optInt("rank");

            JSONArray phoneArray = jsonObj.optJSONArray("promotion");
            if (phoneArray != null && phoneArray.length() > 0) {
                for (int i = 0; i < phoneArray.length(); i++) {
                    JSONObject jsonObject = phoneArray.optJSONObject(i);
                    Promotion promotion = new Promotion();
                    promotion.parseJSON(jsonObject);
                    promotions.add(promotion);
                }
            }

            JSONArray allPosterArray = jsonObj.optJSONArray("allposter");
            if (allPosterArray != null && allPosterArray.length() > 0) {
                for (int i = 0; i < allPosterArray.length(); i++) {
                    JSONObject jsonObject = allPosterArray.optJSONObject(i);
                    NumberTrainPoster numberTrainPoster = new NumberTrainPoster();
                    numberTrainPoster.parseJSON(jsonObject);
                    numberTrainPosters.add(numberTrainPoster);
                }
            }
        }
        return this;
    }

}
