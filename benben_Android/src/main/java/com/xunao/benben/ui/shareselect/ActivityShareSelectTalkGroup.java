package com.xunao.benben.ui.shareselect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.bean.TalkGroupList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.hx.chatuidemo.utils.ImageUtils;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.TallGroup.ActivityCreatedTallGroup;
import com.xunao.benben.ui.item.TallGroup.ActivityFindTalkGroup;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.SimpleDownLoadUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

public class ActivityShareSelectTalkGroup extends BaseActivity implements OnClickListener{

	private ListView listview;
	// 无数据时显示
	private LinearLayout no_talk_group;
	private ArrayList<TalkGroup> talkGroups;
	private MyAdapter myAdapter;
    private String train_id;
    private String train_name;
    private String train_tag;
    private String train_poster;
    private String shop;
    private String url;
	private String type;
	private String msgId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_talk_group);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				if (cubeImageView != null) {
					cubeImageView.setImageResource(R.drawable.ic_group_poster);
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
					imageView.setImageResource(R.drawable.ic_group_poster);
				}
			}
		});
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("群组", "", "", R.drawable.icon_com_title_left,
				0);
		setShowLoding(false);
		listview = (ListView) findViewById(R.id.listview);
		no_talk_group = (LinearLayout) findViewById(R.id.no_talk_group);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
        url = getIntent().getStringExtra("url");
        train_id = getIntent().getStringExtra("train_id");
        train_name = getIntent().getStringExtra("train_name");
        train_tag = getIntent().getStringExtra("train_tag");
        train_poster = getIntent().getStringExtra("train_poster");
        shop = getIntent().getStringExtra("shop");
		type = getIntent().getStringExtra("type");
		msgId = getIntent().getStringExtra("msg_id");
		showLoding("请稍后...");
		InteNetUtils.getInstance(mContext).getTalkGroup(mRequestCallBack);

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
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
        dissLoding();
		try {
			TalkGroupList groupList = new TalkGroupList();
			groupList = groupList.parseJSON(jsonObject);
			talkGroups = groupList.getTalkGroups();

			if (talkGroups != null && talkGroups.size() > 0) {
				dbUtil.deleteAll(TalkGroup.class);
				dbUtil.saveAll(talkGroups);
				// 改变mapplication里的map、
			} else {
				dbUtil.deleteAll(TalkGroup.class);
			}
		} catch (NetRequestException e) {
			e.printStackTrace();
		} catch (DbException e) {
			e.printStackTrace();
		}

		if (myAdapter == null) {
			myAdapter = new MyAdapter();
		}

		try {
			talkGroups = (ArrayList<TalkGroup>) dbUtil.findAll(TalkGroup.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (talkGroups != null && talkGroups.size() > 0) {
			mApplication.mTalkGroupMap.clear();
			listview.setAdapter(myAdapter);
			no_talk_group.setVisibility(View.GONE);
		} else {
			mApplication.mTalkGroupMap.clear();
			myAdapter.notifyDataSetChanged();
			no_talk_group.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		dissLoding();
		no_talk_group.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;

		default:
			break;
		}
	}


	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return talkGroups.size();
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
		public View getView(int position, View converView, ViewGroup arg2) {

			ItemHolder itemHolder;
			final TalkGroup tG = talkGroups.get(position);

			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_talk_group_item, null);
				itemHolder = new ItemHolder();
				itemHolder.talk_group_poster = (CubeImageView) converView
						.findViewById(R.id.talk_group_poster);
				itemHolder.talk_group_name = (TextView) converView
						.findViewById(R.id.talk_group_name);
				itemHolder.talk_group_level = (TextView) converView
						.findViewById(R.id.talk_group_level);
				converView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) converView.getTag();
			}

			CommonUtils.startImageLoader(cubeimageLoader, tG.getPoster(),
					itemHolder.talk_group_poster);

			itemHolder.talk_group_name.setText(tG.getName() + " ( "
					+ tG.getNumber() + "人 ) ");
			itemHolder.talk_group_level.setText("LV" + tG.getLevel());

            converView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
					msgDialog.setContent("确定分享至该群", "", "确认", "取消");
					msgDialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							msgDialog.dismiss();
						}
					});
					msgDialog.setOKListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							final EMConversation conversation = EMChatManager.getInstance().getConversation(tG.getHuanxin_groupid());
							EMMessage message = null;
							if (!TextUtils.isEmpty(type)) {
								final EMMessage forward_msg = EMChatManager.getInstance().getMessage(msgId);
								if ("Forward_img".equals(type)) {
									ImageMessageBody iBody = (ImageMessageBody) forward_msg.getBody();
									String localUrl = iBody.getLocalUrl();
									if (new File(localUrl).exists()) {
										message = forward_msg;
									} else {
										try {
											new File(localUrl).createNewFile();
										} catch (IOException e) {
											e.printStackTrace();
										}
										showLoding("");
										SimpleDownLoadUtils.download(iBody.getRemoteUrl(), localUrl, new SimpleDownLoadUtils.DownloadListener() {
											@Override
											public void DownLoadComplete(String url, String outPath) {
												sendMsgToGroup(conversation, tG, forward_msg, false);
											}

											@Override
											public void DownLoadFailed(String url, String outPath) {
												ToastUtils.Infotoast(mContext, "分享失败");
											}
										});
										return;
									}
								}
								if ("Forward_video".equals(type)) {
									VideoMessageBody vBody = (VideoMessageBody) forward_msg.getBody();
									String localUrl = vBody.getLocalUrl();
									if (new File(localUrl).exists()) {
										message = forward_msg;
									} else {
										try {
											new File(localUrl).createNewFile();
										} catch (IOException e) {
											e.printStackTrace();
										}
										showLoding("");
										SimpleDownLoadUtils.download(vBody.getRemoteUrl(), localUrl, new SimpleDownLoadUtils.DownloadListener() {
											@Override
											public void DownLoadComplete(String url, String outPath) {
												sendMsgToGroup(conversation, tG, forward_msg, false);
											}

											@Override
											public void DownLoadFailed(String url, String outPath) {
												ToastUtils.Infotoast(mContext,"分享失败");
											}
										});
										return;
									}
								}
							} else {
								message = EMMessage.createSendMessage(EMMessage.Type.TXT);
								if (url != null && !url.equals("")) {
									TextMessageBody txtBody = new TextMessageBody("这个商品不错哦，快来看看吧!" + url);
									// 设置消息body
									message.addBody(txtBody);
								} else {
									TextMessageBody txtBody = new TextMessageBody("号码直通车");
									// 设置消息body
									message.addBody(txtBody);
									message.setAttribute("train_id", train_id);
									message.setAttribute("train_name", train_name);
									message.setAttribute("train_tag", train_tag);
									message.setAttribute("train_poster", train_poster);
									message.setAttribute("shop", shop);
								}
							}
							sendMsgToGroup(conversation,tG,message,true);
						}
					});
					msgDialog.show();
				}
			});

			return converView;
		}

	}

	public void sendMsgToGroup(EMConversation conversation,TalkGroup tG,EMMessage message,boolean showLoading){
		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(tG.getHuanxin_groupid());
		message.setChatType(EMMessage.ChatType.GroupChat);
		// 把messgage加到conversation中
		conversation.addMessage(message);
		if(showLoading){
			showLoding("");
		}
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dissLoding();
						ToastUtils.Infotoast(mContext, "分享成功");
						AnimFinsh();
					}
				});

			}

			@Override
			public void onError(int i, String s) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dissLoding();
						ToastUtils.Infotoast(mContext, "分享失败");
					}
				});

			}

			@Override
			public void onProgress(int i, String s) {
				//showLoding("");
			}
		});
	}

	class ItemHolder {
		CubeImageView talk_group_poster;
		TextView talk_group_name;
		TextView talk_group_level;
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
