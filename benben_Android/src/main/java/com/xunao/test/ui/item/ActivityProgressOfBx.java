package com.xunao.test.ui.item;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.BxApplyProgress;
import com.xunao.test.bean.BxApplyProgressList;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.TimeUtil;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.utils.ViewHolderUtil;

public class ActivityProgressOfBx extends BaseActivity implements
		OnClickListener, OnRefreshListener<ListView> {
	private PullToRefreshListView listView;
	private ArrayList<BxApplyProgress> applyProgresses = new ArrayList<BxApplyProgress>();
	private ArrayList<BxApplyProgress> applyProgresses2 = new ArrayList<BxApplyProgress>();

	private BxApplyProgressList applyProgressList;
	private myAdapter adapter;

	private RelativeLayout rl_all;
	private TextView tv_all;
	private ImageView iv_all;
	private View v_all;
	private RelativeLayout rl_wait;
	private TextView tv_wait;
	private ImageView iv_wait;
	private View v_wait;
	private RelativeLayout rl_no_pass;
	private TextView tv_no_pass;
	private ImageView iv_no_pass;
	private View v_no_pass;
	private RelativeLayout rl_back;
	private TextView tv_back;
	private ImageView iv_back;
	private View v_back;
	private RelativeLayout rl_ok;
	private TextView tv_ok;
	private ImageView iv_ok;
	private View v_ok;
	private String type = "-1";
	private refreshBroadCast broadCast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_progress_of_bx);
		setShowLoding(false);

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadCast);
		super.onDestroy();
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		broadCast = new refreshBroadCast();

		registerReceiver(broadCast, new IntentFilter(
				AndroidConfig.ACTIVITYPROGRESSOFBXREFRESH));

		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", R.drawable.refrensh,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 刷新
						listView.setRefreshing(true);
					}
				}, "", R.drawable.ic_back, new OnClickListener() {

					@Override
					public void onClick(View v) {

						mContext.AnimFinsh();
					}
				}, "百姓网申请", 0);

		chanageTitle(mode);

		listView = (PullToRefreshListView) findViewById(R.id.listview);
		adapter = new myAdapter();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				startAnimActivity3Obj(ActivityApplyBaixingDetail.class,
						"PHONE", applyProgresses2.get(arg2 - 1).getPhone(),
						"ID", applyProgresses2.get(arg2 - 1).getId() + "");

			}
		});

		listView.setAdapter(adapter);

		listView.setOnRefreshListener(this);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					listView.setRefreshing(true);
				}
			}, 200);
		}
		rl_all = (RelativeLayout) findViewById(R.id.rl_all);
		tv_all = (TextView) findViewById(R.id.tv_all);
		iv_all = (ImageView) findViewById(R.id.iv_all);
		v_all = findViewById(R.id.v_all);
		rl_wait = (RelativeLayout) findViewById(R.id.rl_wait);
		tv_wait = (TextView) findViewById(R.id.tv_wait);
		iv_wait = (ImageView) findViewById(R.id.iv_wait);
		v_wait = findViewById(R.id.v_wait);
		rl_no_pass = (RelativeLayout) findViewById(R.id.rl_no_pass);
		tv_no_pass = (TextView) findViewById(R.id.tv_no_pass);
		iv_no_pass = (ImageView) findViewById(R.id.iv_no_pass);
		v_no_pass = findViewById(R.id.v_no_pass);
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		tv_back = (TextView) findViewById(R.id.tv_back);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		v_back = findViewById(R.id.v_back);
		rl_ok = (RelativeLayout) findViewById(R.id.rl_ok);
		tv_ok = (TextView) findViewById(R.id.tv_ok);
		iv_ok = (ImageView) findViewById(R.id.iv_ok);
		v_ok = findViewById(R.id.v_ok);

		rl_all.setOnClickListener(this);
		rl_wait.setOnClickListener(this);
		rl_no_pass.setOnClickListener(this);
		rl_back.setOnClickListener(this);
		rl_ok.setOnClickListener(this);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
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
		listView.onRefreshComplete();
		applyProgresses.clear();
		applyProgresses2.clear();
		applyProgressList = new BxApplyProgressList();
		try {
			applyProgressList = (BxApplyProgressList) applyProgressList
					.parseJSON(jsonObject);

			if (applyProgressList != null) {
				applyProgresses.addAll(applyProgressList.getBxaProgresses());

				if (type.equals("-1")) {
					applyProgresses2.addAll(applyProgresses);
				} else {
					for (int i = 0; i < applyProgresses.size(); i++) {
						if ((applyProgresses.get(i).getStatus()).equals(type)) {
							applyProgresses2.add(applyProgresses.get(i));
						}
					}
				}
			}
		} catch (NetRequestException e) {
			e.printStackTrace();
			ToastUtils.Infotoast(mContext, e.getMessage());
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return applyProgresses2.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_progress_of_bx_item, null);
			}

			TextView tv_name = ViewHolderUtil.get(convertView, R.id.tv_name);
			View shortPhone = ViewHolderUtil.get(convertView, R.id.shortPhone);
			TextView tv_phone_short = ViewHolderUtil.get(convertView,
					R.id.tv_phone_short);
			TextView tv_phone = ViewHolderUtil.get(convertView, R.id.tv_phone);
			TextView tv_address = ViewHolderUtil.get(convertView,
					R.id.tv_address);
			TextView tv_date = ViewHolderUtil.get(convertView, R.id.tv_date);
			TextView tv_status = ViewHolderUtil
					.get(convertView, R.id.tv_status);

			if (CommonUtils.isEmpty(applyProgresses2.get(position)
					.getShort_phone())) {
				shortPhone.setVisibility(View.GONE);
			} else {
				shortPhone.setVisibility(View.VISIBLE);
				tv_phone_short.setText(applyProgresses2.get(position)
						.getShort_phone());
			}

			tv_name.setText(applyProgresses2.get(position).getName());
			tv_phone.setText(applyProgresses2.get(position).getPhone());
			tv_address.setText(applyProgresses2.get(position).getAddress());
			tv_date.setText(applyProgresses2.get(position).getCreatedTime());

			switch (applyProgresses2.get(position).getStatus()) {
			case "0":
				tv_status.setText("待审核");
				break;
			case "1":
				tv_status.setText("未通过");
				break;
			case "2":
				tv_status.setText("退回重申");
				break;
			case "3":
				tv_status.setText("已通过");
				break;
			case "4":
				tv_status.setText("已撤销");
				break;
			}

			return convertView;
		}

	}

	@Override
	public void onClick(View arg0) {
		tv_all.setTextColor(Color.parseColor("#676767"));
		tv_wait.setTextColor(Color.parseColor("#676767"));
		tv_no_pass.setTextColor(Color.parseColor("#676767"));
		tv_back.setTextColor(Color.parseColor("#676767"));
		tv_ok.setTextColor(Color.parseColor("#676767"));
		iv_all.setVisibility(View.GONE);
		iv_wait.setVisibility(View.GONE);
		iv_no_pass.setVisibility(View.GONE);
		iv_back.setVisibility(View.GONE);
		iv_ok.setVisibility(View.GONE);
		v_all.setBackgroundColor(Color.parseColor("#dddddd"));
		v_wait.setBackgroundColor(Color.parseColor("#dddddd"));
		v_no_pass.setBackgroundColor(Color.parseColor("#dddddd"));
		v_back.setBackgroundColor(Color.parseColor("#dddddd"));
		v_ok.setBackgroundColor(Color.parseColor("#dddddd"));

		 if (CommonUtils.isNetworkAvailable(mContext)) {
		 new Handler().postDelayed(new Runnable() {
		 @Override
		 public void run() {
		 listView.setRefreshing(true);
		 }
		 }, 200);
		 }

		switch (arg0.getId()) {

		case R.id.rl_all:
			tv_all.setTextColor(Color.parseColor("#068cd9"));
			iv_all.setVisibility(View.VISIBLE);
			v_all.setBackgroundColor(Color.parseColor("#068cd9"));
			type = "-1";

			// applyProgresses2.clear();
			// applyProgresses2.addAll(applyProgresses);
			break;
		case R.id.rl_wait:
			tv_wait.setTextColor(Color.parseColor("#068cd9"));
			iv_wait.setVisibility(View.VISIBLE);
			v_wait.setBackgroundColor(Color.parseColor("#068cd9"));
			type = "0";
			// applyProgresses2.clear();
			// for (int i = 0; i < applyProgresses.size(); i++) {
			// if ((applyProgresses.get(i).getStatus()).equals("0")) {
			// applyProgresses2.add(applyProgresses.get(i));
			// }
			// }
			break;
		case R.id.rl_no_pass:
			tv_no_pass.setTextColor(Color.parseColor("#068cd9"));
			iv_no_pass.setVisibility(View.VISIBLE);
			v_no_pass.setBackgroundColor(Color.parseColor("#068cd9"));
			type = "1";
			// applyProgresses2.clear();
			// for (int i = 0; i < applyProgresses.size(); i++) {
			// if ((applyProgresses.get(i).getStatus()).equals("1")) {
			// applyProgresses2.add(applyProgresses.get(i));
			// }
			// }
			break;
		case R.id.rl_back:
			tv_back.setTextColor(Color.parseColor("#068cd9"));
			iv_back.setVisibility(View.VISIBLE);
			v_back.setBackgroundColor(Color.parseColor("#068cd9"));
			type = "2";
			// applyProgresses2.clear();
			// for (int i = 0; i < applyProgresses.size(); i++) {
			// if ((applyProgresses.get(i).getStatus()).equals("2")) {
			// applyProgresses2.add(applyProgresses.get(i));
			// }
			// }
			break;
		case R.id.rl_ok:
			tv_ok.setTextColor(Color.parseColor("#068cd9"));
			iv_ok.setVisibility(View.VISIBLE);
			v_ok.setBackgroundColor(Color.parseColor("#068cd9"));
			type = "3";
			// applyProgresses2.clear();
			// for (int i = 0; i < applyProgresses.size(); i++) {
			// if ((applyProgresses.get(i).getStatus()).equals("3")) {
			// applyProgresses2.add(applyProgresses.get(i));
			// }
			// }
			break;

		default:
			break;
		}

		if (type.equals("-1")) {
			applyProgresses2.addAll(applyProgresses);
		} else {
			applyProgresses2.clear();
			for (int i = 0; i < applyProgresses.size(); i++) {
				if ((applyProgresses.get(i).getStatus()).equals(type)) {
					applyProgresses2.add(applyProgresses.get(i));
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		if (type.equals("3") || type.equals("-1")) {
			ToastUtils.Infotoast(mContext, "已将短号更新到通讯录");
		}

		if (CommonUtils.isNetworkAvailable(mContext)) {
			String time = TimeUtil.getTime(new Date());
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新:" + time);

			InteNetUtils.getInstance(mContext).searchApplyBxProgress(
					user.getPhone(), mRequestCallBack);
		} else {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					listView.onRefreshComplete();
				}
			}, 200);
		}

	}

	class refreshBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			InteNetUtils.getInstance(mContext).searchApplyBxProgress(
					user.getPhone(), mRequestCallBack);
		}

	}

}
