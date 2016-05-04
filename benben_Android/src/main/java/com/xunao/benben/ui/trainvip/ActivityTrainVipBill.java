package com.xunao.benben.ui.trainvip;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Bill;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.account.ActivityAccountAddressManage;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员账单
 * Created by ltf on 2016/4/18.
 */
public class ActivityTrainVipBill extends BaseActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<ListView>,PullToRefreshBase.OnLastItemVisibleListener {
    private TextView tv_message;
    private PullToRefreshListView lv_detail;
    private List<Bill> billList = new ArrayList<>();
    private DataAdapter adapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int currentPage=1;
    private int pagNum=1;
    private String shop="";
    private String cumulation_type="0";
    private TextView tv_level,tv_recharge,tv_consume;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_train_vip_bill);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("账单", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_level = (TextView) findViewById(R.id.tv_level);
        tv_recharge = (TextView) findViewById(R.id.tv_recharge);
        tv_consume = (TextView) findViewById(R.id.tv_consume);

        lv_detail = (PullToRefreshListView) findViewById(R.id.lv_detail);
        adapter = new DataAdapter();
        lv_detail.setAdapter(adapter);

        lv_detail.setOnRefreshListener(this);
        lv_detail.setOnLastItemVisibleListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        shop = getIntent().getStringExtra("shop");
        cumulation_type = getIntent().getStringExtra("cumulation_type");
        setLoadMore(false);
        currentPage = 1;
        InteNetUtils.getInstance(mContext).VipLog(shop,cumulation_type,currentPage, mRequestCallBack);

    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
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
        lv_detail.onRefreshComplete();
        isMoreData = false;
        if(!isLoadMore){
            billList.clear();
        }
        if(jsonObject.optInt("ret_num")==0){
            try {
                tv_level.setText("会员等级："+jsonObject.optString("level"));
                if(cumulation_type.equals("1")){
                    tv_recharge.setText("累计充值:"+jsonObject.optDouble("recharge")+"");
                    tv_recharge.setVisibility(View.VISIBLE);
                }
                tv_consume.setText("累计消费:"+jsonObject.optDouble("consume")+"");
                tv_consume.setVisibility(View.VISIBLE);

                pagNum = jsonObject.optInt("ap");
                JSONArray jsonArray = jsonObject.getJSONArray("log");
                if(jsonArray!=null && jsonArray.length()>0){
                    tv_message.setVisibility(View.GONE);
                    lv_detail.setVisibility(View.VISIBLE);
                    for (int i=0;i<jsonArray.length();i++){
                        Bill bill = new Bill();
                        bill.parseVipBillJSON(jsonArray.getJSONObject(i));
                        billList.add(bill);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    tv_message.setVisibility(View.VISIBLE);
                    lv_detail.setVisibility(View.GONE);
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
        lv_detail.onRefreshComplete();
        ToastUtils.Infotoast(mContext,"获取账单信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_address:
                startAnimActivity(ActivityAccountAddressManage.class);
                break;
        }
    }

    @Override
    public void onLastItemVisible() {
        if(currentPage>=pagNum){
//            ToastUtils.Infotoast(mContext,"无更多数据");
        }else{
            setLoadMore(true);
            currentPage++;
            InteNetUtils.getInstance(mContext).VipLog(shop,cumulation_type,currentPage, mRequestCallBack);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        setLoadMore(false);
        currentPage = 1;
        lv_detail.setOnLastItemVisibleListener(this);
        InteNetUtils.getInstance(mContext).VipLog(shop,cumulation_type,currentPage, mRequestCallBack);
    }

    class DataAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return billList.size() + 1;
        }

        @Override
        public Object getItem(int arg0) {
            return billList.get(arg0);
        }

        @Override
        public int getItemViewType(int position) {

            return position <= billList.size() - 1 ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_train_vip_bill, null);
                }
                TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                TextView tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                TextView tv_content = (TextView) convertView.findViewById(R.id.tv_content);

                Bill bill = billList.get(position);
                tv_content.setText(bill.getContent());
                tv_time.setText(simpleDateFormat.format(bill.getTime()*1000));
                tv_money.setText(bill.getFee());

            } else {
                if (isMoreData) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_load_more, null);
                    LinearLayout load_more = ViewHolderUtil.get(convertView,
                            R.id.load_more);
                    if (enterNum) {
                        load_more.setVisibility(View.VISIBLE);
                    } else {
                        load_more.setVisibility(View.GONE);
                    }
                } else {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_no_load_more, null);
                    convertView.setVisibility(View.GONE);
                }
            }

            return convertView;
        }


    }

}
