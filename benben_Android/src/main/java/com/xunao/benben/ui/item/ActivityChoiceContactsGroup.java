package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityChoiceContactsGroup extends BaseActivity implements
        OnClickListener{
	private String groupId;
	private ListView listview;
	private myAdapter adapter;
	private String targetGroupId = "";
	private int memberId;
	private Contacts contacts;
    private InputDialog inputDialog;
    private String pecketName;

	private ArrayList<ContactsGroup> contactsGroups = new ArrayList<ContactsGroup>();

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_change_contacts_group);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("选择分组", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_com_title_add);

		listview = (ListView) findViewById(R.id.listview);
		adapter = new myAdapter();
		listview.setAdapter(adapter);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		groupId = getIntent().getStringExtra("groupId");
		contacts = (Contacts) getIntent().getSerializableExtra("contacts");
		memberId = contacts.getId();
		initData();
	}

	private void initData() {
		try {
			List<ContactsGroup> list = dbUtil.findAll(Selector
					.from(ContactsGroup.class)
					.where(WhereBuilder.b("id", "!=", groupId))
					.and("id", "!=", "10000"));
			if ( list.size() == 0) {
                ToastUtils.Infotoast(mContext, "当前没有其他分组!");
            }
				contactsGroups = (ArrayList<ContactsGroup>) list;
				adapter.notifyDataSetChanged();
//			} else {
//				ToastUtils.Infotoast(mContext, "您还没有创建分组!");
//				AnimFinsh();
//			}

			// contacts = dbUtil.findById(Contacts.class, memberId);
		} catch (DbException e) {
			e.printStackTrace();
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

        setOnRightClickLinester(this);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int positon, long arg3) {
				targetGroupId = contactsGroups.get(positon).getId()+"";
				if (CommonUtils.isNetworkAvailable(mContext)) {
					InteNetUtils.getInstance(mContext).EditPacketInfoGroup(
							targetGroupId, contacts.getId()+"", mRequestCallBack);
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
			ToastUtils.Infotoast(mContext, "移动分组成功!");
			Contacts findById = null;
			try {
				findById = dbUtil.findById(Contacts.class, memberId);
				findById.setGroup_id(targetGroupId);
				dbUtil.saveOrUpdate(findById);
			} catch (DbException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent();
			intent.setAction(AndroidConfig.refreshContactsGroup);
			intent.putExtra("group", findById);
			sendBroadcast(intent);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用!");
	}

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // 头部右侧点击
            case R.id.com_title_bar_right_bt:
            case R.id.com_title_bar_right_tv:
                // 添加分组
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

                        pecketName = inputDialog.getInputText();

                        if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
                            ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
                            return;
                        }
//					if(pecketName.length() > 8){
//					}

                        if ("未分组".equals(pecketName)) {
                            ToastUtils.Infotoast(mContext, "该分组已存在");
                        } else {
                            inputDialog.dismiss();
                            if (CommonUtils.isNetworkAvailable(mContext))
                                InteNetUtils.getInstance(mContext).AddPacket(
                                        pecketName, addGroup);
                        }

                    }
                });
                inputDialog.show();
                break;
        }
    }

    class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return contactsGroups.size();
		}

		@Override
		public Object getItem(int arg0) {
			return contactsGroups.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parents) {
			ContactsGroup group = contactsGroups.get(position);
			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.item_edit_contacts_unshort, null);
			}

			ImageView delete_but = (ImageView) converView
					.findViewById(R.id.delete_but);
			MyTextView item_phone_name = (MyTextView) converView
					.findViewById(R.id.item_phone_name);
			item_phone_name.setText(group.getName());
			delete_but.setVisibility(View.GONE);
			return converView;
		}

	}


    /**
     * 新增分组
     */
    public RequestCallBack<String> addGroup = new RequestCallBack<String>() {

        public void onStart() {
        };

        @Override
        public void onSuccess(ResponseInfo<String> arg0) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(arg0.result);
                SuccessMsg msg = new SuccessMsg();
                msg.parseJSON(jsonObject);

                String group_id = jsonObject.optString("group_id");

                ContactsGroup contactsGroup = new ContactsGroup();
                contactsGroup.setId(Integer.parseInt(group_id));
                contactsGroup.setName(pecketName);
                contactsGroup.setProportion("0/0");
                contactsGroup.setSort(contactsGroups.size()+1);

                contactsGroups.add(contactsGroup);
                mApplication.contactsObject.getmContactsGroups().add(contactsGroup);
                mApplication.mContactsGroupMap.put(contactsGroup.getId() + "",
                        contactsGroup);
                adapter.notifyDataSetChanged();
                // 保存到本地
                dbUtil.save(contactsGroup);
                sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
                inputDialog.dismiss();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DbException e) {
                // TODO Auto-generated catch block
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

}
