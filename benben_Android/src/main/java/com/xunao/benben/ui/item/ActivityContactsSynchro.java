/**
 * 通讯录同步
 */
package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.cropper.cropwindow.handle.Handle;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.BxContacts;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

@SuppressLint("ResourceAsColor")
public class ActivityContactsSynchro extends BaseActivity implements
		OnClickListener {

	private ImageView com_title_bar_left_bt;

	private MyTextView com_title_bar_left_tv;

	private ImageView com_title_bar_right_bt;

	private MyTextView com_title_bar_right_tv;
	private LinearLayout ll_all;

	private LinearLayout no_data;
	private TextView tab_left;
	private TextView tab_right;
	private LodingDialog lodingDialog;
	private ListView all_listview;
	private ListView group_listview;

	private String[] phoneAndName;
	private int index = 0;

	// 选中的组
	private ContactsGroup contactsGroup;

	private ContactsObject contactsObject;

	private ArrayList<Contacts> mContacts;

	private ArrayList<ContactsGroup> mContactsGroups = new ArrayList<ContactsGroup>();
	private HashMap<String, ContactsGroup> mapGroup = new HashMap<String, ContactsGroup>();
	private ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();

	private ArrayList<BxContacts> groupListContacts;

	private ArrayList<BxContacts> bxArrayList = new ArrayList<BxContacts>();
	private ArrayList<BxContacts> bxArrayList2 = new ArrayList<BxContacts>();

	private ArrayList<PhoneInfo> phoneInfos;
	private CheckBox all_checkbox;
	private TextView tv_all;

	// 未分组的id
	private String noGroupId = "";

	// 两个适配器
	private MyAdapter allAdapter;
	private MyAdapter groupAdapter;

	// 新建一个map来保存移动之前的groupid 防止别的分组移进去再移出去变成未分组
	private HashMap<String, String> beforMap = new HashMap<String, String>();

    private int addState = 0;  //0处理中；1成功；2失败
    private int updateState = 0;//0处理中；1成功；2失败

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_contect_update);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		tab_left = (TextView) findViewById(R.id.tab_left);
		tab_right = (TextView) findViewById(R.id.tab_right);

		all_Box = findViewById(R.id.all_Box);
		all_listview = (ListView) findViewById(R.id.all_listview);
		group_listview = (ListView) findViewById(R.id.group_listview);
		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
		all_checkbox.setOnCheckedChangeListener(changeListener);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		ll_all = (LinearLayout) findViewById(R.id.ll_all);
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			if (checked) {
				// tv_all.setText("取消");
				for (BxContacts contact : bxArrayList) {
					if (!groupListContacts.contains(contact)) {
						groupListContacts.add(contact);
						contact.setChecked(true);
					}
				}
			} else {
				tv_all.setText("全选");
				for (BxContacts contacts : bxArrayList) {
					contacts.setChecked(false);
				}
				groupListContacts.removeAll(bxArrayList);
			}
			tab_right.setText("已选择(" + groupListContacts.size() + ")");
			allAdapter.notifyDataSetChanged();
			groupAdapter.notifyDataSetChanged();
		}
	};

	private View all_Box;

	@Override
	public void initDate(Bundle savedInstanceState) {
		List<Contacts> contacts = null;
		try {
			contacts = dbUtil.findAll(Selector.from(Contacts.class));
		} catch (DbException e) {
			e.printStackTrace();
			ToastUtils.Infotoast(mContext, e.getMessage());
		}

		if (contacts == null) {
			contacts = new ArrayList<Contacts>();
		}

		mContacts = (ArrayList<Contacts>) contacts;

		for (int i = 0; i < mContacts.size(); i++) {
			List<PhoneInfo> list;
			try {
				list = dbUtil.findAll(Selector.from(PhoneInfo.class).where(
						"contacts_id", "=", mContacts.get(i).getId()));
				phoneInfos = (ArrayList<PhoneInfo>) list;
				for (int j = 0; j < phoneInfos.size(); j++) {
					BxContacts bxContacts = new BxContacts();
					bxContacts.setName(mContacts.get(i).getName());
					bxContacts.setPhone(phoneInfos.get(j).getPhone());
					bxContacts.setPinyin(mContacts.get(i).getPinyin());
					bxContacts.setHasPinYin(mContacts.get(i).isHasPinYin());
					bxArrayList2.add(bxContacts);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		try {
			ContentResolver contentResolver = mContext.getContentResolver();
			getPhones(contentResolver);
		} catch (Exception e) {
			AnimFinsh();
		}
	}

	Comparator<BxContacts> COMPARATOR = new Comparator<BxContacts>() {
		public int compare(BxContacts o1, BxContacts o2) {
			return o1.compareTo(o2);
		}
	};

	private void initUI(String phone) {
		InteNetUtils.getInstance(mContext).newmatchlog(phone, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
			}
		});
		phoneAndName = phone.split("\\|");
		int i = 0;
		if (phoneAndName.length > 0) {
			while (i < phoneAndName.length) {
				String[] phones = phoneAndName[i].split("::");
				String[] phones2;
				phones2 = phones[0].split("#");

				int j = 0;
				if (phones2.length > 0) {
					String phoneNum = "";
                    String allPhone ="";
                    boolean isUpdate = false;
					while (j < phones2.length) {
                        if(bxArrayList2.size() <= 0){
                            phoneNum = phoneNum + "#" + phones2[j];
                        }else{
                            if (isHasPhone(phones2[j], phones[1], bxArrayList2)) {
                                phoneNum = phoneNum + "#" + phones2[j];
                            }else{
                                isUpdate = true;
                            }
                        }
                        allPhone = allPhone + "#" + phones2[j];

//						if (isHasPhone(phones2[j], phones[1], bxArrayList2)
//								|| (bxArrayList2.size() <= 0)) {
//							phoneNum = phoneNum + "#" + phones2[j];
//						}
						j++;
					}
					if (phoneNum.length() > 1) {
						BxContacts bxContacts = new BxContacts();
						bxContacts.setName(phones[1]);
						bxContacts.setPhone(phoneNum.substring(1,
								phoneNum.length()));
						bxContacts.setPinyin(CommonUtils
								.hanYuToPinyin(phones[1]));
						bxContacts.setHasPinYin(true);
                        bxContacts.setUpdate(isUpdate);
                        bxContacts.setAllPhone(allPhone.substring(1,
                                allPhone.length()));
						if(!bxArrayList.contains(bxContacts)){
							bxArrayList.add(bxContacts);
						}
					}

				}
				i++;
			}
		}

		groupListContacts = new ArrayList<BxContacts>();

		if (lodingDialog != null && lodingDialog.isShowing()) {
			lodingDialog.dismiss();
		}

		// if (bxArrayList.size() <= 0) {
		// ToastUtils.Infotoast(mContext, "暂无需要匹配的通讯录!");
		// AnimFinsh();
		// }

		// groupListContacts.addAll(bxArrayList);

		Collections.sort(bxArrayList, COMPARATOR);
		getNewContacts(groupListContacts);
		getNewContacts(bxArrayList);

		// 配置适配器
		allAdapter = new MyAdapter(true, bxArrayList);
		groupAdapter = new MyAdapter(false, groupListContacts);

		all_listview.setAdapter(allAdapter);
		group_listview.setAdapter(groupAdapter);

		tab_right.setText("已选择(" + groupListContacts.size() + ")");
		
		if(bxArrayList.size() <= 0){
			no_data.setVisibility(View.VISIBLE);
			ll_all.setVisibility(View.GONE);
		}
		
	}

	private void getPhones(final ContentResolver contentResolver) {

		lodingDialog = new LodingDialog(mContext);
		lodingDialog.setContent("正在匹配...");
		lodingDialog.show();
		new Thread() {
			public void run() {
				final String phone = PhoneUtils
						.getOnlyContacts(contentResolver);

				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (CommonUtils.isEmpty(phone)) {
                            lodingDialog.dismiss();
                            ToastUtils.Infotoast(ActivityContactsSynchro.this,"通讯录无数据同步！");
							AnimFinsh();
							return;
						}
						initUI(phone);
					}
				});
			};
		}.start();
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		tab_left.setOnClickListener(this);
		tab_right.setOnClickListener(this);

		com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);

		com_title_bar_right_tv.setText("同步");

		com_title_bar_left_tv.setOnClickListener(this);
		com_title_bar_left_bt.setOnClickListener(this);
		com_title_bar_right_tv.setOnClickListener(this);
		com_title_bar_right_bt.setOnClickListener(this);
		tab_left.performClick();
	}

	// 递归判断数据库有没有此人
	private boolean isHasPhone(String phone, String name, ArrayList<BxContacts> bxContacts2) {
		for (BxContacts contacts : bxContacts2) {
			if (contacts.getPhone().equals(phone) && name.equals(contacts.getName())) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
//		dissLoding();
		try {
            addState=1;
			contactsObject = new ContactsObject();
			contactsObject = contactsObject
					.contactsSynchroparseJSON(jsonObject);

			final ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
			for (Contacts contacts : contactsObject.getmContactss()) {
				ArrayList<PhoneInfo> phones = contacts.getPhones();
				mPhoneInfos.addAll(contacts.getPhones());
			}

			dbUtil.saveOrUpdateAll(contactsObject.getmContactss());
			dbUtil.saveOrUpdateAll(mPhoneInfos);

            Message message = new Message();
            message.what=1;
            handler.sendMessage(message);
//			ToastUtils.Infotoast(mContext, "同步通讯录成功!");
//			sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
//			AnimFinsh();

		} catch (Exception e) {
//			ToastUtils.Infotoast(mContext, "同步通讯录失败!");
            addState=2;
            Message message = new Message();
            message.what=1;
            handler.sendMessage(message);
			e.printStackTrace();
		}

		// mContacts.clear();
		// mContactsGroups.clear();
		// mPhoneInfos.clear();
		// mContactsGroups = contactsObject.getmContactsGroups();
		//
		// mContacts = new ArrayList<Contacts>();
		// for (ContactsGroup cg : mContactsGroups) {
		// if (cg.getmContacts() != null) {
		// mContacts.addAll(cg.getmContacts());
		// }
		// }
		//
		// // 记录所有的phone信息
		// for (Contacts contacts : mContacts) {
		// ArrayList<PhoneInfo> phones = contacts.getPhones();
		// mPhoneInfos.addAll(contacts.getPhones());
		// }
		//
		// try {
		// dbUtil.deleteAll(ContactsGroup.class);
		// dbUtil.deleteAll(Contacts.class);
		// dbUtil.deleteAll(PhoneInfo.class);
		//
		// dbUtil.saveAll(mContactsGroups);
		//
		// for (ContactsGroup cg : mContactsGroups) {
		// // 记录所有的phone信息
		// if (cg.getmContacts() != null) {
		// ArrayList<Contacts> mContacts = cg.getmContacts();
		// dbUtil.saveAll(mContacts);
		// }
		// }
		//
		// dbUtil.saveAll(mPhoneInfos);

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
//		dissLoding();
//		ToastUtils.Infotoast(mContext, "网络不可用!");
        addState=2;
        Message message = new Message();
        message.what=1;
        handler.sendMessage(message);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_left:
			tab_left.setBackgroundResource(R.drawable.bac_white_left);
			tab_left.setTextColor(R.color.top_bar_color);

			tab_right.setBackgroundResource(R.drawable.bac_bule_right);
			tab_right.setTextColor(Color.parseColor("#ffffff"));

			all_Box.setVisibility(View.VISIBLE);
			group_listview.setVisibility(View.GONE);
			break;
		case R.id.tab_right:
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
			if (groupListContacts.size() <= 0) {
				ToastUtils.Errortoast(mContext, "请选择要同步的联系人!");
				return;
			}
			String addPhonesAndNames = "";
            String updatePhonesAndNames = "";
			ArrayList<BxContacts> temp = new ArrayList<BxContacts>();
//			if (groupListContacts.size() > 1) {
//				for (int i = 0; i <= groupListContacts.size(); i++) {
//					if (i != 0) {
//						for (int j = 0; j < groupListContacts.size(); j++) {
//							if (i - 1 != j) {
//								if (groupListContacts
//										.get(i - 1)
//										.getName()
//										.equals(groupListContacts.get(j)
//												.getName())) {
//
//									String[] phones = groupListContacts
//											.get(i - 1).getPhone().split("#");
//									boolean isContain = false;
//
//									for (String phone : phones) {
//										if (!CommonUtils
//												.isEmpty(groupListContacts.get(
//														j).getPhone())
//												&& phone.equals(groupListContacts
//														.get(j).getPhone())) {
//											isContain = true;
//											break;
//										}
//									}
//
//									if (!isContain) {
//										groupListContacts.get(i - 1).setPhone(
//												groupListContacts.get(i - 1)
//														.getPhone()
//														+ "#"
//														+ groupListContacts
//																.get(j)
//																.getPhone());
//										groupListContacts.get(j).setPhone("");
//									}
//								}
//							}
//						}
//					}
//				}
//			}
			for (int i = 0; i < groupListContacts.size(); i++) {
				if (!CommonUtils.isEmpty(groupListContacts.get(i).getPhone())) {
                    if(groupListContacts.get(i).isUpdate()){
                        updatePhonesAndNames += groupListContacts.get(i).getAllPhone()
                                + "::" + groupListContacts.get(i).getName() + "|";
                    }else{
                        addPhonesAndNames += groupListContacts.get(i).getPhone()
                                + "::" + groupListContacts.get(i).getName() + "|";
                    }
				}
			}
            setShowLoding(false);
            if (CommonUtils.isNetworkAvailable(mContext)) {
                if (addPhonesAndNames.length() > 0) {
                    addPhonesAndNames = addPhonesAndNames.substring(0,
                            addPhonesAndNames.length() - 1);
                    showLoding("请稍后...");
                    InteNetUtils.getInstance(mContext).contactsSynchro(addPhonesAndNames,
                            mRequestCallBack);
                }else{
                    addState=1;
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
                if (updatePhonesAndNames.length() > 0) {
                    updatePhonesAndNames = updatePhonesAndNames.substring(0,
                            updatePhonesAndNames.length() - 1);
                    showLoding("请稍后...");
                    updateState=0;
                    InteNetUtils.getInstance(mContext).Updatematch(updatePhonesAndNames,
                            new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> arg0) {
                                    updateState = 1;
                                    try {
                                        JSONObject jsonObj = new JSONObject(
                                                arg0.result);
                                        List<Contacts> contactses = new ArrayList<Contacts>();
                                        ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
                                        JSONArray contact = jsonObj.optJSONArray("contact");
                                        for(int i=0;i<contact.length();i++){
                                            JSONObject jsonObject = contact.optJSONObject(i);
                                            JSONObject phoneObject = jsonObject.optJSONObject("phone");
                                            if(!jsonObject.optString("is_benben").equals("")) {

                                                Contacts contacts = new Contacts();
                                                contacts.setId(phoneObject.optInt("contact_info_id"));
                                                contacts.setHuanxin_username(jsonObject.optString("huanxin_username"));
                                                contacts.setPoster(jsonObject.optString("poster"));

//                                            contacts.setIs_baixing(jsonObject.optString("is_baixing"));
                                                if (jsonObject.optString("is_baixing").equals("")) {
                                                    contacts.setIs_baixing("0");
                                                } else {
                                                    contacts.setIs_baixing(jsonObject.optString("is_baixing"));
                                                }
//                                            contacts.setIs_benben(jsonObject.optString("is_benben"));
                                                if (jsonObject.optString("is_benben").equals("")) {
                                                    contacts.setIs_benben("0");
                                                } else {
                                                    contacts.setIs_benben(jsonObject.optString("is_benben"));
                                                }
                                                contactses.add(contacts);
                                            }
                                            PhoneInfo phone = new PhoneInfo();
                                            phone.parseUpdateJSON(phoneObject);
                                            mPhoneInfos.add(phone);
                                        }


                                        dbUtil.updateAll(contactses,"id","is_benben","is_baixing","huanxin_username","poster");

                                        dbUtil.saveOrUpdateAll(mPhoneInfos);



//

//                                        for (Contacts contacts : contactsObject.getmContactss()) {
//                                            ArrayList<PhoneInfo> phones = contacts.getPhones();
//                                            mPhoneInfos.addAll(contacts.getPhones());
//                                        }
//
//                                        dbUtil.saveOrUpdateAll(contactsObject.getmContactss());
//                                        dbUtil.saveOrUpdateAll(mPhoneInfos);
//
//                                        JSONArray phoneArray = jsonObj.optJSONArray("phone");
//                                        Log.d("ltf", "phoneArray============" + phoneArray);
//                                        if (phoneArray != null && phoneArray.length() > 0) {
//                                            for (int i = 0; i < phoneArray.length(); i++) {
//                                                JSONObject jsonObject = phoneArray.optJSONObject(i);
//                                                PhoneInfo phone = new PhoneInfo();
//                                                phone.parseUpdateJSON(jsonObject);
//                                                mPhoneInfos.add(phone);
//                                            }
//                                        }


//                                        ToastUtils.Infotoast(mContext, "同步通讯录成功!");
//                                        sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
//                                        AnimFinsh();
                                        Message message = new Message();
                                        message.what = 1;
                                        handler.sendMessage(message);
                                    } catch (Exception e) {
                                        updateState = 2;
                                        Message message = new Message();
                                        message.what = 1;
                                        handler.sendMessage(message);
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    updateState = 2;
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }
                            });
                }else{
                    updateState=1;
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }else{
                ToastUtils.Errortoast(mContext, "网络不可用");
            }


			break;
		default:
			break;
		}

	}
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(addState!=0 && updateState!=0){
                        dissLoding();
                        if(addState==1 && updateState==1){
                            ToastUtils.Infotoast(mContext,"同步通讯录成功!");
                            sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
                            AnimFinsh();
                        }else{
                            ToastUtils.Infotoast(mContext,"同步通讯录失败!");
                        }

                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


	class MyAdapter extends BaseAdapter {
		private boolean isAll;
		// private ArrayList<Contacts> adapterContacts;
		private ArrayList<BxContacts> adapterContacts;

		public MyAdapter(boolean isAll, ArrayList<BxContacts> adapterContacts) {
			// TODO Auto-generated constructor stub
			this.isAll = isAll;
			this.adapterContacts = adapterContacts;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return adapterContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return adapterContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			final BxContacts contacts = adapterContacts.get(position);
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

			if (isAll) {
				itemHolder.item_phone_checkbox.setVisibility(View.VISIBLE);
				// checkbox的初始化

				itemHolder.item_phone_checkbox.setChecked(contacts.isChecked());

				// item的点击事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (itemHolder.item_phone_checkbox.isChecked()) {
							itemHolder.item_phone_checkbox.setChecked(false);
							groupListContacts.remove(contacts);
							all_checkbox.setOnCheckedChangeListener(null);
							all_checkbox.setChecked(false);
							all_checkbox
									.setOnCheckedChangeListener(changeListener);
							contacts.setChecked(false);
							tv_all.setText("全选");
						} else {
							itemHolder.item_phone_checkbox.setChecked(true);
							groupListContacts.add(contacts);
							contacts.setChecked(true);
							if (groupListContacts.size() >= bxArrayList.size()) {
								all_checkbox.setOnCheckedChangeListener(null);
								all_checkbox.setChecked(true);
								all_checkbox
										.setOnCheckedChangeListener(changeListener);
								// tv_all.setText("取消");
							}

						}
						getNewContacts(groupListContacts);
						groupAdapter.notifyDataSetChanged();
						tab_right.setText("已选择(" + groupListContacts.size()
								+ ")");
					}
				});

			}

			itemHolder.item_phone_poster.setVisibility(View.GONE);

			itemHolder.item_phone_name.setText(contacts.getName());
			itemHolder.item_phone_phone.setText(contacts.getPhone());
			itemHolder.item_phone_phone.setVisibility(View.VISIBLE);
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
