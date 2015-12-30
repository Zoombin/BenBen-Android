package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AddressInfo;
import com.xunao.benben.bean.AddressInfoList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityChoiceTrumpetAddress extends BaseActivity implements
		OnClickListener {

	private ListView listview;

	private ArrayList<AddressInfo> addressInfos;
	private AddressInfoList addressInfoList = new AddressInfoList();

	private MyAdapter myAdatper;

	private String[] leve = { "选择省份", "选择城市", "选择城区或城镇", "选择街道 " };
	private String addressname = "";
	private String[] addressId = new String[3];
	private int id = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_choice_address);

		initTitle_Right_Left_bar(leve[0], "", "",
				R.drawable.icon_com_title_left, 0);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		listview = (ListView) findViewById(R.id.listview);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		// 第一次传空
		InteNetUtils.getInstance(mContext).getAddress("", mRequestCallBack);

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setOnLeftClickLinester(this);
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
		try {
			addressInfoList.parseJSON(jsonObject);
			addressInfos = addressInfoList.getAddressInfos();

			if (addressInfos != null && addressInfos.size() > 0) {
				int i = Integer.valueOf(addressInfos.get(0).getLevel()) - 1;
				((TextView) findViewById(R.id.com_title_bar_content))
						.setText(leve[i]);
			}

			myAdatper = new MyAdapter();
			listview.setAdapter(myAdatper);

		} catch (NetRequestException e) {
			e.getError().print(mContext);
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;

		default:
			break;
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return addressInfos == null ? 0 : addressInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {

			final AddressInfo addressinfo = addressInfos.get(position);

			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_choice_address_item, null);
			}

			((TextView) converView.findViewById(R.id.address_name))
					.setText(addressinfo.getArea_name());

			converView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
						addressname += addressinfo.getArea_name() + " ";
						addressId[id] = addressinfo.getBid();
						id++;
						if (Integer.valueOf(addressinfo.getLevel()) >= 2) {
							Intent intent = new Intent();
							intent.putExtra("address", addressname);
							intent.putExtra("addressId", addressId);
							setResult(AndroidConfig.ChoiceAddressResultCode,
									intent);
							AnimFinsh();
						} else {
							InteNetUtils.getInstance(mContext).getAddress(
									addressinfo.getBid(), mRequestCallBack);
						}
				}
			});

			return converView;
		}

	}

}
