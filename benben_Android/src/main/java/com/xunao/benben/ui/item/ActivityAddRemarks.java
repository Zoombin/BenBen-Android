package com.xunao.benben.ui.item;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONObject;

import android.R.color;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsEnterprise;
import com.xunao.benben.bean.EnterpriseMember;
import com.xunao.benben.bean.EnterpriseVirtualMember;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.item.ActivityEnterpriseInviteMember.ItemHolder;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityAddRemarks extends BaseActivity {
	private String enterpriseId;
	private ArrayList<ContactsEnterprise> contacts = new ArrayList<ContactsEnterprise>();
	private ArrayList<ContactsEnterprise> virtualMembers = new ArrayList<ContactsEnterprise>();
	private int type = 0;
	private ListView listView;
	private myAdapter adapter;
	private InputDialog inputDialog;
	private String pecketName;
	private boolean isCheck = true;
	private String[] pinbiArray;
	private int pinbiNumber = 0;
	private ArrayList<Integer> minganNum = new ArrayList<Integer>();
	private ArrayList<ContactsEnterprise> pinContacts = new ArrayList<ContactsEnterprise>();
	private ArrayList<Integer> chaoguoNum = new ArrayList<Integer>();

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_remarks);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("修改姓名", "", "完成",
				R.drawable.icon_com_title_left, 0);

		listView = (ListView) findViewById(R.id.listView);
		adapter = new myAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		enterpriseId = getIntent().getStringExtra("enterpriseId");
		contacts = (ArrayList<ContactsEnterprise>) getIntent()
				.getSerializableExtra("contacts");

		virtualMembers = (ArrayList<ContactsEnterprise>) getIntent()
				.getSerializableExtra("virtualMembers");

		if (contacts != null && virtualMembers == null) {
			type = 1;
			int position = 0;
			for(ContactsEnterprise con : contacts){
				con.setRemark(con.getName());
				if (RegexUtils.minganciCheck3(con.getName())) {
					if (!pinContacts.contains(con)) {
						pinContacts.add(con);
						minganNum.add(position);
					}
				}
				
				if(!CommonUtils.StringIsSurpass2(con.getName(), 2, 10)){
					chaoguoNum.add(position);
				}
				position += 1;
				
			}
		}

		if (contacts == null && virtualMembers != null) {
			type = 2;
			int position = 0;
			for(ContactsEnterprise con : virtualMembers){
				con.setRemark(con.getName());
				if (RegexUtils.minganciCheck3(con.getName())) {
					if (!pinContacts.contains(con)) {
						pinContacts.add(con);
						minganNum.add(position);
					}
				}
				
				if(!CommonUtils.StringIsSurpass2(con.getName(), 2, 10)){
					chaoguoNum.add(position);
				}
				position += 1;
			}
		}
		
		
		
		
		
		adapter.notifyDataSetChanged();

		// InputStream inputStream =
		// getResources().openRawResource(R.raw.pinbi);
		// String pinbiTxt = CommonUtils.getString(inputStream);
		// pinbiArray = pinbiTxt.split("\\|");

		// ToastUtils.Infotoast(mContext, pinbiTxt);
		
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
				String contactId = "";
				String remarkName = "";

				// if (!isCheck) {
				// ToastUtils.Errortoast(mContext, "请先修改红色非法文字");
				// return;
				// }

				if (type == 1) {
					for (ContactsEnterprise contact : contacts) {
						if (TextUtils.isEmpty(contact.getRemark())) {
							ToastUtils.Errortoast(mContext, "备注名不能为空!");
							return;
						}
						
						if(chaoguoNum.size() > 0){
							ToastUtils.Errortoast(mContext, "名称限制在1-10个字之间");
							listView.setSelection(chaoguoNum.get(0));
							return;
						}
						
						remarkName += contact.getId() + ":"
								+ contact.getRemark() + ":";

						for (PhoneInfo p : contact.getSelectPhones()) {
							remarkName += p.getPhone() + ":";
						}
						remarkName = remarkName.substring(0,
								remarkName.length() - 1)
								+ "||";
					}
					if (pinContacts.size() > 0) {
						ToastUtils.Errortoast(mContext, "请先修改红色非法文字");
//						listView.scrollListBy(minganNum.get(0));
						pinbiNumber = minganNum.get(0);
						listView.setSelection(minganNum.get(0));
//						adapter.notifyDataSetChanged();
						return;
					}

					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext)
								.enterpriseVInviteFriend(
										enterpriseId,
										remarkName.substring(0,
												remarkName.length() - 2), "",
										mRequestCallBack);
					}
				} else {

					for (ContactsEnterprise member : virtualMembers) {
						if (TextUtils.isEmpty(member.getRemark())) {
							ToastUtils.Errortoast(mContext, "备注名不能为空!");
							return;
						}
						
						if(chaoguoNum.size() > 0){
							ToastUtils.Errortoast(mContext, "名称限制在1-10个字之间");
							listView.setSelection(chaoguoNum.get(0));
							return;
						}

						remarkName += member.getName() + ":";
						for (PhoneInfo p : member.getSelectPhones()) {
							remarkName += p.getIs_baixing() + ":";
						}
						remarkName = remarkName.substring(0,
								remarkName.length() - 1)
								+ "||";
					}

					if (pinContacts.size() > 0) {
						ToastUtils.Errortoast(mContext, "请先修改红色非法文字");
						pinbiNumber = minganNum.get(0);
						listView.setSelection(minganNum.get(0));
//						listView.scrollListBy(minganNum.get(0));
//						adapter.notifyDataSetChanged();
						return;
					}

					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext)
								.enterpriseVInviteFriendshort(
										enterpriseId,
										remarkName.substring(0,
												remarkName.length() - 2), "",
										mRequestCallBack);
					}

				}
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
		if (jsonObject.optString("ret_num").equals("0")) {
			ToastUtils.Infotoast(mContext, "添加新成员成功!");

			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseMember));
			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseList));
			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseDetail));

			Intent intent = new Intent();
			setResult(2, intent);
			AnimFinsh();
		} else {
			if(jsonObject.optString("ret_num").equals("2015") ){
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
						mContext, R.style.MyDialog1);
				hint.setContent("奔犇账号在其他手机登录");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});
				CrashApplication.getInstance().logout();
				startActivity(new Intent(mContext, ActivityLogin.class));
			}else{
				ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (type == 1) {
				return contacts.size();
			} else {
				return virtualMembers.size();
			}
		}

		@Override
		public Object getItem(int arg0) {
			if (type == 1) {
				return contacts.get(arg0);
			} else {
				return virtualMembers.get(arg0);
			}

		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {

			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_add_remark_item, null);

				itemHolder = new ItemHolder();
				itemHolder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				itemHolder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				itemHolder.tv_remark_name = (TextView) convertView
						.findViewById(R.id.tv_remark_name);
				itemHolder.rl_nick_name = (RelativeLayout) convertView
						.findViewById(R.id.rl_nick_name);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}
			itemHolder.tv_remark_name.setFocusable(false);

			if (type == 1) {
				final ContactsEnterprise contact = contacts.get(position);
				itemHolder.tv_name.setText(contact.getName() + ":");

				StringBuffer buffer = new StringBuffer();
				for (PhoneInfo p : contact.getSelectPhones()) {
					buffer.append(p.getPhone() + ",");
				}

				if (buffer.length() > 1) {
					itemHolder.tv_phone.setText(buffer.substring(0,
							buffer.length() - 1));
				} else {
					itemHolder.tv_phone.setText("");

				}
				itemHolder.tv_remark_name.setText(contact.getName());
				contact.setRemark(contact.getName());

				if (contact.getName().length() > 0) {

					itemHolder.tv_remark_name.setText(Html.fromHtml(RegexUtils
							.minganciCheck2(contact.getName())));
				} else {
					itemHolder.tv_remark_name.setText(Html.fromHtml(contact
							.getName()));
				}

//				if (RegexUtils.minganciCheck3(contact.getName())) {
//					if (!pinContacts.contains(contact)) {
//						pinContacts.add(contact);
//						minganNum.add(position + "");
//					}
//				}

				itemHolder.rl_nick_name
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								inputDialog = new InputDialog(mContext,
										R.style.MyDialogStyle);
								inputDialog.setContent("真实姓名", "请填写真实姓名", "确认",
										"取消");
								inputDialog.setEditContent(contact.getName());
								inputDialog
										.setCancleListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												inputDialog.dismiss();
											}
										});
								inputDialog
										.setOKListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												pecketName = "";
												pecketName = inputDialog
														.getInputText();

//												if (pecketName.length() > 10) {
//													ToastUtils.Errortoast(
//															mContext,
//															"姓名在10个字以内!");
//													return;
//												}
												
												if(!CommonUtils.StringIsSurpass2(pecketName, 2, 10)){
													if(!chaoguoNum.contains(position)){
														chaoguoNum.add(new Integer(position));
													}
													ToastUtils.Errortoast(mContext, "名称限制在1-10个字之间");
													return;
												}else{
													if(chaoguoNum.contains(position)){
														chaoguoNum.remove(new Integer(position));
													}
												}
												
												if("".equals(pecketName)){
													ToastUtils.Errortoast(mContext, "名字不能为空");
													return;
												}

												itemHolder.tv_remark_name.setText(Html.fromHtml(RegexUtils
														.minganciCheck2(pecketName)));

												if (RegexUtils
														.minganciCheck3(pecketName)) {
													if (!pinContacts
															.contains(contact)) {
														contact.setRemark(pecketName);
														contact.setName(pecketName);
														pinContacts
																.add(contact);
														if(!minganNum.contains(position)){
															minganNum.add(new Integer(position));
														}
														
													}
												} else {
													if (pinContacts
															.contains(contact)) {
														contact.setRemark(pecketName);
														contact.setName(pecketName);
														pinContacts
																.remove(contact);
														
														if(minganNum.contains(pinbiNumber)){
															minganNum.remove(new Integer(pinbiNumber));
														}
														
														
														if(minganNum.size() > 0){
															pinbiNumber = minganNum.get(0);
															listView.setSelection(pinbiNumber);
														}
														
													}
												}

												contact.setRemark(pecketName);
												contact.setName(pecketName);
												inputDialog.dismiss();
											}
										});
								inputDialog.show();
							}
						});
			} else {
				final ContactsEnterprise virtualMember = virtualMembers
						.get(position);
				itemHolder.tv_name.setText(virtualMember.getName() + ":");
				StringBuffer buffer = new StringBuffer();
				for (PhoneInfo p : virtualMember.getSelectPhones()) {
					buffer.append(p.getIs_baixing() + ",");
				}
				virtualMember.setRemark(virtualMember.getName());


				itemHolder.tv_phone.setText(buffer.substring(0,
						buffer.length() - 1));

				itemHolder.tv_remark_name.setText(Html.fromHtml(RegexUtils
						.minganciCheck2(virtualMember.getName())));

				itemHolder.rl_nick_name
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								inputDialog = new InputDialog(mContext,
										R.style.MyDialogStyle);
								inputDialog.setContent("添加姓名", "请输入姓名", "确认",
										"取消");

								inputDialog.setEditContent(virtualMember
										.getName());
								inputDialog
										.setCancleListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												inputDialog.dismiss();
											}
										});
								inputDialog
										.setOKListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												pecketName = "";
												pecketName = inputDialog
														.getInputText();
												
												if("".equals(pecketName)){
													ToastUtils.Errortoast(mContext, "名字不能为空");
													return;
												}

//												if (pecketName.length() > 10) {
//													ToastUtils.Errortoast(
//															mContext,
//															"姓名在10个字以内!");
//													return;
//												}
												
												if(!CommonUtils.StringIsSurpass2(pecketName, 2, 10)){
													if(!chaoguoNum.contains(position)){
														chaoguoNum.add(new Integer(position));
													}
													ToastUtils.Errortoast(mContext, "名称限制在1-10个字之间");
													return;
												}else{
													if(chaoguoNum.contains(position)){
														chaoguoNum.remove(new Integer(position));
													}
												}


												itemHolder.tv_remark_name.setText(Html.fromHtml(RegexUtils
														.minganciCheck2(pecketName)));

												virtualMember
														.setRemark(pecketName);
												virtualMember.setName(pecketName);
												inputDialog.dismiss();
											}
										});
								inputDialog.show();
							}
						});

			}
			return convertView;
		}
	}

	class ItemHolder {
		EditText et_remark;
		TextView tv_phone;
		CheckBox item_phone_checkbox;
		TextView tv_name;
		TextView tv_remark_name;
		TextView item_phone_phone;
		RelativeLayout rl_nick_name;
	}

}
