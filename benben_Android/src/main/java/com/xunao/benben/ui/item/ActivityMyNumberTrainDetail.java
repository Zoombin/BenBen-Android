package com.xunao.benben.ui.item;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.R.drawable;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.NumberTrainDetail;
import com.xunao.benben.bean.NumberTrainPoster;
import com.xunao.benben.bean.Promotion;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.HelpcollectDialog;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.ActivityMyWeb;
import com.xunao.benben.ui.ActivityNumberTrainComment;
import com.xunao.benben.ui.ActivityNumberTrainDetailMap;
import com.xunao.benben.ui.ActivityNumberTrainStore;
import com.xunao.benben.ui.groupbuy.ActivityGroupBuyManage;
import com.xunao.benben.ui.promotion.ActivityMyPromotionAlbum;
import com.xunao.benben.ui.promotion.ActivityPromotionManage;
import com.xunao.benben.ui.shareselect.ActivityShareSelectFriend;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.CircularImage;
import com.xunao.benben.view.NoScrollGridView;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.srain.cube.image.CubeImageView;

public class ActivityMyNumberTrainDetail extends BaseActivity implements
		OnClickListener, ActionSheet.ActionSheetListener {
    private ScrollView scroll;
	private CircularImage iv_poster;
	private TextView tv_name;
    private TextView tv_shop;
	private TextView tv_industry;
	private TextView tv_address;
	private TextView tv_description;
	private TextView tv_views;
	private LinearLayout ll_tag;
	private LinearLayout ll_position;
	private NumberTrainDetail numberTrainDetail;
	private TextView tv_collect_number;
	private int[] color = { Color.parseColor("#42be69"),
			Color.parseColor("#e69a15"), Color.parseColor("#9b41e0") };
	private int[] background = { drawable.textview_bg_1,
			drawable.textview_bg_2, drawable.textview_bg_3, };

    private LinearLayout ll_identity,ll_authentication;
    private Button btn_promotion;
    private Button btn_group_buy;
    private NoScrollGridView item_gridView;
    private ListView item_group_buy;
    private MyGridViewAdapter adapter;
    private GroupBuyAdapter buyAdapter;
    private LinearLayout ll_promotion_album;
    private ArrayList<Promotion> promotions = new ArrayList<>();
    private TextView wx_message;
    private TextView tv_vip_time;
    private FinalBitmap finalBitmap;
    private LinearLayout ll_train_level;
    private LinearLayout ll_rank_num;
    private TextView tv_level;
    private LinearLayout ll_comment;
    private TextView tv_mean_rate,tv_comment_num;
    private TextView tv_poster_num;
    private ArrayList<NumberTrainPoster> numberTrainPosters = new ArrayList<>();
    private String images;
    private ImageView iv_auto_type;
    private LinearLayout ll_store;
    private TextView tv_store_num;
    private TextView tv_notice;
    private int vip_type=0;
    private TextView tv_type;
    private int[][] identityIcons={{R.drawable.icon_blue_identity,R.drawable.icon_blue_xin, R.drawable.icon_blue_bao},
            {R.drawable.icon_red_identity,R.drawable.icon_red_xin, R.drawable.icon_red_bao}};
    private int[] rankTypes = {R.drawable.icon_rating_select1,R.drawable.icon_rating_select2,R.drawable.icon_rating_select3,R.drawable.icon_rating_select4};
//    private ImageView com_title_bar_share;
    private Button btn_renew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my_number_train_detail);
		setShowLoding(false);
        finalBitmap = FinalBitmap.create(mContext);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("我的号码直通车", "", "",
                drawable.icon_com_title_left, drawable.ic_addwihte);
        scroll = (ScrollView) findViewById(R.id.scroll);
        scroll.smoothScrollTo(0,0);
		iv_poster = (CircularImage) findViewById(R.id.iv_poster);
        tv_poster_num = (TextView) findViewById(R.id.tv_poster_num);
		tv_name = (TextView) findViewById(R.id.tv_name);
        tv_shop = (TextView) findViewById(R.id.tv_shop);
		tv_industry = (TextView) findViewById(R.id.tv_industry);
		tv_address = (TextView) findViewById(R.id.tv_address);

		ll_tag = (LinearLayout) findViewById(R.id.ll_tag);
		tv_description = (TextView) findViewById(R.id.tv_description);
		tv_views = (TextView) findViewById(R.id.tv_views);
		tv_collect_number = (TextView) findViewById(R.id.tv_collect_number);

		ll_position = (LinearLayout) findViewById(R.id.ll_position);
		tv_address.setOnClickListener(this);

        ll_identity = (LinearLayout) findViewById(R.id.ll_identity);
        ll_authentication = (LinearLayout) findViewById(R.id.ll_authentication);
        btn_promotion = (Button) findViewById(R.id.btn_promotion);
        btn_promotion.setOnClickListener(this);
        btn_group_buy = (Button) findViewById(R.id.btn_group_buy);
        btn_group_buy.setOnClickListener(this);

        item_gridView = (NoScrollGridView) findViewById(R.id.item_gridView);
        adapter = new MyGridViewAdapter();
        item_gridView.setAdapter(adapter);

        item_group_buy = (ListView) findViewById(R.id.item_group_buy);
        buyAdapter = new GroupBuyAdapter();
        item_group_buy.setAdapter(buyAdapter);

        ll_promotion_album = (LinearLayout) findViewById(R.id.ll_promotion_album);
        ll_promotion_album.setOnClickListener(this);
        wx_message = (TextView) findViewById(R.id.wx_message);
        wx_message.setOnClickListener(this);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_vip_time = (TextView) findViewById(R.id.tv_vip_time);
        ll_train_level = (LinearLayout) findViewById(R.id.ll_train_level);
        ll_rank_num = (LinearLayout) findViewById(R.id.ll_rank_num);
        tv_level = (TextView) findViewById(R.id.tv_level);
        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        ll_comment.setOnClickListener(this);
        tv_mean_rate = (TextView) findViewById(R.id.tv_mean_rate);
        tv_comment_num = (TextView) findViewById(R.id.tv_comment_num);
        iv_auto_type = (ImageView) findViewById(R.id.iv_auto_type);
        ll_store = (LinearLayout) findViewById(R.id.ll_store);
        ll_store.setOnClickListener(this);
        tv_store_num = (TextView) findViewById(R.id.tv_store_num);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_notice.setOnClickListener(this);

        btn_renew = (Button) findViewById(R.id.btn_renew);
        btn_renew.setOnClickListener(this);

		initMyLocation();
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
	}

	/**
	 * 初始化定位相关代码
	 */
	private void initMyLocation() {
		showLoding("请稍后...");
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		// 设置定位的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1);
		mLocClient.setLocOption(option);
	}

	private MyLocationListener myListener = new MyLocationListener();
	private double mCurrentLantitude;
	private double mCurrentLongitude;


    /**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;

			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();

			if (isFristLocation) {
				isFristLocation = false;
				InteNetUtils.getInstance(mContext).getOwnerDetail(id,
                        mCurrentLantitude + "", mCurrentLongitude + "",
                        mRequestCallBack);

                //群组公告弹窗
                getPopInfo();
			}
		}

	}

	@Override
	protected void onStart() {
		// 开启图层定位
		if (!mLocClient.isStarted()) {
			mLocClient.start();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mLocClient != null)
			mLocClient.stop();
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});
//        setOnRightClickLinester(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(ActivityMyNumberTrainDetail.this,ActivityMyNumberTrain.class);
////                intent.putExtra("do", "update");
////                startActivityForResult(intent, 1);
////                overridePendingTransition(
////                        R.anim.in_from_right, R.anim.out_to_left);
//                Intent intent = new Intent(ActivityMyNumberTrainDetail.this,ActivityPromotionManage.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        });


		iv_poster.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				CommonUtils.showPoster(mContext, numberTrainDetail.getPoster(),
//						cubeimageLoader);
                if(images!=null) {
                    Intent intent = new Intent(ActivityMyNumberTrainDetail.this, ActivityContentPicSet.class);
                    intent.putExtra("IMAGES", images);
                    intent.putExtra("POSITION", 0);
                    ActivityMyNumberTrainDetail.this.startActivity(intent);
                }
			}
		});
	}

    private void initRightClick(final int type){
        setOnRightClickLinester(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.ActionSheetStyleIOS7);
                showMoreActionSheet(type);
            }
        });

    }

    public void showMoreActionSheet(int type) {
        if(type==1){
            ActionSheet
                    .createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("编辑")
                            // 设置颜色 必须一一对应
                    .setOtherButtonTitlesColor("#1E82FF")
                    .setCancelableOnTouchOutside(true)
                    .setListener(this).show();
        }else if(type==0){
            String text = "";
            if(vip_type==0){
                text = "促销管理";
            }else{
                text = "团购管理";
            }
            ActionSheet
                    .createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("详情编辑",text)
                            // 设置颜色 必须一一对应
                    .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
                    .setCancelableOnTouchOutside(true)
                    .setListener(this).show();
        }

    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index){
            case 0:
                Intent intent = new Intent(ActivityMyNumberTrainDetail.this,ActivityMyNumberTrain.class);
                intent.putExtra("do", "update");
                startActivityForResult(intent, 1);
                overridePendingTransition(
                        R.anim.in_from_right, R.anim.out_to_left);
                break;
            case 1:
                if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getAuth_status()==1) {
                    ToastUtils.Infotoast(mContext,"请先通过实名认证!");
                    return;
                }
                if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getIs_overtime()==1) {
                    ToastUtils.Infotoast(mContext,"您的套餐已经到期!");
                    return;
                }
                Intent intentPromotion = new Intent();
                if(vip_type==0){
                    intentPromotion.setClass(ActivityMyNumberTrainDetail.this, ActivityPromotionManage.class);
                }else{
                    intentPromotion.setClass(ActivityMyNumberTrainDetail.this, ActivityGroupBuyManage.class);
                }
//                Intent intentPromotion = new Intent(ActivityMyNumberTrainDetail.this, ActivityPromotionManage.class);
//                Intent intentPromotion = new Intent(ActivityMyNumberTrainDetail.this, ActivityGroupBuyManage.class);
                startActivityForResult(intentPromotion, 2);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                break;
        }
    }

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String optString = jsonObject.optString("ret_num");
		dissLoding();
		if (optString != null) {
			if (!optString.equals("0")) {
				String errorMsg = jsonObject.optString("ret_msg");

				if(jsonObject.optString("ret_num").equals("2015") ){
					final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
							mContext, R.style.MyDialog1);
					hint.setContent("奔犇账号在其他手机登录");
					hint.setBtnContent("确定");
					hint.show();
					hint.setOKListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							hint.dismiss();
						}
					});

					hint.show();
					CrashApplication.getInstance().logout();
					startActivity(new Intent(mContext, ActivityLogin.class));
				}else{
					ToastUtils.Infotoast(mContext, errorMsg);
				}
				return;
			}
			try {
				numberTrainDetail = new NumberTrainDetail();
				JSONObject object = jsonObject.optJSONObject("number_info");
				numberTrainDetail.parseJSONMyNumberTrain(object);
				String poster = numberTrainDetail.getPoster();
				String name = numberTrainDetail.getName();
				String industry = numberTrainDetail.getIndustry();
				String address = numberTrainDetail.getAddress();
				String description = numberTrainDetail.getDescription();
				String tag = numberTrainDetail.getTag();
				String views = numberTrainDetail.getViews();
                promotions = numberTrainDetail.getPromotions();
                vip_type = numberTrainDetail.getVip_type();

                ll_tag.removeAllViews();
				String[] tagArr = tag.split("\\s+");

				int tagArrLen = 0;
				if(tagArr.length > 3){
					tagArrLen = 3;
				}else{
					tagArrLen = tagArr.length;
				}

				for (int i = 0; i < tagArrLen; i++) {
					if (TextUtils.isEmpty(tagArr[i])) {
						continue;
					}
					View view = ll_tag.inflate(mContext,
							R.layout.textview_tag_item, null);
					TextView tv_tag = (TextView) view.findViewById(R.id.tv_tag);
					tv_tag.setText(tagArr[i]);
					tv_tag.setTextColor(color[i]);
					tv_tag.setBackgroundResource(background[i]);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.rightMargin = 10;
					ll_tag.addView(view, params);
				}

                if(poster.equals("")){
                    iv_poster.setImageResource(R.drawable.icon_upload_face);
                }else {
                    finalBitmap.display(iv_poster, poster);
                }
                numberTrainPosters = numberTrainDetail.getNumberTrainPosters();
                images=null;
                if(numberTrainPosters==null || numberTrainPosters.size()==0){
                    if (!TextUtils.isEmpty(poster)) {
                        images = poster;
                        tv_poster_num.setText("1张");
                    }else{
                        tv_poster_num.setText("0张");
                    }
                }else{
                    tv_poster_num.setText(numberTrainPosters.size()+"张");
                    for (int i = 0; i < numberTrainPosters.size(); i++) {
                        if (images != null) {
                            images += "^" + numberTrainPosters.get(i).getPoster();
                        } else {
                            images = numberTrainPosters.get(i).getPoster();
                        }
                    }
                }

//				CommonUtils
//						.startImageLoader(cubeimageLoader, poster, iv_poster);
				tv_name.setText(name);
                tv_shop.setText("店铺号:"+numberTrainDetail.getShop());
				tv_industry.setText(industry);
				tv_address.setText(address);
				tv_description.setText(description);
				tv_views.setText("访客:"+views + "人");
				tv_collect_number.setText("收藏:"+numberTrainDetail
						.getCollecitonNumber() + "人");
//				try {
//					dbUtil.saveOrUpdate(numberTrainDetail);
//				} catch (DbException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long vipTime = numberTrainDetail.getVip_time();
                Date date = new Date(vipTime*1000);
                tv_vip_time.setText("有效期至:"+simpleDateFormat.format(date));

                if(numberTrainDetail.getNo_auth()==0){
                    ll_identity.setVisibility(View.GONE);
                    ll_authentication.setVisibility(View.VISIBLE);
                    wx_message.setVisibility(View.VISIBLE);
                    if(numberTrainDetail.getAuth_status()==0){
                        wx_message.setText("实名认证审核中");
                    }else if(numberTrainDetail.getAuth_status()==1){
                        wx_message.setText("实名认证审核失败，点击进入重新提交");
                    }else if(numberTrainDetail.getAuth_status()==2){
                        wx_message.setText("实名认证通过");
                    }

                    if(numberTrainDetail.getAuth_grade()==0){
                        iv_auto_type.setVisibility(View.GONE);
                    }else{
                        iv_auto_type.setImageResource(identityIcons[numberTrainDetail.getType()-1][numberTrainDetail.getAuth_grade()-1]);
                        iv_auto_type.setVisibility(View.VISIBLE);
                    }


                    if(vip_type==0){
                        tv_type.setText("商家促销");
                        ll_train_level.setVisibility(View.GONE);
                        ll_comment.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        item_gridView.setVisibility(View.VISIBLE);
                    }else {
                        tv_notice.setVisibility(View.VISIBLE);
                        tv_type.setText("商家团购");
                        ll_train_level.setVisibility(View.VISIBLE);
                        int rank=numberTrainDetail.getRank();
                        if(rank==0){
                            ll_rank_num.setVisibility(View.GONE);
                        }else{
                            ll_rank_num.setVisibility(View.VISIBLE);
                            ll_rank_num.removeAllViews();
                            int rankType = rank/5;
                            int rankNum = rank%5;
                            if(rankNum==0){
                                rankType--;
                                rankNum=5;
                            }
                            for(int i=0;i<rankNum;i++) {
                                ImageView iv_rank = new ImageView(mContext);
                                iv_rank.setImageResource(rankTypes[rankType]);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                                params.rightMargin = 5;
                                ll_rank_num.addView(iv_rank, params);
                            }
                        }
                        tv_level.setText("("+rank+"级)");
                        ll_comment.setVisibility(View.VISIBLE);
                        tv_mean_rate.setText("好评率： "+numberTrainDetail.getMean_rate());
                        tv_comment_num.setText("共"+numberTrainDetail.getNum()+"条评论");
                        buyAdapter.notifyDataSetChanged();
                        item_group_buy.setVisibility(View.VISIBLE);
                        if(numberTrainDetail.getShopnum()>0){
                            ll_store.setVisibility(View.VISIBLE);
                            tv_store_num.setText(numberTrainDetail.getShopnum()+"家");
                        }else{
                            ll_store.setVisibility(View.GONE);
                        }

                    }

                }else{
                    ll_identity.setVisibility(View.VISIBLE);
                    ll_authentication.setVisibility(View.GONE);
                    wx_message.setVisibility(View.GONE);
                }

                initRightClick(numberTrainDetail.getNo_auth());

			} catch (NetRequestException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, e.getError().toString());
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络请求失败，请重试！");
		dissLoding();
	}


    public void getPopInfo(){
        //群组公告弹窗
        InteNetUtils.getInstance(mContext).popContent(user.getToken(), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseInfo.result);
                    String optString = jsonObject.optString("ret_num");
                    if (optString != null) {
                        if (optString.equals("0")) {
                            String views = jsonObject.optString("views");
                            String over_rate = jsonObject.optString("over_rate");

                            HelpcollectDialog dialog = new HelpcollectDialog(mContext,R.style.MyDialog1);
                            dialog.setData(views,over_rate);
                            dialog.setHelpcollectListener(new HelpcollectDialog.HelpcollectListener() {
                                @Override
                                public void iknow() {
                                }

                                @Override
                                public void continuer() {
                                   startActivity(new Intent(mContext,ActivityHelpCollect.class));
                                }
                            });
                            dialog.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
            case R.id.tv_address:
                startAnimActivity2Obj(ActivityNumberTrainDetailMap.class,
                        "numberTrain", numberTrainDetail);
                break;
            case R.id.btn_promotion:
                Intent intent = new Intent(ActivityMyNumberTrainDetail.this,ActivityNumberTrainPromotionIdentity.class);
                intent.putExtra("from","promotion");
                startActivityForResult(intent,3);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.btn_group_buy:
                Intent groupBuyTntent = new Intent(ActivityMyNumberTrainDetail.this,ActivityNumberTrainPromotionIdentity.class);
                groupBuyTntent.putExtra("from","groupbuy");
                startActivityForResult(groupBuyTntent,3);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.ll_promotion_album:
                if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getAuth_status()==1) {
                    ToastUtils.Infotoast(mContext,"请先通过实名认证!");
                }else if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getIs_overtime()==1) {
                    ToastUtils.Infotoast(mContext,"您的套餐已经到期!");
                }else {
                    Intent albumIntent = new Intent(ActivityMyNumberTrainDetail.this, ActivityMyPromotionAlbum.class);
                    albumIntent.putExtra("id", id);
                    startActivity(albumIntent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
            case R.id.wx_message:
                if(numberTrainDetail.getAuth_status()==1) {
                    Intent identityintent = new Intent(ActivityMyNumberTrainDetail.this, ActivityNumberTrainPromotionIdentity.class);
                    startActivity(identityintent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
            case R.id.ll_comment:
                if(numberTrainDetail.getNum()==0){
                    ToastUtils.Infotoast(mContext,"暂无评价");
                }else {

                    Intent commentIntent = new Intent(ActivityMyNumberTrainDetail.this, ActivityNumberTrainComment.class);
                    commentIntent.putExtra("store_id", id);
                    commentIntent.putExtra("mean_rate", numberTrainDetail.getMean_rate());
                    startActivity(commentIntent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
            case R.id.ll_store:
                Intent storeIntent = new Intent(ActivityMyNumberTrainDetail.this, ActivityNumberTrainStore.class);
                storeIntent.putExtra("trainid",id);
                storeIntent.putExtra("longitude",mCurrentLongitude);
                storeIntent.putExtra("latitude",mCurrentLantitude);
                startActivity(storeIntent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.tv_notice:
                Intent noticeIntent = new Intent(ActivityMyNumberTrainDetail.this, ActivityMyNumberTrainNotice.class);
                noticeIntent.putExtra("bulletin",numberTrainDetail.getBulletin());
                noticeIntent.putExtra("shop",numberTrainDetail.getShop());
                startActivityForResult(noticeIntent,5);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.btn_renew:
                if(vip_type==0){
                    Intent promotionIntent = new Intent(mContext, ActivityMyWeb.class);
                    promotionIntent.putExtra("title", "促销");
                    promotionIntent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService/serviceDetail?type=0&token="+user.getToken());
                    startActivity(promotionIntent);
                }else{
                    Intent groupBuyIntent = new Intent(mContext, ActivityMyWeb.class);
                    groupBuyIntent.putExtra("title", "团购");
                    groupBuyIntent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService/serviceDetail?type=1&token="+user.getToken());
                    startActivity(groupBuyIntent);
                }

                break;

		}
	}



    private LocationClient mLocClient;
	private String id;
	private boolean isFristLocation = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            InteNetUtils.getInstance(mContext).getOwnerDetail(id,
                    mCurrentLantitude + "", mCurrentLongitude + "",
                    mRequestCallBack);
        }
    }

    private class MyGridViewAdapter extends BaseAdapter {

        public MyGridViewAdapter() {
            super();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return promotions.size();
        }

        @Override
        public Promotion getItem(int position) {
            // TODO Auto-generated method stub
            return promotions.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_promotion_gridview, null);
            }
            Promotion promotion = promotions.get(position);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            TextView tv_edittxt = (TextView) convertView
                    .findViewById(R.id.tv_edittxt);
            ImageView iv_over_time = (ImageView) convertView.findViewById(R.id.iv_over_time);

            TextView tv_price = (TextView) convertView
                    .findViewById(R.id.tv_price);
            tv_price.setText(promotion.getPromotion_price()+"元");

            if(numberTrainDetail.getIs_promotion()==0) {

                tv_edittxt.setText("商品已下架");
                tv_edittxt.setVisibility(View.VISIBLE);
            }else if(promotion.getIs_down()==1){
                iv_over_time.setVisibility(View.VISIBLE);
            }else{
                tv_edittxt.setVisibility(View.GONE);
                iv_over_time.setVisibility(View.GONE);
            }


            tv_title.setText(promotion.getName());
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_promotion);
//
//            iv_promotion.setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, PixelUtil
//                            .dp2px(60)));

            iv_promotion.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (mScreenWidth-PixelUtil.dp2px(30))/3));
            if (!TextUtils.isEmpty(promotion.getSmall_poster())) {
                CommonUtils.startImageLoader(cubeimageLoader,promotion.getSmall_poster(), iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getAuth_status()==1) {
                        ToastUtils.Infotoast(mContext,"请先通过实名认证!");
                        return;
                    }
                    if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getIs_overtime()==1) {
                        ToastUtils.Infotoast(mContext,"您的套餐已经到期!");
                        return;
                    }
                    Intent intentPromotion = new Intent();
                    if(vip_type==0){
                        intentPromotion.setClass(ActivityMyNumberTrainDetail.this, ActivityPromotionManage.class);
                    }else{
                        intentPromotion.setClass(ActivityMyNumberTrainDetail.this, ActivityGroupBuyManage.class);
                    }
//                Intent intentPromotion = new Intent(ActivityMyNumberTrainDetail.this, ActivityPromotionManage.class);
//                Intent intentPromotion = new Intent(ActivityMyNumberTrainDetail.this, ActivityGroupBuyManage.class);
                    startActivityForResult(intentPromotion, 2);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });
            return convertView;
        }

    }

    private class GroupBuyAdapter extends BaseAdapter {

        public GroupBuyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return promotions.size();
        }

        @Override
        public Promotion getItem(int position) {
            // TODO Auto-generated method stub
            return promotions.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_group_buy_listview, null);
            }
            Promotion promotion = promotions.get(position);
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_group_buy);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            tv_title.setText(promotion.getName());

            TextView tv_edittxt = (TextView) convertView
                    .findViewById(R.id.tv_edittxt);
            ImageView iv_over_time = (ImageView) convertView.findViewById(R.id.iv_over_time);

            if(numberTrainDetail.getIs_promotion()==0) {

                tv_edittxt.setText("商品已下架");
                tv_edittxt.setVisibility(View.VISIBLE);
            }else if(promotion.getIs_down()==1){
                iv_over_time.setVisibility(View.VISIBLE);
            }else{
                tv_edittxt.setVisibility(View.GONE);
                iv_over_time.setVisibility(View.GONE);
            }

            TextView tv_new_price = (TextView) convertView.findViewById(R.id.tv_new_price);
            tv_new_price.setText(promotion.getPromotion_price()+"");
            TextView tv_origin_price = (TextView) convertView.findViewById(R.id.tv_origin_price);
            tv_origin_price.setText("原价:"+promotion.getOrigion_price()+"元");
            tv_origin_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            tv_num.setText("已售:"+promotion.getSellcount());


            if (!TextUtils.isEmpty(promotion.getSmall_poster())) {
                CommonUtils.startImageLoader(cubeimageLoader,promotion.getSmall_poster(), iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getAuth_status()==1) {
                        ToastUtils.Infotoast(mContext,"请先通过实名认证!");
                        return;
                    }
                    if(numberTrainDetail.getIs_promotion()==0 && numberTrainDetail.getIs_overtime()==1) {
                        ToastUtils.Infotoast(mContext,"您的套餐已经到期!");
                        return;
                    }
                    Intent intentPromotion = new Intent();
                    if(vip_type==0){
                        intentPromotion.setClass(ActivityMyNumberTrainDetail.this, ActivityPromotionManage.class);
                    }else{
                        intentPromotion.setClass(ActivityMyNumberTrainDetail.this, ActivityGroupBuyManage.class);
                    }
//                Intent intentPromotion = new Intent(ActivityMyNumberTrainDetail.this, ActivityPromotionManage.class);
//                Intent intentPromotion = new Intent(ActivityMyNumberTrainDetail.this, ActivityGroupBuyManage.class);
                    startActivityForResult(intentPromotion, 2);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });
            return convertView;
        }

    }
}
