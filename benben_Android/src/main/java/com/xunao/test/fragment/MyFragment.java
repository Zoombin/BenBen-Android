package com.xunao.test.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.base.BaseFragment;
import com.xunao.test.base.IA.CrashApplication;
import com.xunao.test.bean.NumberTrainDetail;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.dialog.InfoSimpleMsgHint;
import com.xunao.test.dialog.LodingDialog;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.ui.ActivityLogin;
import com.xunao.test.ui.ActivityWeb;
import com.xunao.test.ui.account.ActivityMyAccount;
import com.xunao.test.ui.item.ActivityApplyBaiXing;
import com.xunao.test.ui.item.ActivityApplyBaixingDetail;
import com.xunao.test.ui.item.ActivityChoiceFriend;
import com.xunao.test.ui.item.ActivityInviteContactsToBenBen;
import com.xunao.test.ui.item.ActivityMyDynamic;
import com.xunao.test.ui.item.ActivityMyFriendUnion;
import com.xunao.test.ui.item.ActivityMyNumberTrain;
import com.xunao.test.ui.item.ActivityMyNumberTrainDetail;
//import com.xunao.benben.ui.item.ActivityMyNumberTrianInfoPerfect;
import com.xunao.test.ui.item.ActivityMySelfBuy;
import com.xunao.test.ui.item.ActivityMySmallMake;
import com.xunao.test.ui.item.ActivityPersonal;
import com.xunao.test.ui.item.ActivityProgressOfBx;
import com.xunao.test.ui.item.ActivitySetting;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

public class MyFragment extends BaseFragment implements OnClickListener {
	private View view;
	private RelativeLayout rl_setting;
	private RelativeLayout rl_enter_own_msg;
	private RelativeLayout rl_apply_bx;
	private RoundedImageView rv_poster;
	private TextView tv_name;
	private TextView tv_benbenid;
	private RoundedImageView two_code;
	private RelativeLayout rl_invite_friend;
	private RelativeLayout rl_progress_bx;
	private RelativeLayout rl_my_numberttain;
	private RelativeLayout rl_myfriend_union;
	private RelativeLayout rl_my_dynamic;
	private RelativeLayout rl_my_creation;
	private RelativeLayout rl_collection;
    private RelativeLayout rl_my_account;
	private TextView tv_coin;
	private TextView tv_dengji;
	private RelativeLayout rl_my_buy;
	private RelativeLayout rl_invite_friend_tobb;
    private Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_own, null);
		super.onCreateView(inflater, container, savedInstanceState);
        if(getHintState("help2")) {
            showHintDiaLog();
        }
		return view;
	}

	@Override
	protected void initView() {
		rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);
		rl_enter_own_msg = (RelativeLayout) view
				.findViewById(R.id.rl_enter_own_msg);
		rl_apply_bx = (RelativeLayout) view.findViewById(R.id.rl_apply_bx);
		rv_poster = (RoundedImageView) view.findViewById(R.id.rv_poster);
		tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_benbenid = (TextView) view.findViewById(R.id.tv_benbenid);
		two_code = (RoundedImageView) view.findViewById(R.id.two_code);
		rl_invite_friend = (RelativeLayout) view
				.findViewById(R.id.rl_invite_friend);
		rl_progress_bx = (RelativeLayout) view
				.findViewById(R.id.rl_progress_bx);
		rl_my_numberttain = (RelativeLayout) view
				.findViewById(R.id.rl_my_numberttain);
		rl_myfriend_union = (RelativeLayout) view
				.findViewById(R.id.rl_myfriend_union);
		rl_my_dynamic = (RelativeLayout) view.findViewById(R.id.rl_my_dynamic);
		rl_my_creation = (RelativeLayout) view
				.findViewById(R.id.rl_my_creation);
		rl_collection = (RelativeLayout) view.findViewById(R.id.rl_collection);
		tv_coin = (TextView) view.findViewById(R.id.tv_coin);
        tv_coin.setOnClickListener(this);
		tv_dengji = (TextView) view.findViewById(R.id.tv_dengji);
		rl_my_buy = (RelativeLayout) view.findViewById(R.id.rl_my_buy);
		rl_invite_friend_tobb = (RelativeLayout) view
				.findViewById(R.id.rl_invite_friend_tobb);
        rl_my_account = (RelativeLayout) view.findViewById(R.id.rl_my_account);
        rl_my_account.setOnClickListener(this);
		initdata();
	}

	private void initdata() {
		tv_name.setText(mActivity.user.getUserNickname());
		tv_benbenid.setText("奔犇号：" + mActivity.user.getBenbenId());

		CommonUtils.startImageLoader(cubeimageLoader,
				mActivity.user.getPoster(), rv_poster);

		DisplayMetrics metric = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		two_code.setTag(R.string.type, "twocode");
		CommonUtils.startImageLoader(cubeimageLoader,
				mActivity.user.getUserQrCode(), two_code);
		tv_coin.setText(mActivity.user.getIntegral());
		tv_dengji.setText(mActivity.user.getAppellation());
	}

	@Override
	protected void initLinstener() {
		rl_setting.setOnClickListener(this);
		rl_enter_own_msg.setOnClickListener(this);
		rl_apply_bx.setOnClickListener(this);
		rl_invite_friend.setOnClickListener(this);
		rl_progress_bx.setOnClickListener(this);
		rl_my_numberttain.setOnClickListener(this);
		rl_myfriend_union.setOnClickListener(this);
		rl_my_dynamic.setOnClickListener(this);
		rl_my_creation.setOnClickListener(this);
		rl_collection.setOnClickListener(this);
		rl_my_buy.setOnClickListener(this);
		rl_invite_friend_tobb.setOnClickListener(this);
	}

	@Override
	protected void onHttpStart() {
		if (isShowLoding) {
			if (lodingDialog == null) {
				lodingDialog = new LodingDialog(mActivity);
			}
			lodingDialog.show();
		}
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mActivity, "网络不可用!");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
            case R.id.tv_coin:
                Intent intent = new Intent(getActivity(), ActivityWeb.class);
                intent.putExtra("title", "积分说明");
                intent.putExtra("url", AndroidConfig.NETHOST3 + AndroidConfig.Setting + "key/android/type/6");

                startActivity(intent);
                break;
		case R.id.rl_setting:
			((BaseActivity) getActivity())
					.startAnimActivity(ActivitySetting.class);
			break;
		case R.id.rl_enter_own_msg:
			mActivity.startAnimActivity(ActivityPersonal.class);
			break;
		case R.id.rl_apply_bx:
			InteNetUtils.getInstance(mActivity).applyBxInfo(
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);

								int optInt = jsonObject.optInt("ret_num");

								if (optInt == 2126) {
									// if ((Integer.parseInt(mActivity.user
									// .getUserInfo()) & 2) > 0) {
									// mActivity
									// .startAnimActivity(ActivityApplyBaixingDetail.class);
									// } else {
									mActivity
											.startAnimActivity(ActivityApplyBaiXing.class);
									// }
								} else {
									try {
										SuccessMsg msg = new SuccessMsg();
										msg.checkJson(jsonObject);
										JSONObject optJSONObject = jsonObject
												.optJSONObject("info");
										if (optJSONObject != null) {
											int status = optJSONObject
													.optInt("status");
											if (status == 3) {
												ToastUtils
														.Infotoast(mActivity,
																"你已经是百姓网用户,可以通过\"将好友加入百姓网\"提交好友");
											} else if (status == 1) {
												ToastUtils.Infotoast(mActivity,
														"您不符合加入百姓网条件");
											} else {
												if (status == 0 || status == 2) {
													mActivity
															.startAnimActivity(ActivityApplyBaixingDetail.class);

												} else {
													mActivity
															.startAnimActivity(ActivityApplyBaiXing.class);

												}
											}
										} else {
											mActivity
													.startAnimActivity(ActivityApplyBaiXing.class);
										}

									} catch (NetRequestException e) {
										e.printStackTrace();
										e.getError().print(mActivity);
									}
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ToastUtils.Errortoast(mActivity, "网络不可用");
						}
					});
			break;
		case R.id.rl_invite_friend:

			InteNetUtils.getInstance(mActivity).applyBxInfo(
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);

								int optInt = jsonObject.optInt("ret_num");

								if (optInt == 2126) {
									ToastUtils
											.Infotoast(mActivity, "您还不是百姓网用户");
								} else {
									try {
										SuccessMsg msg = new SuccessMsg();
										msg.checkJson(jsonObject);
										JSONObject optJSONObject = jsonObject
												.optJSONObject("info");
										if (optJSONObject != null) {
											int status = optJSONObject
													.optInt("status");
											if (status == 3) {
												mActivity
														.startAnimActivity(ActivityChoiceFriend.class);
											} else {
												ToastUtils.Infotoast(mActivity,
														"您还不是百姓网用户");
											}
										} else {
											ToastUtils.Infotoast(mActivity,
													"您还不是百姓网用户");
										}

									} catch (NetRequestException e) {
										e.printStackTrace();
										e.getError().print(mActivity);
									}
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ToastUtils.Errortoast(mActivity, "网络不可用");
						}
					});
			break;
		case R.id.rl_progress_bx:
			mActivity.startAnimActivity(ActivityProgressOfBx.class);
			break;
		case R.id.rl_my_numberttain:
			// 判断是不是完善了我的号码直通车信息
			if (CrashApplication.getInstance().user.getStore_disable() == 0) {

				if ((Integer.parseInt(mActivity.user.getUserInfo()) & 2) > 0) {
					InteNetUtils.getInstance(mActivity).getMyStore(
							new RequestCallBack<String>() {

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									JSONObject jsonObject = null;
									try {
										jsonObject = new JSONObject(arg0.result);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									String ret_num = jsonObject
											.optString("ret_num");
									String ret_msg = jsonObject
											.optString("ret_msg");

									NumberTrainDetail detail = new NumberTrainDetail();
									JSONObject object = jsonObject
											.optJSONObject("number_info");

									try {

										if ("123".equalsIgnoreCase(ret_num)) {
											mActivity
													.startAnimActivity2Obj(
															ActivityMyNumberTrain.class,
															"do", "created");
										}else if ("2015".equalsIgnoreCase(ret_num)) {
											final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
												mActivity, R.style.MyDialog1);
											hint.setContent(ret_msg);
											hint.setBtnContent("确定");
											hint.show();
											hint.setOKListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													hint.dismiss();
												}
											});
											
											
//											ToastUtils.Infotoast(mActivity, ret_msg);
											CrashApplication.getInstance().logout();
											startActivity(new Intent(mActivity, ActivityLogin.class));
										}else {
											detail.parseJSON(object);
											if (detail.getNumberStatus() == 1) {
												final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
														mActivity,
														R.style.MyDialog1);
												hint.setContent("号码直通车被屏蔽");
												hint.setBtnContent("确定");
												hint.show();
												hint.setOKListener(new OnClickListener() {

													@Override
													public void onClick(View v) {
														hint.dismiss();
													}
												});

												hint.show();
											} else {
                                                Intent intent = new Intent(mActivity,
                                                        ActivityMyNumberTrainDetail.class);

                                                intent.putExtra("id", detail.getId());
                                                intent.putExtra("from", "myself");
                                                startActivity(intent);
                                                mActivity.overridePendingTransition(
                                                        R.anim.in_from_right, R.anim.out_to_left);
//												mActivity
//														.startAnimActivity2Obj(
//																ActivityMyNumberTrain.class,
//																"do", "update");
											}
										}

									} catch (NetRequestException e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									ToastUtils.Infotoast(mActivity, "网络不可用!");
								}
							});
				} else {

//					switch (mActivity.user.getUserInfo()) {
//					case "0":
//						mActivity
//								.startAnimActivity(ActivityMyNumberTrianInfoPerfect.class);
//						break;
//					case "2":
//						mActivity
//								.startAnimActivity(ActivityMyNumberTrianInfoPerfect.class);
//						break;
//					default:
						InteNetUtils.getInstance(mActivity).getMyStore(
								new RequestCallBack<String>() {

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										JSONObject jsonObject = null;
										try {
											jsonObject = new JSONObject(
													arg0.result);
										} catch (JSONException e) {
											e.printStackTrace();
										}
										String ret_num = jsonObject
												.optString("ret_num");
										NumberTrainDetail detail = new NumberTrainDetail();
										JSONObject object = jsonObject
												.optJSONObject("number_info");
										try {
											detail.parseJSON(object);
										} catch (NetRequestException e) {
											e.printStackTrace();
										}
										if ("123".equalsIgnoreCase(ret_num)) {
											mActivity
													.startAnimActivity2Obj(
															ActivityMyNumberTrain.class,
															"do", "created");
										} else if ("2015".equalsIgnoreCase(ret_num)) {
//											ToastUtils.Infotoast(mActivity, jsonObject
//													.optString("ret_msg"));
											
											final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
													mActivity, R.style.MyDialog1);
												hint.setContent(jsonObject.optString("ret_msg"));
												hint.setBtnContent("确定");
												hint.show();
												hint.setOKListener(new OnClickListener() {

													@Override
													public void onClick(View v) {
														hint.dismiss();
													}
												});
											CrashApplication.getInstance().logout();
											startActivity(new Intent(mActivity, ActivityLogin.class));
										}else {
											if (detail.getNumberStatus() == 1) {
												final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
														mActivity,
														R.style.MyDialog1);
												hint.setContent("号码直通车被屏蔽");
												hint.setBtnContent("确定");
												hint.show();
												hint.setOKListener(new OnClickListener() {

													@Override
													public void onClick(View v) {
														hint.dismiss();
													}
												});

												hint.show();
											} else {

                                                Intent intent = new Intent(mActivity,
                                                        ActivityMyNumberTrainDetail.class);

                                                intent.putExtra("id", detail.getId());
                                                intent.putExtra("from", "myself");
                                                startActivity(intent);
                                                mActivity.overridePendingTransition(
                                                        R.anim.in_from_right, R.anim.out_to_left);

											}
										}
									}

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils.Infotoast(mActivity,
												"网络不可用!");
									}
								});
					}
//					break;
//				}

			} else if (CrashApplication.getInstance().user.getStore_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getStore_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}
			break;
		case R.id.rl_myfriend_union:
			if (CrashApplication.getInstance().user.getLeague_disable() == 0) {
				mActivity.startAnimActivity(ActivityMyFriendUnion.class);
			} else if (CrashApplication.getInstance().user.getLeague_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getLeague_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}

			break;
		case R.id.rl_my_dynamic:
			mActivity.startAnimActivity(ActivityMyDynamic.class);
			break;
		case R.id.rl_my_creation:
			if (CrashApplication.getInstance().user.getCreation_disable() == 0) {
				mActivity.startAnimActivity(ActivityMySmallMake.class);
			} else if (CrashApplication.getInstance().user
					.getCreation_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getCreation_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}

			break;
		case R.id.rl_my_buy:
			if (CrashApplication.getInstance().user.getBuy_disable() == 0) {
				mActivity.startAnimActivity(ActivityMySelfBuy.class);
			} else if (CrashApplication.getInstance().user.getBuy_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getBuy_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}
			// mActivity.startAnimActivity(ActivityMySelfBuy.class);
			break;
		case R.id.rl_invite_friend_tobb:
			mActivity.startAnimActivity(ActivityInviteContactsToBenBen.class);
            break;
            case R.id.rl_my_account:
                mActivity.startAnimActivity(ActivityMyAccount.class);
                break;
		}


	}

	// @Override
	// public void onResume() {
	// if (mActivity.user.isUpdate()) {
	// if (isShowLoding) {
	// if (lodingDialog == null) {
	// lodingDialog = new LodingDialog(mActivity);
	// }
	// lodingDialog.show();
	// }
	//
	// mActivity.user.setUpdate(false);
	// }
	// super.onResume();
	// }

	public void refrashUser() {
		tv_name.setText(mActivity.user.getUserNickname());
		CommonUtils.startImageLoader(cubeimageLoader,
				mActivity.user.getPoster(), rv_poster);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	protected void initDate() {
		// TODO Auto-generated method stub

	}

    private void showHintDiaLog(){
        updatHintState("help2",false);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View hintdialogView = inflater.inflate(R.layout.help_hint_dialog, null);
        ImageView iv_hint_help = (ImageView) hintdialogView.findViewById(R.id.iv_hint_help);
        iv_hint_help.setImageResource(R.drawable.img_help2);
        iv_hint_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = new Dialog(getActivity(),R.style.hintDialog);
        // 设置它的ContentView
        dialog.setContentView(hintdialogView);
        Window dialogWindow = dialog.getWindow();
//        //设置在底部
//        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //设置对话框宽
        p.width = d.getWidth();
        p.height = d.getHeight();
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    //获取帮助是否提示
    public boolean getHintState(String key) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        if(sharedPreferences.contains(key)){
            return sharedPreferences.getBoolean(key, true);
        }else{
            return true;
        }
    }

    //修改帮助是否提示
    public void updatHintState(String key,boolean flag) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        //创建数据编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        //传递需要保存的数据
        editor.putBoolean(key, flag);
        //保存数据
        editor.commit();
    }
}
