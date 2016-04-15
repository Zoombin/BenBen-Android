package com.xunao.benben.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Bill;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天地理位置搜索
 */
public class ActivityFindMapAddress extends BaseActivity implements PullToRefreshBase.OnLastItemVisibleListener{
	private myAdapter adapter;
	private ListView lv_city;
    private DataAdapter dataAdapter;
    private PullToRefreshListView lv_poi;
	private String searchKey = "";
    private TextView tv_hint;
	private LinearLayout ll_seach_icon;
	private EditText search_edittext;
    private TextView tv_content;
	private LinearLayout no_data;
	private ImageView iv_search_content_delect;
//    private PoiSearch mPoiSearch;
    private String city="";
    private List<Map<String,String>> cityLists = new ArrayList<>();
    private List<Map<String,String>> poiLists = new ArrayList<>();
    private String ak = "El8BrzKubSWSx3a5RGyGpHbGEaH2GXRI";
    private String mCode = "A1:F7:78:66:26:1A:48:02:5C:EF:78:0A:7F:D8:69:AC:94:5F:4A:FD;com.xunao.benben";

    private int currentPage = 0;
    private int total = 0;
    private boolean isAllSearch = true;
    private int pagNum = 0;
    private String searchCity = "";


	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_find_map_address);
//        mPoiSearch = PoiSearch.newInstance();
//        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
	}


	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("位置查找", "", "",
				R.drawable.icon_com_title_left, 0);

		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		search_edittext = (EditText) findViewById(R.id.search_edittext);
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText("搜索");
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		no_data = (LinearLayout) findViewById(R.id.no_data);

        lv_city = (ListView) findViewById(R.id.lv_city);
		adapter = new myAdapter();
        lv_city.setAdapter(adapter);

        lv_poi = (PullToRefreshListView) findViewById(R.id.lv_poi);
        dataAdapter = new DataAdapter();
        lv_poi.setAdapter(dataAdapter);
        lv_poi.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
//        lv_poi.setOnRefreshListener(this);
        lv_poi.setOnLastItemVisibleListener(this);
        lv_poi.setVisibility(View.GONE);
		no_data.setVisibility(View.VISIBLE);
		iv_search_content_delect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_edittext.setText("");
			}
		});

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
        city = getIntent().getStringExtra("city");
	}


	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

        search_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    // 显示键盘
                    imm.showSoftInput(search_edittext, 0);
                }
            }
        });

        search_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (arg0.length() > 0) {
                    ll_seach_icon.setVisibility(View.GONE);
                    iv_search_content_delect.setVisibility(View.VISIBLE);
                } else {
                    ll_seach_icon.setVisibility(View.VISIBLE);
                    iv_search_content_delect.setVisibility(View.GONE);
                    searchKey = "";
                    // new Handler().postDelayed(new Runnable() {
                    // @Override
                    // public void run() {
                    // listView.setRefreshing(true);
                    // }
                    // }, 200);
                }
            }
        });

        search_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int actionId,
                                          KeyEvent arg2) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) search_edittext.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(mContext.getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    // 更新关键字
                    searchKey = search_edittext.getText().toString().trim();
                    if(searchKey.equals("")){
                        ToastUtils.Infotoast(mContext,"请输入查询内容");
                    }else {
//                        showLoding("");
//                        mPoiSearch.searchInCity((new PoiCitySearchOption())
//                                .city(city)
//                                .keyword(searchKey)
//                                .pageNum(10));
                        isAllSearch = true;
                        setLoadMore(false);
                        currentPage = 0;
                        InteNetUtils.getInstance(mContext).getMapAddress(searchKey,city,ak, mCode,currentPage,mRequestCallBack);
                    }
                    return true;
                }
                return false;
            }
        });



		iv_search_content_delect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_edittext.setText("");
			}
		});


	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
        lv_poi.onRefreshComplete();
        isMoreData = false;
        if(jsonObject.has("results")){
            try {
                total = jsonObject.optInt("total");
                pagNum = total/10+(total%10==0?0:1)-1;
                if(isAllSearch) {
                    tv_hint.setText("查找到" + total + "条结果");
                }else{
                    tv_hint.setText("在'"+searchCity+"'查找到" + total + "条结果");
                }

                JSONArray jsonArray = jsonObject.getJSONArray("results");
                if(jsonArray!=null && jsonArray.length()>0){
                    no_data.setVisibility(View.GONE);
                    boolean isCity = false;
                    JSONObject firstObject = jsonArray.getJSONObject(0);
                    if(firstObject.has("num")){
                        isCity = true;
                        cityLists.clear();
                        lv_city.setVisibility(View.VISIBLE);
                        lv_poi.setVisibility(View.GONE);
                    }else{
                        isCity = false;
                        if(!isLoadMore){
                            poiLists.clear();
                        }
                        lv_city.setVisibility(View.GONE);
                        lv_poi.setVisibility(View.VISIBLE);
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        if(isCity){
                            Map<String,String> map = new HashMap<>();
                            map.put("name",object.optString("name"));
                            map.put("num",object.optInt("num")+"");
                            cityLists.add(map);
                        }else{
                            Map<String,String> map = new HashMap<>();
                            map.put("name",object.optString("name"));
                            if(object.has("address")) {
                                map.put("address", object.optString("address"));
                            }else{
                                map.put("address","");
                            }
                            JSONObject location = object.getJSONObject("location");
                            map.put("latitude",location.optDouble("lat",0)+"");
                            map.put("longitude",location.optDouble("lng",0)+"");
                            poiLists.add(map);
                        }
                    }
                    if(isCity) {
                        adapter.notifyDataSetChanged();
                    }else {
                        dataAdapter.notifyDataSetChanged();
                    }
                }else{
                    no_data.setVisibility(View.VISIBLE);
                    lv_city.setVisibility(View.GONE);
                    lv_poi.setVisibility(View.GONE);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("message"));
        }

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
        lv_poi.onRefreshComplete();
        ToastUtils.Infotoast(mContext,"获取信息失败");
	}

    @Override
    public void onLastItemVisible() {
        if(currentPage >= pagNum){
//            ToastUtils.Infotoast(mContext,"无更多数据");
        }else{
            setLoadMore(true);
            currentPage++;
            if(isAllSearch) {
                InteNetUtils.getInstance(mContext).getMapAddress(searchKey, city, ak, mCode, currentPage, mRequestCallBack);
            }else{
                InteNetUtils.getInstance(mContext).getMapAddress(searchKey, searchCity, ak, mCode, currentPage, mRequestCallBack);
            }
        }
    }



    class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cityLists.size();
		}

		@Override
		public Object getItem(int arg0) {
			return cityLists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final Map<String,String> map = cityLists.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.item_map_address, null);
			}
            TextView tv_name = ViewHolderUtil.get(convertView,
                    R.id.tv_name);
			TextView tv_address = ViewHolderUtil.get(convertView,
					R.id.tv_address);
            tv_name.setText(map.get("name"));
            tv_address.setText("在此城市搜索");

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isAllSearch = false;
                    setLoadMore(false);
                    currentPage = 0;
                    searchCity = map.get("name");
                    InteNetUtils.getInstance(mContext).getMapAddress(searchKey,searchCity,ak, mCode,currentPage,mRequestCallBack);

//                    Intent intent = new Intent();
//                    intent.putExtra("latitude",poiInfo.location.latitude);
//                    intent.putExtra("longitude",poiInfo.location.longitude);
//                    setResult(RESULT_OK,intent);
//                    AnimFinsh();
                }
            });

			return convertView;
		}
	}


    class DataAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return poiLists.size() + 1;
        }

        @Override
        public Object getItem(int arg0) {
            return poiLists.get(arg0);
        }

        @Override
        public int getItemViewType(int position) {

            return position <= poiLists.size() - 1 ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_map_address, null);
                }
                TextView tv_name = ViewHolderUtil.get(convertView,
                        R.id.tv_name);
                TextView tv_address = ViewHolderUtil.get(convertView,
                        R.id.tv_address);
                final Map<String,String> map = poiLists.get(position);
                tv_name.setText(map.get("name"));
                tv_address.setText(map.get("address"));

                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("latitude", Double.parseDouble(map.get("latitude")));
                        intent.putExtra("longitude", Double.parseDouble(map.get("longitude")));
                        setResult(RESULT_OK, intent);
                        AnimFinsh();
                    }
                });

            } else {
                if (isMoreData) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_load_more, null);
                    LinearLayout load_more = ViewHolderUtil.get(convertView,
                            R.id.load_more);
                    if (enterNum) {
                        load_more.setVisibility(View.VISIBLE);
                    } else {
                        load_more.setVisibility(View.GONE);
                    }
                } else {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_no_load_more, null);
                    convertView.setVisibility(View.GONE);
                }
            }

            return convertView;
        }


    }

//    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
//        public void onGetPoiResult(PoiResult result){
//            //获取POI检索结果
//            dissLoding();
//            if(result.getAllPoi()==null){
//                no_data.setVisibility(View.VISIBLE);
//                lv_city.setVisibility(View.GONE);
//            }else {
//                no_data.setVisibility(View.GONE);
//                poiInfos = result.getAllPoi();
//                adapter.notifyDataSetChanged();
//                lv_city.setVisibility(View.VISIBLE);
//            }
//        }
//        public void onGetPoiDetailResult(PoiDetailResult result){
//            //获取Place详情页检索结果
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mPoiSearch.destroy();
    }
}
