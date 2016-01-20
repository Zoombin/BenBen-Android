package com.xunao.test.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.FriendUnion;
import com.xunao.test.bean.FriendUnionMember;
import com.xunao.test.bean.FriendUnionMembers;
import com.xunao.test.bean.FriendUnionOtherChief;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.view.MyTextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityChoiceBroadUnion extends BaseActivity {
    protected static final int CHOICE_FRIEND_BY_AREA = 10002;
	private FriendUnion friendUnion;
	private FloatingGroupExpandableListView listView;
	private myAdapter adapter;
	private FriendUnionMembers friendUnionMembers;
	private ArrayList<FriendUnionOtherChief> friendUnionOtherChiefs = new ArrayList<>();
    private ArrayList<FriendUnionMember> groupContacts = new ArrayList<>();
    private ArrayList<FriendUnionMember> selectContacts = new ArrayList<FriendUnionMember>();

	private WrapperExpandableListAdapter wrapperAdapter;
    private CheckBox all_checkbox;
    private TextView tv_all;
    private Button btn_invite;
    private TextView tv_list;
    private TextView tv_listnum;
    private LinearLayout ll_select_message;
    private TextView tv_select_num;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend_union_invite_member);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("按分组选", "", "",
                R.drawable.icon_com_title_left, R.drawable.icon_by_area);
		setShowLoding(false);

		friendUnion = (FriendUnion) getIntent().getSerializableExtra(
				"friendUnion");

		listView = (FloatingGroupExpandableListView) findViewById(R.id.listView);
		listView.setGroupIndicator(null);

        all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
        all_checkbox.setVisibility(View.VISIBLE);
        tv_all = (TextView) findViewById(R.id.tv_all);
        btn_invite = (Button) findViewById(R.id.btn_invite);
        btn_invite.setText("添加");
        tv_list = (TextView) findViewById(R.id.tv_list);
        tv_listnum = (TextView) findViewById(R.id.tv_listnum);
        ll_select_message = (LinearLayout) findViewById(R.id.ll_select_message);
        tv_select_num = (TextView) findViewById(R.id.tv_select_num);
        all_checkbox.setVisibility(View.VISIBLE);
        tv_all.setVisibility(View.VISIBLE);
        ll_select_message.setVisibility(View.VISIBLE);
        tv_select_num.setVisibility(View.GONE);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
        selectContacts = (ArrayList<FriendUnionMember>) getIntent()
                .getSerializableExtra("contactsList");
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getFriendUnionMember(
					friendUnion.getId(), mRequestCallBack);
		}
	}


	@Override
	public void initLinstener(Bundle savedInstanceState) {

        all_checkbox.setOnCheckedChangeListener(changeListener);
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                startAnimActivityForResult(
                        ActivityChoiceBroadCastUnionByArea.class,
                        "selectContacts", groupContacts, CHOICE_FRIEND_BY_AREA);

			}
		});

        btn_invite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("contacts", groupContacts);
                intent.putExtra("typeWhat", "2");
                setResult(1000, intent);
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
		try {
			friendUnionOtherChiefs.clear();
			JSONObject object = jsonObject.optJSONObject("member_info");
			friendUnionMembers = new FriendUnionMembers();
			friendUnionMembers = friendUnionMembers.parseJSON1(object);
			friendUnionOtherChiefs = friendUnionMembers
					.getFriendUnionOtherChief();



			initData();
		} catch (NetRequestException e) {
			e.printStackTrace();
		}
	}

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean checked) {
            if (checked) {


                for (FriendUnionOtherChief memberGroup : friendUnionOtherChiefs) {
                    if ("盟主成员".equals(memberGroup.getName())) {
                        memberGroup.setSelect(true);
                        for (FriendUnionMember contact : friendUnionMembers.getFriendUnionMember()) {
                            if (!groupContacts.contains(contact)) {
                                contact.setChecked(true);
                                groupContacts.add(contact);
                            }
                        }
                    }else if (memberGroup.getMemberCount() > 0) {
                        memberGroup.setSelect(true);
                        for (FriendUnionMember contact : memberGroup.getMembers()) {
                            if (!groupContacts.contains(contact)) {
                                contact.setChecked(true);
                                groupContacts.add(contact);
                            }
                        }
                    }
                }

            } else {

                for (FriendUnionOtherChief memberGroup : friendUnionOtherChiefs) {
                    memberGroup.setSelect(false);
                    for (FriendUnionMember contact : memberGroup.getMembers()) {
                        contact.setChecked(false);
                        groupContacts.remove(contact);
                    }
                }

                tv_all.setText("全选");
            }
            tv_list.setText(getSelectName());
            tv_listnum.setText(getSelectNum());
            adapter.notifyDataSetChanged();
        }
    };

	private void initData() {
        groupContacts.clear();
        int checkNum=0;
        for (FriendUnionOtherChief friendUnionOtherChief : friendUnionOtherChiefs) {
            int selectNum=0;
            if(friendUnionOtherChief.getName().equals("盟主成员")){
                for (FriendUnionMember friendUnionMember : friendUnionMembers.getFriendUnionMember()) {
                    for(FriendUnionMember member:selectContacts){
                        if(member.getBenbenId().equals(friendUnionMember.getBenbenId())){
                            friendUnionMember.setChecked(true);
                            selectNum++;
                            if (!groupContacts.contains(friendUnionMember)) {
                                groupContacts.add(friendUnionMember);
                            }
                            break;
                        }
                    }
                }
                if(selectNum==friendUnionMembers.getFriendUnionMember().size()) {
                    friendUnionOtherChief.setSelect(true);
                    checkNum++;
                }
            }else{
                for (FriendUnionMember friendUnionMember : friendUnionOtherChief.getMembers()) {
                    for(FriendUnionMember member:selectContacts){
                        if(member.getBenbenId().equals(friendUnionMember.getBenbenId())){
                            friendUnionMember.setChecked(true);
                            selectNum++;
                            if (!groupContacts.contains(friendUnionMember)) {
                                groupContacts.add(friendUnionMember);
                            }
                            break;
                        }
                    }
                }
                if(selectNum==friendUnionOtherChief.getMembers().size()) {
                    friendUnionOtherChief.setSelect(true);
                    checkNum++;
                }
            }
        }
//        if(checkNum==friendUnionOtherChiefs.size()){
//            all_checkbox.setChecked(true);
//        }




		if (friendUnionOtherChiefs != null && friendUnionOtherChiefs.size() > 0) {
			int number = 0;

			adapter = new myAdapter();
			wrapperAdapter = new WrapperExpandableListAdapter(adapter);
			listView.setAdapter(wrapperAdapter);

			for (int i = 0; i < friendUnionOtherChiefs.size(); i++) {
				number += friendUnionOtherChiefs.get(i).getMemberCount();
			}

			friendUnion.setNumber(number
					+ 1
					+ Integer.parseInt(friendUnionMembers
							.getChief_member_count()));
		}
		adapter.notifyDataSetChanged();
        if(checkNum==friendUnionOtherChiefs.size()){
            all_checkbox.setChecked(true);
        }

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用!");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public FriendUnionMember getChild(int arg0, int arg1) {
			return friendUnionOtherChiefs.get(arg0).getMembers().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final FriendUnionMember unionMember = getChild(groupPosition,
					childPosition);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_friend_union_member_content, null);
			}

            final CheckBox checkBox = (CheckBox) convertView
                    .findViewById(R.id.item_all);
			RoundedImageView chief_poster = (RoundedImageView) convertView
					.findViewById(R.id.chief_poster);
			MyTextView chief_name = (MyTextView) convertView
					.findViewById(R.id.chief_name);
			final MyTextView chief_type = (MyTextView) convertView
					.findViewById(R.id.chief_type);
			MyTextView chief_ids = (MyTextView) convertView
					.findViewById(R.id.chief_id);

            checkBox.setChecked(unionMember.isChecked());
			CommonUtils.startImageLoader(cubeimageLoader,
					unionMember.getPoster(), chief_poster);

			String nickName = "";
			if (unionMember.getNickName().length() >= 8) {
				String[] nickNameArr = unionMember.getNickName().split("");
				for (int i = 0; i < nickNameArr.length; i++) {
					if (i == 6) {
						nickName += nickNameArr[i] + "\n";
					} else {
						nickName += nickNameArr[i];
					}
				}
				
			} else {
				nickName = unionMember.getNickName();
			}

			String remark = "";
			if (unionMember.getRemark().length() >= 8) {
				remark = unionMember.getRemark();
				chief_type.setSingleLine(false);
				chief_type.setMaxLines(2);
			} else {
				chief_type.setSingleLine(true);
				remark = unionMember.getRemark();
			}

			chief_name.setText(nickName);
			chief_type.setText(remark + " "); 
			chief_ids.setText("奔犇号：" + unionMember.getBenbenId());

			if (TextUtils.isEmpty(unionMember.getRemark())) {
				chief_type.setVisibility(View.GONE);
			} else {
				chief_type.setVisibility(View.VISIBLE);
			}


            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        unionMember.setChecked(false);

                        groupContacts.remove(unionMember);

                        getGroup(groupPosition).setSelect(false);
                        adapter.notifyDataSetChanged();

                        tv_all.setText("全选");
                        all_checkbox.setOnCheckedChangeListener(null);
                        all_checkbox.setChecked(false);
                        all_checkbox.setOnCheckedChangeListener(changeListener);
                    } else {
                        checkBox.setChecked(true);
                        unionMember.setChecked(true);
                        if (!groupContacts.contains(unionMember)) {
                            groupContacts.add(unionMember);
                        }

                        int gsize = 0;
                        for (FriendUnionMember contacts : getGroup(groupPosition)
                                .getMembers()) {
                            if (contacts.isChecked()) {
                                gsize += 1;
                            }
                        }

                        if (gsize == getChildrenCount(groupPosition)) {
                            getGroup(groupPosition).setSelect(true);
                            adapter.notifyDataSetChanged();
                        }

                        int size = 0;
                        for (int i = 0; i < getGroupCount(); i++) {
                            size += friendUnionOtherChiefs.get(i).getMembers().size();
                        }

                        if (groupContacts.size() >= size) {
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

		@Override
		public int getChildrenCount(int arg0) {
			return friendUnionOtherChiefs.get(arg0).getMembers().size();
		}

		@Override
		public FriendUnionOtherChief getGroup(int arg0) {
			return friendUnionOtherChiefs.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return friendUnionOtherChiefs.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final FriendUnionOtherChief otherChief = getGroup(groupPosition);
			final HeaderViewHolder holder;

			if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = View.inflate(mContext,
                        R.layout.activity_friend_union_member_group_head, null);
                holder.item_group_name = (TextView) convertView
                        .findViewById(R.id.item_group_name);
                holder.status_img = (ImageView) convertView
                        .findViewById(R.id.status_img);
                holder.item_all = (CheckBox) convertView
                        .findViewById(R.id.item_all);
                convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

            holder.item_all.setVisibility(View.VISIBLE);

            if (getGroup(groupPosition).getMembers().size() <= 0) {
                holder.item_all.setChecked(false);
            } else {
                holder.item_all.setChecked(getGroup(groupPosition).isSelect());
            }



			if ("盟主成员".equals(otherChief.getName())) {
                holder.item_group_name.setText(getGroup(groupPosition).getName()
                        + "(" + friendUnionMembers.getChief_member_count()
                        + "人)");
                holder.item_all.setClickable(Integer.parseInt(friendUnionMembers.getChief_member_count())> 0);
			} else {
                holder.item_group_name.setText(getGroup(groupPosition).getName()
                        + "(" + getGroup(groupPosition).getMemberCount()
                        + "人)");
                holder.item_all.setClickable(getGroup(groupPosition).getMemberCount() > 0);
			}


            holder.item_all.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (getGroup(groupPosition).isSelect()) {
                        for (FriendUnionMember contacts : getGroup(groupPosition).getMembers()) {
                            contacts.setChecked(false);
                            groupContacts.remove(contacts);
                        }

                        getGroup(groupPosition).setSelect(false);
                        adapter.notifyDataSetChanged();

                        all_checkbox.setOnCheckedChangeListener(null);
                        tv_all.setText("全选");
                        all_checkbox.setChecked(false);
                        all_checkbox.setOnCheckedChangeListener(changeListener);

                    } else {
                        for (FriendUnionMember contacts : getGroup(groupPosition).getMembers()) {
                            contacts.setChecked(true);
                            if (!groupContacts.contains(contacts)) {
                                groupContacts.add(contacts);
                            }
                        }

                        getGroup(groupPosition).setSelect(true);
                        adapter.notifyDataSetChanged();

                        int size = 0;
                        for (int i = 0; i < getGroupCount(); i++) {
                            size += friendUnionOtherChiefs.get(i).getMembers().size();
                        }

                        if (groupContacts.size() >= size) {
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
            return "等" + groupContacts.size() + "人";
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOICE_FRIEND_BY_AREA:
                if (data != null) {
                    String result = data.getStringExtra("result");
                    if (result != null) {
                        ArrayList<FriendUnionMember> contactsList = new ArrayList<FriendUnionMember>();
                        contactsList = (ArrayList<FriendUnionMember>) data
                                .getSerializableExtra("contacts");
                        Intent intent = new Intent();
                        intent.putExtra("contacts", contactsList);
                        setResult(1000, intent);
                        AnimFinsh();


                    }
                    break;
                }
        }
    }
}
