package com.xunao.benben.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.FriendUnionMember;
import com.xunao.benben.bean.MemberListByArea;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityChoiceBroadCastUnionByArea extends BaseActivity implements
		OnClickListener {
	private static final int CHOCE_ADDRESS = 10000;
	private TextView friend_area;
	private TextView friend_number;
	private ListView listview;

	private CheckBox all_checkbox;
	private TextView tv_all;
	private Button btn_invite;
	private TextView tv_list;
	private TextView tv_listnum;
	private LinearLayout no_data;
	private RelativeLayout rl_choice_area;
	private String[] addressId = { "", "", "" };

	private myAdapter adapter;

	private MemberListByArea memberListByArea;
	private ArrayList<FriendUnionMember> friendUnionMembers = new ArrayList<FriendUnionMember>();
	protected ArrayList<FriendUnionMember> groupContacts = new ArrayList<FriendUnionMember>();
	private ArrayList<FriendUnionMember> selectedContacts = new ArrayList<FriendUnionMember>();

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_send_broadcast_by_area);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("按地区选", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_by_group);

		friend_area = (TextView) findViewById(R.id.friend_area);
		friend_number = (TextView) findViewById(R.id.friend_number);
		listview = (ListView) findViewById(R.id.listview);
		rl_choice_area = (RelativeLayout) findViewById(R.id.rl_choice_area);

		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_list = (TextView) findViewById(R.id.tv_list);
		tv_listnum = (TextView) findViewById(R.id.tv_listnum);
		btn_invite = (Button) findViewById(R.id.btn_invite);

		adapter = new myAdapter();
		listview.setAdapter(adapter);

		no_data = (LinearLayout) findViewById(R.id.no_data);
		no_data.setVisibility(View.VISIBLE);

		rl_choice_area.setOnClickListener(this);
		btn_invite.setOnClickListener(this);
		friend_area.setHint("点击选择地区");
		btn_invite.setText("添加");
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		selectedContacts = (ArrayList<FriendUnionMember>) getIntent().getSerializableExtra("selectContacts");
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getUnionMemberByArea(null,
                    mRequestCallBack);
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

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent();
//				intent.putExtra("groupContacts", groupContacts);
//				setResult(100000, intent);
				AnimFinsh();
			}
		});

		all_checkbox.setOnCheckedChangeListener(changeListener);
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			if (checked) {

				for (FriendUnionMember contacts : friendUnionMembers) {
					contacts.setChecked(true);
					if(!groupContacts.contains(contacts)){
						groupContacts.add(contacts);
					}

				}
			} else {

				for (FriendUnionMember contacts : friendUnionMembers) {
					contacts.setChecked(false);
					if(groupContacts.contains(contacts)){
						groupContacts.remove(contacts);
					}

				}
				tv_all.setText("全选");
			}
			tv_list.setText(getSelectName());
			tv_listnum.setText(getSelectNum());

			adapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
        friendUnionMembers.clear();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("member_list");
            if(jsonArray!=null && jsonArray.length()>0){
                no_data.setVisibility(View.GONE);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    FriendUnionMember friendUnionMember = new FriendUnionMember();
                    friendUnionMember.setBenbenId(object.optString("is_benben"));
                    friendUnionMember.setPoster(object.optString("poster"));
                    friendUnionMember.setNickName(object.optString("name"));
                    for(FriendUnionMember member:selectedContacts){
                        if(member.getBenbenId().equals(friendUnionMember.getBenbenId())){
                            friendUnionMember.setChecked(true);
                            break;
                        }
                    }
                    friendUnionMembers.add(friendUnionMember);
                }

                if(selectedContacts.size() == friendUnionMembers.size()){
                    all_checkbox.setOnCheckedChangeListener(null);
                    all_checkbox.setChecked(true);
                    all_checkbox
                            .setOnCheckedChangeListener(changeListener);
                }
            }else{
                no_data.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//		memberListByArea = new MemberListByArea();
//		try {
//			memberListByArea = memberListByArea.parseJSON(jsonObject);
//			if (memberListByArea.getContactsList() != null) {
//				contactsList = memberListByArea.getContactsList();
//
//				if (contactsList.size() > 0) {
//					no_data.setVisibility(View.GONE);
//					if(selectedContacts != null && selectedContacts.size() > 0){
//						for (Contacts contacts : contactsList) {
//							for(Contacts contacts2  : selectedContacts){
//								if(contacts.getIs_benben().equals(contacts2.getIs_benben())){
//									contacts.setChecked(true);
//									groupContacts.add(contacts);
//								}
//							}
//						}
//
//						tv_list.setText(getSelectName());
//						tv_listnum.setText(getSelectNum());
//
//						if(selectedContacts.size() == contactsList.size()){
//							all_checkbox.setOnCheckedChangeListener(null);
//							all_checkbox.setChecked(true);
//							all_checkbox
//									.setOnCheckedChangeListener(changeListener);
//						}
//
//					//	groupContacts.addAll(selectedContacts);
//					}
//				} else {
//					no_data.setVisibility(View.VISIBLE);
//				}
//
//			}
//
//		} catch (NetRequestException e) {
//			e.printStackTrace();
//		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用!");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rl_choice_area:
			startAnimActivityForResult2(ActivityChoiceAddress.class,
					CHOCE_ADDRESS, "level", "10");
			break;
		case R.id.btn_invite:
			Intent intent = new Intent();
			intent.putExtra("contacts", groupContacts);
			intent.putExtra("result", "OPEN");
			setResult(10086, intent);
			AnimFinsh();
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
		case CHOCE_ADDRESS:
			if (arg2 != null) {
				groupContacts.clear();
				String addressname = arg2.getStringExtra("address");
				addressId = null;
				addressId = arg2.getStringArrayExtra("addressId");
				friend_area.setText(addressname);

				InteNetUtils.getInstance(mContext).getUnionMemberByArea(addressId,
                        mRequestCallBack);
				tv_list.setText(getSelectName());
				tv_listnum.setText(getSelectNum());
			}
			break;
		}

		super.onActivityResult(arg0, arg1, arg2);
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return friendUnionMembers.size();
		}

		@Override
		public FriendUnionMember getItem(int arg0) {
			return friendUnionMembers.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final FriendUnionMember contact = getItem(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_friend_union_member_content, null);
			}

			final CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.item_all);
			RoundedImageView chief_poster = (RoundedImageView) convertView
					.findViewById(R.id.chief_poster);
			MyTextView chief_id = (MyTextView) convertView
					.findViewById(R.id.chief_id);
			MyTextView chief_name = (MyTextView) convertView
					.findViewById(R.id.chief_name);

			CommonUtils.startImageLoader(cubeimageLoader, contact.getPoster(),
					chief_poster);

			checkBox.setChecked(contact.isChecked());
			checkBox.setVisibility(View.VISIBLE);
			chief_id.setText("奔犇号：" + contact.getBenbenId());
			chief_name.setText(contact.getNickName());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (checkBox.isChecked()) {
						checkBox.setChecked(false);
						contact.setChecked(false);
						if (groupContacts.contains(contact)) {
							groupContacts.remove(contact);
						}

						tv_all.setText("全选");
						all_checkbox.setOnCheckedChangeListener(null);
						all_checkbox.setChecked(false);
						all_checkbox.setOnCheckedChangeListener(changeListener);
					} else {
						checkBox.setChecked(true);
						contact.setChecked(true);
						if (!groupContacts.contains(contact)) {
							groupContacts.add(contact);
						}

						if (groupContacts.size() >= friendUnionMembers.size()) {
							all_checkbox.setOnCheckedChangeListener(null);
							all_checkbox.setChecked(true);
							all_checkbox
									.setOnCheckedChangeListener(changeListener);
						}
					}
					tv_list.setText(getSelectName());
					tv_listnum.setText(getSelectNum());
				}

			});

			return convertView;
		}

	}

	private String getSelectName() {
		if (groupContacts != null) {
			StringBuffer buffer = new StringBuffer();
			for (FriendUnionMember c : groupContacts) {
				buffer.append(c.getNickName() + ",");
			}
			if (buffer.length() > 0) {
				return buffer.substring(0, buffer.length() - 1).toString();

			}
		}
		return "";
	}

	private String getSelectNum() {
		if (groupContacts != null && groupContacts.size() > 0) {
			return "等" + groupContacts.size() + "人"
					;
		}
		return "";
	}
}
