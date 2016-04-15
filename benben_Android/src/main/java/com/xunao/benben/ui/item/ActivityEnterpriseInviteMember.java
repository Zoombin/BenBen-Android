package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.cn;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsEnterprise;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsGroupEnterprise;
import com.xunao.benben.bean.Enterprise;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityChoiceFriend.ItemHolder;
import com.xunao.benben.ui.item.ActivityEnterpriseInviteMemberBack.myAdapter;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityEnterpriseInviteMember extends BaseActivity implements
		OnClickListener {

	private static final int ADD_REMARKS = 1000;
	private static final int ADD_MEMBER = 1001;
	private EditText search_edittext;
	private LinearLayout ll_seach_icon;
	private ImageView iv_search_content_delect;
	private Enterprise enterprise;
	private ArrayList<ContactsEnterprise> selectMember = new ArrayList<ContactsEnterprise>();
	private vMyAdapter adapter;
	private WrapperExpandableListAdapter wrapperAdapter;

	private ArrayList<ContactsGroupEnterprise> contactsGroups = new ArrayList<ContactsGroupEnterprise>();
	private FloatingGroupExpandableListView listview;
	private Button btn_invite;
	private String enterpriseId;
	private String enterpriseType;
	private MyBroadcastReceiver myBroadcastReceiver;
	private ArrayList<ContactsEnterprise> arrayList;
	private ArrayList<ContactsEnterprise> selectStatusMember = new ArrayList<ContactsEnterprise>();
	ArrayList<PhoneInfo> selectPhonesed = new ArrayList<PhoneInfo>();
    private int permit=0;
    private TextView tv_invite_content;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_invite_member);
		initdefaultImage(R.drawable.ic_group_poster);
		myBroadcastReceiver = new MyBroadcastReceiver();
		registerReceiver(myBroadcastReceiver, new IntentFilter("close"));
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("添加成员", "", "手动添加",
				R.drawable.icon_com_title_left, 0);

		search_edittext = (EditText) findViewById(R.id.search_edittext);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		btn_invite = (Button) findViewById(R.id.btn_invite);
		listview = (FloatingGroupExpandableListView) findViewById(R.id.listview);
		listview.setGroupIndicator(null);
		iv_search_content_delect.setOnClickListener(this);
		btn_invite.setOnClickListener(this);

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult(ActivityEnterpriseAddMember.class,
						"enterpriseId", enterpriseId, "enterpriseType",
						enterpriseType, "short_length",
						enterprise.getShort_length() + "", ADD_MEMBER);
			}
		});

        tv_invite_content = (TextView) findViewById(R.id.tv_invite_content);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myBroadcastReceiver);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		enterprise = (Enterprise) intent.getSerializableExtra("enterprise");
		enterpriseId = enterprise.getId();
		enterpriseType = enterprise.getType();

		showLoding("请稍后...");
		InteNetUtils.getInstance(mContext).getEnterpriseM(enterpriseId,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtils.Errortoast(mContext, "当前网络不可用,");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						dissLoding();
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							jsonObject.toString();

							SuccessMsg msg = new SuccessMsg();

							msg.checkJson(jsonObject);

							JSONArray optJSONArray = jsonObject
									.optJSONArray("member_ginfo");

							arrayList = new ArrayList<ContactsEnterprise>();
							if (optJSONArray != null) {
								int length = optJSONArray.length();
								for (int i = 0; i < length; i++) {
									JSONObject optJSONObject = optJSONArray
											.optJSONObject(i);
									ContactsEnterprise ce = new ContactsEnterprise();
									ce.setId(optJSONObject.optInt("id") + "");
									ce.setName(optJSONObject.optString("name"));
									ce.setPhone(optJSONObject
											.optString("phone"));
									ce.setShortPhone(optJSONObject
											.optString("short_phone"));
                                    permit = optJSONObject.optInt("permit");
									arrayList.add(ce);

								}
							}
                            tv_invite_content.setText("(0/"+permit+")");

							try {
								List conGroupList = dbUtil.findAll(Selector
										.from(ContactsGroup.class).where(
												WhereBuilder.b("id", "<>",
														"10000")));

								ContactsGroupEnterprise cge = null;
								for (ContactsGroup cg : (ArrayList<ContactsGroup>) conGroupList) {
									cge = new ContactsGroupEnterprise();
									cge.setId(cg.getId());
									cge.setName(cg.getName());
									cge.setOpen(cg.isOpen());
									cge.setProportion(cg.getProportion());
									cge.setSelect(cg.isSelect());
									cge.setCreated_time(cg.getCreated_time());
									contactsGroups.add(cge);
								}

								groupOrderBy();

								for (ContactsGroupEnterprise group : contactsGroups) {
									List contact = dbUtil.findAll(Selector
											.from(Contacts.class)
											.where(WhereBuilder.b("group_id",
													"=", group.getId()))
											.orderBy("is_benben", true));

									ArrayList<ContactsEnterprise> contacts = new ArrayList<ContactsEnterprise>();
									ContactsEnterprise ce = null;
									for (Contacts c : (ArrayList<Contacts>) contact) {
                                        List<PhoneInfo> phoneList = dbUtil.findAll(Selector
                                                .from(PhoneInfo.class)
                                                .where(WhereBuilder.b(
                                                        "contacts_id", "=",
                                                        c.getId()).and(
                                                        "phone", "!=",
                                                        "")));
                                        ArrayList<PhoneInfo> phoneInfos = (ArrayList<PhoneInfo>) phoneList;
                                        ArrayList<PhoneInfo> phoneInfos2 = new ArrayList<PhoneInfo>();
                                        ArrayList<PhoneInfo> phoneInfos3 = new ArrayList<PhoneInfo>();
                                        boolean isCunzai = false;//是否存在
                                        boolean isMatch = false;//企业政企是否有第一位不为0的11位长号并
                                        for (ContactsEnterprise co : arrayList) {
                                            for (PhoneInfo p : phoneList) {
                                                if (p.getPhone().equals(co.getPhone()) || p.getIs_baixing().equals(co.getShortPhone()) || p.getPhone().equals(co.getShortPhone())) {
                                                    isCunzai = true;
                                                    break;
                                                }else{
                                                    String phone = p.getPhone();
                                                    if(phone.length()==11 && phone.charAt(0)!=0){
                                                        isMatch = true;
                                                    }
                                                    if (enterpriseType.equals("2")) {
                                                        isMatch = true;
                                                        if (p.getIs_baixing().length() != enterprise.getShort_length()) {
                                                            if(p.getPhone().length() == enterprise.getShort_length()){
                                                                p.setIs_baixing(p.getPhone());
                                                            }else if (!phoneInfos2.contains(p)) {
																phoneInfos2.add(p);
															}
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if(isCunzai || !isMatch || phoneList.size() == phoneInfos2.size()){
                                            continue;
                                        }
                                        phoneInfos.removeAll(phoneInfos2);
                                        for (PhoneInfo info : phoneInfos) {
                                            if (enterpriseType.equals("1")) {
                                                PhoneInfo info2 = new PhoneInfo();
                                                if (!"".equals(info.getIs_baixing()) && !"0".equals(info.getIs_baixing())) {
                                                    info2.setId(info.getId());
                                                    info2.setPhone(info.getIs_baixing());
                                                    info2.setIs_benben(info.getIs_benben());
                                                    info2.setPoster(info.getPoster());
                                                    info2.setNick_name(info.getNick_name());
                                                    info2.setContacts_id(info.getContacts_id());
                                                    info2.setHuanxin_username(info.getHuanxin_username());
                                                    phoneInfos3.add(info2);
                                                }
                                            }
                                        }
                                        phoneInfos.addAll(phoneInfos3);
                                        ce = new ContactsEnterprise();
                                        ce.setPhones(phoneInfos);
                                        ce.setId(c.getId() + "");
                                        ce.setName(c.getName());
                                        ce.setChecked(c.isChecked());
                                        ce.setGroup_id(c.getGroup_id());
                                        ce.setHasPinYin(c.isHasPinYin());
                                        ce.setHuanxin_username(c
                                                .getHuanxin_username());
                                        ce.setIs_baixing(c.getIs_baixing());
                                        ce.setIs_benben(c.getIs_benben());
                                        ce.setIs_friend(c.getIs_friend());
                                        ce.setPhone(c.getPhone());
                                        ce.setPinyin(c.getPinyin());
                                        ce.setPoster(c.getPoster());
                                        ce.setRemark(c.getRemark());
                                        contacts.add(ce);
                                        group.setmContacts(contacts);

//										List<PhoneInfo> phoneList;
//										if (!enterpriseType.equals("2")) {
//											phoneList = dbUtil.findAll(Selector
//													.from(PhoneInfo.class)
//													.where(WhereBuilder.b(
//															"contacts_id", "=",
//															c.getId())));
//										} else {
//											phoneList = dbUtil.findAll(Selector
//													.from(PhoneInfo.class)
//													.where(WhereBuilder.b(
//															"contacts_id", "=",
//															c.getId()).and(
//															"is_baixing", "!=",
//															"0")));
//										}
//
//										ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
//										phoneInfos = (ArrayList<PhoneInfo>) phoneList;
//										ArrayList<PhoneInfo> phoneInfos2 = new ArrayList<PhoneInfo>();
//										boolean isCunzai = false;
//
//										for (ContactsEnterprise co : arrayList) {
//											if (co.getName()
//													.equals(c.getName())) {
//												phoneInfos2.addAll(phoneList);
//											} else {
//
//												for (PhoneInfo p : phoneList) {
//													if (enterpriseType
//															.equals("2")) {
//														if (p.getIs_baixing()
//																.length() != enterprise
//																.getShort_length()) {
//															phoneList.remove(p);
//															break;
//														}
//													} else {
//
//														if (p.getPhone()
//																.equals(co
//																		.getPhone())
//																|| p.getIs_baixing()
//																		.equals(co
//																				.getShortPhone())
//																|| p.getPhone()
//																		.equals(co
//																				.getShortPhone())) {
//
//															// phoneList.remove(p);
//															// break;
//															isCunzai = true;
//															if (!phoneInfos2
//																	.contains(p)) {
//																phoneInfos2
//																		.add(p);
//															}
//														}
//													}
//
//												}
//											}
//										}
//
//										if(isCunzai){
//											continue;
//										}
//
//										if (phoneList.size() == phoneInfos2
//												.size()) {
//											continue;
//										}
//
//										phoneInfos.removeAll(phoneInfos2);
//
//										ArrayList<PhoneInfo> phoneInfos3 = new ArrayList<PhoneInfo>();
//
//										for (PhoneInfo info : phoneInfos) {
//											if (info.getPhone().length() == 11
//													|| info.getIs_baixing()
//															.length() == 11) {
//												if (!phoneInfos3
//														.containsAll(phoneInfos)) {
//													phoneInfos3
//															.addAll(phoneInfos);
//												}
//												continue;
//											}
//										}
//
//										if (phoneInfos3.size() <= 0) {
//											continue;
//										}
//
//										ArrayList<PhoneInfo> phoneInfos4 = new ArrayList<PhoneInfo>();
//
//										for (PhoneInfo info : phoneInfos3) {
//											if (!phoneInfos4.contains(info)) {
//												phoneInfos4.add(info);
//											}
//											PhoneInfo info2 = new PhoneInfo();
//
//											if (!"".equals(info.getIs_baixing())
//													&& !"0".equals(info
//															.getIs_baixing())) {
//												info2.setId(info.getId());
//												info2.setPhone(info
//														.getIs_baixing());
//												info2.setIs_benben(info
//														.getIs_benben());
//												info2.setPoster(info
//														.getPoster());
//												info2.setNick_name(info
//														.getNick_name());
//												info2.setContacts_id(info
//														.getContacts_id());
//												info2.setHuanxin_username(info
//														.getHuanxin_username());
//												if (!phoneInfos4
//														.contains(info2)) {
//													phoneInfos4.add(info2);
//												}
//											}
//
//										}
//
//										ce = new ContactsEnterprise();
//										if (!enterpriseType.equals("2")) {
//											Collections.sort(phoneInfos4, new Comparator<PhoneInfo>(){
//
//												@Override
//												public int compare(
//														PhoneInfo arg0,
//														PhoneInfo arg1) {
//													Integer phone = arg0.getPhone().length();
//													Integer phone2 = arg1.getPhone().length();
//													return phone2.compareTo(phone);
//												}
//
//											});
//
//
//
//											ce.setPhones(phoneInfos4);
//										} else {
//											Collections.sort(phoneInfos3, new Comparator<PhoneInfo>(){
//
//												@Override
//												public int compare(
//														PhoneInfo arg0,
//														PhoneInfo arg1) {
//													Integer phone = arg0.getPhone().length();
//													Integer phone2 = arg1.getPhone().length();
//													return phone2.compareTo(phone);
//												}
//
//											});
//											ce.setPhones(phoneInfos3);
//										}
//
//										ce.setId(c.getId() + "");
//										ce.setName(c.getName());
//										ce.setChecked(c.isChecked());
//										ce.setGroup_id(c.getGroup_id());
//										ce.setHasPinYin(c.isHasPinYin());
//										ce.setHuanxin_username(c
//												.getHuanxin_username());
//										ce.setIs_baixing(c.getIs_baixing());
//										ce.setIs_benben(c.getIs_benben());
//										ce.setIs_friend(c.getIs_friend());
//										ce.setPhone(c.getPhone());
//										ce.setPinyin(c.getPinyin());
//										ce.setPoster(c.getPoster());
//										ce.setRemark(c.getRemark());
//										contacts.add(ce);
//										group.setmContacts(contacts);
									}
								}

								adapter = new vMyAdapter();
								wrapperAdapter = new WrapperExpandableListAdapter(
										adapter);
								listview.setAdapter(wrapperAdapter);

							} catch (DbException e) {
								e.printStackTrace();
								ToastUtils.Infotoast(mContext, "获取数据失败,请重试!");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NetRequestException e) {
							e.printStackTrace();
							e.getError().print(mContext);
						}

					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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

			String searchKey = search_edittext.getText().toString().trim();
			showLoding("请稍后...");
			try {
				List conGroupList = dbUtil.findAll(Selector.from(
						ContactsGroup.class).where(
						WhereBuilder.b("id", "<>", "10000")));

				ContactsGroupEnterprise cge = null;
				contactsGroups.clear();
				for (ContactsGroup cg : (ArrayList<ContactsGroup>) conGroupList) {
					cge = new ContactsGroupEnterprise();
					cge.setId(cg.getId());
					cge.setName(cg.getName());
					cge.setOpen(cg.isOpen());
					cge.setProportion(cg.getProportion());
					cge.setSelect(cg.isSelect());
					cge.setCreated_time(cg.getCreated_time());
					contactsGroups.add(cge);
				}

				groupOrderBy();

				for (ContactsGroupEnterprise group : contactsGroups) {

					List contact = new ArrayList();
					if ("".equals(searchKey)) {
						contact = dbUtil.findAll(Selector
								.from(Contacts.class)
								.where(WhereBuilder.b("group_id", "=",
										group.getId()))
								.orderBy("is_benben", true));

					} else {

						List<PhoneInfo> phoneList = dbUtil.findAll(Selector
								.from(PhoneInfo.class).where(
										WhereBuilder.b("phone", "like", "%"
												+ searchKey + "%")));
						String contacts_id = "";
						for (PhoneInfo phoneInfo : phoneList) {
							contacts_id += phoneInfo.getContacts_id() + ",";
						}

						contact = dbUtil.findAll(Selector
								.from(Contacts.class)
								.where(WhereBuilder.b("group_id", "=",
										group.getId()))
								.and(WhereBuilder.b("name", "like", "%"
										+ searchKey + "%"))
								.and(WhereBuilder.b(
										"id",
										"in",
										contacts_id.substring(0,
												contacts_id.length() - 1)))
								.orderBy("is_benben", true));
					}

					ArrayList<ContactsEnterprise> contacts = new ArrayList<ContactsEnterprise>();
					ContactsEnterprise ce = null;

					if (contact != null || contact.size() > 0) {
						for (Contacts c : (ArrayList<Contacts>) contact) {
                            List<PhoneInfo> phoneList = dbUtil.findAll(Selector
                                    .from(PhoneInfo.class)
                                    .where(WhereBuilder.b(
                                            "contacts_id", "=",
                                            c.getId()).and(
                                            "phone", "!=",
                                            "")));
                            ArrayList<PhoneInfo> phoneInfos = (ArrayList<PhoneInfo>) phoneList;
                            ArrayList<PhoneInfo> phoneInfos2 = new ArrayList<PhoneInfo>();
                            ArrayList<PhoneInfo> phoneInfos3 = new ArrayList<PhoneInfo>();
                            boolean isCunzai = false;//是否存在
                            boolean isMatch = false;//企业政企是否有第一位不为0的11位长号并
                            for (ContactsEnterprise co : arrayList) {
                                for (PhoneInfo p : phoneList) {
                                    if (p.getPhone().equals(co.getPhone()) || p.getIs_baixing().equals(co.getShortPhone()) || p.getPhone().equals(co.getShortPhone())) {
                                        isCunzai = true;
                                        break;
                                    }else{
                                        String phone = p.getPhone();
                                        if(phone.length()==11 && phone.charAt(0)!=0){
                                            isMatch = true;
                                        }
                                        if (enterpriseType.equals("2")) {
                                            isMatch = true;
                                            if (p.getIs_baixing().length() != enterprise.getShort_length()) {
                                                if(p.getPhone().length() == enterprise.getShort_length()){
                                                    p.setIs_baixing(p.getPhone());
                                                }else if (!phoneInfos2.contains(p)) {
                                                    phoneInfos2.add(p);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(isCunzai || !isMatch || phoneList.size() == phoneInfos2.size()){
                                continue;
                            }
                            phoneInfos.removeAll(phoneInfos2);
                            for (PhoneInfo info : phoneInfos) {
                                if (enterpriseType.equals("1")) {
                                    PhoneInfo info2 = new PhoneInfo();
                                    if (!"".equals(info.getIs_baixing()) && !"0".equals(info.getIs_baixing())) {
                                        info2.setId(info.getId());
                                        info2.setPhone(info.getIs_baixing());
                                        info2.setIs_benben(info.getIs_benben());
                                        info2.setPoster(info.getPoster());
                                        info2.setNick_name(info.getNick_name());
                                        info2.setContacts_id(info.getContacts_id());
                                        info2.setHuanxin_username(info.getHuanxin_username());
                                        phoneInfos3.add(info2);
                                    }
                                }
                            }
                            phoneInfos.addAll(phoneInfos3);
                            ce = new ContactsEnterprise();
                            ce.setPhones(phoneInfos);
                            ce.setId(c.getId() + "");
                            ce.setName(c.getName());
                            ce.setChecked(c.isChecked());
                            ce.setGroup_id(c.getGroup_id());
                            ce.setHasPinYin(c.isHasPinYin());
                            ce.setHuanxin_username(c
                                    .getHuanxin_username());
                            ce.setIs_baixing(c.getIs_baixing());
                            ce.setIs_benben(c.getIs_benben());
                            ce.setIs_friend(c.getIs_friend());
                            ce.setPhone(c.getPhone());
                            ce.setPinyin(c.getPinyin());
                            ce.setPoster(c.getPoster());
                            ce.setRemark(c.getRemark());
                            contacts.add(ce);
                            group.setmContacts(contacts);



//							List<PhoneInfo> phoneList;
//							if (!enterpriseType.equals("2")) {
//								phoneList = dbUtil.findAll(Selector
//										.from(PhoneInfo.class)
//										.where(WhereBuilder.b(
//												"contacts_id", "=",
//												c.getId())));
//							} else {
//								phoneList = dbUtil.findAll(Selector
//										.from(PhoneInfo.class)
//										.where(WhereBuilder.b(
//												"contacts_id", "=",
//												c.getId()).and(
//												"is_baixing", "!=",
//												"0")));
//							}
//
//
//
//
//							ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
//							phoneInfos = (ArrayList<PhoneInfo>) phoneList;
//							ArrayList<PhoneInfo> phoneInfos2 = new ArrayList<PhoneInfo>();
//							boolean isCunzai = false;
//
//							for (ContactsEnterprise co : arrayList) {
//								if (co.getName()
//										.equals(c.getName())) {
//									phoneInfos2.addAll(phoneList);
//								} else {
//
//									for (PhoneInfo p : phoneList) {
//										if (enterpriseType
//												.equals("2")) {
//											if (p.getIs_baixing()
//													.length() != enterprise
//													.getShort_length()) {
//												phoneList.remove(p);
//												break;
//											}
//										} else {
//
//											if (p.getPhone()
//													.equals(co
//															.getPhone())
//													|| p.getIs_baixing()
//															.equals(co
//																	.getShortPhone())
//													|| p.getPhone()
//															.equals(co
//																	.getShortPhone())) {
//
//												// phoneList.remove(p);
//												// break;
//												isCunzai = true;
//												if (!phoneInfos2
//														.contains(p)) {
//													phoneInfos2
//															.add(p);
//												}
//											}
//										}
//
//									}
//								}
//							}
//							// if (phoneList.size() <= 0) {
//							// continue;
//							// }
//
//							if(isCunzai){
//								continue;
//							}
//
//							if (phoneList.size() == phoneInfos2
//									.size()) {
//								continue;
//							}
//
//							phoneInfos.removeAll(phoneInfos2);
//
//							ArrayList<PhoneInfo> phoneInfos3 = new ArrayList<PhoneInfo>();
//
//							for (PhoneInfo info : phoneInfos) {
//								if (info.getPhone().length() == 11
//										|| info.getIs_baixing()
//												.length() == 11) {
//									if (!phoneInfos3
//											.containsAll(phoneInfos)) {
//										phoneInfos3
//												.addAll(phoneInfos);
//									}
//									continue;
//								}
//							}
//
//							if (phoneInfos3.size() <= 0) {
//								continue;
//							}
//
//							ArrayList<PhoneInfo> phoneInfos4 = new ArrayList<PhoneInfo>();
//
//							for (PhoneInfo info : phoneInfos3) {
//								if (!phoneInfos4.contains(info)) {
//									phoneInfos4.add(info);
//								}
//								PhoneInfo info2 = new PhoneInfo();
//
//								if (!"".equals(info.getIs_baixing())
//										&& !"0".equals(info
//												.getIs_baixing())) {
//									info2.setId(info.getId());
//									info2.setPhone(info
//											.getIs_baixing());
//									info2.setIs_benben(info
//											.getIs_benben());
//									info2.setPoster(info
//											.getPoster());
//									info2.setNick_name(info
//											.getNick_name());
//									info2.setContacts_id(info
//											.getContacts_id());
//									info2.setHuanxin_username(info
//											.getHuanxin_username());
//									if (!phoneInfos4
//											.contains(info2)) {
//										phoneInfos4.add(info2);
//									}
//								}
//
//							}
//
//							ce = new ContactsEnterprise();
//							if (!enterpriseType.equals("2")) {
//								Collections.sort(phoneInfos4, new Comparator<PhoneInfo>(){
//
//									@Override
//									public int compare(
//											PhoneInfo arg0,
//											PhoneInfo arg1) {
//										Integer phone = arg0.getPhone().length();
//										Integer phone2 = arg1.getPhone().length();
//										return phone2.compareTo(phone);
//									}
//
//								});
//
//
//
//								ce.setPhones(phoneInfos4);
//							} else {
//								Collections.sort(phoneInfos3, new Comparator<PhoneInfo>(){
//
//									@Override
//									public int compare(
//											PhoneInfo arg0,
//											PhoneInfo arg1) {
//										Integer phone = arg0.getPhone().length();
//										Integer phone2 = arg1.getPhone().length();
//										return phone2.compareTo(phone);
//									}
//
//								});
//								ce.setPhones(phoneInfos3);
//							}
//
//							ce.setId(c.getId() + "");
//							ce.setName(c.getName());
//							ce.setChecked(c.isChecked());
//							ce.setGroup_id(c.getGroup_id());
//							ce.setHasPinYin(c.isHasPinYin());
//							ce.setHuanxin_username(c
//									.getHuanxin_username());
//							ce.setIs_baixing(c.getIs_baixing());
//							ce.setIs_benben(c.getIs_benben());
//							ce.setIs_friend(c.getIs_friend());
//							ce.setPhone(c.getPhone());
//							ce.setPinyin(c.getPinyin());
//							ce.setPoster(c.getPoster());
//							ce.setRemark(c.getRemark());
//							contacts.add(ce);
//							group.setmContacts(contacts);
						}
					}
				}

				dissLoding();

				adapter = new vMyAdapter();
				wrapperAdapter = new WrapperExpandableListAdapter(adapter);
				listview.setAdapter(wrapperAdapter);

			} catch (DbException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, "获取数据失败,请重试!");
			}

			break;
		case R.id.btn_invite:

			if (enterpriseType.equals("1")) {
				if (selectMember.size() <= 0) {
					ToastUtils.Infotoast(mContext, "请选择要添加的好友!");
				} else {
					startAnimActivityForResult5(ActivityAddRemarks.class,
							"enterpriseId", enterpriseId, "contacts",
							selectMember, ADD_REMARKS);
				}
			} else {
				if (selectMember.size() <= 0) {
					ToastUtils.Infotoast(mContext, "请选择要添加的好友!");
				} else {
					startAnimActivityForResult5(ActivityAddRemarks.class,
							"enterpriseId", enterpriseId, "virtualMembers",
							selectMember, ADD_REMARKS);
				}
			}

			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
		case ADD_REMARKS:
			if (arg2 != null) {
				AnimFinsh();
			}
			break;
		case ADD_MEMBER:
			if (arg2 != null) {
				AnimFinsh();
			}
		default:
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
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
					String searchKey = search_edittext.getText().toString()
							.trim();
					if (CommonUtils.isEmpty(searchKey)) {
						return true;
					}
					showLoding("请稍后...");
					try {
						List conGroupList = dbUtil.findAll(Selector.from(
								ContactsGroup.class).where(
								WhereBuilder.b("id", "<>", "10000")));

						ContactsGroupEnterprise cge = null;
						contactsGroups.clear();
						for (ContactsGroup cg : (ArrayList<ContactsGroup>) conGroupList) {
							cge = new ContactsGroupEnterprise();
							cge.setId(cg.getId());
							cge.setName(cg.getName());
							cge.setOpen(cg.isOpen());
							cge.setProportion(cg.getProportion());
							cge.setSelect(cg.isSelect());
							cge.setCreated_time(cg.getCreated_time());
							contactsGroups.add(cge);
						}

						groupOrderBy();

						for (ContactsGroupEnterprise group : contactsGroups) {
							List contact = new ArrayList();
							if ("".equals(searchKey)) {
								contact = dbUtil.findAll(Selector
										.from(Contacts.class)
										.where(WhereBuilder.b("group_id", "=",
												group.getId()))
										.orderBy("is_benben", true));
							} else {
								List<PhoneInfo> phoneList = dbUtil.findAll(Selector
										.from(PhoneInfo.class)
										.where(WhereBuilder.b("phone", "like",
												"%" + searchKey + "%"))
										.or(WhereBuilder.b("is_baixing",
												"like", "%" + searchKey + "%")));
								String contacts_id = "";
								for (PhoneInfo phoneInfo : phoneList) {
									contacts_id += phoneInfo.getContacts_id()
											+ ",";
								}
								
								if("".equals(contacts_id)){
									contact = dbUtil.findAll(Selector
											.from(Contacts.class)
											.where(WhereBuilder.b("group_id", "=",
													group.getId()))
											.and(WhereBuilder.b("name", "like", "%"
													+ searchKey + "%"))
											.orderBy("is_benben", true));
								}else{
									contact = dbUtil.findAll(Selector
											.from(Contacts.class)
											
											.where(WhereBuilder.b("name", "like", "%"
													+ searchKey + "%"))
											.or(WhereBuilder
													.b("id",
															"in",
															contacts_id
																	.substring(
																			0,
																			contacts_id
																					.length() - 1)
																	.split(",")))
																	.and(WhereBuilder.b("group_id", "=",
													group.getId()))
											.orderBy("is_benben", true));
								}

								
							}

							ArrayList<ContactsEnterprise> contacts = new ArrayList<ContactsEnterprise>();
							ContactsEnterprise ce = null;

							if (contact != null || contact.size() > 0) {
								for (Contacts c : (ArrayList<Contacts>) contact) {
                                    List<PhoneInfo> phoneList = dbUtil.findAll(Selector
                                            .from(PhoneInfo.class)
                                            .where(WhereBuilder.b(
                                                    "contacts_id", "=",
                                                    c.getId()).and(
                                                    "phone", "!=",
                                                    "")));
                                    ArrayList<PhoneInfo> phoneInfos = (ArrayList<PhoneInfo>) phoneList;
                                    ArrayList<PhoneInfo> phoneInfos2 = new ArrayList<PhoneInfo>();
                                    ArrayList<PhoneInfo> phoneInfos3 = new ArrayList<PhoneInfo>();
                                    boolean isCunzai = false;//是否存在
                                    boolean isMatch = false;//企业政企是否有第一位不为0的11位长号并
                                    for (ContactsEnterprise co : arrayList) {
                                        for (PhoneInfo p : phoneList) {
                                            if (p.getPhone().equals(co.getPhone()) || p.getIs_baixing().equals(co.getShortPhone()) || p.getPhone().equals(co.getShortPhone())) {
                                                isCunzai = true;
                                                break;
                                            }else{
                                                String phone = p.getPhone();
                                                if(phone.length()==11 && phone.charAt(0)!=0){
                                                    isMatch = true;
                                                }
                                                if (enterpriseType.equals("2")) {
                                                    isMatch = true;
                                                    if (p.getIs_baixing().length() != enterprise.getShort_length()) {
                                                        if(p.getPhone().length() == enterprise.getShort_length()){
                                                            p.setIs_baixing(p.getPhone());
                                                        }else if (!phoneInfos2.contains(p)) {
                                                            phoneInfos2.add(p);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(isCunzai || !isMatch || phoneList.size() == phoneInfos2.size()){
                                        continue;
                                    }
                                    phoneInfos.removeAll(phoneInfos2);
                                    for (PhoneInfo info : phoneInfos) {
                                        if (enterpriseType.equals("1")) {
                                            PhoneInfo info2 = new PhoneInfo();
                                            if (!"".equals(info.getIs_baixing()) && !"0".equals(info.getIs_baixing())) {
                                                info2.setId(info.getId());
                                                info2.setPhone(info.getIs_baixing());
                                                info2.setIs_benben(info.getIs_benben());
                                                info2.setPoster(info.getPoster());
                                                info2.setNick_name(info.getNick_name());
                                                info2.setContacts_id(info.getContacts_id());
                                                info2.setHuanxin_username(info.getHuanxin_username());
                                                phoneInfos3.add(info2);
                                            }
                                        }
                                    }
                                    phoneInfos.addAll(phoneInfos3);
                                    ce = new ContactsEnterprise();
                                    ce.setPhones(phoneInfos);
                                    ce.setId(c.getId() + "");
                                    ce.setName(c.getName());
                                    ce.setChecked(c.isChecked());
                                    ce.setGroup_id(c.getGroup_id());
                                    ce.setHasPinYin(c.isHasPinYin());
                                    ce.setHuanxin_username(c
                                            .getHuanxin_username());
                                    ce.setIs_baixing(c.getIs_baixing());
                                    ce.setIs_benben(c.getIs_benben());
                                    ce.setIs_friend(c.getIs_friend());
                                    ce.setPhone(c.getPhone());
                                    ce.setPinyin(c.getPinyin());
                                    ce.setPoster(c.getPoster());
                                    ce.setRemark(c.getRemark());
                                    contacts.add(ce);
                                    group.setmContacts(contacts);
//									List<PhoneInfo> phoneList;
//									if (!enterpriseType.equals("2")) {
//										phoneList = dbUtil.findAll(Selector
//												.from(PhoneInfo.class)
//												.where(WhereBuilder.b(
//														"contacts_id", "=",
//														c.getId())));
//									} else {
//										phoneList = dbUtil.findAll(Selector
//												.from(PhoneInfo.class)
//												.where(WhereBuilder.b(
//														"contacts_id", "=",
//														c.getId()).and(
//														"is_baixing", "!=",
//														"0")));
//									}
//
//									ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
//									phoneInfos = (ArrayList<PhoneInfo>) phoneList;
//									ArrayList<PhoneInfo> phoneInfos2 = new ArrayList<PhoneInfo>();
//									boolean isCunzai = false;
//
//									for (ContactsEnterprise co : arrayList) {
//										if (co.getName()
//												.equals(c.getName())) {
//											phoneInfos2.addAll(phoneList);
//										} else {
//
//											for (PhoneInfo p : phoneList) {
//												if (enterpriseType
//														.equals("2")) {
//													if (p.getIs_baixing()
//															.length() != enterprise
//															.getShort_length()) {
//														phoneList.remove(p);
//														break;
//													}
//												} else {
//
//													if (p.getPhone()
//															.equals(co
//																	.getPhone())
//															|| p.getIs_baixing()
//																	.equals(co
//																			.getShortPhone())
//															|| p.getPhone()
//																	.equals(co
//																			.getShortPhone())) {
//
//														// phoneList.remove(p);
//														// break;
//														isCunzai = true;
//														if (!phoneInfos2
//																.contains(p)) {
//															phoneInfos2
//																	.add(p);
//														}
//													}
//												}
//
//											}
//										}
//									}
//
//									if(isCunzai){
//										continue;
//									}
//
//									if (phoneList.size() == phoneInfos2
//											.size()) {
//										continue;
//									}
//
//									phoneInfos.removeAll(phoneInfos2);
//
//									ArrayList<PhoneInfo> phoneInfos3 = new ArrayList<PhoneInfo>();
//
//									for (PhoneInfo info : phoneInfos) {
//										if (info.getPhone().length() == 11
//												|| info.getIs_baixing()
//														.length() == 11) {
//											if (!phoneInfos3
//													.containsAll(phoneInfos)) {
//												phoneInfos3
//														.addAll(phoneInfos);
//											}
//											continue;
//										}
//									}
//
//									if (phoneInfos3.size() <= 0) {
//										continue;
//									}
//
//									ArrayList<PhoneInfo> phoneInfos4 = new ArrayList<PhoneInfo>();
//
//									for (PhoneInfo info : phoneInfos3) {
//										if (!phoneInfos4.contains(info)) {
//											phoneInfos4.add(info);
//										}
//										PhoneInfo info2 = new PhoneInfo();
//
//										if (!"".equals(info.getIs_baixing())
//												&& !"0".equals(info
//														.getIs_baixing())) {
//											info2.setId(info.getId());
//											info2.setPhone(info
//													.getIs_baixing());
//											info2.setIs_benben(info
//													.getIs_benben());
//											info2.setPoster(info
//													.getPoster());
//											info2.setNick_name(info
//													.getNick_name());
//											info2.setContacts_id(info
//													.getContacts_id());
//											info2.setHuanxin_username(info
//													.getHuanxin_username());
//											if (!phoneInfos4
//													.contains(info2)) {
//												phoneInfos4.add(info2);
//											}
//										}
//
//									}
//
//									ce = new ContactsEnterprise();
//									if (!enterpriseType.equals("2")) {
//										Collections.sort(phoneInfos4, new Comparator<PhoneInfo>(){
//
//											@Override
//											public int compare(
//													PhoneInfo arg0,
//													PhoneInfo arg1) {
//												Integer phone = arg0.getPhone().length();
//												Integer phone2 = arg1.getPhone().length();
//												return phone2.compareTo(phone);
//											}
//
//										});
//
//
//
//										ce.setPhones(phoneInfos4);
//									} else {
//										Collections.sort(phoneInfos3, new Comparator<PhoneInfo>(){
//
//											@Override
//											public int compare(
//													PhoneInfo arg0,
//													PhoneInfo arg1) {
//												Integer phone = arg0.getPhone().length();
//												Integer phone2 = arg1.getPhone().length();
//												return phone2.compareTo(phone);
//											}
//
//										});
//										ce.setPhones(phoneInfos3);
//									}
//
//									ce.setId(c.getId() + "");
//									ce.setName(c.getName());
//									ce.setChecked(c.isChecked());
//									ce.setGroup_id(c.getGroup_id());
//									ce.setHasPinYin(c.isHasPinYin());
//									ce.setHuanxin_username(c
//											.getHuanxin_username());
//									ce.setIs_baixing(c.getIs_baixing());
//									ce.setIs_benben(c.getIs_benben());
//									ce.setIs_friend(c.getIs_friend());
//									ce.setPhone(c.getPhone());
//									ce.setPinyin(c.getPinyin());
//									ce.setPoster(c.getPoster());
//									ce.setRemark(c.getRemark());
//									contacts.add(ce);
//									group.setmContacts(contacts);
								}
							}
						}

						dissLoding();

						adapter = new vMyAdapter();
						wrapperAdapter = new WrapperExpandableListAdapter(
								adapter);
						listview.setAdapter(wrapperAdapter);

					} catch (DbException e) {
						e.printStackTrace();
						ToastUtils.Infotoast(mContext, "获取数据失败,请重试!");
					}

					return true;
				}
				return false;
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	class vMyAdapter extends BaseExpandableListAdapter {

		@Override
		public ContactsEnterprise getChild(int arg0, int arg1) {
			return contactsGroups.get(arg0).getmContacts().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final ContactsEnterprise virtualMember = getChild(groupPosition,
					childPosition);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_enteraddmember_item, null);

				itemHolder = new ItemHolder();
				itemHolder.phoneBox = (LinearLayout) convertView
						.findViewById(R.id.itemPhoneBox);
				itemHolder.itemName = (TextView) convertView
						.findViewById(R.id.itemName);

				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}
			itemHolder.itemName.setText(virtualMember.getName());
			final ArrayList<PhoneInfo> phones = virtualMember.getPhones();
			itemHolder.phoneBox.removeAllViews();
			for (final PhoneInfo p : phones) {

				View item = LayoutInflater.from(mContext).inflate(
						R.layout.item_eeee, null);

				final CheckBox item_phone_checkbox = (CheckBox) item
						.findViewById(R.id.item_phone_checkbox);
				TextView item_phone = (TextView) item
						.findViewById(R.id.item_phone);

				// LinearLayout.LayoutParams layoutParams = new
				// LinearLayout.LayoutParams(
				// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				// layoutParams.
				item.setPadding(0, PixelUtil.dp2px(5), 0, PixelUtil.dp2px(5));

				if (enterpriseType.equals("1")) {
					item_phone.setText(p.getPhone());
				} else {
					item_phone.setText(p.getIs_baixing());
				}

				OnClickListener clickListener = new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        boolean isMax = false;

                        if (!selectMember.contains(virtualMember)) {
                            if (selectMember.size() < permit) {
                                selectMember.add(virtualMember);
                                selectStatusMember.add(virtualMember);
                                tv_invite_content.setText("(" + selectMember.size() + "/" + permit + ")");
                            } else {
                                ToastUtils.Errortoast(mContext,
                                        "添加人员已达上限");
                                item_phone_checkbox.setChecked(false);
                                isMax = true;
                            }
                        }

                        if (!isMax) {
                            ArrayList<PhoneInfo> selectPhones = virtualMember
                                    .getSelectPhones();

                            if (selectPhones.contains(p)) {

                                selectPhones.remove(p);
                                int num = 0;

                                boolean isSelect = false;
                                for (PhoneInfo info : selectPhones) {
                                    if (!RegexUtils.checkLongPhone(info.getPhone())) {
                                        num += 1;
                                        if (num == selectPhones.size()) {
                                            isSelect = true;
                                        }
                                    }
                                }

                                if (isSelect) {
                                    item_phone_checkbox.setChecked(true);
                                    selectPhones.add(p);
                                    ToastUtils.Errortoast(mContext,
                                            "每个成员最多添加2个号码，其中一个必须是手机长号");
                                    return;
                                } else {
                                    item_phone_checkbox.setChecked(false);
                                    selectPhonesed.remove(p);

                                    if (selectPhones.size() <= 0) {
                                        if (selectMember.contains(virtualMember)) {
                                            selectMember.remove(virtualMember);
                                            tv_invite_content.setText("(" + selectMember.size() + "/" + permit + ")");
                                        }
                                    }
                                }

                            } else {
                                if (enterpriseType.equals("1")) {
                                    if (selectPhones.size() == 0
                                            && !RegexUtils.checkLongPhone(p
                                            .getPhone())
                                            && phones.size() > 1) {

                                        ToastUtils.Errortoast(mContext,
                                                "每个成员最多添加2个号码，其中一个必须是手机长号");
                                        if (selectMember.contains(virtualMember)) {
                                            selectMember.remove(virtualMember);
                                            tv_invite_content.setText("(" + selectMember.size() + "/" + permit + ")");
                                        }
                                        item_phone_checkbox.setChecked(false);
                                        return;

                                    }
                                    if (selectPhones.size() >= 2) {
                                        ToastUtils.Infotoast(mContext, "最多可选2个号码");
                                        item_phone_checkbox.setChecked(false);
                                    } else {
                                        item_phone_checkbox.setChecked(true);
                                        selectPhones.add(p);
                                        selectPhonesed.add(p);
                                    }
                                }else {
                                    if (selectPhones.size() >= 1) {
                                        ToastUtils.Infotoast(mContext, "最多可选1个号码");
                                        item_phone_checkbox.setChecked(false);
                                    } else {
                                        item_phone_checkbox.setChecked(true);
                                        selectPhones.add(p);
                                        selectPhonesed.add(p);
                                    }
                                }
                            }
                        }

                        }
                    };

                    item_phone_checkbox.setChecked(virtualMember.getSelectPhones().contains(p));

                    if(selectPhonesed.contains(p)){
                        item_phone_checkbox.setChecked(true);
                    }else{
                        item_phone_checkbox.setChecked(false);
                    }

                    item_phone_checkbox.setOnClickListener(clickListener);
                    item.setOnClickListener(clickListener);
                    itemHolder.phoneBox.addView(item);


			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

				}

			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return contactsGroups.get(arg0).getmContacts().size();
		}

		@Override
		public ContactsGroupEnterprise getGroup(int arg0) {
			return contactsGroups.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return contactsGroups.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup arg3) {

			final HeaderViewHolder holder;

			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = View.inflate(mContext,
						R.layout.activity_friend_union_hader, null);
				holder.group_name = (TextView) convertView
						.findViewById(R.id.group_name);
				holder.status_img = (ImageView) convertView
						.findViewById(R.id.status_img);
				holder.group_num = (TextView) convertView
						.findViewById(R.id.group_num);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.ll_hader = (LinearLayout) convertView
						.findViewById(R.id.ll_hader);
				holder.iv_edit = (ImageView) convertView
						.findViewById(R.id.iv_edit);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

			holder.iv_edit.setVisibility(View.GONE);
			holder.tv_title.setVisibility(View.GONE);
			holder.group_num.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			holder.group_num.setText("("
					+ getGroup(groupPosition).getmContacts().size() + ")");
			holder.group_name.setText(getGroup(groupPosition).getName());

			if (listview.isGroupExpanded(groupPosition)) {
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
		TextView group_name;
		TextView group_num;
		ImageView status_img;
		TextView tv_title;
		LinearLayout ll_hader;
		ImageView iv_edit;
	}

	class ItemHolder {
		TextView itemName;
		LinearLayout phoneBox;
	}

	private void groupOrderBy() {
		int size = contactsGroups.size();
		// 对组群排序,未分组放在后面
		ContactsGroupEnterprise unGroup = null;
		ContactsGroupEnterprise unGroup2 = null;
		for (int i = 0; i < size; i++) {
			if (contactsGroups.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = contactsGroups.remove(i);
				break;
			}

		}
		if (unGroup != null) {
			contactsGroups.add(unGroup);
		}

		for (int i = 0; i < size; i++) {
			if (contactsGroups.get(i).getName().equalsIgnoreCase("常用号码直通车")) {
				unGroup2 = contactsGroups.remove(i);
				break;
			}
		}

		if (unGroup2 != null) {
			contactsGroups.add(unGroup2);
		}

	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			AnimFinsh();
		}

	}

}
