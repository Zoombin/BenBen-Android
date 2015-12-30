package com.xunao.benben.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AccountAddress;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2015/12/16.
 */
public class ActivityAccountAddressManage extends BaseActivity{
    private LinearLayout no_data;
    private ListView lv_address;
    private MyAdapter adapter;
    private List<AccountAddress> accountAddressList = new ArrayList<>();
    private boolean isUpdate = false;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_account_address_manage);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("地址管理", "", "新增",
                R.drawable.icon_com_title_left, 0);
        no_data = (LinearLayout) findViewById(R.id.no_data);
        lv_address = (ListView) findViewById(R.id.lv_address);
        adapter = new MyAdapter();
        lv_address.setAdapter(adapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Addressdetail(mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityAccountAddressManage.this, ActivityAddressOperate.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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
        dissLoding();
        if(jsonObject.optInt("ret_num")==0){
            accountAddressList.clear();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("address");
                if(jsonArray!=null && jsonArray.length()>0){
                    no_data.setVisibility(View.GONE);
                    lv_address.setVisibility(View.VISIBLE);
                    for(int i=0;i<jsonArray.length();i++){
                        AccountAddress accountAddress = new AccountAddress();
                        accountAddress.parseJSON(jsonArray.getJSONObject(i));
                        accountAddressList.add(accountAddress);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    no_data.setVisibility(View.VISIBLE);
                    lv_address.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NetRequestException e) {
                e.printStackTrace();
            }

        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        dissLoding();
        ToastUtils.Infotoast(mContext, "获取地址信息失败");
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return accountAddressList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return accountAddressList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final AccountAddress accountAddress = accountAddressList.get(position);
            final itemHolder itHolder;
            if (convertView == null) {
                itHolder = new itemHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_account_address, null);
                itHolder.ll_default = (LinearLayout) convertView.findViewById(R.id.ll_default);
                itHolder.cb_default = (CheckBox) convertView.findViewById(R.id.cb_default);
                itHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                itHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                itHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
                itHolder.tv_edt = (TextView) convertView.findViewById(R.id.tv_edt);
                itHolder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
                convertView.setTag(itHolder);
            } else {
                itHolder = (itemHolder) convertView.getTag();
            }
            itHolder.tv_name.setText(accountAddress.getConsignee());
            itHolder.tv_phone.setText(accountAddress.getMobile());
            itHolder.tv_address.setText(accountAddress.getAddress_name());
            if(position==0){
                itHolder.cb_default.setChecked(true);
            }else{
                itHolder.cb_default.setChecked(false);
            }

            itHolder.ll_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if(position!=0){
                        showLoding("");
                        InteNetUtils.getInstance(mContext).DefaultEditaddress(accountAddress.getId(), 1, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                dissLoding();
                                ToastUtils.Infotoast(mContext, "设置成功！");
                                accountAddressList.remove(accountAddress);
                                accountAddressList.add(0,accountAddress);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                dissLoding();
                                ToastUtils.Infotoast(mContext,"设置成功！");
                            }
                        });
                    }
                }
            });

            itHolder.tv_edt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityAccountAddressManage.this, ActivityAddressOperate.class);
                    intent.putExtra("accountAddress",accountAddress);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            itHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final MsgDialog msgDialog = new MsgDialog(ActivityAccountAddressManage.this, R.style.MyDialogStyle);
                    msgDialog.setContent("确定删除该地址吗？", "", "确定", "取消");
                    msgDialog.setCancleListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                            showLoding("");
                            InteNetUtils.getInstance(mContext).Deladdress(accountAddress.getId(), user.getToken(), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    isUpdate = true;
                                    dissLoding();
                                    ToastUtils.Infotoast(mContext,"删除成功！");
                                    accountAddressList.remove(accountAddress);
                                    adapter.notifyDataSetChanged();
                                    if(accountAddressList.size()==0){
                                        no_data.setVisibility(View.VISIBLE);
                                        lv_address.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    dissLoding();
                                    ToastUtils.Infotoast(mContext,"删除失败！");
                                }
                            });
                        }

                    });
                    msgDialog.show();
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final MsgDialog msgDialog = new MsgDialog(ActivityAccountAddressManage.this, R.style.MyDialogStyle);
                    msgDialog.setContent("确定选择该地址吗？", "", "确定", "取消");
                    msgDialog.setCancleListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("accountAddress",accountAddress);
                            setResult(RESULT_OK,intent);
                            AnimFinsh();
                        }
                    });
                    msgDialog.show();
                }
            });
            return convertView;
        }

    }

    class itemHolder {
        LinearLayout ll_default;
        private CheckBox cb_default;
        TextView tv_name;
        TextView tv_phone;
        TextView tv_address;
        TextView tv_edt;
        TextView tv_delete;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(CommonUtils.isNetworkAvailable(mContext)){
                isUpdate = true;
                InteNetUtils.getInstance(mContext).Addressdetail(mRequestCallBack);
            }else{
                ToastUtils.Infotoast(mContext, "网络不可用");
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isUpdate",isUpdate);
        setResult(RESULT_OK,intent);
        AnimFinsh();
        super.onBackPressed();
    }
}
