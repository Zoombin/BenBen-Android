package com.xunao.benben.ui.item;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/11/6.
 */
public class ActivityNewGroupChangeInfo extends BaseActivity implements View.OnClickListener {
    private CubeImageView group_poster;
    private TextView tv_group_name;
    private TextView tv_group_message;
    private LinearLayout ll_result_select,ll_result;
    private TextView tv_refuse,tv_agree,tv_state;
    private PublicMessage publicMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_group_change_info);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("申请与通知", "", "",
                R.drawable.icon_com_title_left, 0);
        group_poster = (CubeImageView) findViewById(R.id.group_poster);
        tv_group_name = (TextView) findViewById(R.id.tv_group_name);
        tv_group_message = (TextView) findViewById(R.id.tv_group_message);
        ll_result_select = (LinearLayout) findViewById(R.id.ll_result_select);
        ll_result = (LinearLayout) findViewById(R.id.ll_result);
        tv_refuse = (TextView) findViewById(R.id.tv_refuse);
        tv_refuse.setOnClickListener(this);
        tv_agree = (TextView) findViewById(R.id.tv_agree);
        tv_agree.setOnClickListener(this);
        tv_state = (TextView) findViewById(R.id.tv_state);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        publicMessage = (PublicMessage) getIntent().getSerializableExtra("publicMessage");
        InteNetUtils.getInstance(mContext).getTransferinfo(publicMessage.getNews_id(), user.getToken(), mRequestCallBack);
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(this);
    }

    @Override
    protected void onHttpStart() {

    }

    @Override
    protected void onLoading(long count, long current, boolean isUploading) {

    }

    @Override
    protected void onSuccess(JSONObject jsonObject) {
        if (jsonObject.optString("ret_num").equals("0")) {
            try {
                JSONObject object = jsonObject.getJSONObject("transfer_info");
                CommonUtils.startImageLoader(cubeimageLoader, object.optString("group_avatar"), group_poster);
                tv_group_name.setText(object.optString("group_name"));
                tv_group_message.setText(publicMessage.getReason());
            } catch (JSONException e) {
                e.printStackTrace();
            }



            switch (publicMessage.getStatus()){
                case PublicMessage.UNAGREE:
                    ll_result_select.setVisibility(View.VISIBLE);
                    ll_result.setVisibility(View.GONE);
                    break;
                case PublicMessage.AGREE:
                    ll_result_select.setVisibility(View.GONE);
                    tv_state.setText("已接受");
                    ll_result.setVisibility(View.VISIBLE);
                    break;
                case PublicMessage.REFUSE:
                    ll_result_select.setVisibility(View.GONE);
                    tv_state.setText("已拒绝");
                    ll_result.setVisibility(View.VISIBLE);
                    break;
            }

        }else{
            ToastUtils.Errortoast(this,"获取信息失败");
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Errortoast(this,"获取信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // 头部左侧点击
            case R.id.com_title_bar_left_bt:
            case R.id.com_title_bar_left_tv:
                AnimFinsh();
                break;
            case R.id.tv_refuse:
                if (CommonUtils.isNetworkAvailable(mContext)) {
                    showLoding("正在处理...");
                    InteNetUtils.getInstance(mContext).refuseTransfer(publicMessage.getNews_id(), user.getToken(),new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            dissLoding();
                            ToastUtils.Errortoast(mContext, "拒绝失败，请稍后再试");
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> arg0) {
                            dissLoding();
                            try {
                                JSONObject jsonObject = new JSONObject(arg0.result);

                                SuccessMsg msg = new SuccessMsg();
                                msg.parseJSON(jsonObject);
                                ToastUtils.Errortoast(mContext, "拒绝成功");
                                publicMessage.setStatus(PublicMessage.REFUSE);
                                try {
                                    dbUtil.saveOrUpdate(publicMessage);
                                } catch (DbException e1) {
                                    e1.printStackTrace();
                                }
                                setResult(AndroidConfig.writeFriendResultCode);
                                AnimFinsh();

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                ToastUtils.Errortoast(mContext, "拒绝失败，请稍后再试");
                            } catch (NetRequestException e) {
                                e.getError().print(mContext);
                                e.printStackTrace();
                                ToastUtils.Errortoast(mContext, "拒绝失败，请稍后再试");
                            }

                        }
                    });
                }else{
                    ToastUtils.Errortoast(mContext,"当前网络不可用");
                }

                break;
            case R.id.tv_agree:
                if (CommonUtils.isNetworkAvailable(mContext)) {
                    showLoding("正在处理...");
                    // 加入好友联盟
                    InteNetUtils.getInstance(mContext).acceptTransfer(publicMessage.getNews_id(),user.getToken(), new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            dissLoding();
                            ToastUtils.Errortoast(mContext, "接受转让失败，请稍后再试");
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> arg0) {
                            dissLoding();
                            try {
                                JSONObject jsonObject = new JSONObject(arg0.result);

                                SuccessMsg msg = new SuccessMsg();

                                msg.parseJSON(jsonObject);
                                ToastUtils.Errortoast(mContext, "接受转让成功");
                                publicMessage.setStatus(PublicMessage.AGREE);
                                try {
                                    dbUtil.saveOrUpdate(publicMessage);
                                } catch (DbException e1) {
                                    // TODO
                                    // Auto-generated
                                    // catch block
                                    e1.printStackTrace();
                                }
                                setResult(AndroidConfig.writeFriendResultCode);
                                AnimFinsh();

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (NetRequestException e) {
                                e.getError().print(mContext);
                                e.printStackTrace();
                            }

                        }
                    });
                }else{
                    ToastUtils.Errortoast(mContext,"当前网络不可用");
                }

                break;

        }
    }
}
