package com.xunao.benben.hx.chatuidemo.activity;

import in.srain.cube.image.CubeImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.xunao.benben.R;
import com.xunao.benben.activity.MainActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.BuyNews;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.bean.tx.BenbenEMConversation;
import com.xunao.benben.hx.chatuidemo.Constant;
import com.xunao.benben.hx.chatuidemo.adapter.ChatAllHistoryAdapter;
import com.xunao.benben.hx.chatuidemo.db.InviteMessgeDao;
import com.xunao.benben.ui.item.ActivityBuyNews;
import com.xunao.benben.ui.item.NewFriendsMsgActivity;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 * 
 */
@SuppressLint("InflateParams")
public class ChatAllHistoryFragment extends Fragment {

	private InputMethodManager inputMethodManager;
	private SwipeMenuListView listView;
	private ChatAllHistoryAdapter adapter;
	private EditText query;
	private ImageButton clearSearch;
	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<BenbenEMConversation> conversationList = new ArrayList<BenbenEMConversation>();
	private SwipeMenuCreator creator;
	private View heardView;
    private View buyView;
	private TextView public_;
	private CubeImageView avatar,avatar_buy;
	private TextView unread_msg_number,unread_msg_number_buy;
	private View nodota;

    private boolean headApply = true;
    private boolean headBuy = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_conversation_history,
				container, false);
	}

	private void initSwipeMenu() {
		creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color
						.parseColor("#ec5d57")));
				// set item width
				deleteItem.setWidth(PixelUtil.dp2px(70));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		listView.setMenuCreator(creator);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false))
			return;
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

		nodota = getView().findViewById(R.id.nodota);
		conversationList.addAll(loadConversationsWithRecentChat());

		initHeardView();

		listView = (SwipeMenuListView) getView().findViewById(R.id.list);
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList,
				((MainActivity) getActivity()).getCubeimageLoader());

		listView.addHeaderView(heardView);
        listView.addHeaderView(buyView);
		// 设置adapter
		listView.setAdapter(adapter);

		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					BenbenEMConversation item2 = adapter.getItem(position);
					EMConversation tobeDeleteCons = item2.getmEMConversation();
					// 删除此会话
					EMChatManager.getInstance().deleteConversation(
							tobeDeleteCons.getUserName(),
							tobeDeleteCons.isGroup());
					InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(
							getActivity());
					inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
					
//					adapter.remove(item2);
//					adapter.notifyDataSetChanged();
					
					conversationList.clear();
					conversationList.addAll(loadConversationsWithRecentChat());
					adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList,
							((MainActivity) getActivity()).getCubeimageLoader());
					listView.setAdapter(adapter);
					if (adapter != null)
						adapter.notifyDataSetChanged();
					
					// 更新消息未读数
					((MainActivity) getActivity()).updateUnreadLabel();

                    avatar.setImageResource(R.drawable.ic_notofication);
                    avatar_buy.setImageResource(R.drawable.ic_public);

					break;
				}
				return false;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ArrayList<PublicMessage> mPublicMessage = CrashApplication
						.getInstance().mPublicMessage;
                ArrayList<BuyNews> buyNewses = CrashApplication
                        .getInstance().buyNewses;
				int newPosition = 0;
//                if(mPublicMessage.size() > 0 && buyNewses.size()>0){
                    if (position == 0) {
                        // 进入申请与通知界面
                        ((MainActivity) getActivity())
                                .startAnimActivity(NewFriendsMsgActivity.class);
                        return;
                    }else if(position == 1){
                        // 进入申请与通知界面
                        ((MainActivity) getActivity())
                                .startAnimActivity(ActivityBuyNews.class);
                        return;
                    }
                    newPosition = position - 2;
//                }else if(mPublicMessage.size() > 0) {
//					if (position == 0) {
//						// 进入申请与通知界面
//						((MainActivity) getActivity())
//								.startAnimActivity(NewFriendsMsgActivity.class);
//						return;
//					}
//					newPosition = position - 1;
//				}else if(buyNewses.size() > 0) {
//                    if (position == 0) {
//                        // 进入申请与通知界面
//                        ((MainActivity) getActivity())
//                                .startAnimActivity(ActivityBuyNews.class);
//                        return;
//                    }
//                    newPosition = position - 1;
//                } else {
//					newPosition = position;
//				}
				BenbenEMConversation item = adapter.getItem(newPosition);
				EMConversation conversation = item.getmEMConversation();
				String username = conversation.getUserName();
				// 进入聊天页面
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				if (conversation.isGroup()) {
					// it is group chat

					// if(conversation.getType() ==
					// EMConversationType.ChatRoom){
					// // it is group chat
					// intent.putExtra("chatType",
					// ChatActivity.CHATTYPE_CHATROOM);
					// intent.putExtra("groupId", username);
					// }else{
					// it is group chat
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId", username);
					// }
					//
					// TalkGroup talkGroup =
					// CrashApplication.getInstance().mTalkGroupMap
					// .get(username);
					// if (talkGroup != null) {
					// intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					// intent.putExtra("tG", talkGroup);
					// startActivity(intent);
					// } else {
					// ToastUtils.Errortoast(
					// ChatAllHistoryFragment.this.getActivity(),
					// "改群组已不存在");
					// EMConversation tobeDeleteCons = item
					// .getmEMConversation();
					// // 删除此会话
					// EMChatManager.getInstance().deleteConversation(
					// tobeDeleteCons.getUserName(),
					// tobeDeleteCons.isGroup());
					// InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(
					// getActivity());
					// inviteMessgeDao.deleteMessage(tobeDeleteCons
					// .getUserName());
					// adapter.remove(item);
					// adapter.notifyDataSetChanged();
					//
					// // 更新消息未读数
					// ((MainActivity) getActivity()).updateUnreadLabel();
					// }

				} else {
					// it is single chat
					intent.putExtra("userId", username);
				}
				startActivity(intent);
			}

		});

		initSwipeMenu();

		// // 注册上下文菜单
		// registerForContextMenu(listView);

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				hideSoftKeyboard();
				return false;
			}

		});
		// 搜索框
		query = (EditText) getView().findViewById(R.id.query);
		String strSearch = getResources().getString(R.string.search);
		query.setHint(strSearch);
		// 搜索框中清除button
		clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else {
					clearSearch.setVisibility(View.INVISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				query.getText().clear();
				hideSoftKeyboard();
			}
		});

	}

	void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		// getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {

			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {

		BenbenEMConversation bme = CrashApplication.getInstance().getBme();
		ArrayList<PublicMessage> mPublicMessage = CrashApplication
				.getInstance().mPublicMessage;
		if (mPublicMessage.size() > 0) {
            if(!headApply){
                headApply=true;
//                listView.addHeaderView(heardView);
//                heardView.setVisibility(View.VISIBLE);
            }
//			if (listView.getHeaderViewsCount() <= 0) {
//				listView.addHeaderView(heardView);
//			}
			if (bme.getPublicNum() > 0) {
				unread_msg_number.setVisibility(View.VISIBLE);
				unread_msg_number.setText(bme.getPublicNum() + "");
			} else {
				unread_msg_number.setVisibility(View.GONE);
			}
		} else {
            headApply = false;
//            heardView.setVisibility(View.GONE);
//			listView.removeHeaderView(heardView);
		}


        ArrayList<BuyNews> buyNewses = CrashApplication
                .getInstance().buyNewses;
        if (buyNewses.size() > 0) {
            if(!headBuy){
                headBuy=true;
//                buyView.setVisibility(View.VISIBLE);
//                listView.addHeaderView(buyView);
            }
//			if (listView.getHeaderViewsCount() <= 0) {
//				listView.addHeaderView(heardView);
//			}
            if (bme.getBuyNum() > 0) {
                unread_msg_number_buy.setVisibility(View.VISIBLE);
                unread_msg_number_buy.setText(bme.getBuyNum() + "");
            } else {
                unread_msg_number_buy.setVisibility(View.GONE);
            }
        } else {
            headBuy = false;
//            buyView.setVisibility(View.GONE);

//            listView.removeHeaderView(buyView);
        }

		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList,
				((MainActivity) getActivity()).getCubeimageLoader());
		listView.setAdapter(adapter);
		if (adapter != null)
			adapter.notifyDataSetChanged();

        avatar.setImageResource(R.drawable.ic_notofication);
        avatar_buy.setImageResource(R.drawable.ic_public);

	}

	private CrashApplication mCrashApplication;

	public void initHeardView() {
		heardView = LayoutInflater.from(getActivity()).inflate(
				R.layout.row_chat_history_heard, null);
		public_ = (TextView) heardView.findViewById(R.id.public_);
		avatar = (CubeImageView) heardView.findViewById(R.id.avatar);
		unread_msg_number = (TextView) heardView
				.findViewById(R.id.unread_msg_number);
		avatar.setImageResource(R.drawable.ic_notofication);
		avatar.setVisibility(View.VISIBLE);
		public_.setVisibility(View.VISIBLE);

        buyView = LayoutInflater.from(getActivity()).inflate(
                R.layout.row_chat_history_buy, null);
        avatar_buy = (CubeImageView) buyView.findViewById(R.id.avatar_buy);
        avatar_buy.setImageResource(R.drawable.ic_public);
        unread_msg_number_buy = (TextView) buyView
                .findViewById(R.id.unread_msg_number_buy);
	}

	/**
	 * 获取所有会话
	 *
	 * @return +
	 */
	private List<BenbenEMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		List<BenbenEMConversation> list = new ArrayList<BenbenEMConversation>();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0) {
				BenbenEMConversation benbenEMConversation = new BenbenEMConversation(
						BenbenEMConversation.NOMAL, conversation);
				list.add(benbenEMConversation);
			}
		}
        mCrashApplication = CrashApplication.getInstance();
		List<BenbenEMConversation> emConversations = new ArrayList<BenbenEMConversation>();
//		emConversations.addAll(list);
				
		for (BenbenEMConversation emConversation : list) {
			if (emConversation.getType() == BenbenEMConversation.NOMAL) {
				EMConversation conversation = emConversation
						.getmEMConversation();
				// 获取用户username或者群组groupid
				final String username = conversation.getUserName();
				EMContact contact = null;
				boolean isGroup = false;
				if (conversation.isGroup()) {
					isGroup = true;
				}

				if (isGroup) {
					TalkGroup contactsGroup = mCrashApplication.mTalkGroupMap
							.get(username);
					if (contactsGroup != null
							&& contactsGroup.getStatus().equals("1")) {
						if(list.contains(emConversation)){
							emConversations.add(emConversation);
						}
					}
				}
			}
		}
		list.removeAll(emConversations);
		// 排序
		sortConversationByLastChatTime(list);
		if ((list == null || list.size() == 0)
				&& CrashApplication.getInstance().getBme().getPublicNum() <= 0
				&& CrashApplication.getInstance().mPublicMessage.size() <= 0
                && CrashApplication.getInstance().buyNewses.size() <= 0) {
			nodota.setVisibility(View.GONE);
		} else {
			nodota.setVisibility(View.GONE);
		}

		return list;
	}

	/**
	 * 根据最后一条消息的时间排序

	 */
	private void sortConversationByLastChatTime(
			List<BenbenEMConversation> conversationList) {
		Collections.sort(conversationList,
				new Comparator<BenbenEMConversation>() {
					@Override
					public int compare(final BenbenEMConversation con1,
							final BenbenEMConversation con2) {

						EMMessage con2LastMessage = con2.getmEMConversation()
								.getLastMessage();
						EMMessage con1LastMessage = con1.getmEMConversation()
								.getLastMessage();
						if (con2LastMessage.getMsgTime() == con1LastMessage
								.getMsgTime()) {
							return 0;
						} else if (con2LastMessage.getMsgTime() > con1LastMessage
								.getMsgTime()) {
							return 1;
						} else {
							return -1;
						}
					}

				});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden && !((MainActivity) getActivity()).isConflict) {
			refresh();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
		}
	}

}
