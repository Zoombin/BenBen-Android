package com.xunao.benben.ui.item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smackx.muc.RoomInfo;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.BirthDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityWriteBuyInfo extends BaseActivity implements
		OnClickListener {

	private MyTextView buyingo_time;
	private EditText buyingo_title;
	private EditText buyingo_info;
	private EditText buyingo_num;
	private RadioGroup buyTime;
	private View select_address;
	private static final int CHOCE_ADDRESS =4;
	// 记录了地区的id
	private String[] addressId = new String[4];
	private TextView tv_chose_address;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_writebuy);
		setShowLoding(false);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		// initTitleView();
		// TitleMode mode = new TitleMode("#068cd9", "发布", 0,
		// new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// send();
		// }
		// }, "", R.drawable.ic_back, new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// mContext.AnimFinsh();
		// }
		// }, "发布", 0);
		// chanageTitle(mode);

		View send = findViewById(R.id.activity_selectimg_send);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		buyingo_title = (EditText) findViewById(R.id.buyingo_title);
		buyingo_info = (EditText) findViewById(R.id.buyingo_info);
		buyingo_num = (EditText) findViewById(R.id.buyingo_num);
		tv_chose_address = (TextView) findViewById(R.id.tv_chose_address);
		buyTime = (RadioGroup) findViewById(R.id.buyTime);
		select_address = findViewById(R.id.select_address);
		select_address.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		switch (id) {
		case R.id.select_address:
			startAnimActivityForResult2(ActivityChoiceAddress.class,
					CHOCE_ADDRESS, "level", "3");
			break;
		}

	}

	private boolean StringIsSurpass(String inputStr, int num) {
		int orignLen = inputStr.length();
		int resultLen = 0;
		String temp = null;
		for (int i = 0; i < orignLen; i++) {
			temp = inputStr.substring(i, i + 1);
			try {// 3 bytes to indicate chinese word,1 byte to indicate english
					// word ,in utf-8 encode
				if (temp.getBytes("utf-8").length == 3) {
					resultLen += 2;
				} else {
					resultLen++;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (resultLen > num) {
				return true;
			}
		}
		return true;
	}

	// 发送我要买信息
	protected void send() {

		String sTitle = buyingo_title.getText().toString();
		String sInfo = buyingo_info.getText().toString();
		String sNum = buyingo_num.getText().toString();
		int checkedRadioButtonId = buyTime.getCheckedRadioButtonId();
		if (TextUtils.isEmpty(sTitle)) {
			ToastUtils.Errortoast(mContext, "标题不可为空");
			return;
		} else if(!CommonUtils.StringIsSurpass2(sTitle, 2, 8)){
			ToastUtils.Errortoast(mContext, "标题限制在1-8个字之间");
			return;
		}
		if (TextUtils.isEmpty(sInfo)) {
			ToastUtils.Errortoast(mContext, "描述不可为空");
			return;
		}
        if (!CommonUtils.StringIsSurpass2(sInfo, 1, 200)) {
            ToastUtils.Errortoast(mContext, "描述限制在200个字之间!");
            return;
        }
		if (TextUtils.isEmpty(sNum)) {
			ToastUtils.Errortoast(mContext, "数量不可为空");
			return;
		}
		
		if(sNum.length() > 10){
			ToastUtils.Errortoast(mContext, "数量最多输入10位");
			return;
		}
		
		
		if ((tv_chose_address.getText().toString().trim()).equals("选择地址")) {
			ToastUtils.Infotoast(mContext, "请选择地址");
			return;
		}
		long sTime = 0;
		long onDay = 60 * 60 * 24;
		switch (checkedRadioButtonId) {
		case R.id.threeDay:
			sTime = 3 * onDay;
			break;
		case R.id.sevenDay:
			sTime = 7 * onDay;

			break;
		case R.id.halfmonth:
			sTime = 15 * onDay;

			break;
		case R.id.onemonth:
			sTime = 30 * onDay;

			break;
		default:
			ToastUtils.Errortoast(mContext, "请选择时间");
			return;
		}

		if (CommonUtils.isNetworkAvailable(mContext)) {
			showLoding("发送中...");
			InteNetUtils.getInstance(mContext).sendBuyInfo(sTitle, sInfo, sNum,
					(sTime + TimeUtil.now()) + "", addressId, mRequestCallBack);
		}
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

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
		dissLoding();
		SuccessMsg msg = new SuccessMsg();
		try {
			msg.parseJSON(jsonObject);
			setResult(AndroidConfig.writeFriendRefreshResultCode);
			mContext.finish();
			mContext.overridePendingTransition(R.anim.in_from_left,
					R.anim.out_to_right);

		} catch (NetRequestException e) {
			e.getError().print(mContext);
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		dissLoding();
		ToastUtils.Errortoast(getApplicationContext(), "网络不可用,请重试");
	}

	// 选择生日
	private void choseBirthday(final TextView view) {
		final BirthDialog dialog = new BirthDialog(mContext, "");
		Window window = dialog.getWindow();
		window.setLayout(mScreenWidth - PixelUtil.dp2px(10),
				LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.BOTTOM);

		dialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.chose_position:
					view.setText(CommonUtils.getDate(mContext,
							dialog.getWheelMain()));
					dialog.dismiss();
					break;
				default:
					break;
				}
			}
		});

		dialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.chose_cancel:
					dialog.dismiss();
					break;
				default:
					break;
				}
			}
		});

		dialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHOCE_ADDRESS:
			if (data != null) {
				if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
					String addressname = data.getStringExtra("address");
					addressId = null;
					addressId = data.getStringArrayExtra("addressId");
					tv_chose_address.setText(addressname);
					tv_chose_address.setTextColor(Color.BLACK);
				}
			}

			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
