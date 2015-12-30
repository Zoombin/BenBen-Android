package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.ui.ActivityWeb;

public class ActivityAboutBenben extends BaseActivity implements OnClickListener {
	private TextView tv_version;
	private RelativeLayout tl_tab_1;
	private RelativeLayout tl_tab_2;
	private RelativeLayout tl_tab_3;
	private RelativeLayout tl_tab_4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_about_benben);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("关于奔犇", "", "", R.drawable.icon_com_title_left, 0);
		
		tv_version = (TextView) findViewById(R.id.tv_version);
		
		tl_tab_1 = (RelativeLayout) findViewById(R.id.tl_tab_1);
		tl_tab_2 = (RelativeLayout) findViewById(R.id.tl_tab_2);
		tl_tab_3 = (RelativeLayout) findViewById(R.id.tl_tab_3);
		tl_tab_4 = (RelativeLayout) findViewById(R.id.tl_tab_4);
		
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String version = packageInfo.versionName;
			tv_version.setText("奔犇"+ version);
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					finish();
				}
			});
		
		tl_tab_1.setOnClickListener(this);
		tl_tab_2.setOnClickListener(this);
		tl_tab_3.setOnClickListener(this);
		tl_tab_4.setOnClickListener(this);
	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tl_tab_1:
			showHtml("关于我们", 3);
			break;
		case R.id.tl_tab_2:
			showHtml("法律声明", 4);
			break;
		case R.id.tl_tab_3:
			showHtml("使用帮助", 5);
			break;
		case R.id.tl_tab_4:
			showHtml("积分说明", 6);
			break;
			
		default:
			break;
		}
	}
	
	//网页
	private void showHtml(String title, int type){
		Intent intent = new Intent(this, ActivityWeb.class);
		intent.putExtra("title", title);
		intent.putExtra("url", AndroidConfig.NETHOST3 + AndroidConfig.Setting + "key/android/type/" + type);
		
		startActivity(intent);
	}
	
}
