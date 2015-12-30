package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.android.bbalbs.common.a.b;
import com.baidu.platform.comapi.map.m;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.EnterpriseGroup;
import com.xunao.benben.bean.EnterpriseGroupList;
import com.xunao.benben.bean.EnterpriseMember;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityEnterpriseGroup extends BaseActivity {
	private ListView listView;
	private myAdapter adapter;
	private String enterpriseId;

	private LinearLayout no_data;
	private ArrayList<EnterpriseGroup> enterpriseGroups = new ArrayList<EnterpriseGroup>();
	private ArrayList<EnterpriseMemberDetail> members = new ArrayList<EnterpriseMemberDetail>();
	private EnterpriseGroupList enterpriseGroupList;

	private InputDialog inputDialog;
	private String pecketName;
	private boolean isUpdate = false;
	private static final int GROUP = 1;
	private int index = 0;
	protected static final int EDIT_MEMBER = 1000;
	private memberBroadcast broadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		broadcast = new memberBroadcast();
		registerReceiver(broadcast, new IntentFilter(
				AndroidConfig.refrashGroupMember));
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_group);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("分组管理", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_com_title_add);

		listView = (ListView) findViewById(R.id.listview);
		no_data = (LinearLayout) findViewById(R.id.no_data);

		adapter = new myAdapter();
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});

		Intent intent = getIntent();
		enterpriseId = intent.getStringExtra("enterpriseId");
		type = intent.getStringExtra("type");
		members = (ArrayList<EnterpriseMemberDetail>) intent
				.getSerializableExtra("member");

		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).enterpriseGroupList(
					enterpriseId, mRequestCallBack);
		}
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
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
				inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("添加分组", "请输入新的分组名", "确认", "取消");
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						pecketName = "";
						pecketName = inputDialog.getInputText();
						
						
					//	if(CommonUtils.StringIsSurpass(pecketName, 1, 8))
						
						if(pecketName.length() <= 0){
							ToastUtils.Errortoast(mContext, "分组名称不能为空");
							return;
						}
						
//						if(pecketName.length() > 8){
//							ToastUtils.Errortoast(mContext, "分组限制在1-8个字符之间");
//							return;
//						}
						
						if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
							ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
							return;
						}
						
						isUpdate = false;
						if (CommonUtils.isNetworkAvailable(mContext))
							InteNetUtils.getInstance(mContext)
									.addEnterpriseGroup(enterpriseId,
											pecketName, requestCallBack);

					}
				});
				inputDialog.show();
			}
		});
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(arg0.result);
				if (jsonObject.optString("ret_num").equals("0")) {

					if (isUpdate) {
						ToastUtils.Infotoast(mContext, "修改分组成功");
						enterpriseGroups.get(index).setGroupName(pecketName);
					} else {
						ToastUtils.Infotoast(mContext, "添加分组成功");
						EnterpriseGroup group = new EnterpriseGroup();
						group.setGroupName(pecketName);
						group.setId(jsonObject.optString("group_id"));
						group.setAllNum("0/" + members.size());
						
						ArrayList<EnterpriseGroup> enterpriseGroupss = new ArrayList<EnterpriseGroup>();
						enterpriseGroupss.addAll(enterpriseGroups);
						
						enterpriseGroups.clear();
						
						enterpriseGroups.add(group);
						enterpriseGroups.addAll(enterpriseGroupss);
						
						sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseMember));
					}
					no_data.setVisibility(View.GONE);
					inputDialog.dismiss();
					adapter.notifyDataSetChanged();
				} else {
					
					if(jsonObject.optString("ret_num").equals("2015") ){
						CrashApplication.getInstance().logout();
						startActivity(new Intent(mContext, ActivityLogin.class));
					}else{
						ToastUtils.Infotoast(mContext,
								jsonObject.optString("ret_msg"));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Infotoast(mContext, "网络不可用!");
		}
	};
	private String type;

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		enterpriseGroups.clear();
		try {
			enterpriseGroupList = new EnterpriseGroupList();
			enterpriseGroupList = enterpriseGroupList.parseJSON(jsonObject);
			if (enterpriseGroupList.getEnterpriseGroups().size() <= 0) {
				no_data.setVisibility(View.VISIBLE);
			} else {
				no_data.setVisibility(View.GONE);
				enterpriseGroups.addAll(enterpriseGroupList
						.getEnterpriseGroups());
			}
			adapter.notifyDataSetChanged();
		} catch (NetRequestException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用!");
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return enterpriseGroups.size();
		}

		@Override
		public Object getItem(int arg0) {
			return enterpriseGroups.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			convertView = getLayoutInflater().inflate(
					R.layout.activity_packet_item, null);
			LinearLayout item_layout = (LinearLayout) convertView
					.findViewById(R.id.item_layout);
			LinearLayout item_delete = (LinearLayout) convertView
					.findViewById(R.id.item_delete);

			ImageView changeNameBut = (ImageView) convertView
					.findViewById(R.id.changeNameBut);
			// convertView.findViewById(R.id.addFriend).setVisibility(View.GONE);

			TextView group_name = (TextView) convertView
					.findViewById(R.id.group_name);

			group_name.setText(enterpriseGroups.get(position).getGroupName()
					+ "(" + enterpriseGroups.get(position).getAllNum() + ")");
			
			if(!"未分组".equals(enterpriseGroups.get(position)
					.getGroupName())){
				changeNameBut.setVisibility(View.VISIBLE);
			} else {
				changeNameBut.setVisibility(View.GONE);
			}

            if(enterpriseGroups.size()==1){
                convertView.findViewById(R.id.addFriend).setVisibility(View.GONE);
            }
			

			item_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startAnimActivityForResult(
							ActivityEnterpriseEditGroupMember.class,
							"enterpriseId", enterpriseId, "type", type,
							"contactsGroup", enterpriseGroups.get(position),
							"member", members, EDIT_MEMBER);
				}
			});

			changeNameBut.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (!"未分组".equals(enterpriseGroups.get(position)
							.getGroupName())) {
						inputDialog = new InputDialog(mContext,
								R.style.MyDialogStyle);
						inputDialog.setContent("修改分组名", "请输入新的分组名", "确认", "取消");
						inputDialog.setEditContent(enterpriseGroups.get(
								position).getGroupName());
						inputDialog.setCancleListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								inputDialog.dismiss();
							}
						});
						inputDialog.setOKListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								pecketName = "";
								pecketName = inputDialog.getInputText();
								
//								if(pecketName.length() > 8){
//									ToastUtils.Errortoast(mContext, "分组在8个字以内");
//									return;
//								}
								
								if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
									ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
									return;
								}

								if (TextUtils.isEmpty(pecketName)) {
									ToastUtils.Errortoast(mContext, "请输入分组名!");
									return;
								}

								if (pecketName.equals("未分组")) {
									ToastUtils.Errortoast(mContext,
											"分组名不能使用未分组!");
									return;
								}

								if (CommonUtils.isNetworkAvailable(mContext)) {
									index = position;
									isUpdate = true;
									InteNetUtils
											.getInstance(mContext)
											.updateEnterpriseGroup(
													enterpriseGroups.get(
															position).getId(),
													pecketName, requestCallBack);

								}
							}
						});
						inputDialog.show();
					}
				}
			});

			item_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					startAnimActivityForResult8(
							ActivityDeleteEnterpriseGroup.class, "group",
							enterpriseGroups.get(position), "groupList",
							enterpriseGroups, "member", members,
							"enterpriseId", enterpriseId, GROUP);
				}
			});

			// item_layout.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View arg0) {
			//
			// }
			// });

			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		switch (arg0) {
		case GROUP:
			if (data != null) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					enterpriseGroups.clear();
					InteNetUtils.getInstance(mContext).enterpriseGroupList(
							enterpriseId, mRequestCallBack);
					user.setUpdate(true);
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(arg0, arg1, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcast);
	}

	class memberBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				InteNetUtils.getInstance(mContext).enterpriseGroupList(
						enterpriseId, mRequestCallBack);
			}
		}

	}

}
