package com.xunao.test.ui.item;

import in.srain.cube.image.CubeImageView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.Contacts;
import com.xunao.test.bean.ContactsGroup;
import com.xunao.test.bean.MyFriendToUnion;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

public class ActivityInviteMyFriendToUnion extends BaseActivity implements
		OnClickListener {
	private ListView listView;
	private ArrayList<Contacts> mContacts;
	private ArrayList<MyFriendToUnion> myFriendToUnions = new ArrayList<>();
	private MyFriendToUnion friendToUnion;
	private MyAdapter adapter;
	private ArrayList<MyFriendToUnion> myFriendToUnions2 = new ArrayList<>();
	private EditText search_edittext;
	private LinearLayout ll_seach_icon;
	private ImageView iv_search_content_delect;
	private TextView tv_invite_content;
	private Button btn_invite;
	private String benbenId;
	private String unionId;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_invite_friend_to_union);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("邀请新成员", "", "",
				R.drawable.icon_com_title_left, 0);

		listView = (ListView) findViewById(R.id.listview);
		search_edittext = (EditText) findViewById(R.id.search_edittext);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		tv_invite_content = (TextView) findViewById(R.id.tv_invite_content);
		btn_invite = (Button) findViewById(R.id.btn_invite);
		initListview();
		btn_invite.setOnClickListener(this);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		unionId = intent.getStringExtra("unionId");

	}

	private void initListview() {
		List<Contacts> contacts = null;
		try {
			contacts = dbUtil.findAll(Selector.from(Contacts.class).where(
					WhereBuilder.b("is_benben", "!=", "0")));
		} catch (DbException e) {
			e.printStackTrace();
			ToastUtils.Infotoast(mContext, "数据错误,请重试!");
		}

		if (contacts == null) {
			ToastUtils.Infotoast(mContext, "无法获得好友信息!");
			AnimFinsh();
			return;
		}

		mContacts = (ArrayList<Contacts>) contacts;

		for (Contacts contact : mContacts) {
			friendToUnion = new MyFriendToUnion();
			friendToUnion.setGroupId(contact.getGroup_id());
			friendToUnion.setGroupName(friendToUnion.getGroupName());
			friendToUnion.setName(contact.getName());
			friendToUnion.setPoster(contact.getPoster());
			friendToUnion.setId(contact.getIs_benben());

			try {
				ContactsGroup contactsGroup = dbUtil.findById(
						ContactsGroup.class, contact.getGroup_id());
				friendToUnion.setGroupName(contactsGroup.getName());
				friendToUnion.setGroup(true);
			} catch (DbException e) {
				e.printStackTrace();
			}

			myFriendToUnions.add(friendToUnion);
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

		search_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getApplicationContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					// 显示键盘
					imm.showSoftInput(search_edittext, 0);
				}
			}
		});

		search_edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (arg0.length() > 0) {
					ll_seach_icon.setVisibility(View.GONE);
					iv_search_content_delect.setVisibility(View.VISIBLE);
				} else {
					ll_seach_icon.setVisibility(View.VISIBLE);
					iv_search_content_delect.setVisibility(View.GONE);

				}
			}
		});

		search_edittext.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 先隐藏键盘
					((InputMethodManager) search_edittext.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(mContext.getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					// 更新关键字
					String searchKey = search_edittext.getText().toString()
							.trim();

					List<Contacts> contacts = null;
					try {
						contacts = dbUtil.findAll(Selector
								.from(Contacts.class)
								.where(WhereBuilder.b("is_benben", "!=", "0"))
								.and(WhereBuilder.b("name", "like", "%"
										+ searchKey + "%")));
					} catch (DbException e) {
						e.printStackTrace();
						ToastUtils.Infotoast(mContext, "数据错误,请重试!");
					}

					if (contacts != null) {
						mContacts.clear();
						myFriendToUnions.clear();
						myFriendToUnions2.clear();
						mContacts = (ArrayList<Contacts>) contacts;

						for (Contacts contact : mContacts) {
							friendToUnion = new MyFriendToUnion();
							friendToUnion.setGroupId(contact.getGroup_id());
							friendToUnion.setGroupName(friendToUnion
									.getGroupName());
							friendToUnion.setName(contact.getName());
							friendToUnion.setPoster(contact.getPoster());
							friendToUnion.setId(contact.getIs_benben());

							try {
								ContactsGroup contactsGroup = dbUtil.findById(
										ContactsGroup.class,
										contact.getGroup_id());
								friendToUnion.setGroupName(contactsGroup
										.getName());
								friendToUnion.setGroup(true);
							} catch (DbException e) {
								e.printStackTrace();
							}

							myFriendToUnions.add(friendToUnion);
						}
					}
					adapter.notifyDataSetInvalidated();
					return true;
				}
				return false;
			}
		});

		iv_search_content_delect.setOnClickListener(this);
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

		if (ret_num.equals("0")) {
			ToastUtils.Infotoast(mContext, "邀请好友加入联盟成功!");
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, "邀请好友加入联盟失败!");
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return myFriendToUnions.size();
		}

		@Override
		public Object getItem(int arg0) {
			return myFriendToUnions.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final MyFriendToUnion friendToUnion = myFriendToUnions
					.get(position);
			final ItemHolder itemHolder;

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_my_friend_to_union_item, null);
				itemHolder = new ItemHolder();
				itemHolder.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				itemHolder.item_phone_checkbox = (CheckBox) convertView
						.findViewById(R.id.item_phone_checkbox);
				itemHolder.item_phone_poster = (RoundedImageView) convertView
						.findViewById(R.id.item_phone_poster);
				itemHolder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				itemHolder.item_phone_phone = (TextView) convertView
						.findViewById(R.id.item_phone_phone);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}

			itemHolder.item_phone_checkbox.setVisibility(View.VISIBLE);
			itemHolder.item_phone_name.setText(friendToUnion.getName());
			String poster = friendToUnion.getPoster();
			if (!TextUtils.isEmpty(poster)) {
				CommonUtils.startImageLoader(cubeimageLoader, poster,
						itemHolder.item_phone_poster);
			} else {
				CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
						itemHolder.item_phone_poster);
			}

			itemHolder.item_pinyin.setText(friendToUnion.getGroupName());

			if (position >= 1) {
				if (friendToUnion.getGroupName().equals(
						myFriendToUnions.get(position - 1).getGroupName())) {
					itemHolder.item_pinyin.setVisibility(View.GONE);
				} else {
					itemHolder.item_pinyin.setVisibility(View.VISIBLE);
				}
			} else {
				itemHolder.item_pinyin.setVisibility(View.VISIBLE);
			}

			itemHolder.item_phone_checkbox.setChecked(false);

			// item的点击事件
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (itemHolder.item_phone_checkbox.isChecked()) {
						itemHolder.item_phone_checkbox.setChecked(false);
						myFriendToUnions2.remove(friendToUnion);
						String str = "";
						if (myFriendToUnions2.size() >= 1) {
							benbenId = "";
							for (MyFriendToUnion union : myFriendToUnions2) {
								str += union.getName() + "，";
								benbenId += union.getId() + ",";
							}

							tv_invite_content.setText(str.substring(0,
									str.length() - 1)
									+ "等");
						}
					} else {
						itemHolder.item_phone_checkbox.setChecked(true);
						myFriendToUnions2.add(friendToUnion);
						if (myFriendToUnions2.size() <= 3) {
							String str = "";
							benbenId = "";
							for (MyFriendToUnion union : myFriendToUnions2) {
								str += union.getName() + "，";
								benbenId += union.getId() + ",";
							}
							tv_invite_content.setText(str.substring(0,
									str.length() - 1)
									+ "等");
						}
					}
				}
			});
			return convertView;
		}

	}

	class ItemHolder {
		TextView item_pinyin;
		CheckBox item_phone_checkbox;
		CubeImageView item_phone_poster;
		TextView item_phone_name;
		TextView item_phone_phone;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_search_content_delect:
			iv_search_content_delect.setVisibility(View.GONE);
			ll_seach_icon.setVisibility(View.VISIBLE);
			search_edittext.setText("");
			// 影藏键盘
			((InputMethodManager) search_edittext.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(mContext.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			mContacts.clear();
			myFriendToUnions.clear();
			myFriendToUnions2.clear();

			initListview();
			adapter.notifyDataSetChanged();
			break;
		case R.id.btn_invite:
			if (TextUtils.isEmpty(benbenId)) {
				ToastUtils.Infotoast(mContext, "请选择好友!");
			} else {
				if (CommonUtils.isNetworkAvailable(mContext))
					InteNetUtils.getInstance(mContext).inviteFriendToUnion(
							unionId, benbenId, mRequestCallBack);
			}
		}
	}
}
