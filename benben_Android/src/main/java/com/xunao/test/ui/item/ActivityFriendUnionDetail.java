package com.xunao.test.ui.item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.FriendUnion;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.dialog.InputDialog;
import com.xunao.test.dialog.MsgDialog;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.view.MyTextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityFriendUnionDetail extends BaseActivity implements
		OnClickListener {
	private static final int ADD_BULLETIN = 1000;
	private static final int UPDATE_FRIEND_UNION = 1001;
	private static final int UPDATE_FRIEND_MEMBER = 1002;
	private RelativeLayout friend_union_header;
	private RoundedImageView friend_union_poster;
	private MyTextView friend_union_name;
	private MyTextView friend_union_namebox;
	private MyTextView friend_union_area;
	private MyTextView friend_union_description;
	private MyTextView friend_union_bulletin;
	private MyTextView friend_union_number;
	private MyTextView friend_union_type;
    private MyTextView friend_union_time;
	private Button friend_union_exit;
	private RelativeLayout friend_union_member;
	private RelativeLayout friend_union_n;
	private RelativeLayout friend_union_add_bulletin;
	private ImageView iv_toright;
	private ImageView iv_toright2;
	// private RelativeLayout friend_union_remark;
	// private MyTextView friend_union_remark_name;

	private InputDialog inputDialog;
	private String pecketName;

	private FriendUnion friendUnion;
	private detailMembeBroadcastr broadcastr;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String legid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		broadcastr = new detailMembeBroadcastr();
		registerReceiver(broadcastr, new IntentFilter(
				AndroidConfig.detFriendMember));
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend_union_detail);
		initdefaultImage(R.drawable.ic_group_poster);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("好友联盟详情", "", "",
				R.drawable.icon_com_title_left, 0);
		friend_union_poster = (RoundedImageView) findViewById(R.id.friend_union_poster);
		friend_union_name = (MyTextView) findViewById(R.id.friend_union_name);
		friend_union_namebox = (MyTextView) findViewById(R.id.friend_union_namebox);
		friend_union_area = (MyTextView) findViewById(R.id.friend_union_area);
		friend_union_description = (MyTextView) findViewById(R.id.friend_union_description);
		friend_union_number = (MyTextView) findViewById(R.id.friend_union_number);
		friend_union_bulletin = (MyTextView) findViewById(R.id.friend_union_bulletin);
		friend_union_exit = (Button) findViewById(R.id.friend_union_exit);
		friend_union_header = (RelativeLayout) findViewById(R.id.friend_union_header);
		friend_union_member = (RelativeLayout) findViewById(R.id.friend_union_member);
		friend_union_n = (RelativeLayout) findViewById(R.id.friend_union_n);
		friend_union_add_bulletin = (RelativeLayout) findViewById(R.id.friend_union_add_bulletin);
        friend_union_time = (MyTextView) findViewById(R.id.friend_union_time);
		iv_toright = (ImageView) findViewById(R.id.iv_toright);
		iv_toright2 = (ImageView) findViewById(R.id.iv_toright2);
		// friend_union_remark = (RelativeLayout)
		// findViewById(R.id.friend_union_remark);
		// friend_union_remark_name = (MyTextView)
		// findViewById(R.id.friend_union_remark_name);
		friend_union_type = (MyTextView) findViewById(R.id.friend_union_type);

		friend_union_area.setFocusable(false);
		
		friend_union_header.setOnClickListener(this);
		friend_union_number.setOnClickListener(this);
		friend_union_n.setOnClickListener(this);
		friend_union_exit.setOnClickListener(this);
		friend_union_add_bulletin.setOnClickListener(this);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		friendUnion = (FriendUnion) intent.getSerializableExtra("friendUnion");
        if(friendUnion!=null){
            initFriendData();
        }else{
            showLoding("");
            legid = getIntent().getStringExtra("legid");
            InteNetUtils.getInstance(mContext).leagueDetail(legid, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> arg0) {
                    dissLoding();
                    try {
                        JSONObject jsonObject = new JSONObject(
                                arg0.result);
                        if (jsonObject.optString("ret_num").equals("0")) {
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("enterprise_list");
                                JSONObject object = jsonArray.getJSONObject(0);
                                friendUnion = new FriendUnion();
                                friendUnion.parseJSON(object);
                                initFriendData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NetRequestException e) {
                                e.printStackTrace();
                            }
                        }else{
                            ToastUtils.Errortoast(mContext,"获取信息失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    dissLoding();
                    ToastUtils.Errortoast(mContext,"获取信息失败");
                }
            });
        }




//		if (friendUnion.getNumber() <= 1) {
//			friend_union_number.setText("组建联盟 ");
//		} else {
//			friend_union_number.setText("联盟成员 (" + friendUnion.getNumber()
//					+ ")");
//		}
//
//		if (!CommonUtils.isEmpty(friendUnion.getAnnocement())) {
//			friend_union_bulletin.setText(friendUnion.getAnnocement());
//		}
//
//        if (!CommonUtils.isEmpty(friendUnion.getChange_time())) {
//            String time = friendUnion.getChange_time();
//            Date date = new Date(Long.parseLong(time)*1000);
//            friend_union_time.setText(simpleDateFormat.format(date));
//        }else{
//            friend_union_time.setText("");
//        }
//
//		// friend_union_remark_name.setText(friendUnion.getRemarkContent());
//
//		if (friendUnion.getCategory().equals("1")) {
//			friend_union_type.setText("工作联盟");
//		} else if (friendUnion.getCategory().equals("2")) {
//			friend_union_type.setText("英雄联盟");
//		}
//
//		if (friendUnion.getType().equals("0")) {
//			friend_union_exit.setText("解散该盟");
//			// friend_union_remark.setVisibility(View.GONE);
//		} else {
//			iv_toright.setVisibility(View.GONE);
//			iv_toright2.setVisibility(View.GONE);
//			// friend_union_remark.setVisibility(View.GONE);
//		}
//
//		if (friendUnion.getType().equals("1")) {
//			// friend_union_remark.setVisibility(View.VISIBLE);
//		}
//
//		if (friendUnion.getType().equals("0")) {
//			friend_union_header.setOnClickListener(this);
//		}else{
//			friend_union_header.setOnClickListener(null);
//			friend_union_header.setClickable(false);
//		}


	}

	private void initFriendData() {
		friend_union_name.setText(friendUnion.getName());
		friend_union_namebox.setText(friendUnion.getNinkName());
		CommonUtils.startImageLoader(cubeimageLoader, friendUnion.getPoster(),
				friend_union_poster);
		friend_union_area.setText(friendUnion.getFullArea());
		friend_union_description.setText(friendUnion.getInfo());

        if (friendUnion.getNumber() <= 1) {
            friend_union_number.setText("组建联盟 ");
        } else {
            friend_union_number.setText("联盟成员 (" + friendUnion.getNumber()
                    + ")");
        }

        if (!CommonUtils.isEmpty(friendUnion.getAnnocement())) {
            friend_union_bulletin.setText(friendUnion.getAnnocement());
        }

        if (!CommonUtils.isEmpty(friendUnion.getChange_time())) {
            String time = friendUnion.getChange_time();
            Date date = new Date(Long.parseLong(time)*1000);
            friend_union_time.setText(simpleDateFormat.format(date));
        }else{
            friend_union_time.setText("");
        }

        // friend_union_remark_name.setText(friendUnion.getRemarkContent());

        if (friendUnion.getCategory().equals("1")) {
            friend_union_type.setText("工作联盟");
        } else if (friendUnion.getCategory().equals("2")) {
            friend_union_type.setText("英雄联盟");
        }

        if (friendUnion.getType().equals("0")) {
            friend_union_exit.setText("解散该盟");
            // friend_union_remark.setVisibility(View.GONE);
        } else {
            iv_toright.setVisibility(View.GONE);
            iv_toright2.setVisibility(View.GONE);
            // friend_union_remark.setVisibility(View.GONE);
        }

        if (friendUnion.getType().equals("1")) {
            // friend_union_remark.setVisibility(View.VISIBLE);
        }

        if (friendUnion.getType().equals("0")) {
            friend_union_header.setOnClickListener(this);
        }else{
            friend_union_header.setOnClickListener(null);
            friend_union_header.setClickable(false);
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
	}

	@Override
	protected void onHttpStart() {

	}
	

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String ret_num = jsonObject.optString("ret_num");
		int league = jsonObject.optInt("league");
		if (ret_num.equals("0")) {
			if (friendUnion.getType().equals("0")) {
				ToastUtils.Infotoast(mContext, "解散该好友联盟成功!");
			} else {
				ToastUtils.Infotoast(mContext, "退出该好友联盟成功!");
			}
			user.setSysLeague(league);
			try {
				dbUtil.saveOrUpdate(user);
				sendBroadcast(new Intent(AndroidConfig.refreshFUBroadCasting));
			} catch (DbException e) {
				e.printStackTrace();
			}
			user.setUpdate(true);
			AnimFinsh();
		} else {
			if (friendUnion.getType().equals("0")) {
				ToastUtils.Infotoast(mContext, "解散该好友联盟失败!");
			} else {
				ToastUtils.Infotoast(mContext, "退出该好友联盟失败!");
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.friend_union_header:
			if (friendUnion.getType().equals("0")) {
				startAnimActivityForResult(ActivityCreatedFriendUnion.class,
						"friendUnion", friendUnion, UPDATE_FRIEND_UNION);
			}
			break;
		case R.id.friend_union_number:
			startAnimActivityForResult(ActivityFriendUnionMember.class,
					"friendUnion", friendUnion, UPDATE_FRIEND_MEMBER);
			break;
		case R.id.friend_union_n:
			// 修改群名片
			
			inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
			inputDialog.setContent("我的名片", "请填写名片", "确认", "取消");
			inputDialog.setEditContent(friend_union_namebox.getText()
					.toString());
			inputDialog.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					inputDialog.dismiss();
				}
			});
			inputDialog.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String inputText = inputDialog.getInputText();
					
					if (!CommonUtils.StringIsSurpass(inputText, 0, 12)) {
						ToastUtils.Infotoast(mContext, "我的名片不可超过12字");
						return;
					}

					InteNetUtils.getInstance(mContext).editFriendUN(
							friendUnion.getId(), inputText,
							new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									ToastUtils.Errortoast(mContext, "修改名片失败!");
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									try {
										JSONObject object = new JSONObject(
												arg0.result);
										SuccessMsg msg = new SuccessMsg();
										msg.parseJSON(object);
										ToastUtils.Infotoast(mContext,
												"修改名片成功!");
										friend_union_namebox.setText(inputText);
										sendBroadcast(new Intent(
												AndroidConfig.refreshFriendUnion));
										friendUnion.setNinkName(inputText);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NetRequestException e) {
										e.printStackTrace();
										e.getError().print(mContext);
									}

								}
							});

					inputDialog.dismiss();
				}
			});
			inputDialog.show();
			break;
		case R.id.friend_union_exit:
			final MsgDialog inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
			inputDialog.setContent("是否要" + friend_union_exit.getText().toString(), "", "确认", "取消");
			inputDialog.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					inputDialog.dismiss();
				}
			});
			inputDialog.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).quitFriendUnion(
								friendUnion.getId(), user.getId() + "",
								mRequestCallBack);
					}
					inputDialog.dismiss();
				}
			});
			inputDialog.show();
			
			
			
			break;
		case R.id.friend_union_add_bulletin:
			if (friendUnion.getType().equals("0")) {
				startAnimActivityForResult(
						ActivityFriendUnionAddBulletin.class, "friendUnion",
						friendUnion, ADD_BULLETIN);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ADD_BULLETIN:
			if (data != null) {
				FriendUnion union = (FriendUnion) data
						.getSerializableExtra("friendUnion");
				friend_union_bulletin.setText(union.getAnnocement());
				friendUnion.setAnnocement(union.getAnnocement());
                friend_union_time.setText(simpleDateFormat.format(new Date()));
				sendBroadcast(new Intent(AndroidConfig.refreshFriendUnion));
			}
			break;
		case UPDATE_FRIEND_UNION:
			if (data != null) {
				FriendUnion union = (FriendUnion) data
						.getSerializableExtra("friendUnion");

				friend_union_name.setText(union.getName());
				friend_union_namebox.setText(union.getNinkName());
				CommonUtils.startImageLoader(cubeimageLoader,
						union.getPoster(), friend_union_poster);
				friend_union_area.setText(union.getFullArea());
				friend_union_description.setText(union.getInfo());

				friendUnion.setName(union.getName());
				friendUnion.setNinkName(union.getNinkName());
				friendUnion.setPoster(union.getPoster());
				friendUnion.setArea(union.getArea());
				friendUnion.setInfo(union.getInfo());
			}
			break;
		case UPDATE_FRIEND_MEMBER:
			if (data != null) {
				int number = data.getIntExtra("member_number", 0);
				friend_union_number.setText("联盟成员 (" + number + ")");
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastr);
	}

	class detailMembeBroadcastr extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent data) {
			friendUnion.setNumber(friendUnion.getNumber() - 1);
			friend_union_number.setText("联盟成员 (" + friendUnion.getNumber()
					+ ")");
			sendBroadcast(new Intent(AndroidConfig.refreshFriendUnion));
		}
	}

}
