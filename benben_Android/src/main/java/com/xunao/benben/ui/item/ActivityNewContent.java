package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.BufferType;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.News;
import com.xunao.benben.bean.SmallMakeData;
import com.xunao.benben.bean.SmallMakeDataComment;
import com.xunao.benben.bean.SmallMakeSingleData;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.utils.SmileUtils;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.click.VoicelistPlayClickListener;
import com.xunao.benben.utils.click.VoicelistSinglePlayClickListener;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;
import com.xunao.benben.view.NoScrollGridView;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityNewContent extends BaseActivity {

	private View msg_box;
	private View public_box;
	private News mNews;
	private View small_trumpet_box;
	private LinearLayout comment_box;
	private View item_friend_voice_loding;
	private View item_friend_voice_error;
	// private ImageView click_zan;
	// private ImageView attention;
	private MyTextView item_friend_looknum;
	private MyTextView item_friend_content;
	// private ImageView comment;
	private ImageView item_friend_voice;
	private View item_friend_voice_box;
	private CubeImageView item_friend_singleImg;
	private NoScrollGridView item_friend_gridView;
	private MyTextView small_item_time;
	private MyTextView small_item_name;
	private RoundedImageView small_item_iv;
	private RoundedImageView item_iv;
	private MyTextView item_name;
	private MyTextView item_time;
	private MyTextView add_blue;
	private MyTextView public_time;
	private MyTextView public_content;
	private InputDialog inputDialog;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_new_content);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				if (cubeImageView != null) {
					Boolean ispost = (Boolean) cubeImageView
							.getTag(R.string.ispost);
					if (ispost != null && ispost) {
						cubeImageView.setImageResource(R.drawable.default_face);
					} else {
						cubeImageView.setImageResource(R.drawable.loading);
					}

				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null) {
					if (imageTask.getIdentityUrl().equalsIgnoreCase(
							(String) cubeImageView.getTag())) {

						cubeImageView.setVisibility(View.VISIBLE);
						cubeImageView.setImageDrawable(drawable);

					}

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					Boolean ispost = (Boolean) imageView
							.getTag(R.string.ispost);
					if (ispost != null && ispost) {
						imageView.setImageResource(R.drawable.default_face);
					} else {
						imageView.setImageResource(R.drawable.error);
					}
				}
			}
		});

	}

	@Override
	public void initView(Bundle savedInstanceState) {

		initTitleView();

		msg_box = findViewById(R.id.msg_box);
		item_iv = (RoundedImageView) findViewById(R.id.item_iv);
		item_name = (MyTextView) findViewById(R.id.item_name);
		item_time = (MyTextView) findViewById(R.id.item_time);
		add_blue = (MyTextView) findViewById(R.id.add_blue);

		public_box = findViewById(R.id.public_box);
		public_time = (MyTextView) findViewById(R.id.public_time);
		public_content = (MyTextView) findViewById(R.id.public_content);

		small_trumpet_box = findViewById(R.id.small_trumpet_box);
		comment_box = (LinearLayout) findViewById(R.id.comment_box);
		item_friend_voice_loding = findViewById(R.id.item_friend_voice_loding);
		item_friend_voice_error = findViewById(R.id.item_friend_voice_error);
		// click_zan = (ImageView) findViewById(R.id.click_zan);
		// attention = (ImageView) findViewById(R.id.attention);
		item_friend_looknum = (MyTextView) findViewById(R.id.item_friend_looknum);
		item_friend_content = (MyTextView) findViewById(R.id.item_friend_content);
		small_item_time = (MyTextView) findViewById(R.id.small_item_time);
		small_item_name = (MyTextView) findViewById(R.id.small_item_name);
		small_item_iv = (RoundedImageView) findViewById(R.id.small_item_iv);
		// comment = (ImageView) findViewById(R.id.comment);
		item_friend_voice = (ImageView) findViewById(R.id.item_friend_voice);
		item_friend_voice_box = findViewById(R.id.item_friend_voice_box);
		item_friend_singleImg = (CubeImageView) findViewById(R.id.item_friend_singleImg);
		item_friend_gridView = (NoScrollGridView) findViewById(R.id.item_friend_gridView);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		public_box.setVisibility(View.GONE);
		msg_box.setVisibility(View.GONE);
		small_trumpet_box.setVisibility(View.GONE);
		Intent intent = getIntent();

		mNews = (News) intent.getSerializableExtra("NEWS");
		String titleNmae = "";

		switch (mNews.getType()) {
		case 1:// 公告
			titleNmae = "公告";
			break;
		case 2:// 通知
			titleNmae = "通知";
			break;
		case 3:// 小喇叭
			titleNmae = "小喇叭";
			break;
		case 4:// 通知
			titleNmae = "政企通讯录";
			break;
		case 5:// 通知
			titleNmae = "好友联盟";
			break;
		case 6:// 微创作
			titleNmae = "微创作";
			break;
		case 7:
			titleNmae = "号码直通车";
			break;

		}

		TitleMode mode = new TitleMode("#068cd9", "", 0, new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 好友联盟

			}
		}, "", R.drawable.ic_back, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.AnimFinsh();
			}
		}, titleNmae, 0);
		chanageTitle(mode);

		InteNetUtils.getInstance(mContext).readNews(mNews.getId() + "",
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						mNews.setStatus(1);
						try {
							dbUtil.saveOrUpdate(mNews);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});

		
		switch (mNews.getType()) {
		case 1:// 公告
		case 2:// 通知
		case 3:// 小喇叭
			public_box.setVisibility(View.VISIBLE);
			public_time.setText(TimeUtil.unix2date(mNews.getCreated_time(),
					"yy-MM-dd HH:mm:ss"));
			public_content.setText(mNews.getContent());

			break;
		case 6:// 微创作
			InteNetUtils.getInstance(mContext).getSingleSmall(
					mNews.getIdentity1() + "", mRequestCallBack);
			break;
		case 4:// 政企
			item_iv.setTag(R.string.ispost, true);
			CommonUtils.startImageLoader(cubeimageLoader, mNews.getPoster(),
					item_iv);
			item_name.setText(mNews.getContent());
			item_time.setText(TimeUtil.unix2date(mNews.getCreated_time(),
					"yy-MM-dd HH:mm:ss"));
		
			
			try {
				dbUtil.saveOrUpdate(mNews);
			} catch (DbException e) {
				e.printStackTrace();
			}
			if (mNews.getStatus() != 2) {
				add_blue.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						if (mNews.getIdentity2() == 2) {// 虚拟网
							inputDialog = new InputDialog(mContext,
									R.style.MyDialogStyle);
							inputDialog.setContent("政企短号", "请输入您的政企短号", "确认",
									"取消");
							inputDialog
									.setCancleListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											inputDialog.dismiss();
										}
									});
							inputDialog.setOKListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									String shortPhone = inputDialog
											.getInputText();
									addCompany(shortPhone);
									inputDialog.dismiss();
								}
							});
							inputDialog.show();
						} else {
							addCompany("");
						}

					}
				});
			} else {
				add_blue.setText("已加入");
				add_blue.setBackgroundResource(R.drawable.but_bg_public_agree);
			}

			msg_box.setVisibility(View.VISIBLE);
			break;
		case 5:// 好友联盟
			msg_box.setVisibility(View.VISIBLE);
			item_iv.setTag(R.string.ispost, true);
			CommonUtils.startImageLoader(cubeimageLoader, mNews.getPoster(),
					item_iv);
			item_name.setText(mNews.getContent());
			item_time.setText(TimeUtil.unix2date(mNews.getCreated_time(),
					"yy-MM-dd HH:mm:ss"));
			mNews.setStatus(1);
			try {
				dbUtil.saveOrUpdate(mNews);
			} catch (DbException e) {
				e.printStackTrace();
			}
			if (mNews.getStatus() != 2) {
				add_blue.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						addFriendUN(mNews.getId());
					}
				});
			} else {
				add_blue.setText("已加入");
				add_blue.setBackgroundResource(R.drawable.but_bg_public_agree);
			}

			break;
		}

	}

	private void addFriendUN(String newId) {
		// 加入好友联盟
		InteNetUtils.getInstance(mContext).acceptFriendUN(newId, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtils.Errortoast(mContext, "网络不可用!请重试");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							JSONObject jsonObject = new JSONObject(arg0.result);

							SuccessMsg msg = new SuccessMsg();

							msg.parseJSON(jsonObject);
							ToastUtils.Errortoast(mContext, "加入成功");
							mNews.setStatus(2);
							add_blue.setClickable(false);
							add_blue.setText("已加入");
							add_blue.setBackgroundResource(R.drawable.but_bg_public_agree);

							try {
								dbUtil.saveOrUpdate(mNews);
							} catch (DbException e) {
								e.printStackTrace();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NetRequestException e) {
							e.getError().print(mContext);
							e.printStackTrace();
						}

					}
				});
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
		SmallMakeSingleData singleData = new SmallMakeSingleData();
		mNews.setStatus(1);
		try {
			dbUtil.saveOrUpdate(mNews);
		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			singleData.parseJSON(jsonObject);
			setSmall(singleData);
		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.getError().print(mContext);
			e.printStackTrace();
		}
	}

	private void setSmall(final SmallMakeSingleData singleData) {
		small_trumpet_box.setVisibility(View.VISIBLE);

		small_item_name.setText(singleData.getName());
		small_item_time.setText(TimeUtil.getDescriptionTimeFromTimestamp(Long
				.parseLong(singleData.getCreatedTime()) * 1000));
		item_friend_looknum.setText("浏览:" + singleData.getViews() + "次");
		item_friend_content.setText(singleData.getDescription());
		ArrayList<SmallMakeDataComment> getmFriendDataComments = singleData
				.getmFriendDataComments();

		if (getmFriendDataComments != null && getmFriendDataComments.size() > 0) {
			comment_box.setVisibility(View.VISIBLE);
			comment_box.removeAllViews();
			LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			for (SmallMakeDataComment fc : getmFriendDataComments) {
				MyTextView myTextView = new MyTextView(mContext);

				Spannable span = SmileUtils.getSmiledText(mContext,
						fc.getReview());

				String s = fc.getNick_name() + ": " + span;
				SpannableStringBuilder style = new SpannableStringBuilder(s);
				style.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0e7bba")),
						0, fc.getNick_name().length() + 2,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				myTextView.setText(style, BufferType.SPANNABLE);
				myTextView.setLineSpacing(PixelUtil.dp2px(3), 1);
				if (comment_box.getChildCount() > 1) {
					params.topMargin = PixelUtil.dp2px(5);
				} else {
					params.topMargin = PixelUtil.dp2px(0);
				}
				comment_box.addView(myTextView, params);

			}

		} else {
			comment_box.setVisibility(View.GONE);
		}
		String poster = singleData.getPoster();
		small_item_iv.setTag(R.string.ispost, true);
		CommonUtils.startImageLoader(cubeimageLoader, poster, small_item_iv);
		// if (singleData.getLaud() == 1) {
		// click_zan.setImageResource(R.drawable.ic_click_zaned);
		// } else {
		// click_zan.setImageResource(R.drawable.ic_click_zan);
		// }

		// attention.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// if (Integer.parseInt(singleData.getMemberId()) == user.getUserId()) {
		// ToastUtils.Infotoast(mContext, "不能关注自己");
		// } else {
		// // 调用关注
		// showActionSheet(new ActionSheetListener() {
		// @Override
		// public void onOtherButtonClick(ActionSheet actionSheet,
		// int index) {
		// switch (0) {
		// case 0:
		// if (CommonUtils.isNetworkAvailable(mContext)) {
		// // 关注该作者
		// InteNetUtils
		// .getInstance(mContext)
		// .attention(
		// singleData.getMemberId() + "",
		// new RequestCallBack<String>() {
		//
		// @Override
		// public void onSuccess(
		// ResponseInfo<String> arg0) {
		// try {
		// JSONObject jsonObject = new JSONObject(
		// arg0.result);
		// SuccessMsg msg = new SuccessMsg();
		// try {
		// msg.parseJSON(jsonObject);
		// mApplication.isAttenRefresh = true;
		// ToastUtils
		// .Infotoast(
		// mContext,
		// "关注成功");
		// } catch (NetRequestException e) {
		// e.getError()
		// .print(mContext);
		// }
		// } catch (JSONException e) {
		// ToastUtils
		// .Errortoast(
		// mContext,
		// "关注失败,请重试");
		// }
		//
		// }
		//
		// @Override
		// public void onFailure(
		// HttpException arg0,
		// String arg1) {
		// ToastUtils
		// .Errortoast(
		// mContext,
		// "关注失败,请重试");
		// }
		// });
		// }
		//
		// break;
		// }
		// }
		//
		// @Override
		// public void onDismiss(ActionSheet actionSheet,
		// boolean isCancel) {
		//
		// }
		// });
		// }
		// }
		// });

		// click_zan.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (singleData.getLaud() == 1) {
		// if (CommonUtils.isNetworkAvailable(mContext)) {
		// // 取消点赞
		// InteNetUtils.getInstance(mContext)
		// .cancelSmallClickGood(singleData.getId() + "",
		// new RequestCallBack<String>() {
		//
		// @Override
		// public void onSuccess(
		// ResponseInfo<String> arg0) {
		// // 点赞成功
		// try {
		// JSONObject jsonObject = new JSONObject(
		// arg0.result);
		// SuccessMsg msg = new SuccessMsg();
		// try {
		// msg.parseJSON(jsonObject);
		// singleData.setLaud(0);
		//
		// } catch (NetRequestException e) {
		// e.getError().print(
		// mContext);
		// }
		// } catch (JSONException e) {
		// ToastUtils.Errortoast(
		// mContext,
		// "取消点赞失败,请重试");
		// }
		//
		// }
		//
		// @Override
		// public void onFailure(
		// HttpException arg0,
		// String arg1) {
		// // 点赞失败
		// ToastUtils.Errortoast(mContext,
		// "取消点赞失败,请重试");
		// }
		// });
		// }

		// } else {
		// if (CommonUtils.isNetworkAvailable(mContext)) {
		// // 点赞
		// InteNetUtils.getInstance(mContext).clickSmallGood(
		// singleData.getId() + "",
		// new RequestCallBack<String>() {
		//
		// @Override
		// public void onSuccess(
		// ResponseInfo<String> arg0) {
		// // 点赞成功
		// try {
		// JSONObject jsonObject = new JSONObject(
		// arg0.result);
		// SuccessMsg msg = new SuccessMsg();
		// try {
		// msg.parseJSON(jsonObject);
		// singleData.setLaud(1);
		// } catch (NetRequestException e) {
		// // TODO
		// // Auto-generated
		// // catch
		// // block
		// e.getError().print(mContext);
		// }
		// } catch (JSONException e) {
		// ToastUtils.Errortoast(mContext,
		// "点赞失败,请重试");
		// }
		//
		// }
		//
		// @Override
		// public void onFailure(HttpException arg0,
		// String arg1) {
		// // 点赞失败
		// ToastUtils.Errortoast(mContext,
		// "点赞失败,请重试");
		// }
		// });
		// }
		// }
		// }
		// });

		// comment.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// curentID = singleData.getId() + "";
		// bar_bottom.setVisibility(View.VISIBLE);
		// }
		// });
		item_friend_singleImg.setVisibility(View.GONE);
		item_friend_gridView.setVisibility(View.GONE);
		item_friend_voice_box.setVisibility(View.GONE);
		String images = singleData.getThumb();
		if (singleData.getType() == 0) {// 图文
			if (!TextUtils.isEmpty(images)) {
				String[] split = images.split("\\^");
				int length = split.length;
				if (length > 1) {
					// 多图用GridView
					item_friend_gridView.setVisibility(View.VISIBLE);
					MyGridViewAdapter adapter = new MyGridViewAdapter(split);
					item_friend_gridView.setAdapter(adapter);
					item_friend_gridView
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									startActivity2StringAndPosition(
											ActivityContentPicSet.class,
											"IMAGES", singleData.getImages(),
											arg2);
								}
							});
				} else {
					// 单图
					item_friend_singleImg.setVisibility(View.VISIBLE);

					item_friend_singleImg.getLayoutParams().width = singleData
							.getSingImageW();
					item_friend_singleImg.getLayoutParams().height = singleData
							.getSingImageH();

					CommonUtils.startImageLoader(cubeimageLoader, split[0],
							item_friend_singleImg);
					item_friend_singleImg
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									startActivity2StringAndPosition(
											ActivityContentPicSet.class,
											"IMAGES", singleData.getImages(), 0);
								}
							});

				}
			}
		} else {// 音频
			item_friend_voice_box.setVisibility(View.VISIBLE);
			if (images != null) {

				AnimationDrawable voiceAnimation = (AnimationDrawable) item_friend_voice
						.getDrawable();
				voiceAnimation.stop();
				voiceAnimation.selectDrawable(0);

				item_friend_voice_box
						.setOnClickListener(new VoicelistSinglePlayClickListener(
								singleData, item_friend_voice,
								item_friend_voice_error,
								item_friend_voice_loding, mContext));
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(mContext, "网络不可用,请重试");
	}

	public void startActivity2StringAndPosition(Class<?> cla, String key,
			String value, int position) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		intent.putExtra("POSITION", position);
		this.startActivity(intent);
	}

	private class MyGridViewAdapter extends BaseAdapter {

		String[] images;

		public MyGridViewAdapter(String[] images) {
			super();
			this.images = images;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return images.length;
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return images[position];
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
						R.layout.item_friend_gridview, null);
			}

			convertView
					.setLayoutParams(new AbsListView.LayoutParams(
							AbsListView.LayoutParams.MATCH_PARENT, PixelUtil
									.dp2px(60)));

			CommonUtils.startImageLoader(cubeimageLoader, getItem(position),
					((CubeImageView) convertView));
			return convertView;
		}

	}

	// 加入政企通讯录
	protected void addCompany(String shortPhone) {
		if (CommonUtils.isNetworkAvailable(mContext))
			InteNetUtils.getInstance(mContext).addCompany(mNews.getId() + "",
					shortPhone, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								SuccessMsg msg = new SuccessMsg();
								try {
									msg.parseJSON(jsonObject);
									ToastUtils.Errortoast(mContext, "加入成功");
									mNews.setStatus(2);
									add_blue.setClickable(false);
									add_blue.setText("已加入");
									add_blue.setBackgroundResource(R.drawable.but_bg_public_agree);
									try {
										dbUtil.saveOrUpdate(mNews);
									} catch (DbException e) {
										e.printStackTrace();
									}
								} catch (NetRequestException e) {
									e.getError().print(mContext);
								}
							} catch (JSONException e) {
								ToastUtils.Errortoast(mContext, "加入失败");
							}

						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ToastUtils.Errortoast(mContext, "加入失败");
						}
					});
	}
}
