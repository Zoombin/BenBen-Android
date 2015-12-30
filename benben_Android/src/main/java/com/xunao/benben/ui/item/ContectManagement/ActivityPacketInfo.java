package com.xunao.benben.ui.item.ContectManagement;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smackx.ping.packet.Pong;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
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
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityContactsInfo;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

//实现Serializable接口  方便传递
public class ActivityPacketInfo extends BaseActivity implements
		OnClickListener, ActionSheetListener, Serializable {

	// 选中的组
	private ContactsGroup contactsGroup;

	private ContactsObject contactsObject;

	private ArrayList<ContactsGroup> mContactsGroups;

	// 显示用的arraylist
	private ArrayList<Contacts> listContacts;

	private ListView listview;
	private MyAdapter myAdapter = null;

	// 用于群发短信的号码合集
	private String phoneNum;

	private InputDialog inputDialog;
	private String pecketName;

	private PacketInfoBroadCast mPacketInfoBroadCast;
    private boolean isDelete = false;
    private LinearLayout ll_all;
    private CheckBox all_checkbox;
    private TextView tv_delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPacketInfoBroadCast = new PacketInfoBroadCast();
		registerReceiver(mPacketInfoBroadCast, new IntentFilter(
				AndroidConfig.refreshPackageInfo));
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_packet_info);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				Boolean ispost = (Boolean) cubeImageView
						.getTag(R.string.ispost);
				if (cubeImageView != null) {
					cubeImageView.setImageResource(R.drawable.default_face);
				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null) {
					if (imageTask.getIdentityUrl().equalsIgnoreCase(
							(String) cubeImageView.getTag())) {

						cubeImageView.setVisibility(View.VISIBLE);
						cubeImageView.setImageDrawable(drawable);

					}

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					Boolean ispost = (Boolean) imageView
							.getTag(R.string.ispost);
					imageView.setImageResource(R.drawable.default_face);
				}
			}
		});
		initTitle_Right_Left_bar("", "", "", R.drawable.icon_com_title_left,
				R.drawable.icon_com_title_more);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		listview = (ListView) findViewById(R.id.listview);
        ll_all = (LinearLayout)findViewById(R.id.ll_all);
        all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
        all_checkbox.setOnCheckedChangeListener(changeListener);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
	}

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean checked) {
            if (checked) {
                for (Contacts contact : listContacts) {
                    contact.setChecked(true);
                }
            } else {
                for (Contacts contact : listContacts) {
                    contact.setChecked(false);
                }
            }
            myAdapter.notifyDataSetChanged();
        }
    };

	@Override
	public void initDate(Bundle savedInstanceState) {
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

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

	@Override
	protected void onResume() {
		super.onResume();
		initLocalData();
	}

	public void initLocalData() {

		contactsGroup = mApplication.contactsGroup;

		try {
			List<Contacts> findAllgroup = dbUtil.findAll(Selector
					.from(Contacts.class)
					.where("group_id", "=", "" + contactsGroup.getId())
					.orderBy("pinyin", false));
			contactsGroup.setmContacts((ArrayList<Contacts>) findAllgroup);
			List<Contacts> findAllContacts = dbUtil.findAll(Selector
					.from(Contacts.class).where("group_id", "!=", "10000")
					.orderBy("pinyin", false));
			mApplication.contactsObjectManagement
					.setmContactss((ArrayList<Contacts>) findAllContacts);
		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		contactsObject = mApplication.contactsObjectManagement;

		mContactsGroups = contactsObject.getmContactsGroups();

		phoneNum = "";
		// 获得分组下的所有成员 和成员id
		listContacts = contactsGroup.getmContacts();

		// 通过成员id 查找出所有的手机号码
		try {
			List<PhoneInfo> phones = dbUtil.findAll(PhoneInfo.class);
			if (phones != null) {

				for (PhoneInfo phoneInfo : phones) {
					for (Contacts cn : listContacts) {
						if (cn.getId()==phoneInfo.getContacts_id()) {
							cn.getPhones().add(phoneInfo);
							phoneNum += phoneInfo.getPhone() + ";";
						}
					}
				}
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (phoneNum.length() > 0) {
			phoneNum = phoneNum.substring(0, phoneNum.length() - 1);
		}
		initTitle_Right_Left_bar(contactsGroup.getName(), "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_com_title_more);

		// 找出有拼音出现的位置
		if (listContacts != null) {
			int pinyin = -1;
			// int pinyin = listContacts.get(0).getPinyin().charAt(0);
			for (int i = 0; i < listContacts.size(); i++) {
				int j = listContacts.get(i).getPinyin().charAt(0);
				listContacts.get(i).setHasPinYin(false);
				if (j != pinyin) {
					pinyin = j;
					listContacts.get(i).setHasPinYin(true);
				}
			}
			myAdapter = new MyAdapter();
			listview.setAdapter(myAdapter);

//			listview.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					Intent intent = new Intent(mContext,
//							ActivityContactsInfo.class);
//					intent.putExtra("contacts", myAdapter.getItem(arg2));
//					startActivityForResult(intent,
//							AndroidConfig.ContactsFragmentRequestCode);
//					mContext.overridePendingTransition(R.anim.in_from_right,
//							R.anim.out_to_left);
//				}
//			});

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            // 头部左侧点击
            case R.id.com_title_bar_left_bt:
            case R.id.com_title_bar_left_tv:
                onbackupdata();
                break;

            // 右侧
            case R.id.com_title_bar_right_bt:
            case R.id.com_title_bar_right_tv:
                if(isDelete) {
                    ll_all.setVisibility(View.GONE);
                    initTitle_Right_Left_bar(contactsGroup.getName(), "", "", R.drawable.icon_com_title_left,
                            R.drawable.icon_com_title_more);
                    isDelete = false;
                    myAdapter.notifyDataSetChanged();
                }else{
                    setTheme(R.style.ActionSheetStyleIOS7);
                    showActionSheet();
                }
                break;
            case R.id.tv_delete:
                String ids = "";
                for (Contacts contact : listContacts) {
                    if(contact.isChecked()){
                        ids += contact.getId()+";";
                    }
                }
                if(ids.equals("")){
                    ToastUtils.Infotoast(this,"请选择需要删除联系人！");
                }else {
                    this.showLoding("删除中...");
                    ids=ids.substring(0,ids.length()-1);
                    InteNetUtils.getInstance(mContext).deleteContact(ids,
                            new RequestCallBack<String>() {

                                @Override
                                public void onFailure(
                                        HttpException arg0, String arg1) {
                                    ActivityPacketInfo.this.dissLoding();
                                    ToastUtils.Errortoast(mContext,
                                            "删除联系人失败");
                                }

                                @Override
                                public void onSuccess(
                                        ResponseInfo<String> arg0) {
                                    ActivityPacketInfo.this.dissLoding();
                                    isDelete = false;
                                    for (Contacts contact : listContacts) {
                                        if(contact.isChecked()){
                                            try {
                                                EMChatManager.getInstance().clearConversation(contact.getHuanxin_username());
                                                dbUtil.delete(
                                                        Contacts.class,
                                                        WhereBuilder
                                                                .b("id",
                                                                        "=",
                                                                        contact
                                                                                .getId()));
                                                dbUtil.delete(
                                                        PhoneInfo.class,
                                                        WhereBuilder
                                                                .b("contacts_id",
                                                                        "=",
                                                                        contact
                                                                                .getId()));
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }
                                            // setResult(AndroidConfig.writeFriendRefreshResultCode);
                                            mApplication.mContactsMap
                                                    .remove(contact
                                                            .getHuanxin_username());
                                            EMChatManager.getInstance().deleteConversation(contact
                                                    .getHuanxin_username(), true);
                                        }
                                    }
                                    Intent intent = new Intent();
                                    intent.setAction(AndroidConfig.Finsh);

                                    intent.putExtra("type",
                                            100);
                                    sendBroadcast(intent);
                                    sendBroadcast(new Intent(
                                            AndroidConfig.ContactsRefresh));
                                    sendBroadcast(new Intent(
                                            AndroidConfig.refreshActivityCaptureContactsInfo));

                                    ll_all.setVisibility(View.GONE);
                                    all_checkbox.setChecked(false);
                                    initLocalData();
                                }
                            });
                }
                break;

            default:
                break;
		}

	}

	public void showActionSheet() {
		ActionSheet
				.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("群发短信", "修改组名","批量删除")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF", "#1E82FF",
						"#e81d00").setCancelableOnTouchOutside(true)
				.setListener(this).show();
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
		// Toast.makeText(getApplicationContext(), "dismissed isCancle = " +
		// isCancel, 0).show();
	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index) {
		case 0:
			PhoneUtils.sendSMS(phoneNum, "", this);
			break;
		case 1:
			// mApplication.contactsGroup = contactsGroup;
			// startAnimActivity(ActivityPacketInfoManagement.class);
			// break;
			// case 2:
			if (!contactsGroup.getName().equals("未分组")) {
				inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("修改分组名", "请输入新的分组名", "确认", "取消");
				inputDialog.setEditContent(contactsGroup.getName());
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						pecketName = inputDialog.getInputText();
						
						if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
							ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
							return;
						}
						
						if (CommonUtils.isNetworkAvailable(mContext))
							InteNetUtils.getInstance(mContext).EditPacket(
									pecketName, contactsGroup.getId() + "",
									changeName);
					}
				});
				inputDialog.show();
			}else{
                ToastUtils.Infotoast(mContext, "该组不能改名");
            }
			break;
        case 2:// 批量删除
            isDelete = true;
            initTitle_Right_Left_bar(contactsGroup.getName(), "", "取消", R.drawable.icon_com_title_left,
                    0);
            ll_all.setVisibility(View.VISIBLE);
            myAdapter.notifyDataSetChanged();
            break;
		// default:
		// break;
		}
	}

	ContactsHold contactsHold;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listContacts.size();
		}

		@Override
		public Contacts getItem(int arg0) {
			return listContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			contactsHold = new ContactsHold();
			final Contacts contacts = listContacts.get(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_packageinfo, null);
				contactsHold.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				contactsHold.item_phone_poster = (CubeImageView) convertView
						.findViewById(R.id.item_phone_poster);
				contactsHold.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
                contactsHold.item_phone_checkbox = (CheckBox) convertView
                        .findViewById(R.id.item_phone_checkbox);
				convertView.setTag(contactsHold);
			} else {
				contactsHold = (ContactsHold) convertView.getTag();
			}

            if(isDelete){
                contactsHold.item_phone_checkbox.setVisibility(View.VISIBLE);
                contactsHold.item_phone_checkbox.setChecked(contacts.isChecked());
            }else{
                contactsHold.item_phone_checkbox.setVisibility(View.GONE);
            }

			String poster = contacts.getPoster();
			if (!TextUtils.isEmpty(poster)) {
				CommonUtils.startImageLoader(cubeimageLoader, poster,
						contactsHold.item_phone_poster);
			} else {
				CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
						contactsHold.item_phone_poster);
			}
			StringBuffer text = new StringBuffer();
			text.append(contacts.getName()+"  ");
			for (PhoneInfo p : contacts.getPhones()) {
				text.append(p.getPhone() + ",");
			}
			if (text.length() > 0) {
				contactsHold.item_phone_name.setText(text.substring(0,
						text.length() - 1));
			}

			contactsHold.item_pinyin.setText(contacts.getPinyin().charAt(0)
					+ "");

			if (contacts.isHasPinYin()) {
				contactsHold.item_pinyin.setVisibility(View.VISIBLE);
			} else {
				contactsHold.item_pinyin.setVisibility(View.GONE);
			}

            // item的点击事件
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if(isDelete) {
                        if (contacts.isChecked()) {
                            contacts.setChecked(false);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            contacts.setChecked(true);
                            myAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Intent intent = new Intent(mContext,
							ActivityContactsInfo.class);
					intent.putExtra("contacts", contacts);
					startActivityForResult(intent,
							AndroidConfig.ContactsFragmentRequestCode);
					mContext.overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
                    }
                }
            });

			return convertView;
		}
	}

	class ContactsHold {
		TextView item_pinyin;
		CubeImageView item_phone_poster;
		TextView item_phone_name;
        CheckBox item_phone_checkbox;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (AndroidConfig.PacketManagementResultCodeInfo == resultCode) {
			initLocalData();
			myAdapter.notifyDataSetChanged();
		}
		if (AndroidConfig.PacketManagementResultCode == resultCode) {
			setResult(AndroidConfig.PacketManagementResultCode);
			AnimFinsh();
		}
	}

	public void onbackupdata() {
		setResult(AndroidConfig.PacketManagementResultCode);
		AnimFinsh();
	}

	@Override
	public void onBackPressed() {
		onbackupdata();
	}

	/**
	 * 修改分组名
	 */
	public RequestCallBack<String> changeName = new RequestCallBack<String>() {

		public void onStart() {
		};

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(arg0.result);
				SuccessMsg msg = new SuccessMsg();
				msg.checkJson(jsonObject);

				ContactsGroup cg = mApplication.mContactsGroupMap
						.get(contactsGroup.getId());
				if (cg != null) {
					cg.setName(pecketName);
					// 更新数据库
					dbUtil.saveOrUpdate(cg);
				}
				inputDialog.dismiss();
				contactsGroup.setName(pecketName);
				// 修改头部组名
				changeTitileContent(pecketName);

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (DbException e) {
				e.printStackTrace();
			} catch (NetRequestException e) {
				e.getError().print(mContext);
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Errortoast(mContext, arg1);
			inputDialog.dismiss();
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mPacketInfoBroadCast);
	};

	class PacketInfoBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			initLocalData();
		}

	}

}
