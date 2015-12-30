package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.TrumpetArea;

public class ActivityTrumpetForArea extends BaseActivity {
	private ListView listView;
	private myAdapter adapter;
	private ArrayList<TrumpetArea> trumpetAreas = new ArrayList<>();
	private static final int CHOICE_ADDRESS = 1;
	private static final int RESULT  = 1;
	private Button btn_add;
	
	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_trumpet);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("发送小喇叭", "", "完成",
				R.drawable.icon_com_title_left, 0);

		listView = (ListView) findViewById(R.id.listview);
		btn_add = (Button) findViewById(R.id.btn_add);

		adapter = new myAdapter();
		listView.setAdapter(adapter);
		
		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult(ActivityChoiceTrumpetAddress.class,
						CHOICE_ADDRESS);
			}
		});
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("trumpetAreas", trumpetAreas);
				setResult(RESULT, intent);
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

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return trumpetAreas.size();
		}

		@Override
		public Object getItem(int arg0) {
			return trumpetAreas.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_trumpet_item, null);
			}
			
			TextView tv_area = (TextView) convertView
					.findViewById(R.id.tv_area);
			LinearLayout ll_area =  (LinearLayout) convertView
					.findViewById(R.id.ll_area);
			tv_area.setText(trumpetAreas.get(position).getAddressName());
			
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					trumpetAreas.remove(trumpetAreas.get(position));
					adapter.notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		switch (arg0) {
		case CHOICE_ADDRESS:
			if(data != null){
				TrumpetArea area = new TrumpetArea();
				area.setAddressId(data.getStringArrayExtra("addressId"));
				area.setAddressName(data.getStringExtra("address"));
				trumpetAreas.add(area);
				adapter.notifyDataSetChanged();
			}
			break;
		}
		super.onActivityResult(arg0, arg1, data);
	}

}
