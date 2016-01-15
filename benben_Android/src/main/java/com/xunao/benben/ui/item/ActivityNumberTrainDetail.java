package com.xunao.benben.ui.item;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.NumberTrainDetail;
import com.xunao.benben.bean.NumberTrainPoster;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.Promotion;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.TrainNoticeDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.ActivityNumberTrainComment;
import com.xunao.benben.ui.ActivityNumberTrainDetailMap;
import com.xunao.benben.ui.ActivityNumberTrainStore;
import com.xunao.benben.ui.ActivityWeb;
import com.xunao.benben.ui.groupbuy.ActivityGroupBuyDetail;
import com.xunao.benben.ui.order.ActivityMakeOrder;
import com.xunao.benben.ui.promotion.ActivityMyPromotionAlbum;
import com.xunao.benben.ui.promotion.ActivityPromotionAlbum;
import com.xunao.benben.ui.promotion.ActivityPromotionDetail;
import com.xunao.benben.ui.shareselect.ActivityShareSelectFriend;
import com.xunao.benben.ui.shareselect.ActivityShareSelectTalkGroup;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;
import com.xunao.benben.view.CircularImage;
import com.xunao.benben.view.NoScrollGridView;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;

public class ActivityNumberTrainDetail extends BaseActivity implements
		OnClickListener {
	private CircularImage iv_poster;
	private TextView tv_name;
    private TextView tv_shop;
	private TextView tv_industry;
	private TextView tv_address;
	private LinearLayout ll_sendmsg;
	private LinearLayout ll_make_phone;
	private TextView tv_description;
	private TextView tv_views;
	private LinearLayout ll_tag;
	private LinearLayout ll_position;
	private ImageView iv_collect;
	private NumberTrainDetail numberTrainDetail;
	private TextView tv_collect_number;
    private int collect_num=0;
	private String phone;
	private String numberId;
	private boolean isCollect;
	private Contacts contacts;
	private int[] color = { Color.parseColor("#42be69"),
			Color.parseColor("#e69a15"), Color.parseColor("#9b41e0") };
	private int[] background = { R.drawable.textview_bg_1,
			R.drawable.textview_bg_2, R.drawable.textview_bg_3, };

    private LinearLayout ll_authentication;
    private NoScrollGridView item_gridView;
    private MyGridViewAdapter adapter;
    private ArrayList<Promotion> promotions = new ArrayList<>();
    private ListView item_group_buy;
    private GroupBuyAdapter buyAdapter;
    private LinearLayout ll_promotion_album;
    private String ids="";

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
    private TextView tv_consultant;

    private int vip_type=0;
    private TextView tv_type;
    private int[][] identityIcons={{R.drawable.icon_blue_identity,R.drawable.icon_blue_xin, R.drawable.icon_blue_bao},
            {R.drawable.icon_red_identity,R.drawable.icon_red_xin, R.drawable.icon_red_bao}};
    private int[] rankTypes = {R.drawable.icon_rating_select1,R.drawable.icon_rating_select2,R.drawable.icon_rating_select3,R.drawable.icon_rating_select4};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_number_train_detail);
		setShowLoding(false);
        finalBitmap = FinalBitmap.create(mContext);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("商家详情", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_share);
		iv_poster = (CircularImage) findViewById(R.id.iv_poster);
        tv_poster_num = (TextView) findViewById(R.id.tv_poster_num);
		tv_name = (TextView) findViewById(R.id.tv_name);
        tv_shop = (TextView) findViewById(R.id.tv_shop);
		tv_industry = (TextView) findViewById(R.id.tv_industry);
		tv_address = (TextView) findViewById(R.id.tv_address);
        ll_make_phone = (LinearLayout) findViewById(R.id.ll_make_phone);
		ll_tag = (LinearLayout) findViewById(R.id.ll_tag);
		tv_description = (TextView) findViewById(R.id.tv_description);
		tv_views = (TextView) findViewById(R.id.tv_views);
		iv_collect = (ImageView) findViewById(R.id.iv_collect);
		tv_collect_number = (TextView) findViewById(R.id.tv_collect_number);
        ll_sendmsg = (LinearLayout) findViewById(R.id.ll_sendmsg);
		ll_position = (LinearLayout) findViewById(R.id.ll_position);

        ll_authentication = (LinearLayout) findViewById(R.id.ll_authentication);
        item_gridView = (NoScrollGridView) findViewById(R.id.item_gridView);
        adapter = new MyGridViewAdapter();
        item_gridView.setAdapter(adapter);
        item_group_buy = (ListView) findViewById(R.id.item_group_buy);
        buyAdapter = new GroupBuyAdapter();
        item_group_buy.setAdapter(buyAdapter);
        ll_promotion_album = (LinearLayout) findViewById(R.id.ll_promotion_album);
        ll_promotion_album.setOnClickListener(this);

        ll_make_phone.setOnClickListener(this);
        ll_sendmsg.setOnClickListener(this);
		iv_collect.setOnClickListener(this);
		tv_address.setOnClickListener(this);
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
        tv_consultant = (TextView) findViewById(R.id.tv_consultant);
        tv_consultant.setOnClickListener(this);
        tv_type = (TextView) findViewById(R.id.tv_type);
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
				InteNetUtils.getInstance(mContext).getStoreListDetail(id,
						mCurrentLantitude + "", mCurrentLongitude + "",
						mRequestCallBack);
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
        setOnRightClickLinester(new OnClickListener() {
            @Override
            public void onClick(View view) {

                setTheme(R.style.ActionSheetStyleIOS7);
                showShareActionSheet();
            }
        });


		iv_poster.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				CommonUtils.showPoster(mContext, numberTrainDetail.getPoster(),
//						cubeimageLoader);
                if(images!=null) {
                    Intent intent = new Intent(ActivityNumberTrainDetail.this, ActivityContentPicSet.class);
                    intent.putExtra("IMAGES", images);
                    intent.putExtra("POSITION", 0);
                    ActivityNumberTrainDetail.this.startActivity(intent);
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
				String collection = numberTrainDetail.getCollection();
                promotions = numberTrainDetail.getPromotions();
                vip_type = numberTrainDetail.getVip_type();

//                adapter.notifyDataSetChanged();


				phone = numberTrainDetail.getPhone();
				numberId = numberTrainDetail.getId();

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

				if (collection.equals("1")) {
					iv_collect.setImageResource(R.drawable.icon_collect);
					isCollect = true;
				} else {
					iv_collect.setImageResource(R.drawable.icon_collect_normal);
					isCollect = false;
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
                tv_shop.setText("店铺号："+numberTrainDetail.getShop());
				tv_industry.setText(industry);
				tv_address.setText(address);
				tv_description.setText(description);
                tv_views.setText("访客:"+views + "人");
                collect_num = Integer.parseInt(numberTrainDetail.getCollecitonNumber());
                tv_collect_number.setText("收藏:"+collect_num + "人");

                if(numberTrainDetail.getNo_auth()==0){
                    ll_authentication.setVisibility(View.VISIBLE);
                    if(promotions!=null && promotions.size()>0){
                        for(int i=0;i<promotions.size();i++){
                            ids += promotions.get(i).getPromotionid()+";";
                        }
                        ids = ids.substring(0,ids.length()-1);
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
                        tv_type.setText("商家团购");
                        ll_train_level.setVisibility(View.VISIBLE);
                        int rank=numberTrainDetail.getRank();
                        if(rank==0){
                            ll_rank_num.setVisibility(View.GONE);
                        }else{
                            ll_rank_num.setVisibility(View.VISIBLE);

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
                    ll_authentication.setVisibility(View.GONE);
                }



//                tv_notice.setVisibility(View.VISIBLE);

				try {
					dbUtil.saveOrUpdate(numberTrainDetail);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
            // 发送短信
            case R.id.ll_sendmsg:
                startAnimActivity2Obj(ChatActivity.class, "userId",
                        numberTrainDetail.getHuanxinUsername());
                break;
            // 拨打电话
            case R.id.ll_make_phone:
                setTheme(R.style.ActionSheetStyleIOS7);

                String phonesNum = numberTrainDetail.getPhone() + ","
                        + numberTrainDetail.getTelPhone();

                String[] phones = phonesNum.split(",");
                showActionSheet(phones);
                break;
            // 收藏直通车
            case R.id.iv_collect:
                if (CommonUtils.isNetworkAvailable(mContext)) {
                    if (isCollect) {
                        InteNetUtils.getInstance(mContext).CancelCollectStore(
                                numberId, requestCallBack);
                        ToastUtils.Infotoast(mContext, "已取消收藏");
                        iv_collect.setImageResource(R.drawable.icon_collect_normal);
                        msg = "已取消收藏";
                    } else {
                        InteNetUtils.getInstance(mContext).collectStore(numberId,
                                requestCallBack);
                        ToastUtils.Infotoast(mContext, "店铺已收藏至通讯录");
                        iv_collect.setImageResource(R.drawable.icon_collect);
                        msg = "店铺已收藏至通讯录";
                    }
                }
                break;
            case R.id.tv_address:
                startAnimActivity2Obj(ActivityNumberTrainDetailMap.class,
                        "numberTrain", numberTrainDetail);
                break;
            case R.id.ll_promotion_album:
                Intent albumIntent = new Intent(ActivityNumberTrainDetail.this,ActivityPromotionAlbum.class);
                albumIntent.putExtra("id",id);
                startActivity(albumIntent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.ll_comment:
                Intent commentIntent = new Intent(ActivityNumberTrainDetail.this, ActivityNumberTrainComment.class);
                commentIntent.putExtra("store_id",id);
                commentIntent.putExtra("mean_rate",numberTrainDetail.getMean_rate());
                startActivity(commentIntent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.ll_store:
                Intent storeIntent = new Intent(ActivityNumberTrainDetail.this, ActivityNumberTrainStore.class);
                storeIntent.putExtra("trainid",id);
                storeIntent.putExtra("longitude",mCurrentLongitude);
                storeIntent.putExtra("latitude",mCurrentLantitude);
                startActivity(storeIntent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.tv_consultant:
                ToastUtils.Infotoast(mContext,"新功能开发中!");
//                setTheme(R.style.ActionSheetStyleIOS7);
//                String[] names = {"李先生","张某某"};
//                String[] benbenIds = {numberTrainDetail.getHuanxinUsername(),numberTrainDetail.getHuanxinUsername()};
//                showConsultantActionSheet(names,benbenIds);
                break;
            case R.id.tv_notice:
                TrainNoticeDialog hint = new TrainNoticeDialog(
                        mContext, R.style.MyDialog1);
                hint.setContent("我的公告");
                hint.show();
                break;
        }
	}

    private void showShareActionSheet() {
        ActionSheet
                .createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("分享给好友","分享到群组")
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF","#1E82FF")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index){
                            case 0:
                                Intent intent = new Intent(ActivityNumberTrainDetail.this, ActivityShareSelectFriend.class);
                                intent.putExtra("train_id",numberTrainDetail.getId());
                                intent.putExtra("train_name",numberTrainDetail.getShortName());
                                intent.putExtra("train_tag",numberTrainDetail.getTag());
                                intent.putExtra("train_poster",numberTrainDetail.getPoster());
                                intent.putExtra("shop",numberTrainDetail.getShop());
                                startActivity(intent);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                break;
                            case 1:
                                Intent groupintent = new Intent(ActivityNumberTrainDetail.this, ActivityShareSelectTalkGroup.class);
                                groupintent.putExtra("train_id",numberTrainDetail.getId());
                                groupintent.putExtra("train_name",numberTrainDetail.getShortName());
                                groupintent.putExtra("train_tag",numberTrainDetail.getTag());
                                groupintent.putExtra("train_poster",numberTrainDetail.getPoster());
                                groupintent.putExtra("shop",numberTrainDetail.getShop());
                                startActivity(groupintent);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                break;
                        }
                    }
                }).show();
    }

	private void showActionSheet(final String[] phones) {

		ActionSheet.createBuilder(mContext, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles(phones)
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {
						PhoneUtils.makeCall(Integer.parseInt(numberTrainDetail.getId()),numberTrainDetail.getName(),
								phones[index], mContext);

					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {
					}
				}).show();
	}

    public void showConsultantActionSheet(final String[] name,final String[] benbenids) {
        ActionSheet
                .createBuilder(mContext, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(name)
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheetListener() {

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        startAnimActivity2Obj(ChatActivity.class, "userId",
                                benbenids[index]);
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {
                        // TODO Auto-generated method stub

                    }
                }).show();
    }

	// 收藏添加到联系人数据库中
	protected void addNumberTraionCollection() {
		if (!isCollect) {
			try {
				int id = Integer.parseInt(numberTrainDetail.getId()) + 1000000;
				dbUtil.delete(PhoneInfo.class,
						WhereBuilder.b("contacts_id", "=", id + ""));
				dbUtil.delete(Contacts.class, WhereBuilder
						.b("id", "=", id + "").and("group_id", "=", "10000"));
			} catch (DbException e) {
				e.printStackTrace();
			}
		} else {
			try {
				contacts = new Contacts();
				int id = Integer.parseInt(numberTrainDetail.getId()) + 1000000;
				contacts.setId(id);
				contacts.setGroup_id("10000");
				contacts.setName(numberTrainDetail.getShortName());
				contacts.setPoster(numberTrainDetail.getPoster());
				contacts.setIs_baixing("0");
				contacts.setIs_benben("0");
				dbUtil.save(contacts);

				PhoneInfo phoneInfo = new PhoneInfo();
				phoneInfo.setIs_baixing("0");
				phoneInfo.setContacts_id(id);
				phoneInfo.setIs_benben("0");
				phoneInfo.setName(numberTrainDetail.getName());
				phoneInfo.setPoster(numberTrainDetail.getPoster());
				phoneInfo.setPhone(numberTrainDetail.getPhone());
				dbUtil.save(phoneInfo);
				phoneInfo.setPhone(numberTrainDetail.getTelPhone());
				dbUtil.save(phoneInfo);

			} catch (DbException e) {
				e.printStackTrace();
			}
		}

		sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			String result = arg0.result;
			try {
				JSONObject jsonObject = new JSONObject(result);
				String ret_num = jsonObject.optString("ret_num");
				String ret_msg = jsonObject.optString("ret_msg");
				if ("0".equals(ret_num)) {
					ToastUtils.Infotoast(mContext, msg);
					if (isCollect) {
						numberTrainDetail.setCollection("0");
						isCollect = false;
                        collect_num--;
                        tv_collect_number.setText("收藏:"+collect_num + "人");
//						tv_collect_number.setText((collect_num - 1 >= 0 ? collect_num - 1 : 0)
//								+ "");
						iv_collect
								.setImageResource(R.drawable.icon_collect_normal);
					} else {
						numberTrainDetail.setCollection("1");
						isCollect = true;
                        collect_num++;
                        tv_collect_number.setText("收藏:"+collect_num + "人");
						iv_collect.setImageResource(R.drawable.icon_collect);
					}

					addNumberTraionCollection();

					try {
						dbUtil.saveOrUpdate(numberTrainDetail);
					} catch (DbException e) {
						e.printStackTrace();
					}
				} else {
					if(ret_num.equals("2015") ){
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
						ToastUtils.Errortoast(mContext, ret_msg);
					}
					
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, e.getLocalizedMessage());
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			if (isCollect) {
				ToastUtils.Infotoast(mContext, "取消收藏直通车失败！");
			} else {
				ToastUtils.Infotoast(mContext, "收藏直通车失败！");
			}
		}
	};
	private String msg;
	private LocationClient mLocClient;
	private String id;
	private boolean isFristLocation = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            InteNetUtils.getInstance(mContext).getStoreListDetail(id,
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_promotion_gridview, null);
            }
            Promotion promotion = promotions.get(position);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
//            TextView tv_edittxt = (TextView) convertView
//                    .findViewById(R.id.tv_edittxt);
//            ImageView iv_over_time = (ImageView) convertView.findViewById(R.id.iv_over_time);
//
//            if(numberTrainDetail.getIs_promotion()==0) {
//
//                tv_edittxt.setText("商品已下架");
//                tv_edittxt.setVisibility(View.VISIBLE);
//            }else if(promotion.getIs_down()==1){
//                iv_over_time.setVisibility(View.VISIBLE);
//            }else{
//                tv_edittxt.setVisibility(View.GONE);
//                iv_over_time.setVisibility(View.GONE);
//            }
            tv_title.setText(promotion.getName());
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_promotion);
            iv_promotion.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (mScreenWidth- PixelUtil.dp2px(30))/3));
            if (!TextUtils.isEmpty(promotion.getSmall_poster())) {
                CommonUtils.startImageLoader(cubeimageLoader,promotion.getSmall_poster(), iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityNumberTrainDetail.this, ActivityPromotionDetail.class);
                    intent.putExtra("ids", ids);
                    intent.putExtra("position", position);
                    startActivity(intent);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_group_buy_listview, null);
            }
            final Promotion promotion = promotions.get(position);
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_group_buy);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            tv_title.setText(promotion.getName());
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
                    Intent intent = new Intent(ActivityNumberTrainDetail.this, ActivityGroupBuyDetail.class);
                    intent.putExtra("ids", ids);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });
            return convertView;
        }

    }
}
