/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunao.benben.hx.chatuidemo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.MessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.FileUtils;
import com.easemob.util.LatLng;
import com.easemob.util.TextFormater;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.ChatImages;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsTemp;
import com.xunao.benben.bean.MyEMConversation;
import com.xunao.benben.bean.MyEMConversationList;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.Constant;
import com.xunao.benben.hx.chatuidemo.activity.AlertDialog;
import com.xunao.benben.hx.chatuidemo.activity.BaiduMapActivity;
import com.xunao.benben.hx.chatuidemo.activity.BaseActivity;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.hx.chatuidemo.activity.ContextMenu;
import com.xunao.benben.hx.chatuidemo.activity.ShowNormalFileActivity;
import com.xunao.benben.hx.chatuidemo.activity.ShowVideoActivity;
import com.xunao.benben.hx.chatuidemo.task.LoadImageTask;
import com.xunao.benben.hx.chatuidemo.task.LoadVideoImageTask;
import com.xunao.benben.hx.chatuidemo.utils.ImageCache;
import com.xunao.benben.hx.chatuidemo.utils.ImageUtils;
import com.xunao.benben.hx.chatuidemo.utils.SmileUtils;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityChatPicSet;
import com.xunao.benben.ui.item.ActivityContactsInfo;
import com.xunao.benben.ui.item.ActivityFriendUnionDetail;
import com.xunao.benben.ui.item.ActivityMyNumberTrainDetail;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.ui.promotion.ActivityPromotionDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;

public class MessageAdapter extends BaseAdapter {

	private final static String TAG = "msg";

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;
	private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
	private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
	private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
	private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private String username;
	private LayoutInflater inflater;
	private Activity activity;

	// reference to conversation object in chatsdk
	private EMConversation conversation;
	// private ArrayList<MyEMConversation> mMyEMConversations;

	private Context context;
	private int chatType;
	ImageLoader cubeImageLoader;
	private MyEMConversationList conversationList = new MyEMConversationList();
	private Map<String, Timer> timers = new Hashtable<String, Timer>();

	private User user;
    private List<ChatImages> chatImageses = new ArrayList<>();

	public MessageAdapter(Context context, String username, int chatType,
			ImageLoader cubeImageLoader, User user) {
		this.user = user;
		this.username = username;
		this.context = context;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.cubeImageLoader = cubeImageLoader;
		this.conversation = EMChatManager.getInstance().getConversation(
				username);
		this.chatType = chatType;
		// public static final int CHATTYPE_SINGLE = 1;
		// public static final int CHATTYPE_GROUP = 2;

		try {
			List<MyEMConversation> findAll = CrashApplication
					.getInstance()
					.getDb()
					.findAll(
							Selector.from(MyEMConversation.class).where(
									"groupId", "==", username));
			if (findAll != null) {
				for (MyEMConversation e : findAll) {
					conversationList.MyEMConversationMap.put(
							e.getHuanxin_username(), e);
				}
			}

		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (chatType == ChatActivity.CHATTYPE_GROUP) {
			InteNetUtils.getInstance(context).getTalkGroupMemberInfo(username,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {

						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								MyEMConversationList temp = new MyEMConversationList();
								temp.parseJSON(jsonObject);
								conversationList = temp;
								MessageAdapter.this.notifyDataSetChanged();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NetRequestException e) {
								e.printStackTrace();
							}

						}
					});
		}

//        initPic();

	}

    private void initPic(){
        chatImageses.clear();
        for(int i=0;i<conversation.getMsgCount();i++) {
            EMMessage message = getItem(i);
            String thumbnailPath = "";
            String filePath = "";
            String remotePath = "";
            String secret="";
            if (message.getType() == EMMessage.Type.IMAGE) {
                // 接收方向的消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                    secret = imgBody.getSecret();
                    if (imgBody.getLocalUrl() != null) {
                        // String filePath = imgBody.getLocalUrl();
                        remotePath = imgBody.getRemoteUrl();
                        filePath = ImageUtils.getImagePath(remotePath);
                        String thumbRemoteUrl = imgBody.getThumbnailUrl();
                        thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    }
                } else {
                    // 发送的消息
                    // process send message
                    // send pic, show the pic directly
                    ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                    filePath = imgBody.getLocalUrl();
                    thumbnailPath = ImageUtils.getThumbnailImagePath(filePath);
                    secret = imgBody.getSecret();
                    if (filePath != null && new File(filePath).exists()) {
                        remotePath = "";
                    } else {
                        remotePath = IMAGE_DIR;
                    }
                }
                ChatImages chatImages = new ChatImages();
                chatImages.setMsgId(message.getMsgId());
                chatImages.setThumbnailPath(thumbnailPath);
                chatImages.setFilePath(filePath);
                chatImages.setRemotePath(remotePath);
                chatImages.setSecret(secret);
                chatImageses.add(chatImages);
            }
        }
    }

	// public void setUser(String user) {
	// this.user = user;
	// }

	public void MynotifyDataSetChanged() {

		if (chatType == ChatActivity.CHATTYPE_GROUP) {
			MessageAdapter.this.notifyDataSetChanged();
			InteNetUtils.getInstance(context).getTalkGroupMemberInfo(username,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							MessageAdapter.this.notifyDataSetChanged();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								conversationList = new MyEMConversationList();
								conversationList.parseJSON(jsonObject);
								MessageAdapter.this.notifyDataSetChanged();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NetRequestException e) {
								e.printStackTrace();
							}

						}
					});
		} else {
			MessageAdapter.this.notifyDataSetChanged();
		}
	}

	/**
	 * 获取item数
	 */
	public int getCount() {
		return conversation.getMsgCount();
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		// notifyDataSetChanged();
        initPic();
		MynotifyDataSetChanged();
	}

	/**
	 * 刷新页面
	 */
	public void Deleterefresh() {
        initPic();
		notifyDataSetChanged();
	}

	public EMMessage getItem(int position) {
		return conversation.getMessage(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = conversation.getMessage(position);

		MessageBody body = message.getBody();

		if (body instanceof TextMessageBody) {
			TextMessageBody txtBody = (TextMessageBody) body;
			Spannable span = SmileUtils.getSmiledText(context,
					txtBody.getMessage());
			String string = span.toString();
			String info = "";
			if (string.length() > 10) {
				info = string.substring(string.length() - 10, string.length());
				if (info.equals("&XUNAOEXIT")) {
					return 16;
				}
			}
		}

		if (message.getType() == EMMessage.Type.TXT) {
			if (message.getBooleanAttribute(
					Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL
						: MESSAGE_TYPE_SENT_VOICE_CALL;
			else if (message.getBooleanAttribute(
					Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL
						: MESSAGE_TYPE_SENT_VIDEO_CALL;
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT
					: MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE
					: MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getType() == EMMessage.Type.LOCATION) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION
					: MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE
					: MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO
					: MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == EMMessage.Type.FILE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE
					: MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}

	public int getViewTypeCount() {
		return 17;
	}

	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {
		case LOCATION:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_location, null) : inflater
					.inflate(R.layout.row_sent_location, null);
		case IMAGE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_picture, null) : inflater
					.inflate(R.layout.row_sent_picture, null);

		case VOICE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_voice, null) : inflater
					.inflate(R.layout.row_sent_voice, null);
		case VIDEO:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_video, null) : inflater
					.inflate(R.layout.row_sent_video, null);
		case FILE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_file, null) : inflater
					.inflate(R.layout.row_sent_file, null);
		default:
			if (message.getBooleanAttribute(// 语音通话
					Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                return message.direct == EMMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_voice_call, null)
                        : inflater.inflate(R.layout.row_sent_voice_call, null);
            }else if (message.getBooleanAttribute(// 视频通话
					Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                return message.direct == EMMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_video_call, null)
                        : inflater.inflate(R.layout.row_sent_video_call, null);
            }else {
                String train_id = message.getStringAttribute("train_id", "");
                int type = message.getIntAttribute("type",0);
                if (!train_id.equals("") && type!=1) {
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater
                            .inflate(R.layout.row_received_number_train, null) : inflater
                            .inflate(R.layout.row_sent_number_train, null);

                } else {
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater
                            .inflate(R.layout.row_received_message, null) : inflater
                            .inflate(R.layout.row_sent_message, null);
                }
            }

		}
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		final ChatType chatType = message.getChatType();
		final ViewHolder holder;

		if (message.getType() == EMMessage.Type.TXT) {
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			Spannable span = SmileUtils.getSmiledText(context,
					txtBody.getMessage());
			String string = span.toString();
			String info = "";
			if (string.length() > 10) {
				info = string.substring(string.length() - 10, string.length());
				if (!info.equals("&XUNAOEXIT")) {
					convertView = getNomalView(position, convertView, message,
							chatType);
				} else {
					if (convertView == null) {
						holder = new ViewHolder();
						convertView = View.inflate(context,
								R.layout.activity_member_exit_group_item, null);
						convertView.setTag(holder);
						holder.member_exit = ((TextView) convertView
								.findViewById(R.id.member_exit));
					} else {
						holder = (ViewHolder) convertView.getTag();
					}
					String[] exit = string.split("&");
					holder.member_exit.setText(exit[0]);
				}
			} else {
				convertView = getNomalView(position, convertView, message,
						chatType);
			}
		} else {
			convertView = getNomalView(position, convertView, message, chatType);
		}

		return convertView;
	}

	private View getNomalView(final int position, View convertView,
			final EMMessage message, final ChatType chatType) {
		final ViewHolder holder;
//		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (message.getType() == EMMessage.Type.IMAGE) {
				try {
					holder.iv = ((ImageView) convertView
							.findViewById(R.id.iv_sendPicture));
					holder.head_iv = (CubeImageView) convertView
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView
							.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

			} else if (message.getType() == EMMessage.Type.TXT) {

				try {
                    String train_id = message.getStringAttribute("train_id", "");
                    int type = message.getIntAttribute("type",0);
                    if (!train_id.equals("") && type!=1) {
                        holder.ll_number_train = (LinearLayout) convertView.findViewById(R.id.ll_number_train);
                        holder.train_poster = (CubeImageView) convertView.findViewById(R.id.train_poster);
                        holder.train_name = (TextView) convertView.findViewById(R.id.train_name);
                        holder.train_tag = (TextView) convertView.findViewById(R.id.train_tag);
                    }else{
                        holder.tv = (TextView) convertView
                                .findViewById(R.id.tv_chatcontent);
                    }
//                    if(train_id.equals("")){
//                        // 这里是文字内容
//                        holder.tv = (TextView) convertView
//                                .findViewById(R.id.tv_chatcontent);
//                    }else{
//                        holder.ll_number_train = (LinearLayout) convertView.findViewById(R.id.ll_number_train);
//                        holder.train_poster = (CubeImageView) convertView.findViewById(R.id.train_poster);
//                        holder.train_name = (TextView) convertView.findViewById(R.id.train_name);
//                        holder.train_tag = (TextView) convertView.findViewById(R.id.train_tag);
//                    }
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					holder.head_iv = (CubeImageView) convertView
							.findViewById(R.id.iv_userhead);

					holder.tv_userId = (TextView) convertView
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

				// 语音通话及视频通话
				if (message.getBooleanAttribute(
						Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
						|| message.getBooleanAttribute(
								Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
					holder.iv = (ImageView) convertView
							.findViewById(R.id.iv_call_icon);
					holder.tv = (TextView) convertView
							.findViewById(R.id.tv_chatcontent);
				}

			} else if (message.getType() == EMMessage.Type.VOICE) {
				try {
					holder.iv = ((ImageView) convertView
							.findViewById(R.id.iv_voice));
					holder.head_iv = (CubeImageView) convertView
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView
							.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView
							.findViewById(R.id.tv_userid);
					holder.iv_read_status = (ImageView) convertView
							.findViewById(R.id.iv_unread_voice);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.LOCATION) {
				try {
					holder.head_iv = (CubeImageView) convertView
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView
							.findViewById(R.id.tv_location);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.VIDEO) {
				try {
					holder.iv = ((ImageView) convertView
							.findViewById(R.id.chatting_content_iv));
					holder.head_iv = (CubeImageView) convertView
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView
							.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					holder.size = (TextView) convertView
							.findViewById(R.id.chatting_size_iv);
					holder.timeLength = (TextView) convertView
							.findViewById(R.id.chatting_length_iv);
					holder.playBtn = (ImageView) convertView
							.findViewById(R.id.chatting_status_btn);
					holder.container_status_btn = (LinearLayout) convertView
							.findViewById(R.id.container_status_btn);
					holder.tv_userId = (TextView) convertView
							.findViewById(R.id.tv_userid);

				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.FILE) {
				try {
					holder.head_iv = (CubeImageView) convertView
							.findViewById(R.id.iv_userhead);
					holder.tv_file_name = (TextView) convertView
							.findViewById(R.id.tv_file_name);
					holder.tv_file_size = (TextView) convertView
							.findViewById(R.id.tv_file_size);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					holder.tv_file_download_state = (TextView) convertView
							.findViewById(R.id.tv_file_state);
					holder.ll_container = (LinearLayout) convertView
							.findViewById(R.id.ll_file_container);
					// 这里是进度值
					holder.tv = (TextView) convertView
							.findViewById(R.id.percentage);
				} catch (Exception e) {
				}
				try {
					holder.tv_userId = (TextView) convertView
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

			}

			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}

		// 群聊时，显示接收的消息的发送人的名称
		if (chatType == ChatType.GroupChat) {
			if (message.direct == EMMessage.Direct.RECEIVE) {
				// demo用username代替nick
				if (conversationList != null) {
					MyEMConversation myEMConversation = conversationList.MyEMConversationMap
							.get(message.getFrom());
					if (myEMConversation != null) {
						// holder.tv_userId.setText(myEMConversation
						// .getGroup_nick_name());

						if ("null"
								.equals(myEMConversation.getGroup_nick_name())
								|| "".equals(myEMConversation
										.getGroup_nick_name())) {
							holder.tv_userId.setText(myEMConversation
									.getNick_name());

						} else {
							holder.tv_userId.setText(myEMConversation
									.getGroup_nick_name());
						}
						CommonUtils.startImageLoader(cubeImageLoader,
								myEMConversation.getPoster(), holder.head_iv);
					} else {
						InteNetUtils
								.getInstance(CrashApplication.getInstance())
								.getContactInfoFromHXg(message.getFrom(),
										new RequestCallBack<String>() {

											@Override
											public void onSuccess(
													ResponseInfo<String> arg0) {
												try {
													JSONObject jsonObject = new JSONObject(
															arg0.result);
													MyEMConversation myEMConversation = new MyEMConversation();
													myEMConversation
															.parseSingleJSON(jsonObject);
													myEMConversation
															.setGroupId(username);

													holder.tv_userId
															.setText(myEMConversation
																	.getNick_name());
													CommonUtils
															.startImageLoader(
																	cubeImageLoader,
																	myEMConversation
																			.getPoster(),
																	holder.head_iv);
													conversationList.MyEMConversationMap
															.put(myEMConversation
																	.getHuanxin_username(),
																	myEMConversation);
													CrashApplication
															.getInstance()
															.getDb()
															.saveOrUpdate(
																	myEMConversation);
												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}

											@Override
											public void onFailure(
													HttpException arg0,
													String arg1) {

											}
										});
					}
				}
			} else {
				CommonUtils.startImageLoader(cubeImageLoader, user.getPoster(),
						holder.head_iv);
			}
		} else {
			Contacts contacts = (Contacts) CrashApplication.getInstance().mContactsMap
					.get(username);

			if (message.direct == EMMessage.Direct.RECEIVE) {
				if (contacts != null) {
					CommonUtils.startImageLoader(cubeImageLoader,
							contacts.getPoster(), holder.head_iv);
				} else {
					try {
						ContactsTemp findFirst = CrashApplication
								.getInstance()
								.getDb()
								.findFirst(
										Selector.from(ContactsTemp.class)
												.where("huanxin_username", "=",
														username));
						if (findFirst != null) {
							CommonUtils.startImageLoader(cubeImageLoader,
									findFirst.getPoster(), holder.head_iv);
						}else{
                            String img = message.getStringAttribute("leg_poster","");
                            CommonUtils.startImageLoader(cubeImageLoader,
                                    img, holder.head_iv);
                        }
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else if (message.direct == EMMessage.Direct.SEND) {
				CommonUtils.startImageLoader(cubeImageLoader, user.getPoster(),
						holder.head_iv);
			}
		}

		// 如果是发送的消息并且不是群聊消息，显示已读textview
		if (message.direct == EMMessage.Direct.SEND
				&& chatType != ChatType.GroupChat) {
			holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
			holder.tv_delivered = (TextView) convertView
					.findViewById(R.id.tv_delivered);
			if (holder.tv_ack != null) {
				if (message.isAcked) {
					if (holder.tv_delivered != null) {
						holder.tv_delivered.setVisibility(View.INVISIBLE);
					}
					holder.tv_ack.setVisibility(View.VISIBLE);
				} else {
					holder.tv_ack.setVisibility(View.INVISIBLE);

					// check and display msg delivered ack status
					if (holder.tv_delivered != null) {
						if (message.isDelivered) {
							holder.tv_delivered.setVisibility(View.VISIBLE);
						} else {
							holder.tv_delivered.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		} else {
			// 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION)
					&& !message.isAcked && chatType != ChatType.GroupChat) {
				// 不是语音通话记录
				if (!message.getBooleanAttribute(
						Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
					try {
						EMChatManager.getInstance().ackMessageRead(
								message.getFrom(), message.getMsgId());
						// 发送已读回执
						message.isAcked = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		switch (message.getType()) {
		// 根据消息type显示item
		case IMAGE: // 图片
			handleImageMessage(message, holder, position, convertView);
			break;
		case TXT: // 文本
			if (message.getBooleanAttribute(
					Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
					|| message.getBooleanAttribute(
							Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
				// 音视频通话
				handleCallMessage(message, holder, position);
			else
				handleTextMessage(message, holder, position);
			break;
		case LOCATION: // 位置
			handleLocationMessage(message, holder, position, convertView);
			break;
		case VOICE: // 语音
			handleVoiceMessage(message, holder, position, convertView);
			break;
		case VIDEO: // 视频
			handleVideoMessage(message, holder, position, convertView);
			break;
		case FILE: // 一般文件
			handleFileMessage(message, holder, position, convertView);
			break;
		default:
			// not supported
		}

		if (message.direct == EMMessage.Direct.SEND) {
			View statusView = convertView.findViewById(R.id.msg_status);
			// 重发按钮点击事件
			statusView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 显示重发消息的自定义alertdialog
					Intent intent = new Intent(activity, AlertDialog.class);
					intent.putExtra("msg",
							activity.getString(R.string.confirm_resend));
					intent.putExtra("title",
							activity.getString(R.string.resend));
					intent.putExtra("cancel", true);
					intent.putExtra("position", position);
					if (message.getType() == EMMessage.Type.TXT)
						activity.startActivityForResult(intent,
								ChatActivity.REQUEST_CODE_TEXT);
					else if (message.getType() == EMMessage.Type.VOICE)
						activity.startActivityForResult(intent,
								ChatActivity.REQUEST_CODE_VOICE);
					else if (message.getType() == EMMessage.Type.IMAGE)
						activity.startActivityForResult(intent,
								ChatActivity.REQUEST_CODE_PICTURE);
					else if (message.getType() == EMMessage.Type.LOCATION)
						activity.startActivityForResult(intent,
								ChatActivity.REQUEST_CODE_LOCATION);
					else if (message.getType() == EMMessage.Type.FILE)
						activity.startActivityForResult(intent,
								ChatActivity.REQUEST_CODE_FILE);
					else if (message.getType() == EMMessage.Type.VIDEO)
						activity.startActivityForResult(intent,
								ChatActivity.REQUEST_CODE_VIDEO);

				}
			});

		} else {
			// final String st = context.getResources().getString(
			// R.string.Into_the_blacklist);
			// // 点击头像进入联系人详情

			holder.head_iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String name = "";
					if (chatType == ChatType.GroupChat) {
						MyEMConversation myEMConversation = conversationList.MyEMConversationMap
								.get(message.getFrom());
						if (myEMConversation != null) {
							name = myEMConversation.getHuanxin_username();
						} else {
							ToastUtils.Errortoast(activity, "网络不可用");
						}
					} else {
						name = username;
					}

					// if (contacts != null) {
					// Intent intent = new Intent(context,
					// ActivityContactsInfo.class);
					// intent.putExtra("contacts", contacts);
					// ((BaseActivity) context).startActivityForResult(intent,
					// AndroidConfig.ContactsFragmentRequestCode);
					// ((BaseActivity) context).overridePendingTransition(
					// R.anim.in_from_right, R.anim.out_to_left);
					// } else {
                    String leg_id = message.getStringAttribute("leg_id","");
                    if(leg_id.equals("")) {
                        Intent intent = new Intent(context,
                                ActivityContactsInfo.class);
                        intent.putExtra("username", name);
                        ((BaseActivity) context).startActivityForResult(intent,
                                AndroidConfig.ContactsFragmentRequestCode);
                        ((BaseActivity) context).overridePendingTransition(
                                R.anim.in_from_right, R.anim.out_to_left);
                    }else{
                        Intent intentUnion = new Intent(context, ActivityFriendUnionDetail.class);
                        ((BaseActivity) context).overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_to_left);
                        intentUnion.putExtra("legid", leg_id);
                        ((BaseActivity) context).startActivityForResult(intentUnion, AndroidConfig.ContactsFragmentRequestCode);
                    }
				}

				// }
			});
		}

		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);

		if (position == 0) {
//			timestamp.setText(DateUtils.getTimestampString(new Date(message
//					.getMsgTime())));
			timestamp.setText(TimeUtil.getTime(new Date(message.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			if (DateUtils.isCloseEnough(message.getMsgTime(), conversation
					.getMessage(position - 1).getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
//				timestamp.setText(DateUtils.getTimestampString(new Date(message
//						.getMsgTime())));
				timestamp.setText(TimeUtil.getTime(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(EMMessage message, final ViewHolder holder,
			final int position) {
        final String train_id = message.getStringAttribute("train_id", "");
        final String shop = message.getStringAttribute("shop", "");
        int type = message.getIntAttribute("type",0);
        if(train_id.equals("")){
            TextMessageBody txtBody = (TextMessageBody) message.getBody();
            Spannable span = SmileUtils
                    .getSmiledText(context, txtBody.getMessage());
            // 设置内容
            holder.tv.setText(span, BufferType.SPANNABLE);
            holder.tv.setTextColor(Color.parseColor("#000000"));
			//自定义处理文本消息点击（有链接需要内部跳转）
			String linktype = message.getStringAttribute("link_type", "");//暂时没用
			//[小喇叭]我开通了新的促销,http://112.124.101.177:81/index.php/v2/promotion/promotiondetail/key/android?promotionid=44,来给我捧捧场吧!
			final String textContent = holder.tv.getText().toString();
			holder.tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(textContent.contains("http")|| textContent.contains("com")|| textContent.contains("www")){
						String url = textContent.substring(textContent.indexOf("http"));
						if(textContent.contains("promotionid")){
							//内部跳转
							Intent intent = new Intent(context, ActivityPromotionDetail.class);
							intent.putExtra("url", url);
							context.startActivity(intent);
						}else{
							//默认跳转
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(url));
							context.startActivity(intent);
						}
					}
				}
			});

            // 设置长按事件监听
            holder.tv.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    activity.startActivityForResult((new Intent(activity,
                                    ContextMenu.class)).putExtra("position", position)
                                    .putExtra("type", EMMessage.Type.TXT.ordinal()),
                            ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                    return true;
                }
            });
        }else{
            if(type==1){
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                Spannable span = SmileUtils
                        .getSmiledText(context, txtBody.getMessage());
                // 设置内容
//                holder.tv.setText(span, BufferType.SPANNABLE);
                holder.tv.setText(Html.fromHtml("<u>" + span + "</u>"));
                holder.tv.setTextColor(Color.parseColor("#068cd9"));
                holder.tv.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        activity.startActivityForResult((new Intent(activity,
                                        ContextMenu.class)).putExtra("position", position)
                                        .putExtra("type", EMMessage.Type.IMAGE.ordinal()),
                                ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                        return true;
                    }
                });
                holder.tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(shop.contains(user.getBenbenId())){
                            Intent intent = new Intent();
                            intent.setClass(activity,ActivityMyNumberTrainDetail.class);
                            intent.putExtra("id",train_id);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }else{
                            Intent intent = new Intent();
                            intent.setClass(activity,ActivityNumberTrainDetail.class);
                            intent.putExtra("id",train_id);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }


                    }
                });
            }else{
                String train_poster = message.getStringAttribute("train_poster", "");
                String train_name = message.getStringAttribute("train_name", "");
                String train_tag = message.getStringAttribute("train_tag", "");
                CommonUtils.startImageLoader(cubeImageLoader,train_poster, holder.train_poster);
                holder.train_name.setText(train_name);
                holder.train_tag.setText(train_tag);
                holder.ll_number_train.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        activity.startActivityForResult((new Intent(activity,
                                        ContextMenu.class)).putExtra("position", position)
                                        .putExtra("type", EMMessage.Type.IMAGE.ordinal()),
                                ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                        return true;
                    }
                });
                holder.ll_number_train.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(shop.contains(user.getBenbenId())){
                            Intent intent = new Intent();
                            intent.setClass(activity,ActivityMyNumberTrainDetail.class);
                            intent.putExtra("id",train_id);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }else{
                            Intent intent = new Intent();
                            intent.setClass(activity,ActivityNumberTrainDetail.class);
                            intent.putExtra("id",train_id);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }
                    }
                });
            }

        }

//        if(train_id.equals("")){
//            TextMessageBody txtBody = (TextMessageBody) message.getBody();
//            Spannable span = SmileUtils
//                    .getSmiledText(context, txtBody.getMessage());
//            // 设置内容
//            holder.tv.setText(span, BufferType.SPANNABLE);
//            // 设置长按事件监听
//            holder.tv.setOnLongClickListener(new OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    activity.startActivityForResult((new Intent(activity,
//                                    ContextMenu.class)).putExtra("position", position)
//                                    .putExtra("type", EMMessage.Type.TXT.ordinal()),
//                            ChatActivity.REQUEST_CODE_CONTEXT_MENU);
//                    return true;
//                }
//            });
//        }else{
//            String train_poster = message.getStringAttribute("train_poster", "");
//            String train_name = message.getStringAttribute("train_name", "");
//            String train_tag = message.getStringAttribute("train_tag", "");
//            CommonUtils.startImageLoader(cubeImageLoader,train_poster, holder.train_poster);
//            holder.train_name.setText(train_name);
//            holder.train_tag.setText(train_tag);
//            holder.ll_number_train.setOnLongClickListener(new OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    activity.startActivityForResult((new Intent(activity,
//                                    ContextMenu.class)).putExtra("position", position)
//                                    .putExtra("type", EMMessage.Type.IMAGE.ordinal()),
//                            ChatActivity.REQUEST_CODE_CONTEXT_MENU);
//                    return true;
//                }
//            });
//            holder.ll_number_train.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent();
//                    intent.setClass(activity,ActivityNumberTrainDetail.class);
//                    intent.putExtra("id",train_id);
//                    activity.startActivity(intent);
//                    activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                }
//            });
//        }



		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	/**
	 * 音视频通话记录
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleCallMessage(EMMessage message, ViewHolder holder,
			final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		holder.tv.setText(txtBody.getMessage());

	}

	/**
	 * 图片消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message,
			final ViewHolder holder, final int position, View convertView) {
		holder.pb.setTag(position);
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity,
						ContextMenu.class)).putExtra("position", position)
						.putExtra("type", EMMessage.Type.IMAGE.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		// 接收方向的消息
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.default_image);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					String thumbnailPath = ImageUtils
							.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv, filePath,
							imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// 发送的消息
		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists()) {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					holder.iv, filePath, null, message);
		} else {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					holder.iv, filePath, IMAGE_DIR, message);
		}

		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
//			holder.tv.setVisibility(View.VISIBLE);
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.GONE);
//							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
                                notifyDataSetChanged();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(
										activity,
										activity.getString(R.string.send_fail)
												+ activity
														.getString(R.string.connect_failuer_toast),
										0).show();
								timer.cancel();
                                notifyDataSetChanged();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			sendPictureMessage(message, holder);
		}
	}

	/**
	 * 视频消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVideoMessage(final EMMessage message,
			final ViewHolder holder, final int position, View convertView) {

		VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
		// final File image=new File(PathUtil.getInstance().getVideoPath(),
		// videoBody.getFileName());
		String localThumb = videoBody.getLocalThumb();

		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult(new Intent(activity,
						ContextMenu.class).putExtra("position", position)
						.putExtra("type", EMMessage.Type.VIDEO.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (localThumb != null) {

			showVideoThumbView(localThumb, holder.iv,
					videoBody.getThumbnailUrl(), message);
		}
		if (videoBody.getLength() > 0) {
			String time = DateUtils.toTimeBySecond(videoBody.getLength());
			holder.timeLength.setText(time);
		}
		holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (videoBody.getVideoFileLength() > 0) {
				String size = TextFormater.getDataSize(videoBody
						.getVideoFileLength());
				holder.size.setText(size);
			}
		} else {
			if (videoBody.getLocalUrl() != null
					&& new File(videoBody.getLocalUrl()).exists()) {
				String size = TextFormater.getDataSize(new File(videoBody
						.getLocalUrl()).length());
				holder.size.setText(size);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {

			// System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				// System.err.println("!!!! back receive");
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);

			} else {
				// System.err.println("!!!! not back receive, show image directly");
				holder.iv.setImageResource(R.drawable.default_image);
				if (localThumb != null) {
					showVideoThumbView(localThumb, holder.iv,
							videoBody.getThumbnailUrl(), message);
				}

			}

			return;
		}
		holder.pb.setTag(position);

		// until here ,deal with send video msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:

			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.GONE);
//							holder.tv.setText(message.progress + "%");
                            Log.d("ltf","3===================="+message.status);
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
                                notifyDataSetChanged();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(
										activity,
										activity.getString(R.string.send_fail)
												+ activity
														.getString(R.string.connect_failuer_toast),
										0).show();
								timer.cancel();
                                notifyDataSetChanged();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// sendMsgInBackground(message, holder);
			sendPictureMessage(message, holder);

		}

	}

	/**
	 * 语音消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message,
			final ViewHolder holder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
		holder.tv.setText(voiceBody.getLength() + "\"");
		holder.iv.setOnClickListener(new VoicePlayClickListener(message,
				holder.iv, holder.iv_read_status, this, activity, username));
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity,
						ContextMenu.class)).putExtra("position", position)
						.putExtra("type", EMMessage.Type.VOICE.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});
		if (((ChatActivity) activity).playMsgId != null
				&& ((ChatActivity) activity).playMsgId.equals(message
						.getMsgId()) && VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.anim.voice_from_icon);
			} else {
				holder.iv.setImageResource(R.anim.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				holder.iv.setImageResource(R.drawable.chatto_voice_playing);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (message.isListened()) {
				// 隐藏语音未听标志
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.VISIBLE);
			}
			System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				holder.pb.setVisibility(View.VISIBLE);
				System.err.println("!!!! back receive");
				((FileMessageBody) message.getBody())
						.setDownloadCallback(new EMCallBack() {

							@Override
							public void onSuccess() {
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										holder.pb.setVisibility(View.INVISIBLE);
										notifyDataSetChanged();
									}
								});

							}

							@Override
							public void onProgress(int progress, String status) {
							}

							@Override
							public void onError(int code, String message) {
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										holder.pb.setVisibility(View.INVISIBLE);
									}
								});

							}
						});

			} else {
				holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 文件消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleFileMessage(final EMMessage message,
			final ViewHolder holder, int position, View convertView) {
		final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message
				.getBody();
		final String filePath = fileMessageBody.getLocalUrl();
		holder.tv_file_name.setText(fileMessageBody.getFileName());
		holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody
				.getFileSize()));
		holder.ll_container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				File file = new File(filePath);
				if (file != null && file.exists()) {
					// 文件存在，直接打开
					FileUtils.openFile(file, (Activity) context);
				} else {
					// 下载
					context.startActivity(new Intent(context,
							ShowNormalFileActivity.class).putExtra("msgbody",
							fileMessageBody));
				}
				if (message.direct == EMMessage.Direct.RECEIVE
						&& !message.isAcked) {
					try {
						EMChatManager.getInstance().ackMessageRead(
								message.getFrom(), message.getMsgId());
						message.isAcked = true;
					} catch (EaseMobException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		String st1 = context.getResources().getString(R.string.Have_downloaded);
		String st2 = context.getResources()
				.getString(R.string.Did_not_download);
		if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
			System.err.println("it is receive msg");
			File file = new File(filePath);
			if (file != null && file.exists()) {
				holder.tv_file_download_state.setText(st1);
			} else {
				holder.tv_file_download_state.setText(st2);
			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.INVISIBLE);
			holder.tv.setVisibility(View.INVISIBLE);
			holder.staus_iv.setVisibility(View.INVISIBLE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.INVISIBLE);
			holder.tv.setVisibility(View.INVISIBLE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.GONE);
//							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.INVISIBLE);
								holder.tv.setVisibility(View.INVISIBLE);
								timer.cancel();
                                notifyDataSetChanged();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.INVISIBLE);
								holder.tv.setVisibility(View.INVISIBLE);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(
										activity,
										activity.getString(R.string.send_fail)
												+ activity
														.getString(R.string.connect_failuer_toast),
										0).show();
								timer.cancel();
                                notifyDataSetChanged();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// 发送消息
			sendMsgInBackground(message, holder);
		}

	}

	/**
	 * 处理位置消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleLocationMessage(final EMMessage message,
			final ViewHolder holder, final int position, View convertView) {
		TextView locationView = ((TextView) convertView
				.findViewById(R.id.tv_location));
		LocationMessageBody locBody = (LocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());
		LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
		locationView.setOnClickListener(new MapClickListener(loc, locBody
				.getAddress()));
		locationView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity,
						ContextMenu.class)).putExtra("position", position)
						.putExtra("type", EMMessage.Type.LOCATION.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return false;
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) {
			return;
		}
		// deal with send message
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @param holder
	 */
	public void sendMsgInBackground(final EMMessage message,
			final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);

		final long start = System.currentTimeMillis();
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				// umeng自定义事件，
				sendEvent2Umeng(message, start);

				updateSendedView(message, holder);
			}

			@Override
			public void onError(int code, String error) {
				sendEvent2Umeng(message, start);

				updateSendedView(message, holder);
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	/*
	 * chat sdk will automatic download thumbnail image for the image message we
	 * need to register callback show the download progress
	 */
	private void showDownloadImageProgress(final EMMessage message,
			final ViewHolder holder) {
		System.err.println("!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();
		if (holder.pb != null)
			holder.pb.setVisibility(View.VISIBLE);
		if (holder.tv != null)
			holder.tv.setVisibility(View.GONE);

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							holder.tv.setText(progress + "%");

						}
					});
				}

			}

		});
	}

	/*
	 * send message with new sdk
	 */
	private void sendPictureMessage(final EMMessage message,
			final ViewHolder holder) {
		try {
			String to = message.getTo();

			// before send, update ui
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.GONE);
			holder.tv.setText("0%");

			final long start = System.currentTimeMillis();
			// if (chatType == ChatActivity.CHATTYPE_SINGLE) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.d(TAG, "send image message successfully");
					sendEvent2Umeng(message, start);
					activity.runOnUiThread(new Runnable() {
						public void run() {
							// send success
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					sendEvent2Umeng(message, start);

					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
							holder.staus_iv.setVisibility(View.VISIBLE);
							Toast.makeText(
									activity,
									activity.getString(R.string.send_fail)
											+ activity
													.getString(R.string.connect_failuer_toast),
									0).show();
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message,
			final ViewHolder holder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == EMMessage.Status.SUCCESS) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// holder.staus_iv.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// holder.staus_iv.setVisibility(View.GONE);
					// }

				} else if (message.status == EMMessage.Status.FAIL) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// }
					// holder.staus_iv.setVisibility(View.VISIBLE);
					Toast.makeText(
							activity,
							activity.getString(R.string.send_fail)
									+ activity
											.getString(R.string.connect_failuer_toast),
							0).show();
				}

				notifyDataSetChanged();
			}
		});
	}

	/**
	 * load image into image view
	 *
	 * @param thumbernailPath
	 * @param iv
	 * @return the image exists or not
	 */
	private boolean showImageView(final String thumbernailPath,
			final ImageView iv, final String localFullSizePath,
			String remoteDir, final EMMessage message) {
		// String imagename =
		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
		// localFullSizePath.length());
		// final String remote = remoteDir != null ? remoteDir+imagename :
		// imagename;
		final String remote = remoteDir;
		EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			android.view.ViewGroup.LayoutParams layoutParams = iv
					.getLayoutParams();
			if (bitmap != null) {
				layoutParams.width = (int) (bitmap.getWidth() * (PixelUtil
						.getScale()));
				layoutParams.height = (int) (bitmap.getHeight() * (PixelUtil
						.getScale()));
			}
			iv.setLayoutParams(layoutParams);
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    if(ChatActivity.isMySend) {
                        ChatActivity.isMySend = false;
                        initPic();
                    }
					System.err.println("image view on click");
                    Intent intent = new Intent(activity, ActivityChatPicSet.class);
                    intent.putExtra("chatImageses",(Serializable)chatImageses);

                    if (message != null
                            && message.direct == EMMessage.Direct.RECEIVE
                            && !message.isAcked
                            && message.getChatType() != ChatType.GroupChat) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(
                                    message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    int position=0;
                    for(int i=0;i<chatImageses.size();i++){
                        if(message.getMsgId().equals(chatImageses.get(i).getMsgId())){
                            position = i;
                            break;
                        }
                    }
                    intent.putExtra("position",position);
                    activity.startActivity(intent);
//					Intent intent = new Intent(activity, ShowBigImage.class);
//					File file = new File(localFullSizePath);
//					if (file.exists()) {
//						Uri uri = Uri.fromFile(file);
//						intent.putExtra("uri", uri);
//						System.err
//								.println("here need to check why download everytime");
//					} else {
//						// The local full size pic does not exist yet.
//						// ShowBigImage needs to download it from the server
//						// first
//						// intent.putExtra("", message.get);
//						ImageMessageBody body = (ImageMessageBody) message
//								.getBody();
//						intent.putExtra("secret", body.getSecret());
//						intent.putExtra("remotepath", remote);
//					}

//					activity.startActivity(intent);
				}
			});
			return true;
		} else {

			new LoadImageTask().execute(thumbernailPath, localFullSizePath,
					remote, message.getChatType(), iv, activity, message);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ChatActivity.isMySend) {
                        ChatActivity.isMySend = false;
                        initPic();
                    }
                    System.err.println("image view on click");
                    Intent intent = new Intent(activity, ActivityChatPicSet.class);
                    intent.putExtra("chatImageses", (Serializable) chatImageses);

                    if (message != null
                            && message.direct == EMMessage.Direct.RECEIVE
                            && !message.isAcked
                            && message.getChatType() != ChatType.GroupChat) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(
                                    message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    int position = 0;
                    for (int i = 0; i < chatImageses.size(); i++) {
                        if (message.getMsgId().equals(chatImageses.get(i).getMsgId())) {
                            position = i;
                            break;
                        }
                    }
                    intent.putExtra("position", position);
                    activity.startActivity(intent);
                }
            });
			return true;
		}

	}

	/**
	 * 展示视频缩略图
	 * 
	 * @param localThumb
	 *            本地缩略图路径
	 * @param iv
	 * @param thumbnailUrl
	 *            远程缩略图路径
	 * @param message
	 */
	private void showVideoThumbView(String localThumb, ImageView iv,
			String thumbnailUrl, final EMMessage message) {
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(localThumb);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					VideoMessageBody videoBody = (VideoMessageBody) message
							.getBody();
					System.err.println("video view is on click");
					Intent intent = new Intent(activity,
							ShowVideoActivity.class);
					intent.putExtra("localpath", videoBody.getLocalUrl());
					intent.putExtra("secret", videoBody.getSecret());
					intent.putExtra("remotepath", videoBody.getRemoteUrl());
					if (message != null
							&& message.direct == EMMessage.Direct.RECEIVE
							&& !message.isAcked
							&& message.getChatType() != ChatType.GroupChat) {
						message.isAcked = true;
						try {
							EMChatManager.getInstance().ackMessageRead(
									message.getFrom(), message.getMsgId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);

				}
			});

		} else {
			new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv,
					activity, message, this);
		}

	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		CubeImageView head_iv;
		TextView tv_userId;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;
		TextView member_exit;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
        LinearLayout ll_number_train;
        CubeImageView train_poster;
        TextView train_name;
        TextView train_tag;
	}

	/*
	 * 点击地图消息listener
	 */
	class MapClickListener implements View.OnClickListener {

		LatLng location;
		String address;

		public MapClickListener(LatLng loc, String address) {
			location = loc;
			this.address = address;

		}

		@Override
		public void onClick(View v) {
			Intent intent;
			intent = new Intent(context, BaiduMapActivity.class);
			intent.putExtra("latitude", location.latitude);
			intent.putExtra("longitude", location.longitude);
			intent.putExtra("address", address);
			activity.startActivity(intent);
		}

	}

	/**
	 * umeng自定义事件统计
	 * 
	 * @param message
	 */
	private void sendEvent2Umeng(final EMMessage message, final long start) {
		// TODO
		// activity.runOnUiThread(new Runnable() {
		// public void run() {
		// long costTime = System.currentTimeMillis() - start;
		// Map<String, String> params = new HashMap<String, String>();
		// if(message.status == EMMessage.Status.SUCCESS)
		// params.put("status", "success");
		// else
		// params.put("status", "failure");
		//
		// switch (message.getType()) {
		// case TXT:
		// case LOCATION:
		// MobclickAgent.onEventValue(activity, "text_msg", params, (int)
		// costTime);
		// MobclickAgent.onEventDuration(activity, "text_msg", (int) costTime);
		// break;
		// case IMAGE:
		// MobclickAgent.onEventValue(activity, "img_msg", params, (int)
		// costTime);
		// MobclickAgent.onEventDuration(activity, "img_msg", (int) costTime);
		// break;
		// case VOICE:
		// MobclickAgent.onEventValue(activity, "voice_msg", params, (int)
		// costTime);
		// MobclickAgent.onEventDuration(activity, "voice_msg", (int) costTime);
		// break;
		// case VIDEO:
		// MobclickAgent.onEventValue(activity, "video_msg", params, (int)
		// costTime);
		// MobclickAgent.onEventDuration(activity, "video_msg", (int) costTime);
		// break;
		// default:
		// break;
		// }
		//
		// }
		// });
	}

}