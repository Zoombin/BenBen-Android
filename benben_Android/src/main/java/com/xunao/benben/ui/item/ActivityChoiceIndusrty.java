package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.xunao.benben.bean.AddressInfo;
import com.xunao.benben.bean.AddressInfoList;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.IndustryInfo;
import com.xunao.benben.bean.IndustryInfoList;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityChoiceIndusrty extends BaseActivity implements
		OnClickListener {

	private ListView listview;

	private ArrayList<IndustryInfo> industryInfos;
    private List<IndustryInfo> infoList = new ArrayList<>();
	private IndustryInfoList industryInfoList = new IndustryInfoList();

	private MyAdapter myAdatper;

	private String[] leve = { "选择一级行业", "选择二级行业" };
	private String industry;
	private int id = 0;
	private String industryId;
    private String level="0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
        Intent intent = getIntent();
        if(intent.hasExtra("level")){
            level = intent.getStringExtra("level");
        }

		// 第一次传空
		InteNetUtils.getInstance(mContext).getIndustry(getLatestTime(), mRequestCallBack);

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
            String reg_num = jsonObject.optString("ret_num");
            if(reg_num.equals("0")){
                try {
                    dbUtil.deleteAll(IndustryInfo.class);
                    industryInfoList.parseJSON(jsonObject);
                    industryInfos = industryInfoList.getIndustryInfos();
                    dbUtil.saveOrUpdateAll(industryInfos);
                    updateLatestTime(new Date().getTime()/1000);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            try {
                infoList = dbUtil.findAll(Selector.from(IndustryInfo.class).where(
                        "level", "=", "1"));
            } catch (DbException e) {
                e.printStackTrace();
            }


            // if (industryInfos != null && industryInfos.size() > 0) {
			// int i = Integer.valueOf(industryInfos.get(0).getLevel()) ;
			// ((TextView) findViewById(R.id.com_title_bar_content))
			// .setText(leve[i]);
			// }else{
			// ToastUtils.Infotoast(mContext, "此分类下面已没有子行业,请选择其他行业");
			// return;
			// }

			myAdatper = new MyAdapter();
			listview.setAdapter(myAdatper);

		} catch (NetRequestException e) {
			e.getError().print(mContext);
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "获取行业信息失败！");
		AnimFinsh();
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

                    industry = industryInfo.getName();
                    industryId = industryInfo.getId();
                    Intent intent = new Intent();
                    intent.putExtra("industry", industry);
                    intent.putExtra("industryId", industryId);
                    String last = industryInfo.getLast();
                    if(last.equals("0")){
                        intent.setClass(ActivityChoiceIndusrty.this,ActivityChoiceSecondIndusrty.class);
                        intent.putExtra("level",level);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }else{
                        setResult(AndroidConfig.ChoiceIndustryResultCode, intent);
                        AnimFinsh();
                    }
				}
			});

			return converView;
		}

	}


    public long getLatestTime() {
        SharedPreferences sharedPreferences = getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        if(sharedPreferences.contains("createdTime")){
            return sharedPreferences.getLong("createdTime",0);
        }else{
            return 0;
        }
    }

    public void updateLatestTime(long time) {
        SharedPreferences sharedPreferences = getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        //创建数据编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("createdTime");
        //传递需要保存的数据
        editor.putLong("createdTime", time);
        //保存数据
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AndroidConfig.ChoiceIndustryResultCode:
                setResult(AndroidConfig.ChoiceIndustryResultCode, data);
                AnimFinsh();
                break;
        }
    }
}
