package com.xunao.test.ui.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.BxContactsList;
import com.xunao.test.bean.Contacts;
import com.xunao.test.bean.ContactsGroup;
import com.xunao.test.bean.ContactsObject;
import com.xunao.test.bean.PhoneInfo;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.PhoneUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.utils.ViewHolderUtil;
import com.xunao.test.view.MyTextView;

@SuppressLint("ResourceAsColor")
public class ActivityInviteContactsToBenBen extends BaseActivity implements
		OnClickListener {

	private ImageView com_title_bar_left_bt;

	private MyTextView com_title_bar_left_tv;

	private ImageView com_title_bar_right_bt;

	private MyTextView com_title_bar_right_tv;

	private TextView tab_left;
	private TextView tab_right;

	private FloatingGroupExpandableListView all_listview;
	private ListView group_listview;

	private ArrayList<ContactsGroup> mContactsGroups;
	private WrapperExpandableListAdapter wrapperAdapter;
	// 选中的组
	private ContactsGroup contactsGroup;

	private ContactsObject contactsObject;

	private ArrayList<Contacts> groupListContacts = new ArrayList<Contacts>();

	private ArrayList<Contacts> bxArrayList = new ArrayList<Contacts>();
	private ArrayList<Contacts> contList = new ArrayList<Contacts>();

	private BxContactsList bxContactsList;
	// 未分组的id
	private int noGroupId;
	private CheckBox all_checkbox;
	private TextView tv_all;

	// 两个适配器
	private MyAdapter allAdapter;
	private MyAdapter groupAdapter;

	// 新建一个map来保存移动之前的groupid 防止别的分组移进去再移出去变成未分组
	private HashMap<String, String> beforMap = new HashMap<String, String>();
    private LinearLayout ll_search_item;
    private EditText search_edittext;
    private ImageView iv_search_content_delect;
    private LinearLayout ll_seach_icon;
    private String searchKey="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_invitecomtacts);
	}

	private View all_Box;

	@Override
	public void initView(Bundle savedInstanceState) {
		tab_left = (TextView) findViewById(R.id.tab_left);
		tab_right = (TextView) findViewById(R.id.tab_right);
		all_Box = findViewById(R.id.all_Box);
		all_listview = (FloatingGroupExpandableListView) findViewById(R.id.all_listview);
		group_listview = (ListView) findViewById(R.id.group_listview);
		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
		all_listview.setGroupIndicator(null);
        ll_search_item = (LinearLayout) findViewById(R.id.ll_search_item);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
        ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		initLocalData();
	}

	private void initLocalData() {
		// try {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					List<ContactsGroup> findAll2 = dbUtil
							.findAll(Selector.from(ContactsGroup.class).where(
									"id", "!=", 10000));

					mContactsGroups = (ArrayList<ContactsGroup>) findAll2;
					ArrayList<Contacts> getmContacts = null;
					ArrayList<Contacts> com = new ArrayList<Contacts>();
					ArrayList<Contacts> benben = new ArrayList<Contacts>();
					for (ContactsGroup cg : mContactsGroups) {

						for (ContactsGroup contactsGroup : mContactsGroups) {
							if (("未分组").equals(contactsGroup.getName())) {
								noGroupId = contactsGroup.getId();
							}
						}

						getmContacts = cg.getmContacts();
						getmContacts.clear();
						List<Contacts> findAll = dbUtil.findAll(Selector
								.from(Contacts.class)
								.where("group_id", "=", cg.getId())
								.orderBy("pinyin", false));
						if (findAll != null) {
							getmContacts.clear();
							for (Contacts contacts : findAll) {

								List<PhoneInfo> findAll3 = dbUtil
										.findAll(Selector
												.from(PhoneInfo.class)
												.where("contacts_id", "=",
														contacts.getId())
												.and("is_benben", "=", "0"));

								for (PhoneInfo phoneInfo : findAll3) {
									// phone += phoneInfo.getPhone() + ",";
									Contacts temp = new Contacts();
									temp.setChecked(contacts.isChecked());
									temp.setGroup_id(contacts.getGroup_id());
									temp.setHasPinYin(contacts.isHasPinYin());
									temp.setPinyin(contacts.getPinyin());
									temp.setName(contacts.getName());
									temp.setPoster(contacts.getPoster());
									temp.setPhone(phoneInfo.getPhone());
									temp.setId(phoneInfo.getId());
                                    if(searchKey.equals("") || temp.getName().contains(searchKey) || temp.getPinyin().contains(searchKey.toUpperCase()) || temp.getPhone().contains(searchKey)) {
                                        getmContacts.add(temp);
                                    }
								}
							}
						}
						cg.setProportion(getmContacts.size() + "");
					}
					ContactsObject contactsObject = new ContactsObject();
					contactsObject.setmContactsGroups(mContactsGroups);

					groupOrderBy();

					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									mContext.dissLoding();
								}
							});
                            isAllCheck = false;
                            all_checkbox.setChecked(false);
                            groupListContacts.clear();
                            tab_right.setText("已选择(" + groupListContacts.size()
                                    + ")");

							if (wrapperAdapter == null) {
								AllAdapter adapter = new AllAdapter();
								wrapperAdapter = new WrapperExpandableListAdapter(
										adapter);
								all_listview.setAdapter(wrapperAdapter);
							} else {
								wrapperAdapter.notifyDataSetChanged();
							}

							groupAdapter = new MyAdapter(false,
									groupListContacts);
							group_listview.setAdapter(groupAdapter);

							// selectAdapter = new SelectAdapter();
							// selectListview.setAdapter(selectAdapter);
						}
					});

				} catch (NumberFormatException e) {
					e.printStackTrace();
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.dissLoding();
						}
					});
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.dissLoding();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.dissLoding();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		tab_left.setOnClickListener(this);
		tab_right.setOnClickListener(this);

		com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);

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
				for (ContactsGroup c : mContactsGroups) {
					if (c.getmContacts() != null && c.getmContacts().size() > 0) {
						c.setSelect(isAllCheck);
						for (Contacts con : c.getmContacts()) {

							if (isAllCheck) {
								if (!con.isChecked())
									groupListContacts.add(con);
							} else {
								groupListContacts.remove(con);
							}

							con.setChecked(isAllCheck);
						}
					}
				}
				tab_right.setText("已选择(" + groupListContacts.size() + ")");
				wrapperAdapter.notifyDataSetChanged();
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
                    initLocalData();
//                    if (CommonUtils.isNetworkAvailable(mContext))
//                        InteNetUtils.getInstance(mContext).buySearchFriend("",
//                                searchKey, mRequestCallBack);
                    return true;
                }
                return false;
            }
        });

        iv_search_content_delect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                search_edittext.setText("");
            }
        });
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
		SuccessMsg msg = new SuccessMsg();
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
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
			tab_left.setBackgroundResource(R.drawable.bac_bule_left);
			tab_left.setTextColor(Color.parseColor("#ffffff"));

			tab_right.setBackgroundResource(R.drawable.bac_white_right);
			tab_right.setTextColor(R.color.top_bar_color);

            ll_search_item.setVisibility(View.GONE);
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
			if (groupListContacts.size() <= 0) {
				ToastUtils.Infotoast(mContext, "请选择要邀请的好友!");
				return;
			} else {
				String phone = "";
				for (Contacts contacts : groupListContacts) {
					// List phoneList;
					// try {
					// phoneList = dbUtil.findAll(Selector.from(
					// PhoneInfo.class).where(
					// WhereBuilder.b("contacts_id", "=",
					// contacts.getId())));
					// ArrayList<PhoneInfo> phoneInfos = new ArrayList<>();
					// phoneInfos = (ArrayList<PhoneInfo>) phoneList;
					// for (PhoneInfo phoneInfo : phoneInfos) {
					phone += contacts.getPhone() + ",";
					// }
					// } catch (DbException e) {
					// e.printStackTrace();
					// }
				}
				PhoneUtils.sendSMSAll(phone.substring(0, phone.length() - 1),
						AndroidConfig.smsContext, mContext);
			}
			break;
		default:
			break;
		}

	}

	class AllAdapter extends BaseExpandableListAdapter {

		@Override
		public Contacts getChild(int arg0, int arg1) {
			return mContactsGroups.get(arg0).getmContacts().get(arg1);
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
						R.layout.item_addfriend_item, null);
			}

			final CheckBox checkBox = (CheckBox) ViewHolderUtil.get(
					convertView, R.id.item_all);
			MyTextView chief_name = (MyTextView) ViewHolderUtil.get(
					convertView, R.id.item_name);
			RoundedImageView item_poster = (RoundedImageView) ViewHolderUtil
					.get(convertView, R.id.item_poster);

			checkBox.setChecked(contact.isChecked());
			checkBox.setVisibility(View.VISIBLE);
			
			item_poster.setVisibility(View.GONE);

			CommonUtils.startImageLoader(cubeimageLoader, contact.getPoster(),
					item_poster);

			chief_name.setText(contact.getName() + "  " + contact.getPhone());

			OnClickListener click = new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					ContactsGroup group = getGroup(groupPosition);

					checkBox.setChecked(!contact.isChecked());
					contact.setChecked(!contact.isChecked());

					if (!contact.isChecked()) {
						group.setSelect(false);
						groupListContacts.remove(contact);
						setIsAllCheck(false);
						tab_right.setText("已选择(" + groupListContacts.size()
								+ ")");
						AllAdapter.this.notifyDataSetChanged();
					} else {
						groupListContacts.add(contact);
						boolean checkAllSelect = true;

						for (Contacts c : group.getmContacts()) {
							if (!c.isChecked()) {
								checkAllSelect = false;
								break;
							}
						}
						group.setSelect(checkAllSelect);
						AllAdapter.this.notifyDataSetChanged();
						tab_right.setText("已选择(" + groupListContacts.size()
								+ ")");
						if (checkAllSelect) {
							boolean allSelect = true;
							for (ContactsGroup c : mContactsGroups) {
								if (!c.isSelect()&&c.getmContacts().size()>0) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);
						}

					}

				}
			};

			convertView.setOnClickListener(click);
			checkBox.setOnClickListener(click);

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return mContactsGroups.get(arg0).getmContacts().size();
		}

		@Override
		public ContactsGroup getGroup(int arg0) {
			return mContactsGroups.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return mContactsGroups.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
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
			final ContactsGroup group = getGroup(groupPosition);
			holder.item_all.setVisibility(View.VISIBLE);
			holder.item_all.setChecked(group.isSelect());

			if (all_listview.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			holder.item_group_name.setText(getGroup(groupPosition).getName()
					+ "  (" + group.getmContacts().size() + ")");

			if (group.getmContacts().size() > 0) {
				holder.item_all.setClickable(true);
				holder.item_all.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (group.isSelect()) {
							group.setSelect(false);
							setIsAllCheck(false);
							for (Contacts c : group.getmContacts()) {
								c.setChecked(false);
								groupListContacts.remove(c);
							}
							tab_right.setText("已选择(" + groupListContacts.size()
									+ ")");
							AllAdapter.this.notifyDataSetChanged();
						} else {
							group.setSelect(true);
							holder.item_all.setChecked(true);
							for (Contacts c : group.getmContacts()) {
								if (!c.isChecked()) {
									c.setChecked(true);
									groupListContacts.add(c);
								}
							}
							tab_right.setText("已选择(" + groupListContacts.size()
									+ ")");
							AllAdapter.this.notifyDataSetChanged();
							boolean allSelect = true;
							for (ContactsGroup c : mContactsGroups) {
								if (!c.isSelect()
										&& c.getmContacts().size() > 0) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);
						}
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

	class MyAdapter extends BaseAdapter {
		private boolean isAll;
		private ArrayList<Contacts> adapterContacts;

		public MyAdapter(boolean isAll, ArrayList<Contacts> adapterContacts) {
			// TODO Auto-generated constructor stub
			this.isAll = isAll;
			this.adapterContacts = adapterContacts;
		}

		@Override
		public int getCount() {
			return adapterContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return adapterContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			final Contacts contacts = adapterContacts.get(position);
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

			itemHolder.item_phone_name.setText(contacts.getName() + "  "
					+ contacts.getPhone());
			// itemHolder.item_phone_name.setText(contacts.getPhone());
			itemHolder.item_pinyin.setText(contacts.getPinyin().charAt(0) + "");
			if (contacts.isHasPinYin()) {
				itemHolder.item_pinyin.setVisibility(View.VISIBLE);
			} else {
				itemHolder.item_pinyin.setVisibility(View.GONE);
			}

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

	/**
	 * 序列化 找出位置
	 */
	public void getNewContacts(ArrayList<Contacts> bxContacts) {

		if (bxContacts != null && bxContacts.size() > 0) {
			int pinyin = -1;
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

	private void groupOrderBy() {
		int size = mContactsGroups.size();
		// 对组群排序,未分组放在后面
		ContactsGroup unGroup = null;
		ContactsGroup unGroup2 = null;
		for (int i = 0; i < size; i++) {
			if (mContactsGroups.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = mContactsGroups.remove(i);
				break;
			}

		}
		if (unGroup != null) {
			mContactsGroups.add(unGroup);
		}

	}
}
