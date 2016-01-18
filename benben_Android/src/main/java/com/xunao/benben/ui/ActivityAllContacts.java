package com.xunao.benben.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

//实现Serializable接口  方便传递
public class ActivityAllContacts extends BaseActivity implements
		OnClickListener, Serializable {
    private LinearLayout ll_top;
	// 显示用的arraylist
	private List<Contacts> listContacts;
	private ListView listview;
	private MyAdapter myAdapter = null;
	private PacketInfoBroadCast mPacketInfoBroadCast;
    private LinearLayout ll_seach_icon;
    private EditText search_edittext;
    private ImageView iv_search_content_delect;
    private LinearLayout no_data;
    private MsgDialog msgDialog;
    private LatelyLinkeMan latelyLinkeMan;


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
		initTitle_Right_Left_bar("联系人", "", "", R.drawable.icon_com_title_left,
				0);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        ll_top.setVisibility(View.VISIBLE);
        ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		listview = (ListView) findViewById(R.id.listview);
        no_data = (LinearLayout) findViewById(R.id.no_data);
	}


	@Override
	public void initDate(Bundle savedInstanceState) {
        latelyLinkeMan = (LatelyLinkeMan) getIntent().getSerializableExtra("latelyLinkeMan");
	}

    int curSearchNum;
	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
        search_edittext.setImeOptions(EditorInfo.IME_ACTION_DONE);

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

            private String s;

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
                final int last = ++curSearchNum;
                if (arg0.length() > 0) {
                    ll_seach_icon.setVisibility(View.GONE);
                    iv_search_content_delect.setVisibility(View.VISIBLE);
                }else {
                    ll_seach_icon.setVisibility(View.VISIBLE);
                    iv_search_content_delect.setVisibility(View.GONE);
                }
                    s = arg0.toString();
                    new Thread() {
                        public void run() {
                            if (s.length() > 0) {
                                // 更新关键字
                                listContacts.clear();
                                try {
                                    List list = new ArrayList<Contacts>();
                                    list = dbUtil.findAll(Selector
                                            .from(Contacts.class)
                                            .where(WhereBuilder
                                                    .b("name", "like",
                                                            "%" + s + "%")
                                                    .or("pinyin", "like",
                                                            "%" + s + "%")
                                                    .or("is_baixing", "like",
                                                            "%" + s + "%")
                                                    .or("is_benben", "like",
                                                            "%" + s + "%")
                                                    .and("group_id", "!=", "10000")
                                            )
                                            .orderBy("pinyin", false));

                                    List<PhoneInfo> list2 = dbUtil
                                            .findAll(Selector
                                                    .from(PhoneInfo.class)
                                                    .where(WhereBuilder
                                                            .b("phone",
                                                                    "like",
                                                                    "%"
                                                                            + s
                                                                            + "%")
                                                            .or("is_baixing",
                                                                    "like",
                                                                    "%"
                                                                            + s
                                                                            + "%")
                                                            .or("is_benben",
                                                                    "like",
                                                                    "%"
                                                                            + s
                                                                            + "%")
                                                            .and("contacts_id", "!=", "0")));

                                    for (PhoneInfo p : list2) {
                                        list.addAll(dbUtil.findAll(Selector
                                                .from(Contacts.class)
                                                .where(WhereBuilder.b("id",
                                                        "=", p.getContacts_id()))));
                                    }

                                    list = CommonUtils.getNewList(list);

                                    if (last == curSearchNum) {
                                        listContacts = (ArrayList<Contacts>) list;
                                        mContext.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (listContacts.size() > 0) {
                                                    int pinyin = -1;
                                                    // int pinyin = listContacts.get(0).getPinyin().charAt(0);
                                                    for (int i = 0; i < listContacts.size(); i++) {
                                                        if(listContacts.get(i).getPinyin().length()!=0) {
                                                            int j = listContacts.get(i).getPinyin().charAt(0);
                                                            listContacts.get(i).setHasPinYin(false);
                                                            if (j != pinyin) {
                                                                pinyin = j;
                                                                listContacts.get(i).setHasPinYin(true);
                                                            }
                                                        }
                                                    }
                                                    myAdapter.notifyDataSetChanged();
                                                    no_data.setVisibility(View.GONE);
                                                } else {
                                                    no_data.setVisibility(View.VISIBLE);
                                                }

                                            }
                                        });
                                    }
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }.start();

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
		try {
            listContacts = dbUtil.findAll(Selector
					.from(Contacts.class)
                    .where("group_id", "!=", "10000")
					.orderBy("pinyin", false));
		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 找出有拼音出现的位置
		if (listContacts != null) {
			int pinyin = -1;
			// int pinyin = listContacts.get(0).getPinyin().charAt(0);
			for (int i = 0; i < listContacts.size(); i++) {
                if(listContacts.get(i).getPinyin().length()!=0) {
                    int j = listContacts.get(i).getPinyin().charAt(0);
                    listContacts.get(i).setHasPinYin(false);
                    if (j != pinyin) {
                        pinyin = j;
                        listContacts.get(i).setHasPinYin(true);
                    }
                }
			}
			myAdapter = new MyAdapter();
			listview.setAdapter(myAdapter);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            // 头部左侧点击
            case R.id.com_title_bar_left_bt:
            case R.id.com_title_bar_left_tv:
                onBackPressed();
                break;



            default:
                break;
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
				convertView.setTag(contactsHold);
			} else {
				contactsHold = (ContactsHold) convertView.getTag();
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
            if(contacts.getPinyin().length()==0){
                contactsHold.item_pinyin.setText("#");
            }else {
                contactsHold.item_pinyin.setText(contacts.getPinyin().charAt(0)
                        + "");
            }

			if (contacts.isHasPinYin()) {
				contactsHold.item_pinyin.setVisibility(View.VISIBLE);
			} else {
				contactsHold.item_pinyin.setVisibility(View.GONE);
			}

            // item的点击事件
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    List<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
                    try {
                        phoneInfos = dbUtil.findAll(Selector.from(PhoneInfo.class).where("contacts_id","=",contacts.getId()));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    if (phoneInfos != null && phoneInfos.size() >= 5) {
                        ToastUtils.Errortoast(mContext, "最多添加5个手机号");
                    }else {

                        msgDialog = new MsgDialog(ActivityAllContacts.this, R.style.MyDialogStyle);
                        msgDialog.setContent("确定保存至联系人" + contacts.getName(), "", "确定", "取消");
                        msgDialog.setCancleListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                            }
                        });
                        msgDialog.setOKListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                    ActivityAllContacts.this.showLoding("添加中...");
                                    InteNetUtils.getInstance(mContext).addPhone(latelyLinkeMan.getLinkeManPhone(),
                                            contacts.getId() + "", user.getToken(),
                                            new RequestCallBack<String>() {

                                                @Override
                                                public void onFailure(
                                                        HttpException arg0, String arg1) {
                                                    ToastUtils.Errortoast(mContext,
                                                            "保存失败");
                                                    ActivityAllContacts.this.dissLoding();
                                                }

                                                @Override
                                                public void onSuccess(
                                                        ResponseInfo<String> arg0) {
                                                    ActivityAllContacts.this.dissLoding();
                                                    msgDialog.dismiss();
                                                    ToastUtils.Infotoast(mContext,
                                                            "保存成功");
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(
                                                                arg0.result);
                                                        SuccessMsg msg = new SuccessMsg();
                                                        try {
                                                            msg.parseJSON(jsonObject);
                                                            try {
                                                                JSONObject optJSONObject = jsonObject
                                                                        .optJSONObject("phone_info");
                                                                PhoneInfo phoneInfo = new PhoneInfo();
                                                                phoneInfo
                                                                        .parseJSON(optJSONObject);

                                                                JSONObject opt = jsonObject
                                                                        .optJSONObject("contact_info");

                                                                contacts.setPoster(opt
                                                                        .optString("poster"));
                                                                contacts
                                                                        .setHuanxin_username(opt
                                                                                .optString("huanxin_username"));
                                                                contacts
                                                                        .setIs_benben(opt
                                                                                .optString("is_benben"));
                                                                contacts
                                                                        .setIs_baixing(opt
                                                                                .optString("is_baixing"));

                                                                dbUtil.saveOrUpdate(contacts);
                                                                sendBroadcast(new Intent(
                                                                        AndroidConfig.ContactsRefresh));

                                                                phoneInfo
                                                                        .setContacts_id(contacts
                                                                                .getId());
                                                                dbUtil.saveOrUpdate(phoneInfo);

                                                                latelyLinkeMan.setCid(contacts.getId());
                                                                latelyLinkeMan.setLinkeManName(contacts.getName());
//                                                            if (latelyLinkeMan.getId() != 0) {
//                                                                dbUtil.saveOrUpdate(latelyLinkeMan);
//                                                            }
                                                                dbUtil.update(latelyLinkeMan,
                                                                        WhereBuilder.b("linkeManPhone", "=", latelyLinkeMan.getLinkeManPhone()), "cid", "linkeManName");
                                                                setResult(AndroidConfig.writeFriendRefreshResultCode);
                                                                AnimFinsh();
                                                            } catch (DbException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } catch (NetRequestException e) {
                                                            e.getError()
                                                                    .print(mContext);
                                                        }
                                                    } catch (JSONException e) {
                                                        ToastUtils.Errortoast(mContext,
                                                                "保存失败");
                                                        ActivityAllContacts.this.dissLoding();
                                                    }
                                                }
                                            });
                                }

                        });
                        msgDialog.show();
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
	}


	@Override
	public void onBackPressed() {
        AnimFinsh();
	}



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
