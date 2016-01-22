package com.xunao.benben.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2016/1/21.
 */
public class ActivityReport extends BaseActivity implements View.OnClickListener {
    private ListView lv_reason;
    private MyAdapter myAdapter;
    private EditText edt_reason;
    private Button btn_submit;

    private String[] reasons = {"欺诈","色情","政治谣言","常识性谣言","诱导分享","恶意营销","隐私信息收集","其它侵权类(冒名,诽谤,抄袭)"};
    private int selectPos = -1;
    private String reason="";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_report);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("举报", "", "", R.drawable.icon_com_title_left,0);
        lv_reason = (ListView) findViewById(R.id.lv_reason);
        myAdapter = new MyAdapter();
        lv_reason.setAdapter(myAdapter);
        edt_reason = (EditText) findViewById(R.id.edt_reason);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        reason = getIntent().getStringExtra("reason");
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
                if(selectPos == -1){
                    ToastUtils.Infotoast(mContext,"请选择举报原因");
                }else{
                    String str = String.valueOf(edt_reason.getText()).trim();
                    String context = reason+reasons[selectPos]+str;
                    Log.d("ltf","context====="+context);
                    if (CommonUtils.isNetworkAvailable(mContext)) {
                        showLoding("");
                        InteNetUtils.getInstance(mContext).doComplain(context,new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                dissLoding();
                                ToastUtils.Infotoast(mContext,"我们已收到您的举报，将会尽快处理！");
                                AnimFinsh();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                dissLoding();
                                ToastUtils.Infotoast(mContext,"举报失败");
                            }
                        });
                    }else{
                        ToastUtils.Infotoast(mContext,"网络不可用");
                    }
                }
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return reasons.length;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View converView, ViewGroup arg2) {

            ItemHolder itemHolder;

            if (converView == null) {
                converView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_report, null);
                itemHolder = new ItemHolder();
                itemHolder.cb_select = (CheckBox) converView
                        .findViewById(R.id.cb_select);
                itemHolder.tv_reason = (TextView) converView
                        .findViewById(R.id.tv_reason);

                converView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) converView.getTag();
            }

            if(selectPos == position){
                itemHolder.cb_select.setVisibility(View.VISIBLE);
            }else{
                itemHolder.cb_select.setVisibility(View.INVISIBLE);
            }

            itemHolder.tv_reason.setText(reasons[position]);

            converView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    selectPos = position;
                    myAdapter.notifyDataSetChanged();
                }
            });

            return converView;
        }

    }

    class ItemHolder {
        CheckBox cb_select;
        TextView tv_reason;
    }
}
