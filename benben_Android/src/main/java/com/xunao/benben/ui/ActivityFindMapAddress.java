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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityFindMapAddress extends BaseActivity {
	private myAdapter adapter;
	private ListView listview;
	private String searchKey = "";
	private LinearLayout ll_seach_icon;
	private EditText search_edittext;
    private TextView tv_content;
	private LinearLayout no_data;
	private ImageView iv_search_content_delect;
    private PoiSearch mPoiSearch;
    private String city="";
    private List<PoiInfo> poiInfos = new ArrayList<>();

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_find_map_address);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
	}


	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("位置查找", "", "",
				R.drawable.icon_com_title_left, 0);

		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		search_edittext = (EditText) findViewById(R.id.search_edittext);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText("搜索");
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		no_data = (LinearLayout) findViewById(R.id.no_data);

		listview = (ListView) findViewById(R.id.listview);
		adapter = new myAdapter();
		listview.setAdapter(adapter);

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
                        showLoding("");
                        Log.d("ltf","city========"+city+"=====searchKey==="+searchKey);
                        mPoiSearch.searchInCity((new PoiCitySearchOption())
                                .city(city)
                                .keyword(searchKey)
                                .pageNum(10));
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

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return poiInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return poiInfos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final PoiInfo poiInfo = poiInfos.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.item_map_address, null);
			}
            TextView tv_name = ViewHolderUtil.get(convertView,
                    R.id.tv_name);
			TextView tv_address = ViewHolderUtil.get(convertView,
					R.id.tv_address);
            tv_name.setText(poiInfo.name);
            tv_address.setText(poiInfo.address);

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("latitude",poiInfo.location.latitude);
                    intent.putExtra("longitude",poiInfo.location.longitude);
                    setResult(RESULT_OK,intent);
                    AnimFinsh();
                }
            });

			return convertView;
		}
	}


    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        public void onGetPoiResult(PoiResult result){
            //获取POI检索结果
            dissLoding();
            if(result.getAllPoi()==null){
                no_data.setVisibility(View.VISIBLE);
                listview.setVisibility(View.GONE);
            }else {
                no_data.setVisibility(View.GONE);
                poiInfos = result.getAllPoi();
                adapter.notifyDataSetChanged();
                listview.setVisibility(View.VISIBLE);
            }
        }
        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }
}
