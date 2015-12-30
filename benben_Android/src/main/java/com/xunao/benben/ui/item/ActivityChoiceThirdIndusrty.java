package com.xunao.benben.ui.item;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.IndustryInfo;
import com.xunao.benben.config.AndroidConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityChoiceThirdIndusrty extends BaseActivity implements
		OnClickListener {
	private ListView listview;
    private List<IndustryInfo> infoList = new ArrayList<>();

	private MyAdapter myAdatper;
	private String industry;
	private String industryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_choice_address);
        industryId = getIntent().getStringExtra("industryId");
        industry =  getIntent().getStringExtra("industry");
        initTitle_Right_Left_bar(industry, "", "",
                R.drawable.icon_com_title_left, 0);

	}

	@Override
	public void initView(Bundle savedInstanceState) {
		listview = (ListView) findViewById(R.id.listview);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

        try {
            infoList = dbUtil.findAll(Selector.from(IndustryInfo.class).where(
                    "parentId", "=", industryId));
            myAdatper = new MyAdapter();
            listview.setAdapter(myAdatper);
        } catch (DbException e) {
            e.printStackTrace();
        }
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
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

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
			return infoList == null ? 0 : infoList.size();
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

			final IndustryInfo industryInfo = infoList.get(position);

			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_choice_address_item, null);
			}

			((TextView) converView.findViewById(R.id.address_name))
					.setText(industryInfo.getName());

			converView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    industryId = industryInfo.getId();
                    Intent intent = new Intent();
                    industry += " "+industryInfo.getName();
                    intent.putExtra("industry", industry);
                    intent.putExtra("industryId", industryId);
                    setResult(AndroidConfig.ChoiceIndustryResultCode, intent);
                    AnimFinsh();
                }

			});

			return converView;
		}

	}

}
