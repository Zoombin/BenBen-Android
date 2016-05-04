package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Enterprise;
import com.xunao.benben.bean.EnterpriseList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;
import com.xunao.benben.view.DragListView;

public class ActivityEnterprisesContacts extends BaseActivity implements ActionSheetListener {
    private DragListView listView;
    private MyAdapter adapter;
    private ArrayList<Enterprise> enterprises = new ArrayList<>();
    private EnterpriseList enterpriseList;
    private LinearLayout no_data;
    private boolean hasData = true;
    private int createdEnterpriseNum = 0;
    private enterpriseListCast broadCast;

    private int start=0;
    private int end=0;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_enterprises_contacts);
        setShowLoding(false);

        broadCast = new enterpriseListCast();
        registerReceiver(broadCast, new IntentFilter(AndroidConfig.refreshEnterpriseList));

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("政企通讯录", "", "",
                R.drawable.icon_com_title_left, R.drawable.icon_com_title_more);

        listView = (DragListView) findViewById(R.id.listview);
        listView.setDropListener(mDropListener);
        no_data = (LinearLayout) findViewById(R.id.no_data);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);

    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        initNetDate();
    }

    private void initNetDate() {
        if (CommonUtils.isNetworkAvailableNoShow(mContext)) {
            InteNetUtils.getInstance(mContext).getEnterprises(mRequestCallBack);
        } else {
            getDataFromLocal();
            if (hasData) {
                adapter.notifyDataSetChanged();
            } else {
                no_data.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getDataFromLocal() {
        try {

            List<Enterprise> myEnterprises = dbUtil.findAll(Selector.from(Enterprise.class).where(
                    "benben_id", "=", user.getBenbenId()).orderBy("sort"));

            if (myEnterprises != null && myEnterprises.size() > 0) {
                hasData = true;
                enterprises = (ArrayList<Enterprise>) myEnterprises;
                no_data.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

            } else {
                hasData = false;
                enterprises.clear();
                adapter.notifyDataSetChanged();
                no_data.setVisibility(View.VISIBLE);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

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
                showActionSheet();
            }
        });
    }

    protected void showActionSheet() {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("创建政企通讯录", "查找政企通讯录")
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }

    //交换listview的数据
    private DragListView.DropListener mDropListener =
            new DragListView.DropListener() {
                public void drop(int from, int to) {
                    Enterprise item = enterprises.get(from);
                    enterprises.remove(item);//.remove(from);
                    enterprises.add(to, item);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void start(int from) {
                    start = from;
                }

                @Override
                public void stop(int to) {
                    end=to;
                    String sort = "";
                    if(start!=end) {
                        if(start>end){
                            int pos = start;
                            start = end;
                            end = pos;
                        }
                        for (int i = start; i <= end; i++) {
                            sort += enterprises.get(i).getEnterprise_id() + ";" + (i + 1) + "|";
                        }
                        if(sort.length()>1) {
                            sort = sort.substring(0, sort.length() - 1);
                        }
                        ActivityEnterprisesContacts.this.showLoding("加载中...");
                        InteNetUtils.getInstance(mContext).sortEnterprise(
                                user.getToken(), sort, new RequestCallBack<String>() {

                                    @Override
                                    public void onFailure(HttpException arg0, String arg1) {
                                        ActivityEnterprisesContacts.this.dissLoding();
                                        ToastUtils.Errortoast(mContext, "操作失败！");
                                        getDataFromLocal();
                                    }

                                    @Override
                                    public void onSuccess(ResponseInfo<String> arg0) {
                                        ActivityEnterprisesContacts.this.dissLoding();
                                        for (int i = start; i <= end; i++) {
                                            enterprises.get(i).setSort(i + 1);
                                            try {
                                                dbUtil.saveOrUpdate(enterprises.get(i));
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
            };

    @Override
    protected void onHttpStart() {

    }

    @Override
    protected void onLoading(long count, long current, boolean isUploading) {
    }

    @Override
    protected void onSuccess(JSONObject jsonObject) {
        try {
            enterprises.clear();
            enterpriseList = new EnterpriseList();
            enterpriseList = enterpriseList.parseJSON(jsonObject);
            if (enterpriseList != null
                    && enterpriseList.getEnterprises() != null) {
                enterprises.addAll(enterpriseList.getEnterprises());
                for(int i=0;i<enterprises.size();i++){
                    enterprises.get(i).setBenben_id(user.getBenbenId());
                }
                saveLocalData(enterprises);
                getDataFromLocal();
            }

        } catch (NetRequestException e) {
            if (hasData) {
                adapter.notifyDataSetChanged();
            } else {
                no_data.setVisibility(View.VISIBLE);
            }
            e.printStackTrace();
        }
    }

    private void saveLocalData(ArrayList<Enterprise> enterprise) {
        try {

            dbUtil.deleteAll(Enterprise.class);
            if (enterprise != null && enterprise.size() > 0) {

                dbUtil.saveOrUpdateAll(enterprise);
            }
            createdEnterpriseNum = 0;
            for (Enterprise enterprise2 : enterprise) {
                if(Integer.parseInt(enterprise2.getMemberId()) == user.getId()){
                    createdEnterpriseNum +=1;
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
    }



    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return enterprises.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return enterprises.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final Enterprise enterprise = enterprises.get(position);
            final itemHolder itHolder;
//            if (convertView == null) {
                itHolder = new itemHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_activirty_entrprises_contacts, null);
                itHolder.item_layout = (LinearLayout) convertView.findViewById(R.id.item_layout);
                itHolder.iv_tag = (ImageView) convertView
                        .findViewById(R.id.iv_tag);
                itHolder.enterprise_name = (TextView) convertView
                        .findViewById(R.id.tv_enterprise_name);
                itHolder.enterprise_number = (TextView) convertView
                        .findViewById(R.id.tv_enterprise_number);
                convertView.setTag(itHolder);
//            } else {
//                itHolder = (itemHolder) convertView.getTag();
//            }

            itHolder.enterprise_name.setText(enterprises.get(position).getName());
            itHolder.enterprise_number.setText("("
                    + enterprises.get(position).getNumber() + "人)");
//
            if(enterprises.get(position).getTag().equals("虚拟")){
                if(enterprises.get(position).getOrigin()==2){
                    itHolder.iv_tag.setImageResource(R.drawable.icon_enterprises_xuni_back);
                }else {
                    itHolder.iv_tag.setImageResource(R.drawable.icon_enterprises_xuni);
                }
            }else if(enterprises.get(position).getTag().equals("企业")){
                if(enterprises.get(position).getOrigin()==2){
                    itHolder.iv_tag.setImageResource(R.drawable.icon_enterprises_company_back);
                }else {
                    itHolder.iv_tag.setImageResource(R.drawable.icon_enterprises_company);
                }

            }else{
                itHolder.iv_tag.setImageResource(R.drawable.icon_enterprises_baixing);
            }

            itHolder.item_layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if(enterprises.get(position).getStatus() == 1){
						final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
								R.style.MyDialog1);
						hint.setContent("该政企被屏蔽");
						hint.setBtnContent("确定");
						hint.show();
						hint.setOKListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								hint.dismiss();
							}
						});

						hint.show();
					}else{
                        Intent intent = new Intent(mContext, ActivityEnterpriseMember.class);
                        intent.putExtra("id", enterprises.get(position).getId());
                        intent.putExtra("name", enterprises.get(position).getName());
                        intent.putExtra("origin", enterprises.get(position).getOrigin());
                        intent.putExtra("type", enterprises.get(position).getType());
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}


				}
			});


            return convertView;
        }

    }

    class itemHolder {
        LinearLayout item_layout;
        TextView enterprise_name;
        TextView enterprise_number;
        ImageView iv_tag;
    }


    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
//			if(enterprises.size() >=6){
//				ToastUtils.Errortoast(mContext, "您最多只能加入6个政企!");
//			}else{
                startAnimActivity(ActivityCreatedEnterpriseContacts.class);
//			}
                break;
            case 1:
                startAnimActivity(ActivitySearchEnterprise.class);
                break;
        }
    }

    @Override
    protected void onResume() {
        if (user.isUpdate()) {
            initNetDate();
            user.setUpdate(false);
        }
        super.onResume();
    }


    class enterpriseListCast extends BroadcastReceiver{
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            initNetDate();
        }

    }

}
