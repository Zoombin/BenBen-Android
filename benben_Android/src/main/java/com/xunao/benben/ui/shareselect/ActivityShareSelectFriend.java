package com.xunao.benben.ui.shareselect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.hx.chatuidemo.utils.ImageUtils;
import com.xunao.benben.ui.item.ActivityChoiceBroadCastFriendByArea;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.SimpleDownLoadUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityShareSelectFriend extends BaseActivity {
	private FloatingGroupExpandableListView listView;
	private WrapperExpandableListAdapter wrapperAdapter;
	private myAdapter adapter;
	private ArrayList<ContactsGroup> contactsGroup = new ArrayList<>();
    private String train_id;
    private String train_name;
    private String train_tag;
    private String train_poster;
    private String shop;
    private String url;
	private String type;
	private String msgId;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_share_select_friend);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("好友分享", "", "",
				R.drawable.icon_com_title_left, 0);

		listView = (FloatingGroupExpandableListView) findViewById(R.id.listView);
		listView.setGroupIndicator(null);
		adapter = new myAdapter();
		wrapperAdapter = new WrapperExpandableListAdapter(adapter);
		listView.setAdapter(wrapperAdapter);
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

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

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
        initLocalData();
	}

	private void groupOrderBy() {
		int size = contactsGroup.size();
		// 对组群排序,未分组放在后面
		ContactsGroup unGroup = null;
		ContactsGroup unGroup2 = null;
		for (int i = 0; i < size; i++) {
			if (contactsGroup.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = contactsGroup.remove(i);
				break;
			}

		}
		if (unGroup != null) {
			contactsGroup.add(unGroup);
		}

		for (int i = 0; i < size; i++) {
			if (contactsGroup.get(i).getName().equalsIgnoreCase("常用号码直通车")) {
				unGroup2 = contactsGroup.remove(i);
				break;
			}
		}

		if (unGroup2 != null) {
			contactsGroup.add(unGroup2);
		}

	}

	private void initLocalData() {
		try {
			List list = dbUtil.findAll(Selector.from(ContactsGroup.class)
					.where(WhereBuilder.b("id", "!=", "10000")));
			contactsGroup = (ArrayList<ContactsGroup>) list;
			groupOrderBy();
			for (ContactsGroup group : contactsGroup) {
				ArrayList<Contacts> arrayList = new ArrayList<Contacts>();
				List cList = dbUtil.findAll(Selector.from(Contacts.class)
						.where(WhereBuilder.b("group_id", "=", group.getId())
								.and("is_benben", "!=", "0")));

				if (cList != null && cList.size() > 0  ) {
					arrayList = (ArrayList<Contacts>) cList;
				}
				ArrayList<Contacts> contactsLArrayList = new ArrayList<Contacts>();
				
				
				for (Contacts contacts : arrayList) {

					contactsLArrayList.add(contacts);
				}

				group.setmContacts(contactsLArrayList);

			}
		} catch (DbException e) {
			e.printStackTrace();
		}

		adapter.notifyDataSetChanged();
	}


	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public Contacts getChild(int arg0, int arg1) {
			return contactsGroup.get(arg0).getmContacts().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final Contacts contact = getChild(groupPosition, childPosition);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_friend_union_member_content, null);
			}

			RoundedImageView item_phone_poster = (RoundedImageView) convertView
					.findViewById(R.id.chief_poster);
			MyTextView chief_id = (MyTextView) convertView
					.findViewById(R.id.chief_id);
			MyTextView chief_name = (MyTextView) convertView
					.findViewById(R.id.chief_name);
            CheckBox item_all = (CheckBox) convertView.findViewById(R.id.item_all);
            item_all.setVisibility(View.GONE);

			chief_id.setText("奔犇号：" + contact.getIs_benben());
			chief_name.setText(contact.getName());

			CommonUtils.startImageLoader(cubeimageLoader, contact.getPoster(),
					item_phone_poster);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                    msgDialog.setContent("确定分享给该好友", "", "确认", "取消");
                    msgDialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							msgDialog.dismiss();
						}
					});
                    msgDialog.setOKListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							final EMConversation conversation = EMChatManager.getInstance().getConversation(contact.getHuanxin_username());
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
												sendMsgToFriend(conversation,contact,forward_msg,false);
											}

											@Override
											public void DownLoadFailed(String url, String outPath) {
												ToastUtils.Infotoast(mContext,"分享失败");
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
												sendMsgToFriend(conversation,contact,forward_msg,false);
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
							sendMsgToFriend(conversation, contact, message, true);
						}
					});
                    msgDialog.show();
                }
            });


			return convertView;
		}

		public void sendMsgToFriend(EMConversation conversation,Contacts contact,EMMessage message,boolean showLoading){
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(contact.getHuanxin_username());
			message.setChatType(EMMessage.ChatType.Chat);
			// 把messgage加到conversation中
			conversation.addMessage(message);
			if(showLoading){
				showLoding("");
			}
			EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
				@Override
				public void onSuccess() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dissLoding();
							ToastUtils.Infotoast(mContext,"分享成功");
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
							ToastUtils.Infotoast(mContext,"分享失败");
						}
					});

				}

				@Override
				public void onProgress(int i, String s) {
					//showLoding("");
				}
			});
		}

		@Override
		public int getChildrenCount(int arg0) {
			return contactsGroup.get(arg0).getmContacts().size();
		}

		@Override
		public ContactsGroup getGroup(int arg0) {
			return contactsGroup.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return contactsGroup.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			HeaderViewHolder holder;

			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = View.inflate(mContext,
						R.layout.activity_friend_union_member_group_head, null);
				holder.item_group_name = (TextView) convertView
						.findViewById(R.id.item_group_name);
				holder.status_img = (ImageView) convertView
						.findViewById(R.id.status_img);
                holder.item_all = (CheckBox) convertView.findViewById(R.id.item_all);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
            holder.item_all.setVisibility(View.GONE);

			holder.item_group_name.setText(getGroup(groupPosition).getName()
					+ "(" + getGroup(groupPosition).getmContacts().size()
					+ "人)");



			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (listView.isGroupExpanded(groupPosition)) {
						listView.collapseGroup(groupPosition);
					} else {
						listView.expandGroup(groupPosition);
					}
				}
			});

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}


			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return false;
		}
	}

	class HeaderViewHolder {
		TextView item_group_name;
		ImageView status_img;
        CheckBox item_all;
	}


}
