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

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import u.aly.cp;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsTemp;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.bean.TalkGroupList;
import com.xunao.benben.bean.tx.BenbenEMConversation;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.Constant;
import com.xunao.benben.hx.chatuidemo.utils.SmileUtils;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.TimeUtil;

/**
 * 显示所有聊天记录adpater
 * 
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<BenbenEMConversation> {

	private LayoutInflater inflater;
	private List<BenbenEMConversation> conversationList;
	private List<BenbenEMConversation> copyConversationList;
	private ConversationFilter conversationFilter;
	private ImageLoader cubeimageLoader;
	private CrashApplication mCrashApplication;
	private Contacts contacts;
	private HashMap<String, ContactsTemp> mContactsTempMap;

	public ChatAllHistoryAdapter(Context context, int textViewResourceId,
			List<BenbenEMConversation> objects, ImageLoader cubeimageLoader) {
		super(context, textViewResourceId, objects);
		this.conversationList = objects;
		copyConversationList = new ArrayList<BenbenEMConversation>();
		copyConversationList.addAll(objects);
		mCrashApplication = CrashApplication.getInstance();
		mContactsTempMap = new HashMap<String, ContactsTemp>();
		try {
			List<ContactsTemp> mContactsTemps = mCrashApplication.getDb()
					.findAll(ContactsTemp.class);
			if (mContactsTemps != null) {
				for (ContactsTemp cm : mContactsTemps) {
					mContactsTempMap.put(cm.getHuanxin_username(), cm);
				}
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		inflater = LayoutInflater.from(context);
		this.cubeimageLoader = cubeimageLoader;

	}
	
	@Override
	public int getCount() {
		return copyConversationList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_chat_history, parent,
					false);
			ViewHolder holder1 = new ViewHolder();
			holder1.public_ = (TextView) convertView.findViewById(R.id.public_);
			holder1.name = (TextView) convertView.findViewById(R.id.name);
            holder1.tv_union_type = (TextView) convertView.findViewById(R.id.tv_union_type);
			holder1.unreadLabel = (TextView) convertView
					.findViewById(R.id.unread_msg_number);
			holder1.message = (TextView) convertView.findViewById(R.id.message);
			holder1.time = (TextView) convertView.findViewById(R.id.time);
			holder1.avatar = (CubeImageView) convertView
					.findViewById(R.id.avatar);
			holder1.msgState = convertView.findViewById(R.id.msg_state);
			holder1.list_item_layout = (RelativeLayout) convertView
					.findViewById(R.id.list_item_layout);
			convertView.setTag(holder1);
		}
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {

		}
		if (position % 2 == 0) {
			holder.list_item_layout
					.setBackgroundResource(R.drawable.mm_listitem);
		} else {
			holder.list_item_layout
					.setBackgroundResource(R.drawable.mm_listitem_grey);
		}
		BenbenEMConversation item = copyConversationList.get(position);
		if (item.getType() == BenbenEMConversation.NOMAL) {
			holder.public_.setVisibility(View.GONE);
			
			
			// 获取与此用户/群组的会话
			EMConversation conversation = item.getmEMConversation();
			// 获取用户username或者群组groupid
			final String username = conversation.getUserName();
			EMContact contact = null;
			boolean isGroup = false;
			if (conversation.isGroup()) {
				isGroup = true;
			}
			// for (EMGroup group : groups) {
			// if (group.getGroupId().equals(username)) {
			// isGroup = true;
			// contact = group;
			// break;
			// }
			// }
			if (isGroup) {
				holder.public_.setVisibility(View.GONE);
				
				// 群聊消息，显示群聊头像
				holder.avatar.setTag(R.string.type, "group");
				holder.avatar.setBackgroundResource(R.drawable.ic_group_poster);
				TalkGroup contactsGroup = mCrashApplication.mTalkGroupMap
						.get(username);
				
				if(contactsGroup != null && contactsGroup.getStatus().equals("1")){
                    holder.tv_union_type.setVisibility(View.GONE);
					holder.unreadLabel.setVisibility(View.GONE);
					holder.message.setVisibility(View.GONE);
					holder.time.setVisibility(View.GONE);
					holder.avatar.setVisibility(View.GONE);
					holder.msgState.setVisibility(View.GONE);
					holder.list_item_layout.setVisibility(View.GONE);
				}
				
				if (contactsGroup != null) {
					CommonUtils.startImageLoader(cubeimageLoader,
							contactsGroup.getPoster(), holder.avatar);
					holder.name.setText(contactsGroup.getName());
//                            + "("+ contactsGroup.getNumber() + ")");
				} else {
					InteNetUtils.getInstance(mCrashApplication).getTalkGroup(
							new RequestCallBack<String>() {

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {

									try {
										JSONObject jsonObject = new JSONObject(
												arg0.result);
										TalkGroupList groupList = new TalkGroupList();
										groupList = groupList
												.parseJSON(jsonObject);
										ArrayList<TalkGroup> talkGroups = groupList
												.getTalkGroups();

										if (talkGroups != null
												&& talkGroups.size() > 0) {
											mCrashApplication.getDb()
													.deleteAll(TalkGroup.class);
											mCrashApplication.getDb().saveAll(
													talkGroups);
											// 改变mapplication里的map、
										} else {
											mCrashApplication.getDb()
													.deleteAll(TalkGroup.class);
										}
									} catch (NetRequestException e) {
										e.printStackTrace();
									} catch (DbException e) {
										e.printStackTrace();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									try {
										ArrayList<TalkGroup> talkGroups = (ArrayList<TalkGroup>) mCrashApplication
												.getDb().findAll(
														TalkGroup.class);

										for (TalkGroup t : talkGroups) {
											mCrashApplication.mTalkGroupMap.put(
													t.getHuanxin_groupid(), t);
										}

										ChatAllHistoryAdapter.this
												.notifyDataSetChanged();

									} catch (DbException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									// TODO Auto-generated method stub

								}
							});

				}

			} else {
				// 本地或者服务器获取用户详情，以用来显示头像和nick
				holder.avatar.setBackgroundResource(R.drawable.default_face);
				holder.avatar.setTag(R.string.type, "default");
				contacts = mCrashApplication.mContactsMap.get(username);
				if (contacts != null) {
					CommonUtils.startImageLoader(cubeimageLoader,
							contacts.getPoster(), holder.avatar);

					holder.name.setText(contacts.getName());
				} else {
					ContactsTemp contactsTemp = mContactsTempMap.get(username);
					if (contactsTemp != null) {
						CommonUtils.startImageLoader(cubeimageLoader,
								contactsTemp.getPoster(), holder.avatar);

						holder.name.setText(contactsTemp.getName());
					} else {
						InteNetUtils.getInstance(mCrashApplication)
								.getContactInfoFromHXg(username,
										new RequestCallBack<String>() {
											@Override
											public void onFailure(
													HttpException arg0,
													String arg1) {
											}

											@Override
											public void onSuccess(
													ResponseInfo<String> arg0) {
												try {
													JSONObject jsonObject = new JSONObject(
															arg0.result);
													ContactsTemp contacts = new ContactsTemp();
													contacts.parseJSONSingle(jsonObject);
													mContactsTempMap.put(
															username, contacts);

													CommonUtils
															.startImageLoader(
																	cubeimageLoader,
																	contacts.getPoster(),
																	holder.avatar);

													holder.name
															.setText(contacts
																	.getName());
													try {

														CrashApplication
																.getInstance()
																.getDb()
																.delete(ContactsTemp.class,
																		WhereBuilder
																				.b("huanxin_username",
																						"=",
																						username));
														mCrashApplication
																.getDb()
																.saveOrUpdate(
																		contacts);
													} catch (DbException e) {
														// TODO Auto-generated
														// catch block
														e.printStackTrace();
													}
												} catch (JSONException e) {
													e.printStackTrace();
												} catch (NetRequestException e) {
													e.printStackTrace();
												}

											}
										});
					}
				}
			}

			if (conversation.getUnreadMsgCount() > 0) {
				// 显示与此用户的消息未读数
				holder.unreadLabel.setText(String.valueOf(conversation
						.getUnreadMsgCount()));
				holder.unreadLabel.setVisibility(View.VISIBLE);
			} else {
				holder.unreadLabel.setVisibility(View.INVISIBLE);
			}

			if (conversation.getMsgCount() != 0) {
				// 把最后一条消息的内容作为item的message内容


				EMMessage lastMessage = conversation.getLastMessage();
                holder.tv_union_type.setVisibility(View.GONE);
                if(String.valueOf(holder.name.getText()).equals("")){
                    holder.name.setText(lastMessage.getFrom());
                    String img= lastMessage.getStringAttribute("leg_poster","");
                    CommonUtils.startImageLoader(cubeimageLoader,img, holder.avatar);
                    String leg_type= lastMessage.getStringAttribute("leg_type","");
                    holder.tv_union_type.setVisibility(View.VISIBLE);
                    if(leg_type.equals("2")){
                        holder.tv_union_type.setVisibility(View.VISIBLE);
                        holder.tv_union_type.setText("英雄");
                        holder.tv_union_type.setTextColor(Color.rgb(33, 207, 213));
                        holder.tv_union_type.setBackgroundResource(R.drawable.textview_friend_union_2);
                    }else if(leg_type.equals("1")){
                        holder.tv_union_type.setVisibility(View.VISIBLE);
                        holder.tv_union_type.setText("工作");
                        holder.tv_union_type.setTextColor(Color.rgb(233,81,135));
                        holder.tv_union_type.setBackgroundResource(R.drawable.textview_friend_union_1);
                    }else{
                        holder.tv_union_type.setVisibility(View.GONE);
                    }
                }
				Spannable smiledText = SmileUtils.getSmiledText(getContext(),
						getMessageDigest(lastMessage, (this.getContext())));

				String string = smiledText.toString();
				String substring = "";
				if (string.length() > 9) {
					substring = string.substring(string.length() - 9,
							string.length());
					if (substring.equals("XUNAOEXIT")) {
						String[] strings = string.split("&");
						holder.message.setText(strings[0]);
					} else {
						holder.message
								.setText(smiledText, BufferType.SPANNABLE);
					}

				} else {
					holder.message.setText(smiledText, BufferType.SPANNABLE);
				}

//				holder.time.setText(DateUtils.getTimestampString(new Date(
//						lastMessage.getMsgTime())));

				holder.time.setText(TimeUtil.getTime(new Date(
						lastMessage.getMsgTime())));
				if (lastMessage.direct == EMMessage.Direct.SEND
						&& lastMessage.status == EMMessage.Status.FAIL) {
					holder.msgState.setVisibility(View.VISIBLE);
				} else {
					holder.msgState.setVisibility(View.GONE);
				}
			}
		}

		return convertView;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
//			if (message.direct == EMMessage.Direct.RECEIVE) {
//				// 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
//				// digest = EasyUtils.getAppResourceString(context,
//				// "location_recv");
//				digest = getStrng(context, R.string.location_recv);
//				digest = String.format(digest, message.getFrom());
//				return digest;
//			} else {
//				// digest = EasyUtils.getAppResourceString(context,
//				// "location_prefix");
//				digest = getStrng(context, R.string.location_prefix);
//			}
            LocationMessageBody locBody = (LocationMessageBody) message.getBody();
            digest = getStrng(context, R.string.location_recv)
                    + locBody.getAddress();
			break;
		case IMAGE: // 图片消息
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.picture)
					+ imageBody.getFileName();
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			if (!message.getBooleanAttribute(
					Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			} else {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call)
						+ txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	private static class ViewHolder {
		TextView public_;
		/** 和谁的聊天记录 */
		TextView name;

        TextView tv_union_type;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		CubeImageView avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 整个list中每一行总布局 */
		RelativeLayout list_item_layout;

	}

	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	@Override
	public Filter getFilter() {
		if (conversationFilter == null) {
			conversationFilter = new ConversationFilter(conversationList);
		}
		return conversationFilter;
	}

	private class ConversationFilter extends Filter {
		List<BenbenEMConversation> mOriginalValues = null;

		public ConversationFilter(List<BenbenEMConversation> mList) {
			mOriginalValues = mList;
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<BenbenEMConversation>();
			}
			if (prefix == null || prefix.length() == 0) {
				results.values = copyConversationList;
				results.count = copyConversationList.size();
			} else {
				String prefixString = prefix.toString();
				final int count = mOriginalValues.size();
				final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

				for (int i = 0; i < count; i++) {
					final EMConversation value = mOriginalValues.get(i)
							.getmEMConversation();
					String username = value.getUserName();

					EMGroup group = EMGroupManager.getInstance().getGroup(
							username);
					if (group != null) {
						username = group.getGroupName();
					}

					// First match against the whole ,non-splitted value
					if (username.startsWith(prefixString)) {
						newValues.add(value);
					} else {
						final String[] words = username.split(" ");
						final int wordCount = words.length;

						// Start at index 0, in case valueText starts with
						// space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(value);
								break;
							}
						}
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			conversationList.clear();
			for (BenbenEMConversation em : (List<BenbenEMConversation>) results.values) {
				conversationList.add(new BenbenEMConversation(
						BenbenEMConversation.NOMAL, em.getmEMConversation()));
			}
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}

	}
}
