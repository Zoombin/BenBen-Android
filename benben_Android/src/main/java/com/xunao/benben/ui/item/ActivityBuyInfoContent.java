package com.xunao.benben.ui.item;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.BuyInfo;
import com.xunao.benben.bean.BuyInfoContent;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.QuoteContent;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.BuyDialog;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.dialog.BuyDialog.onSuccessLinstener;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityBuyInfoContent extends BaseActivity implements
		OnClickListener, onSuccessLinstener {

	private MyTextView item_name;
	private MyTextView item_address;
	private MyTextView proinfo;
	private MyTextView proname;
	private MyTextView item_num;
	private MyTextView item_time;
	private MyTextView price_num;
	private MyTextView self_but;
	private MyTextView price_but;
	// private MyTextView appeal_but;
	private LinearLayout noself;
	private LinearLayout listview;
	private RoundedImageView item_iv;
	private BuyInfoContent info;
	private ArrayList<QuoteContent> getmQuoteContents;
	private View nodota;
	private String iD;
	private MyTextView publicTime;
	private int priceNum = 0;;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_buyinfo_content);
		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				if (cubeImageView != null) {
					cubeImageView.setImageResource(R.drawable.default_face_buy);
				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null
						&& imageTask.getIdentityUrl().equalsIgnoreCase(
								(String) cubeImageView.getTag())) {
					cubeImageView.setImageDrawable(drawable);

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					imageView.setImageResource(R.drawable.default_face_buy);
				}
			}
		});

	}

	@Override
	public void initView(Bundle savedInstanceState) {

		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", 0, new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		}, "", R.drawable.ic_back, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.AnimFinsh();
			}
		}, "我要买详情", 0);
		chanageTitle(mode);

		RelativeLayout buy_box = (RelativeLayout) findViewById(R.id.buy_box);
		nodota = findViewById(R.id.nodota);
		item_iv = (RoundedImageView) findViewById(R.id.item_iv);
		item_name = (MyTextView) findViewById(R.id.item_name);
		publicTime = (MyTextView) findViewById(R.id.publicTime);
		item_address = (MyTextView) findViewById(R.id.item_address);
		proinfo = (MyTextView) findViewById(R.id.proinfo);
		proname = (MyTextView) findViewById(R.id.proname);
		item_num = (MyTextView) findViewById(R.id.item_num);
		item_time = (MyTextView) findViewById(R.id.item_time);
		price_num = (MyTextView) findViewById(R.id.price_num);
		self_but = (MyTextView) findViewById(R.id.self_but);
		price_but = (MyTextView) findViewById(R.id.price_but);
		// appeal_but = (MyTextView) findViewById(R.id.appeal_but);
		noself = (LinearLayout) findViewById(R.id.noself);
		listview = (LinearLayout) findViewById(R.id.listview);

		self_but.setOnClickListener(this);
		price_but.setOnClickListener(this);
		// appeal_but.setOnClickListener(this);
		nodota.setOnClickListener(this);

		// FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
		// FrameLayout.LayoutParams.MATCH_PARENT, mScreenHeight);
		// buy_box.setLayoutParams(params);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		iD = getIntent().getStringExtra("ID");
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getBuyInfoContent(iD,
					mRequestCallBack);
		} else {
			nodota.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		nodota.setVisibility(View.GONE);
		info = new BuyInfoContent();
		try {
			info.parseJSON(jsonObject);
			addData();
		} catch (NetRequestException e) {
			info = null;
			e.printStackTrace();
			e.getError().print(mContext);
		}

	}

	private void addData() {

		if (info != null) {
			CommonUtils.startImageLoader(cubeimageLoader, info.getPoster(),
					item_iv);
			item_name.setText(info.getNickName());
			item_address.setText(info.getPro_city());
			proinfo.setText("描述:" + info.getDescription());
			proname.setText(info.getTitle());
			item_num.setText("数量:" + info.getAmount());
			publicTime.setText("发布时间:"
					+ TimeUtil.unix2date(info.getCreated_time(),
							TimeUtil.FORMAT_DATE1_TIME));
			// long time = (info.getDeadline())
			// - (System.currentTimeMillis() / 1000);

			String secToTime = secToTime(info.getLeft_time());
			item_time.setText("距离结束:" + secToTime);

			price_num.setText(info.getQuotedNumber() + "人报价");

			if (user.getId() == info.getMemberId()) {
				long deadline = info.getDeadline();
				if (info.getIs_close() == 1) {
					self_but.setVisibility(View.GONE);
					noself.setVisibility(View.GONE);
				} else {
					self_but.setVisibility(View.VISIBLE);
					noself.setVisibility(View.GONE);
				}

			} else {
				self_but.setVisibility(View.GONE);
				noself.setVisibility(View.VISIBLE);
			}

			getmQuoteContents = info.getmQuoteContents();
			listview.removeAllViews();
			for (QuoteContent q : getmQuoteContents) {
				if (q.getMember_id() == user.getId()) {
					priceNum++;
				}
				listview.addView(creatItem(q));
			}

			// listview.setAdapter(adapter);
		}

	}

	private View creatItem(final QuoteContent item) {
		View arg1 = View.inflate(mContext, R.layout.item_buyinfo_quot, null);
		RoundedImageView view = ViewHolderUtil.get(arg1, R.id.item_iv);
		MyTextView item_name = ViewHolderUtil.get(arg1, R.id.item_name);
		MyTextView item_time = ViewHolderUtil.get(arg1, R.id.item_time);
		MyTextView item_content = ViewHolderUtil.get(arg1, R.id.item_content);
		MyTextView item_content_info = ViewHolderUtil.get(arg1,
				R.id.item_content_info);
		ImageView item_but = ViewHolderUtil.get(arg1, R.id.item_but);

		String poster = item.getPoster();
		CommonUtils.startImageLoader(cubeimageLoader, poster, view);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                if(item.getHuanxinUsername().equals(user.getHuanxin_username())){
                    startAnimActivity2Obj(ActivityMyNumberTrainDetail.class, "id",
                            item.getStore_id() + "");
                }else {
                    startAnimActivity2Obj(ActivityNumberTrainDetail.class, "id",
                            item.getStore_id() + "");
                }

				// Intent intent = new Intent(mActivity,
				// ActivityNumberTrainDeatail.class);
				// int id = Integer.parseInt(cs.getId()) - 1000000;
				// intent.putExtra("id", id + "");
				// startActivity(intent);
				// mActivity.overridePendingTransition(
				// R.anim.in_from_right, R.anim.out_to_left);
			}
		});

		item_name.setText(item.getShort_name());
		item_time.setText(TimeUtil.longToString(item.getCreated_time() * 1000,
				"yyyy-MM-dd HH:MM"));

		String spa = "报价:" + item.getPrice() + "元"
				+ (item.getAccept() == 1 ? "(已接受)" : "");

		SpannableStringBuilder style = new SpannableStringBuilder(spa);
		// SpannableStringBuilder实现CharSequence接口
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#3c3c3c")), 0,
				3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(
				new ForegroundColorSpan(Color.parseColor("#c00909")),
				3,
				4 + (item.getPrice() + (item.getAccept() == 1 ? "(已接受)" : "") + "")
						.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		item_content_info.setText("备注:"+item.getDescription());
		item_content.setText(style);
		long deadline = info.getDeadline();
		if (info.getMemberId() == user.getId()
				&& (deadline) - (System.currentTimeMillis() / 1000) > 0) {
			item_but.setVisibility(View.VISIBLE);
			item_but.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mContext.setTheme(R.style.ActionSheetStyleIOS7);
					ActionSheet
							.createBuilder(mContext,
									mContext.getSupportFragmentManager())
							.setCancelButtonTitle("取消")
							.setOtherButtonTitles("接受报价", "聊天", "我要举报")
							// 设置颜色 必须一一对应
							.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
							.setCancelableOnTouchOutside(true)
							.setListener(new ActionSheetListener() {

								@Override
								public void onOtherButtonClick(
										ActionSheet actionSheet, int index) {

									switch (index) {
									case 0:// 接受报价
                                        final MsgDialog msgDialog = new MsgDialog(ActivityBuyInfoContent.this, R.style.MyDialogStyle);
                                        msgDialog.setContent("确定接受报价吗？", "", "确定", "取消");
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
                                                if (CommonUtils
                                                        .isNetworkAvailable(mContext)) {
                                                    InteNetUtils
                                                            .getInstance(mContext)
                                                            .acceptPrice(
                                                                    info.getId(),
                                                                    item.getStore_id(),
                                                                    item.getId() + "",
                                                                    new RequestCallBack<String>() {

                                                                        @Override
                                                                        public void onFailure(
                                                                                HttpException arg0,
                                                                                String arg1) {
                                                                            ToastUtils
                                                                                    .Errortoast(
                                                                                            mContext,
                                                                                            "接受报价失败");
                                                                        }

                                                                        @Override
                                                                        public void onSuccess(
                                                                                ResponseInfo<String> arg0) {

                                                                            try {
                                                                                JSONObject jsonObject = new JSONObject(
                                                                                        arg0.result);

                                                                                SuccessMsg msg = new SuccessMsg();

                                                                                msg.parseJSON(jsonObject);
                                                                                ToastUtils
                                                                                        .Errortoast(
                                                                                                mContext,
                                                                                                "接受报价成功");

                                                                                if (CommonUtils
                                                                                        .isNetworkAvailable(mContext)) {
                                                                                    InteNetUtils
                                                                                            .getInstance(
                                                                                                    mContext)
                                                                                            .getBuyInfoContent(
                                                                                                    iD,
                                                                                                    mRequestCallBack);
                                                                                }
                                                                                // ActivityBuyInfoContent.this.item_time
                                                                                // .setText("已经结束");
                                                                                setResult(AndroidConfig.writeFriendRefreshResultCode);

                                                                            } catch (JSONException e) {
                                                                                ToastUtils
                                                                                        .Errortoast(
                                                                                                mContext,
                                                                                                "接受报价失败");
                                                                                e.printStackTrace();
                                                                            } catch (NetRequestException e) {
                                                                                e.getError()
                                                                                        .print(mContext);
                                                                                e.printStackTrace();
                                                                            }

                                                                        }
                                                                    });
                                                }
                                            }
                                        });
                                        msgDialog.show();


										break;
									case 1:// 聊天
											// 进入聊天页面
										startAnimActivity2Obj(
												ChatActivity.class, "userId",
												item.getHuanxinUsername());
										break;
									case 2:// 举报
										startAnimActivity2Obj(
												ActivityAppeal.class, "ID",
												item.getStore_id() + "");
										break;
									}

								}

								@Override
								public void onDismiss(ActionSheet actionSheet,
										boolean isCancel) {
									// TODO Auto-generated method stub

								}
							}).show();
				}
			});
		} else {
			item_but.setVisibility(View.GONE);
		}
		return arg1;
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		nodota.setVisibility(View.VISIBLE);
	}

	class QuoteContentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return getmQuoteContents.size();
		}

		@Override
		public QuoteContent getItem(int arg0) {
			// TODO Auto-generated method stub
			return getmQuoteContents.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			return arg1;
		}

	}

	public static String secToTime(long time) {
		String timeStr = null;
		long hour = 0;
		long minute = 0;
		long second = 0;
		long day = 0;
		if (time <= 0) {
			return "00:00:00";
		} else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = "00时" + unitFormat(minute) + "分" + unitFormat(second)
						+ "秒";
			} else {
				hour = minute / 60;
				if (hour < 48) {
					minute = minute % 60;
					second = time - hour * 3600 - minute * 60;
					timeStr = unitFormat(hour) + "时" + unitFormat(minute) + "分"
							+ unitFormat(second) + "秒";
				} else {
					day = hour / 24;
					hour = hour % 24;
					minute = minute % 60;
					second = time - day * 24 * 3600 - hour * 3600 - minute * 60;

					if (day > 99) {
						timeStr = unitFormat(day) + "天";
					} else {
						timeStr = unitFormat(day) + "天" + unitFormat(hour)
								+ "时" + unitFormat(minute) + "分";
					}
				}
			}
		}
		return timeStr;
	}

	public static String unitFormat(long i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Long.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		long deadline = info.getDeadline();

		switch (id) {
		case R.id.self_but: // 关闭交易

			final MsgDialog inputDialog = new MsgDialog(mContext,
					R.style.MyDialogStyle);
			inputDialog.setContent("关闭交易", "您确定要关闭交易吗?", "确认", "取消");
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
						InteNetUtils.getInstance(mContext).closeBuy(
								info.getId() + "",
								new RequestCallBack<String>() {
									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils.Errortoast(mContext, "关闭失败");
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {

										try {
											JSONObject jsonObj = new JSONObject(
													arg0.result);
											SuccessMsg msg = new SuccessMsg();
											msg.parseJSON(jsonObj);
											setResult(AndroidConfig.writeFriendRefreshResultCode);
											AnimFinsh();
										} catch (JSONException e) {
											ToastUtils.Errortoast(mContext,
													"关闭失败");
											e.printStackTrace();
										} catch (NetRequestException e) {
											e.printStackTrace();
											e.getError().print(mContext);
										}
									}
								});
					}
					inputDialog.dismiss();
				}
			});
			inputDialog.show();

			break;
		case R.id.price_but:// 我要报价

			if (info.getIs_close()==1) {
				ToastUtils.Infotoast(mContext, "交易已经截止");
				return;
			}

			if (user != null && user.getZhiId() != 0) {
				if (priceNum < 2) {
					BuyDialog buyDialog = new BuyDialog(mContext,
							R.style.BuyDialog);
					buyDialog.setSuccessLinstener(ActivityBuyInfoContent.this);
					buyDialog.setBuyId(Integer.parseInt(info.getId()));
					buyDialog.show();
				} else {
					ToastUtils.Infotoast(mContext, "您已经报价两次");
				}

			} else {
				hint = new InfoMsgHint(mContext, R.style.MyDialog1);
				hint.setContent("需要先开通号码直通车", "", "去开通", "取消");
				hint.setCancleListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});
				hint.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 判断是不是完善了我的号码直通车信息
						switch (mContext.user.getUserInfo()) {
						case "0":
							mContext.startAnimActivity(ActivityMyNumberTrianInfoPerfect.class);
							break;
						case "2":
							mContext.startAnimActivity(ActivityMyNumberTrianInfoPerfect.class);
							break;
						default:
							InteNetUtils.getInstance(mContext).getMyStore(
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

											if ("123".equalsIgnoreCase(ret_num)) {
												mContext.startAnimActivity2Obj(
														ActivityMyNumberTrain.class,
														"do", "created");
											} else {
												mContext.startAnimActivity2Obj(
														ActivityMyNumberTrain.class,
														"do", "update");
											}
										}

										@Override
										public void onFailure(
												HttpException arg0, String arg1) {
											ToastUtils.Infotoast(mContext,
													"网络不可用!");
										}
									});

						}
						hint.dismiss();
					}
				});
				hint.show();
			}

			break;
		// case R.id.appeal_but:// 我要申诉
		// startAnimActivity2Obj(ActivityAppeal.class, "ID", iD);
		// break;
		case R.id.nodota:// 重新联网
			if (CommonUtils.isNetworkAvailable(mContext)) {
				InteNetUtils.getInstance(mContext).getBuyInfoContent(iD,
						mRequestCallBack);
			} else {
				nodota.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	@Override
	public void onSuccess(String name, String pricr, String info) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			setResult(AndroidConfig.writeFriendRefreshResultCode);
			InteNetUtils.getInstance(mContext).getBuyInfoContent(iD,
					mRequestCallBack);
		} else {
			nodota.setVisibility(View.VISIBLE);
		}
	}
}
