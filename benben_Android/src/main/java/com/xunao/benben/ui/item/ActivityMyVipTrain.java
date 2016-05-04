package com.xunao.benben.ui.item;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.NumberTrain;
import com.xunao.benben.bean.NumberTrainList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityNumberTrainMap;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2016/3/16.
 */
public class ActivityMyVipTrain extends BaseActivity implements View.OnClickListener{
    private String curNmae;
    private ListView listView;
    private LinearLayout ll_seach_icon;
    private LinearLayout ll_search_item;
    private LinearLayout no_data;
    private String searchKey = "";
    private myAdapter adapter;
    private Button btn_search_range;
    private NumberTrainList numberTrainList;
    private ImageView iv_search_content_delect;
    private ArrayList<NumberTrain> numberTrains = new ArrayList<NumberTrain>();
    private EditText search_edittext;
    private String phone;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_vip_train);
        initdefaultImage(R.drawable.ic_group_poster);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("我的会员号", "", "",
                R.drawable.icon_com_title_left, 0);

        search_edittext = (EditText) findViewById(R.id.search_edittext);
        ((TextView) findViewById(R.id.searchName)).setText("商铺简称/服务项目/店铺号");
        iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
        ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
        listView = (ListView) findViewById(R.id.listView);
        btn_search_range = (Button) findViewById(R.id.btn_search_range);
        no_data = (LinearLayout) findViewById(R.id.no_data);
        ll_search_item = (LinearLayout) findViewById(R.id.ll_search_item);
        ll_search_item.setVisibility(View.GONE);

        iv_search_content_delect.setOnClickListener(this);
        btn_search_range.setOnClickListener(this);
        btn_search_range.setText("搜索");

        adapter = new myAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).MyVipStore(searchKey, requestCallBack);
        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AnimFinsh();
            }
        });


        search_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    // 显示键盘
                    imm.showSoftInput(search_edittext, 0);
                }
            }
        });

        search_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (arg0.length() > 0) {
                    ll_seach_icon.setVisibility(View.GONE);
                    iv_search_content_delect.setVisibility(View.VISIBLE);
                } else {
                    ll_seach_icon.setVisibility(View.VISIBLE);
                    iv_search_content_delect.setVisibility(View.GONE);
                    searchKey = "";
                }
            }
        });

        search_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int actionId,
                                          KeyEvent arg2) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) search_edittext.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(mContext.getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    // 更新关键字
                    searchKey = search_edittext.getText().toString().trim();


                    InteNetUtils.getInstance(mContext).MyVipStore(searchKey, requestCallBack);
                    return true;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {

                    String id = numberTrains.get(position).getId();
                    String kil = numberTrains.get(position )
                            .getDistance_kilometers();
                    String shop = numberTrains.get(position ).getShop();
                    if(shop.contains(user.getBenbenId())){
                        startAnimActivity3Obj(ActivityMyNumberTrainDetail.class,
                                "id", id, "kil", kil);
                    }else {
                        startAnimActivity3Obj(ActivityNumberTrainDetail.class,
                                "id", id, "kil", kil);
                    }

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


    private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

        @Override
        public void onFailure(HttpException arg0, String arg1) {
            if (lodingDialog != null && lodingDialog.isShowing()) {
                lodingDialog.dismiss();
            }
//			ToastUtils.Errortoast(mContext, arg1);
        }

        @Override
        public void onSuccess(ResponseInfo<String> arg0) {
            if (lodingDialog != null && lodingDialog.isShowing()) {
                lodingDialog.dismiss();
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(arg0.result);
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtils.Infotoast(mContext, "网络不可用,请重试");

            }
            if(jsonObject!=null) {
                if (jsonObject.optInt("ret_num") == 0) {
                    numberTrainList = new NumberTrainList();
                    try {
                        numberTrainList.parseJSON(jsonObject);
                        if (numberTrainList == null || numberTrainList.getNumberTrains() == null || numberTrainList.getNumberTrains().size() == 0) {
                            numberTrains.clear();
                            no_data.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            no_data.setVisibility(View.GONE);
                            numberTrains = numberTrainList.getNumberTrains();
                        }


                    } catch (NetRequestException e) {
                        e.getError().print(mContext);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.Infotoast(mContext, jsonObject.optString("ret_message"));
                }
            }
        }

    };

    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return numberTrains.size();
        }

        @Override
        public Object getItem(int arg0) {
            return numberTrains.get(arg0);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_my_vip_train, null);
                }

                TextView number_train_title = ViewHolderUtil.get(convertView,
                        R.id.number_train_title);
                RoundedImageView iv_face = ViewHolderUtil.get(convertView,
                        R.id.iv_face);
                ImageView iv_call_phone = ViewHolderUtil.get(convertView,
                        R.id.iv_call_phone);



                final NumberTrain numberTrain = numberTrains.get(position);


                iv_call_phone.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        setTheme(R.style.ActionSheetStyleIOS7);
                        phone = numberTrain.getPhone();
                        String telPhone = numberTrain.getTelePhone();
                        String telPhones = phone + "," + telPhone;
                        String[] phones = telPhones.split(",");

                        curNmae = numberTrain.getName();

                        int cid = Integer.parseInt(numberTrain.getId());
                        showActionSheet(cid,phones);

                    }
                });
                number_train_title.setText(numberTrain.getShortName());
                CommonUtils.startImageLoader(cubeimageLoader,
                        numberTrain.getPoster(), iv_face);
            return convertView;
        }

    }

    public void showActionSheet(final int cid,final String[] phone) {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(phone)
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        switch (index) {
                            case 0:
                                PhoneUtils.makeCall(cid, curNmae, phone[0], mContext);
                                break;
                            case 1:
                                PhoneUtils.makeCall(cid,curNmae, phone[1], mContext);
                                break;
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {

                    }
                }).show();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            // 搜索删除内容
            case R.id.iv_search_content_delect:
                iv_search_content_delect.setVisibility(View.GONE);
                searchKey = "";
                ll_seach_icon.setVisibility(View.VISIBLE);
                search_edittext.setText("");
                // 影藏键盘
                ((InputMethodManager) search_edittext.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(mContext.getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                break;

            // 选择搜索范围
            case R.id.btn_search_range:
                // 更新关键字
                searchKey = search_edittext.getText().toString().trim();

                InteNetUtils.getInstance(mContext).MyVipStore(searchKey, requestCallBack);
                break;

            default:
                break;
        }
    }

}
