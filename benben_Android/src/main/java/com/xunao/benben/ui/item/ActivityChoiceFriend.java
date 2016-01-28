package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.BxContacts;
import com.xunao.benben.bean.BxContactsAll;
import com.xunao.benben.bean.BxContactsList;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.GroupAddMember;
import com.xunao.benben.bean.GroupAddMembers;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityChoiceFriend extends BaseActivity implements
		OnClickListener {

	private static final int INVITE_FRIEND_TO_BX = 10001;

	private ImageView com_title_bar_left_bt;

	private MyTextView com_title_bar_left_tv;

	private ImageView com_title_bar_right_bt;

	private MyTextView com_title_bar_right_tv;

	private TextView tab_left;
	private TextView tab_right;

	private FloatingGroupExpandableListView all_listview;
	private WrapperExpandableListAdapter wrapperAdapter;
	private ListView group_listview;

	// 选中的组
	private ContactsGroup contactsGroup;

	private ContactsObject contactsObject;

	private ArrayList<BxContacts> groupListContacts;

	private ArrayList<BxContactsAll> bxArrayList = new ArrayList<BxContactsAll>();

	private ArrayList<PhoneInfo> phoneInfos;

	private BxContactsList bxContactsList;
	private CheckBox all_checkbox;
	private TextView tv_all;
	// 未分组的id
	private String noGroupId = "";

	// 两个适配器
	private myAdapter allAdapter;
	private SelectAdapter groupAdapter;

	// 新建一个map来保存移动之前的groupid 防止别的分组移进去再移出去变成未分组
	private HashMap<String, String> beforMap = new HashMap<String, String>();
    private LinearLayout ll_search_item;
    private EditText search_edittext;
    private ImageView iv_search_content_delect;
    private LinearLayout ll_seach_icon;
    private String searchKey;
    private JSONObject jsonObject = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_packet_info_management);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		tab_left = (TextView) findViewById(R.id.tab_left);
		tab_right = (TextView) findViewById(R.id.tab_right);

		all_listview = (FloatingGroupExpandableListView) findViewById(R.id.all_listview);
		all_listview.setGroupIndicator(null);
		group_listview = (ListView) findViewById(R.id.group_listview);
		all_Box = findViewById(R.id.all_Box);

		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
        ll_search_item = (LinearLayout) findViewById(R.id.ll_search_item);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
        ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		showLoding("请稍后...");
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getNotBaixing(requestCallBack);
		}

	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			dissLoding();
			ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {

			dissLoding();
			try {
				jsonObject = new JSONObject(arg0.result);
                bxContactsList = new BxContactsList();
				bxContactsList.parseJSON(jsonObject);

				if (bxContactsList != null) {
					bxArrayList.addAll(bxContactsList.getBxContacts());

					groupListContacts = new ArrayList<BxContacts>();

					groupAdapter = new SelectAdapter();

					if (wrapperAdapter == null) {
						allAdapter = new myAdapter();
						wrapperAdapter = new WrapperExpandableListAdapter(
								allAdapter);
						all_listview.setAdapter(wrapperAdapter);
					} else {
						wrapperAdapter.notifyDataSetChanged();
					}

					group_listview.setAdapter(groupAdapter);

					tab_right.setText("已选择(" + groupListContacts.size() + ")");

				}

			} catch (Exception e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, "网络不可用,请重试");
			}
		}

	};

	private View all_Box;

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		tab_left.setOnClickListener(this);
		tab_right.setOnClickListener(this);

		com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);
		com_title_bar_right_tv.setText("下一步");

		com_title_bar_left_tv.setOnClickListener(this);
		com_title_bar_left_bt.setOnClickListener(this);
		com_title_bar_right_tv.setOnClickListener(this);
		com_title_bar_right_bt.setOnClickListener(this);
		tab_left.performClick();

		all_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAllCheck) {
					isAllCheck = false;
					tv_all.setText("全选");
				} else {
					isAllCheck = true;
				}
				for (BxContactsAll c : bxArrayList) {
					if (c.getBxContactslist() != null
							&& c.getBxContactslist().size() > 0) {
						c.setChecked(isAllCheck);
						for (BxContacts con : c.getBxContactslist()) {
							con.setChecked(isAllCheck);
							if (isAllCheck) {
								if(!groupListContacts.contains(con)){
									groupListContacts.add(con);
								} 
							} else {
								if(groupListContacts.contains(con)){
									groupListContacts.remove(con);
								} 
							}
						}
					}
				}
				wrapperAdapter.notifyDataSetChanged();
				tab_right.setText("已选择(" + groupListContacts.size() + ")");
			}
		});

        search_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    searchKey = search_edittext.getText().toString().trim();
                } else {
                    ll_seach_icon.setVisibility(View.VISIBLE);
                    iv_search_content_delect.setVisibility(View.GONE);
                    searchKey = "";
                }
            }
        });

        search_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    searchKey = search_edittext.getText().toString().trim();
                    isLoadMore = false;
                    enterNum = false;
                    keySearchContacts();
                    return true;
                }
                return false;
            }
        });

        iv_search_content_delect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                search_edittext.setText("");
                searchKey = search_edittext.getText().toString().trim();
                isLoadMore = false;
                enterNum = false;
                keySearchContacts();
            }
        });

	}

    private void keySearchContacts() {
        try {
            isAllCheck = false;
            all_checkbox.setChecked(false);
            bxArrayList.clear();

            bxContactsList = new BxContactsList();
            bxContactsList.parseJSON(jsonObject);
            if (bxContactsList != null) {
                ArrayList<BxContactsAll> bxContactsAlls = bxContactsList.getBxContacts();
                for(int i=0;i<bxContactsAlls.size();i++){
                    BxContactsAll bxContactsAll = bxContactsAlls.get(i);
                    ArrayList<BxContacts> bxContactses = new ArrayList<>();
                    for(int j=0;j<bxContactsAll.getBxContactslist().size();j++){
                        BxContacts bxContacts = bxContactsAll.getBxContactslist().get(j);
                        if(bxContacts.getName().contains(searchKey) || bxContacts.getPhone().contains(searchKey) || bxContacts.getPinyin().contains(searchKey.toUpperCase())){
                            bxContactses.add(bxContacts);
                        }
                    }
                    bxContactsAll.setBxContactslist(bxContactses);
                    String name = bxContactsAll.getName();
                    name = name.substring(0,name.indexOf("(")+1)+bxContactses.size()+"人)";
                    bxContactsAll.setName(name);
                }
                bxArrayList.addAll(bxContactsList.getBxContacts());
            }

            groupListContacts = new ArrayList<BxContacts>();

            groupAdapter = new SelectAdapter();
            if (wrapperAdapter == null) {
                allAdapter = new myAdapter();
                wrapperAdapter = new WrapperExpandableListAdapter(
                        allAdapter);
                all_listview.setAdapter(wrapperAdapter);
            } else {
                wrapperAdapter.notifyDataSetChanged();
            }

//                group_listview.setAdapter(groupAdapter);
            groupAdapter.notifyDataSetChanged();

            tab_right.setText("已选择(" + groupListContacts.size() + ")");
        } catch (NetRequestException e) {
                e.printStackTrace();
        }
    }


    @Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		SuccessMsg msg = new SuccessMsg();
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_left:
			tab_left.setBackgroundResource(R.drawable.bac_white_left);
			tab_left.setTextColor(R.color.top_bar_color);

			tab_right.setBackgroundResource(R.drawable.bac_bule_right);
			tab_right.setTextColor(Color.parseColor("#ffffff"));

            ll_search_item.setVisibility(View.VISIBLE);
			all_Box.setVisibility(View.VISIBLE);
			group_listview.setVisibility(View.GONE);
			break;
		case R.id.tab_right:
            ll_search_item.setVisibility(View.GONE);
			tab_left.setBackgroundResource(R.drawable.bac_bule_left);
			tab_left.setTextColor(Color.parseColor("#ffffff"));

			tab_right.setBackgroundResource(R.drawable.bac_white_right);
			tab_right.setTextColor(R.color.top_bar_color);

			all_Box.setVisibility(View.GONE);
			group_listview.setVisibility(View.VISIBLE);
			break;

		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;
		// 右侧 完成
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			if(groupListContacts == null){
				ToastUtils.Infotoast(mContext, "请选择要邀请的好友!");
				return;
			}
			if (groupListContacts.size() > 0) {
				startAnimActivityForResult(ActivityInviteFriendToBx.class,
						"contacts", groupListContacts, INVITE_FRIEND_TO_BX);
			//	finish();
			} else {
				ToastUtils.Infotoast(mContext, "请选择要邀请的好友!");
			}
			break;
		default:
			break;
		}

	}
	
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg2 != null){
			AnimFinsh();
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	private boolean isAllCheck;

	private void setIsAllCheck(boolean allCheck) {
		if (allCheck) {
			isAllCheck = true;
			all_checkbox.setChecked(true);

		} else {
			isAllCheck = false;
			all_checkbox.setChecked(false);
			tv_all.setText("全选");
		}
	}

	class SelectAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return groupListContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return groupListContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			final BxContacts contacts = groupListContacts.get(position);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_info_item, null);
				itemHolder = new ItemHolder();
				itemHolder.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				itemHolder.item_phone_checkbox = (CheckBox) convertView
						.findViewById(R.id.item_phone_checkbox);
				itemHolder.item_phone_poster = (ImageView) convertView
						.findViewById(R.id.item_phone_poster);
				itemHolder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				itemHolder.item_phone_phone = (TextView) convertView
						.findViewById(R.id.item_phone_phone);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}

			itemHolder.item_phone_name.setText(contacts.getName());
			itemHolder.item_phone_phone.setText(contacts.getPhone());
			String pinyin = contacts.getPinyin();
			if (position == 0) {
				itemHolder.item_pinyin.setVisibility(View.VISIBLE);
				itemHolder.item_pinyin.setText(pinyin);
			} else {
				String lastPinyin = groupListContacts.get(position - 1)
						.getPinyin();
				if (pinyin.equals(lastPinyin)) {
					itemHolder.item_pinyin.setVisibility(View.GONE);
					itemHolder.item_pinyin.setText(pinyin);
				} else {
					itemHolder.item_pinyin.setVisibility(View.VISIBLE);
					itemHolder.item_pinyin.setText(pinyin);
				}

			}

			// itemHolder.item_pinyin.setText(contacts.getPinyin().charAt(0) +
			// "");
			// if (contacts.isHasPinYin()) {
			// itemHolder.item_pinyin.setVisibility(View.VISIBLE);
			// } else {
			// itemHolder.item_pinyin.setVisibility(View.GONE);
			// }

			return convertView;
		}

	}

	class ItemHolder {
		TextView item_pinyin;
		CheckBox item_phone_checkbox;
		ImageView item_phone_poster;
		TextView item_phone_name;
		TextView item_phone_phone;
	}

	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public BxContacts getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return bxArrayList.get(groupPosition).getBxContactslist()
					.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final BxContacts contact = getChild(groupPosition, childPosition);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.item_groupaddmember, null);
			}

			final CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.item_all);
			MyTextView chief_name = (MyTextView) convertView
					.findViewById(R.id.chiefNameAndPhone);
			RoundedImageView item_poster = (RoundedImageView) convertView
					.findViewById(R.id.item_poster);

			checkBox.setChecked(contact.isChecked());
			checkBox.setVisibility(View.VISIBLE);

			chief_name.setText(contact.getName() + "  " + contact.getPhone());

			CommonUtils.startImageLoader(cubeimageLoader, contact.getPoster(),
					item_poster);

			OnClickListener click = new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					BxContactsAll group = getGroup(groupPosition);

					checkBox.setChecked(!contact.isChecked());
					contact.setChecked(!contact.isChecked());

					if (!contact.isChecked()) {
						group.setChecked(false);
						groupListContacts.remove(contact);
						setIsAllCheck(false);
						myAdapter.this.notifyDataSetChanged();
					} else {
						groupListContacts.add(contact);
						boolean checkAllSelect = true;

						for (BxContacts c : group.getBxContactslist()) {
							if (!c.isChecked()) {
								checkAllSelect = false;
								break;
							}
						}
						group.setChecked(checkAllSelect);
						myAdapter.this.notifyDataSetChanged();
						if (checkAllSelect) {
							boolean allSelect = true;
							for (BxContactsAll c : bxArrayList) {
								if (!c.isChecked()) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);
						}

					}
					tab_right.setText("已选择(" + groupListContacts.size() + ")");
				}
			};

			checkBox.setOnClickListener(click);
			convertView.setOnClickListener(click);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return bxArrayList.get(groupPosition).getBxContactslist().size();
		}

		@Override
		public BxContactsAll getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return bxArrayList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return bxArrayList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
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
			final BxContactsAll group = getGroup(groupPosition);
			holder.item_all.setVisibility(View.VISIBLE);
			holder.item_all.setChecked(group.isChecked());

			if (all_listview.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			holder.item_group_name.setText(getGroup(groupPosition).getName());

			if (getGroup(groupPosition).getBxContactslist().size() > 0) {
				holder.item_all.setClickable(true);
				holder.item_all.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (group.isChecked()) {
							group.setChecked(false);
							setIsAllCheck(false);
							for (BxContacts c : group.getBxContactslist()) {
								c.setChecked(false);
								groupListContacts.remove(c);
							}
							myAdapter.this.notifyDataSetChanged();
						} else {
							group.setChecked(true);
							holder.item_all.setChecked(true);
							for (BxContacts c : group.getBxContactslist()) {
								c.setChecked(true);
								groupListContacts.add(c);
							}
							myAdapter.this.notifyDataSetChanged();
							boolean allSelect = true;
							for (BxContactsAll c : bxArrayList) {
								if (!c.isChecked()
										&& c.getBxContactslist().size() > 0) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);
						}
						tab_right.setText("已选择(" + groupListContacts.size()
								+ ")");
					}
				});
			} else {
				holder.item_all.setClickable(false);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (all_listview.isGroupExpanded(groupPosition)) {
						all_listview.collapseGroup(groupPosition);
					} else {
						all_listview.expandGroup(groupPosition);
					}
				}
			});

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	class HeaderViewHolder {
		TextView item_group_name;
		ImageView status_img;
		CheckBox item_all;
	}

	/**
	 * 序列化 找出位置
	 */
	public void getNewContacts(ArrayList<BxContacts> bxContacts) {
		// 根据拼音序列化
		// Collections.sort(bxContacts);

		// 找出有拼音出现的位置
		if (bxContacts != null && bxContacts.size() > 0) {
			// 这样写是为了第一个一定会出现
			int pinyin = -1;
			// int pinyin = contacts.get(0).getPinyin().charAt(0);
			for (int i = 0; i < bxContacts.size(); i++) {
				int j = bxContacts.get(i).getPinyin().charAt(0);
				bxContacts.get(i).setHasPinYin(false);
				if (j != pinyin) {
					pinyin = j;
					bxContacts.get(i).setHasPinYin(true);
				}
			}
		}
	}

}
