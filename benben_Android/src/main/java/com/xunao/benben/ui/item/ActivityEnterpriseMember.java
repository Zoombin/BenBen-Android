package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.Enterprise;
import com.xunao.benben.bean.EnterpriseMember;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.bean.EnterpriseMemberGroup;
import com.xunao.benben.bean.EnterpriseMemberList;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.NumberTrainDetail;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.InfobulletinHint;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.item.ContectManagement.ActivityPacketManagement;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityEnterpriseMember extends BaseActivity implements
		OnClickListener, ActionSheetListener {
    private ImageView com_title_bar_left_bt,com_title_bar_right_bt;
    private TextView com_title_bar_content,com_title_bar_right_tv;

	private EditText search_edittext;
    private TextView searchName;
	private LinearLayout ll_seach_icon;
	private EMBoradcast mEMBoradcast;
	private ImageView iv_search_content_delect;
	private String id;
	private String name;
    private int origin;
    private String type;
	private FloatingGroupExpandableListView listView;
	private myAdapter adapter;
	private LinearLayout no_data;

	private EnterpriseMember member;
	private ArrayList<EnterpriseMemberDetail> eMemberDetails = new ArrayList<EnterpriseMemberDetail>();
	private ArrayList<EnterpriseMemberGroup> memberGroups = new ArrayList<EnterpriseMemberGroup>();
	private EnterpriseMemberDetail memberDetail;
	private HashMap<String, Integer> groupIdAndPosition = new HashMap<String, Integer>();

	private String phone;
	private String shortPhone;
	private String phoneName;
    private String eid;
	private final static int INVITE_FRIEND = 1;
	private static final int CHOICE_MEMBER = 1000;
	protected static final int SEARCH_COMMON = 1001;
	private static final int ENTERPRISE_GROUP = 1002;
	protected InfoMsgHint hint;
	private int positions;
	private int enterpriseId;
	private TextView tv_common;

	private boolean isSearch = false;

	private boolean isDelete = false;
    private InputDialog inputDialog;
    private String pecketName;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_member);
		setShowLoding(false);
		mEMBoradcast = new EMBoradcast();
		registerReceiver(mEMBoradcast, new IntentFilter(
				AndroidConfig.refreshEnterpriseMember));
	}

	@Override
	public void initView(Bundle savedInstanceState) {
        com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
        com_title_bar_left_bt.setOnClickListener(this);
        com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
        com_title_bar_right_bt.setOnClickListener(this);
        com_title_bar_content = (TextView) findViewById(R.id.com_title_bar_content);
        com_title_bar_right_tv = (TextView) findViewById(R.id.com_title_bar_right_tv);
        com_title_bar_right_tv.setOnClickListener(this);
		search_edittext = (EditText) findViewById(R.id.search_edittext);
        searchName = (TextView) findViewById(R.id.searchName);
        searchName.setText("搜索其他联系人");
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		listView = (FloatingGroupExpandableListView) findViewById(R.id.listview);

		tv_common = (TextView) findViewById(R.id.tv_common);

		listView.setGroupIndicator(null);
//		adapter = new myAdapter();
//		wrapperAdapter = new WrapperExpandableListAdapter(adapter);
//		listView.setAdapter(wrapperAdapter);

		iv_search_content_delect.setOnClickListener(this);
		search_edittext.setFocusable(false);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		name = intent.getStringExtra("name");
        origin = intent.getIntExtra("origin",1);
        type = intent.getStringExtra("type");
        com_title_bar_content.setText(name);
        if(origin==2 && !type.equals("3")){
            com_title_bar_right_bt.setVisibility(View.VISIBLE);
        }

        if (CommonUtils.isNetworkAvailableNoShow(mContext)) {
            showLoding("请稍后...");
            InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
                    mRequestCallBack);
        }else{
            getDataFromLocal();
        }
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
//		setOnLeftClickLinester(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				AnimFinsh();
//			}
//		});
//
//		setOnRightClickLinester(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				startAnimActivityForResult5(ActivityEnterpriseDetail.class,
//						"id", id, "member", eMemberDetails, INVITE_FRIEND);
//			}
//		});

		search_edittext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult(ActivityAddCommon.class,
						"enterpriseId", id, "commonNum",
						member.getCommontCounts(), SEARCH_COMMON);
			}
		});

	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long comContextunt, long current,
			boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		dissLoding();
		member = new EnterpriseMember();
		try {
			member = member.parseJSON(jsonObject);
            if(member.getFirstin()==1){
                inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
                inputDialog.setContent("通讯录名片", "请输入新的通讯录名片", "确认", "取消");
                inputDialog.setEditContent(member.getName());
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

                        if (!CommonUtils.StringIsSurpass(pecketName, 0, 10)) {
                            ToastUtils.Infotoast(mContext, "通讯录名片限制在1-10个字数");
                            return;
                        }

                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            InteNetUtils.getInstance(mContext)
                                    .updateEnterpriseRemarkName(id, pecketName,
                                            requestCallBack2);
                            sendBroadcast(new Intent(
                                    AndroidConfig.refreshEnterpriseList));
                        }
                    }
                });
                inputDialog.show();
            }


            dbUtil.delete(EnterpriseMember.class,
                    WhereBuilder.b("benben_id","=",user.getBenbenId())
                            .and("eid", "=", id));
            dbUtil.delete(EnterpriseMemberGroup.class,
                    WhereBuilder.b("benben_id","=",user.getBenbenId())
                            .and("eid","=",id));
            dbUtil.delete(EnterpriseMemberDetail.class,
                    WhereBuilder.b("benben_id","=",user.getBenbenId())
                            .and("eid","=",id));
			memberGroups.clear();
			eMemberDetails=new ArrayList<>();
			if (member == null
					&& member.getEnterpriseMemberDetails().size() <= 0) {
			} else {
                member.setEid(id);
                member.setBenben_id(user.getBenbenId());
                dbUtil.save(member);
				memberGroups = member.getEnterpriseMemberDetails();
				if (memberGroups != null) {
					for (EnterpriseMemberGroup detail : memberGroups) {
                        detail.setEid(id);
                        detail.setBenben_id(user.getBenbenId());
                        dbUtil.save(detail);
						for (EnterpriseMemberDetail detail2 : detail
								.getMemberDetails()) {
							detail2.setGroupName(detail.getGroupName());
							detail2.seteGroupId(detail.getId());
							detail2.setNumber(detail.getNumber());
                            detail2.setEid(id);
                            detail2.setBenben_id(user.getBenbenId());
                            dbUtil.save(detail2);
							eMemberDetails.add(detail2);
						}
					}
				}
			}
            getDataFromLocal();


		} catch (NetRequestException e) {
			e.printStackTrace();
		} catch (DbException e) {
            e.printStackTrace();
        }
    }

    private RequestCallBack<String> requestCallBack2 = new RequestCallBack<String>() {
        @Override
        public void onSuccess(ResponseInfo<String> arg0) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(arg0.result);
                if (jsonObject.optString("ret_num").equals("0")) {

                        ToastUtils.Infotoast(mContext, "修改通讯录名片成功!");
                    member.setName(pecketName);

                        sendBroadcast(new Intent(
                                AndroidConfig.refreshEnterpriseMember));
                        sendBroadcast(new Intent(
                                AndroidConfig.refreshEnterpriseList));

                    inputDialog.dismiss();
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

                        hint.show();
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
            ToastUtils.Infotoast(mContext, "网络不可用,请重试");
        }
    };

    private void getDataFromLocal() {
        try {
            member = dbUtil.findFirst(Selector.from(EnterpriseMember.class).where("benben_id", "=", user.getBenbenId())
                    .and("eid", "=", id));
            List<EnterpriseMemberGroup> groups = dbUtil.findAll(Selector.from(EnterpriseMemberGroup.class).where("benben_id", "=", user.getBenbenId())
                    .and("eid", "=", id));
            memberGroups = (ArrayList<EnterpriseMemberGroup>)groups;
            if(memberGroups!=null) {
                for (int i = 0; i < memberGroups.size(); i++) {
                    List<EnterpriseMemberDetail> details = dbUtil.findAll(Selector.from(EnterpriseMemberDetail.class).where("benben_id", "=", user.getBenbenId())
                            .and("eid", "=", id).and("eGroupId", "=", memberGroups.get(i).getId()).orderBy("pinyin"));
                    memberGroups.get(i).setMemberDetails((ArrayList<EnterpriseMemberDetail>) details);
                }
            }
            List<EnterpriseMemberDetail> details = dbUtil.findAll(Selector.from(EnterpriseMemberDetail.class).where("benben_id","=",user.getBenbenId())
                    .and("eid", "=", id));

            eMemberDetails = (ArrayList<EnterpriseMemberDetail>)details;
            if(member==null){
                member = new EnterpriseMember();
                member.setCommontCounts(0);
                member.setMaxShow(50);
                member.setFilter("0");
                memberGroups = new ArrayList<>();
            }
            if(member!=null) {
                tv_common.setText("常用联系人 (" + member.getCommontCounts()
                        + "/" + member.getMaxShow() + ")");

                if (member.getFilter().equals("1")) {
                    startAnimActivityForResult7(
                            ActivityEnterpriseChoiceMember.class, "members",
                            eMemberDetails, "maxShow", member.getMaxShow(),
                            "enterpriseId", id, CHOICE_MEMBER);
                }

                if (memberGroups.size() <= 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }

                if (isDelete && memberGroups.size()>0) {
                    memberGroups.get(position).setClicked(true);
                    // for (int i = 0; i < memberGroups.size(); i++) {
                    // memberGroups.get(i).setClicked(true);
                    // }
                }
                if(adapter==null) {
                    adapter = new myAdapter();
                    wrapperAdapter = new WrapperExpandableListAdapter(adapter);
                    listView.setAdapter(wrapperAdapter);
                }else {
                    adapter.notifyDataSetChanged();
                }
            }else{
                ToastUtils.Infotoast(mContext, "获取信息失败!");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
        getDataFromLocal();
        dissLoding();
//		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
            case R.id.com_title_bar_left_bt:
                AnimFinsh();
                break;
            case R.id.com_title_bar_right_bt:
                showLoding("");
                InteNetUtils.getInstance(mContext).enterprisesDetail(id,
                        new RequestCallBack<String>() {

                            @Override
                            public void onSuccess(ResponseInfo<String> arg0) {
                                dissLoding();
                                try {
                                    JSONObject jsonObject = new JSONObject(
                                            arg0.result);
                                    JSONObject object = jsonObject
                                            .optJSONObject("enterprise_info");
                                    Log.d("ltf","object============="+object);
                                    Enterprise enterprise = new Enterprise();
                                    enterprise.parseJSON(object);
                                    String content="暂无公告";
                                    String time = "";
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    if(!enterprise.getBulletin().equals("")){
                                        content = enterprise.getBulletin();
                                        Date date = new Date(enterprise.getUpdate_time()*1000);
                                        time = format.format(date);
                                    }


                                    final InfobulletinHint hint = new InfobulletinHint(
                                            mContext, R.style.MyDialog1);
                                    hint.setContent(content,time);
                                    hint.show();
                                    hint.setOKListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            hint.dismiss();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(HttpException arg0, String arg1) {
                                dissLoding();
                                ToastUtils.Errortoast(mContext, "获取公告失败!");
                            }
                        });


                break;
            case R.id.com_title_bar_right_tv:
                startAnimActivityForResult5(ActivityEnterpriseDetail.class,
						"id", id, "member", eMemberDetails, INVITE_FRIEND);
                break;

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
			isSearch = false;

//			InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
//					mRequestCallBack);
			break;
		case R.id.management_packet:

			// startAnimActivity2Obj(ActivityEnterpriseGroup.class,
			// "enterpriseId", id);

			InteNetUtils.getInstance(mContext).enterprisesDetail(id,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								JSONObject object = jsonObject
										.optJSONObject("enterprise_info");
								Enterprise enterprise = new Enterprise();
								enterprise.parseJSON(object);
								startAnimActivityForResult51(
										ActivityEnterpriseGroup.class,
										"enterpriseId", id, "type",
										enterprise.getType(), "member",
										eMemberDetails, ENTERPRISE_GROUP);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ToastUtils.Errortoast(mContext, "网络不可用");
						}
					});

			mPopupWindow.dismiss();
			break;
		}

	}

	private int position;

	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public EnterpriseMemberDetail getChild(int arg0, int arg1) {
			return memberGroups.get(arg0).getMemberDetails().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {

			ViewHolder holder;
			final EnterpriseMemberDetail enterpriseMemberDetail = getChild(
					groupPosition, childPosition);

			holder = new ViewHolder();

			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.activity_enterprise_member_item, null);
				holder.tv_enterprise_name = (TextView) convertView
						.findViewById(R.id.tv_enterprise_name);
				holder.iv_message = (ImageView) convertView
						.findViewById(R.id.iv_message);
				holder.iv_make_phone = (ImageView) convertView
						.findViewById(R.id.iv_make_phone);
				holder.tv_enterprise_phone = (TextView) convertView
						.findViewById(R.id.tv_enterprise_phone);
				holder.tv_enterprise_shortphone = (TextView) convertView
						.findViewById(R.id.tv_enterprise_shortphone);
				holder.iv_shortmessage = (ImageView) convertView
						.findViewById(R.id.iv_shortmessage);
				holder.iv_make_shortphone = (ImageView) convertView
						.findViewById(R.id.iv_make_shortphone);
				holder.shortBox = (LinearLayout) convertView
						.findViewById(R.id.shortBox);
				holder.longBox = (LinearLayout) convertView
						.findViewById(R.id.longBox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			phone = enterpriseMemberDetail.getPhone();
			shortPhone = enterpriseMemberDetail.getShortPhone();
			phoneName = enterpriseMemberDetail.getName();
            eid = enterpriseMemberDetail.getId();
			if (enterpriseMemberDetail.getMemberId().equals(user.getId() + "")) {
				holder.iv_message.setVisibility(View.GONE);
				holder.iv_make_phone.setVisibility(View.GONE);
				holder.iv_shortmessage.setVisibility(View.GONE);
				holder.iv_make_shortphone.setVisibility(View.GONE);
			} else {
				holder.iv_message.setVisibility(View.VISIBLE);
				holder.iv_make_phone.setVisibility(View.VISIBLE);
				holder.iv_shortmessage.setVisibility(View.VISIBLE);
				holder.iv_make_shortphone.setVisibility(View.VISIBLE);
			}
			holder.tv_enterprise_name.setText(phoneName);
			holder.tv_enterprise_phone.setText(phone);
			holder.tv_enterprise_shortphone.setText(TextUtils
					.isEmpty(shortPhone) ? "" : shortPhone);

			if (TextUtils.isEmpty(shortPhone) || shortPhone.equals("0")) {
				holder.shortBox.setVisibility(View.GONE);
				holder.iv_shortmessage.setVisibility(View.GONE);
				holder.iv_make_shortphone.setVisibility(View.GONE);
			} else {
				holder.shortBox.setVisibility(View.VISIBLE);
				holder.iv_shortmessage.setVisibility(View.VISIBLE);
				holder.iv_make_shortphone.setVisibility(View.VISIBLE);
			}

			if (TextUtils.isEmpty(phone) || phone.equals("0")) {
				holder.longBox.setVisibility(View.GONE);
				holder.iv_message.setVisibility(View.GONE);
				holder.iv_make_phone.setVisibility(View.GONE);
			} else {
				holder.longBox.setVisibility(View.VISIBLE);
				holder.iv_message.setVisibility(View.VISIBLE);
				holder.iv_make_phone.setVisibility(View.VISIBLE);
			}

            holder.tv_enterprise_phone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    InteNetUtils.getInstance(mContext).AddTelNum(enterpriseMemberDetail.getId(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                        }
                    });
                    PhoneUtils.makeCall(Integer.parseInt(enterpriseMemberDetail.getId()),enterpriseMemberDetail.getName(),
                            enterpriseMemberDetail.getPhone(), mContext);
                }
            });

			holder.iv_make_phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    InteNetUtils.getInstance(mContext).AddTelNum(enterpriseMemberDetail.getId(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                        }
                    });
					PhoneUtils.makeCall(Integer.parseInt(enterpriseMemberDetail.getId()),enterpriseMemberDetail.getName(),
							enterpriseMemberDetail.getPhone(), mContext);
				}
			});

			holder.iv_message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PhoneUtils.sendSMS(enterpriseMemberDetail.getPhone(), "",
							mContext);
				}
			});

            holder.tv_enterprise_shortphone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    InteNetUtils.getInstance(mContext).AddTelNum(enterpriseMemberDetail.getId(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                        }
                    });
                    PhoneUtils.makeCall(Integer.parseInt(enterpriseMemberDetail.getId()),enterpriseMemberDetail.getName(),
                            enterpriseMemberDetail.getShortPhone(), mContext);
                }
            });

			holder.iv_make_shortphone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    InteNetUtils.getInstance(mContext).AddTelNum(enterpriseMemberDetail.getId(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                        }
                    });
					PhoneUtils.makeCall(Integer.parseInt(enterpriseMemberDetail.getId()),enterpriseMemberDetail.getName(),
							enterpriseMemberDetail.getShortPhone(), mContext);
				}
			});

			holder.iv_shortmessage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PhoneUtils.sendSMS(enterpriseMemberDetail.getShortPhone(),
							"", mContext);
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					isDelete = true;
					position = groupPosition;
					showInfoMsgDialog("删除常用联系人",
							"是否将" + enterpriseMemberDetail.getName()
									+ "从常用联系人中删除",
							enterpriseMemberDetail.getId(), false);
					return false;
				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
            if(memberGroups.get(arg0).getMemberDetails()==null){
                return 0;
            }else {
                return memberGroups.get(arg0).getMemberDetails().size();
            }
		}

		@Override
		public EnterpriseMemberGroup getGroup(int arg0) {
			return memberGroups.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return memberGroups.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			HeaderViewHolder holder;
			final EnterpriseMemberGroup enterpriseMemberGroup = getGroup(groupPosition);
			groupIdAndPosition
					.put(enterpriseMemberGroup.getId(), groupPosition);

			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = View.inflate(mContext,
						R.layout.activity_enterprise_item_group, null);
				holder.tv_enterprise_group = (TextView) convertView
						.findViewById(R.id.tv_enterprise_group);

				holder.status_img = (ImageView) convertView
						.findViewById(R.id.status_img);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
			holder.tv_enterprise_group.setText(enterpriseMemberGroup
					.getGroupName()
					+ " ("
					+ enterpriseMemberGroup.getNumber()
					+ ")");

			holder.tv_enterprise_group
					.setTextColor(Color.parseColor("#000000"));

			if (enterpriseMemberGroup.isClicked()) {
				listView.expandGroup(groupPosition);
			} else {
				listView.collapseGroup(groupPosition);
			}

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			final View headview = convertView;

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (enterpriseMemberGroup.isClicked()) {
						listView.collapseGroup(groupPosition);
						enterpriseMemberGroup.setClicked(false);
					} else {
						listView.expandGroup(groupPosition);
						enterpriseMemberGroup.setClicked(true);
					}
				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					getPopupWindowInstance();
					mPopupWindow.showAsDropDown(headview,
							(mContext.mScreenWidth - PixelUtil.dp2px(95)) / 2,
							0 - PixelUtil.dp2px(84));
					enterpriseMemberGroup.setClicked(enterpriseMemberGroup
							.isClicked());
					adapter.notifyDataSetChanged();
					return false;
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

	/*
	 * 获取PopupWindow实例
	 */
	private PopupWindow mPopupWindow;

	private void getPopupWindowInstance() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	/*
	 * 创建PopupWindow
	 */
	private void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View popupWindow = layoutInflater.inflate(R.layout.popup_packet, null);
		ImageView management_packet = (ImageView) popupWindow
				.findViewById(R.id.management_packet);
		management_packet.setOnClickListener(this);

		// 创建一个PopupWindow
		// 参数1：contentView 指定PopupWindow的内容
		// 参数2：width 指定PopupWindow的width
		// 参数3：height 指定PopupWindow的height
		mPopupWindow = new PopupWindow(popupWindow,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置点击外围空间 popup消失
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOutsideTouchable(true);
		// 获取屏幕和PopupWindow的width和height
		// mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		// mScreenWidth = getWindowManager().getDefaultDisplay().getHeight();
		// mPopupWindowWidth = mPopupWindow.getWidth();
		// mPopupWindowHeight = mPopupWindow.getHeight();
	}

	class HeaderViewHolder {
		TextView tv_enterprise_group;
		ImageView status_img;
	}

	class ViewHolder {
		TextView tv_enterprise_name;
		TextView tv_enterprise_phone;
		ImageView iv_message;
		ImageView iv_make_phone;

		TextView tv_enterprise_shortphone;
		ImageView iv_shortmessage;
		ImageView iv_make_shortphone;

		LinearLayout shortBox;
		LinearLayout longBox;

	}

	protected void showActionSheet(String phone, String shortPhone,
			boolean hasShortPhone) {
		setTheme(R.style.ActionSheetStyleIOS7);

		if (hasShortPhone) {
			ActionSheet.createBuilder(mContext, getSupportFragmentManager())
					.setCancelButtonTitle("取消").setOtherButtonTitles(phone)
					// 设置颜色 必须一一对应
					.setOtherButtonTitlesColor("#1E82FF")
					.setCancelableOnTouchOutside(true).setListener(this).show();

		} else {
			ActionSheet.createBuilder(mContext, getSupportFragmentManager())
					.setCancelButtonTitle("取消")
					.setOtherButtonTitles(phone, shortPhone)
					// 设置颜色 必须一一对应
					.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
					.setCancelableOnTouchOutside(true).setListener(this).show();
		}

	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index) {
		case 0:
			PhoneUtils.makeCall(Integer.parseInt(eid),phoneName, phone, mContext);
			break;
		case 1:
			PhoneUtils.makeCall(Integer.parseInt(eid),phoneName, shortPhone, mContext);
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		switch (arg0) {
		case INVITE_FRIEND:
			if (data != null) {
				if (TextUtils.isEmpty(data.getStringExtra("exit"))) {
					InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
							mRequestCallBack);
				} else {
					AnimFinsh();
				}
				user.setUpdate(true);
			}
			break;
		case CHOICE_MEMBER:
			if (data != null) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
							mRequestCallBack);
				}
				user.setUpdate(true);
			} else {
				AnimFinsh();
			}
			break;
		case SEARCH_COMMON:
			if (data != null) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
							mRequestCallBack);
				}
				user.setUpdate(true);
			}
			break;
		}
		super.onActivityResult(arg0, arg1, data);
	}

	@Override
	protected void onResume() {
		if (user.isUpdate()) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				eMemberDetails.clear();
				InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
						mRequestCallBack);
			}
		}
		super.onResume();
	}

	private void showInfoMsgDialog(String msg, String msg2,
			final String memberId, final boolean status) {
		hint = new InfoMsgHint(mContext, R.style.MyDialog1);
		hint.setContent(msg, msg2, "完成", "取消");
		hint.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (status) {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).addCommon(id,
								memberId, new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils
												.Infotoast(mContext, "网络不可用!");
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										try {
											JSONObject jsonObject = new JSONObject(
													arg0.result);
											if (jsonObject.optString("ret_num")
													.equals("0")) {
												ToastUtils.Infotoast(mContext,
														"添加常用联系人成功!");
												eMemberDetails.get(positions)
														.setCommon("1");
											} else {
												ToastUtils
														.Infotoast(
																mContext,
																jsonObject
																		.optString("ret_msgd"));
											}
										} catch (JSONException e) {
											e.printStackTrace();
											ToastUtils.Infotoast(mContext,
													"操作失败!");
										}

										adapter.notifyDataSetChanged();
									}
								});
					}
				} else {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).delCommon(id,
								memberId, new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils
												.Infotoast(mContext, "网络不可用!");
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										try {
											JSONObject jsonObject = new JSONObject(
													arg0.result);
											if (jsonObject.optString("ret_num")
													.equals("0")) {
												ToastUtils.Infotoast(mContext,
														"删除常用联系人成功!");

												InteNetUtils
														.getInstance(mContext)
														.enterpriseMember(id,
																"",
																mRequestCallBack);
											} else {
												ToastUtils
														.Infotoast(
																mContext,
																jsonObject
																		.optString("ret_msgd"));
											}
										} catch (JSONException e) {
											e.printStackTrace();
											ToastUtils.Infotoast(mContext,
													"操作失败!");
										}

										adapter.notifyDataSetChanged();
									}
								});
					}

				}
				hint.dismiss();
			}
		});
		hint.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hint.dismiss();
			}
		});
		hint.show();
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			try {
				JSONObject jsonObject = new JSONObject(arg0.result);

			} catch (JSONException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, "操作失败!");
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Infotoast(mContext, "网络不可用!");
		}
	};
	private WrapperExpandableListAdapter wrapperAdapter;

	private void refresh() {
		InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
				mRequestCallBack);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mEMBoradcast);
	}

	class EMBoradcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			InteNetUtils.getInstance(mContext).enterpriseMember(id, "",
					mRequestCallBack);
		}
	}

}
