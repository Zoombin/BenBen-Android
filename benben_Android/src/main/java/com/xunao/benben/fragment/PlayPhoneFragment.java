package com.xunao.benben.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity.TitleBarType;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.base.BaseFragment;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.LinkePhone;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.fragment.PlayPhoneFragment.PhoneAdapter.ViewHolder;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityAllContacts;
import com.xunao.benben.ui.item.ActivityAddNewFriend;
import com.xunao.benben.ui.item.ActivityContactsInfo;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PlayPhoneUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

/**
 * 拨号页面
 */
public class PlayPhoneFragment extends BaseFragment implements OnClickListener,
		OnLongClickListener, ActionSheetListener {

	View view;
	private SwipeMenuListView listView;
	private ImageView numPlay;
	private ArrayList<LatelyLinkeMan> latelyLinkeManList = new ArrayList<LatelyLinkeMan>();
	private ArrayList<PhoneInfo> linkePhones = new ArrayList<PhoneInfo>();
	private PhoneAdapter phoneAdapter;
	private LinkedPhoneAdapter linkedPhoneAdapter;
	private TextView showPlayNumBox;
	private String number;
    private InputDialog inputDialog;
//    private String phone;
    private LatelyLinkeMan latelyLinkeMan;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_playphone, null);
		super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	private void initSwipeMenu() {
		creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color
						.parseColor("#ec5d57")));
				// set item width
				deleteItem.setWidth(PixelUtil.dp2px(70));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		listView.setMenuCreator(creator);

	}

	@Override
	protected void initView() {
		listView = (SwipeMenuListView) view.findViewById(R.id.listView);
		numPlay = (ImageView) view.findViewById(R.id.numPlay);
		initSwipeMenu();
	}

	@Override
	protected void initLinstener() {
		numPlay.setOnClickListener(this);
		numPlay.performClick();
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					LatelyLinkeMan remove = latelyLinkeManList.remove(position);
					try {
						dbUtil.delete(remove);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					initDate();
					break;
				}
				return false;
			}
		});

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showActionSheet(new String[] { latelyLinkeManList.get(i).getLinkeManPhone() },
                        latelyLinkeManList.get(i));
            }
        });

	}

	@Override
	protected void initDate() {
		try {
			List<LatelyLinkeMan> findAll = dbUtil.findAll(Selector.from(
					LatelyLinkeMan.class).orderBy("latelyTime", true));
			if (findAll != null)
				latelyLinkeManList = new ArrayList<LatelyLinkeMan>(findAll);
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (phoneAdapter != null) {
			phoneAdapter.notifyDataSetChanged();
		} else {
			phoneAdapter = new PhoneAdapter();
            listView.setAdapter(phoneAdapter);
		}


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
	protected void onSuccess(JSONObject t) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub


	}

	private void writeNum(TextView tv, String num) {
		tv.setText(tv.getText().toString() + num);
	}

	public static final int LATELYPHONE = 0;
	public static final int PHONE = 1;
	protected static final int SIGN = 18;
	private int curStatus;

	private void changeListStatus(int status, ArrayList<PhoneInfo> list) {

		switch (status) {
		case LATELYPHONE:

			try {
				List<LatelyLinkeMan> findAll = dbUtil.findAll(Selector.from(
						LatelyLinkeMan.class).orderBy("latelyTime", true));
				if (findAll != null)
					latelyLinkeManList = new ArrayList<LatelyLinkeMan>(findAll);
			} catch (DbException e) {
				e.printStackTrace();
			}

			if (status != curStatus) {
				curStatus = LATELYPHONE;
				listView.setMenuCreator(creator);
				listView.setAdapter(phoneAdapter);
			} else {
				phoneAdapter.notifyDataSetChanged();
			}
			break;
		case PHONE:
			curStatus = PHONE;
			listView.setMenuCreator(null);
			HashMap<Integer, PhoneInfo> hashMap = new HashMap<Integer, PhoneInfo>();
			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (i < list.size()) {

					PhoneInfo phoneInfo = list.get(i);

					if (!phoneInfo.getIs_baixing().equals("0")) {
						PhoneInfo p = new PhoneInfo();
						p.setContacts_id(phoneInfo.getContacts_id());
						p.setHuanxin_username(phoneInfo.getHuanxin_username());
						p.setPhone(phoneInfo.getIs_baixing());
						p.setNick_name(phoneInfo.getNick_name());
						p.setName(phoneInfo.getName());
						p.setPoster(phoneInfo.getPoster());
						hashMap.put(i, p);

					} else {
						if (list.get(i).getPhone().indexOf(number) == -1) {
							list.remove(i);
						}
					}
				}
			}

			int size2 = hashMap.size();
			Set<Entry<Integer, PhoneInfo>> entrySet = hashMap.entrySet();

			for (int i = 0; i < size2; i++) {
				Entry<Integer, PhoneInfo> next = entrySet.iterator().next();
				Integer key = next.getKey();
				PhoneInfo value = next.getValue();
				if (list.size() - 1 >= key + 1 + i) {
					list.add(key + 1 + i, value);

				} else {
					list.add(value);
				}
			}
			ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
			for (PhoneInfo phone : list) {
				if (phone.getPhone().indexOf(number) != -1) {
					if (!phoneInfos.contains(phone)) {
						phoneInfos.add(phone);
					}
				} else {
					if (!"".equals(phone.getIs_baixing())
							&& phone.getIs_baixing() != null) {
						if (phone.getIs_baixing().indexOf(number) != -1) {
							if (!phoneInfos.contains(phone)) {
								phone.setPhone(phone.getIs_baixing());
								phoneInfos.add(phone);
							}
						}
					}
				}
			}

			linkePhones = phoneInfos;
			linkedPhoneAdapter = new LinkedPhoneAdapter(phoneInfos);
			listView.setAdapter(linkedPhoneAdapter);

			break;
		}

	}

	int curSearchNum;
	private SwipeMenuCreator creator;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.numPlay:// 唤醒拨号盘
			PlayPhoneUtils
					.showPlayPhone(mActivity, numPlay, this, dbUtil, this);
			showPlayNumBox = PlayPhoneUtils.showPlayNumBox(mActivity,
					mActivity.comritlebar, this);
			showPlayNumBox.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(final CharSequence s, int start,
						int before, int count) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(final Editable s) {
					final int last = ++curSearchNum;
					new Thread() {
						public void run() {
							if (s.length() > 0) {
								number = s.toString();
								try {
									List<PhoneInfo> findAll = dbUtil
											.findAll(Selector
													.from(PhoneInfo.class)
													.where(WhereBuilder.b(
															"phone", "like",
															"%" + s.toString()
																	+ "%"))
													.or("is_baixing", "like",
															"%" + s + "%").and("phone","!=",""));

									if (findAll != null)
										linkePhones = new ArrayList<PhoneInfo>(
												findAll);

								} catch (DbException e) {
								}
								mActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (last == curSearchNum)
											changeListStatus(PHONE, linkePhones);
									}
								});

							} else {
								mActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (last == curSearchNum)
											changeListStatus(LATELYPHONE, null);
									}
								});
							}

						};
					}.start();

				}
			});
			break;
		case R.id.dialdel:
			CharSequence text = showPlayNumBox.getText();
			if (text.length() > 0) {
				showPlayNumBox.setText(text.subSequence(0, text.length() - 1));
			}
			break;
		case R.id.dialNum0:
			writeNum(showPlayNumBox, 0 + "");
			break;
		case R.id.dialNum1:
			writeNum(showPlayNumBox, 1 + "");
			break;
		case R.id.dialNum2:
			writeNum(showPlayNumBox, 2 + "");
			break;
		case R.id.dialNum3:
			writeNum(showPlayNumBox, 3 + "");
			break;
		case R.id.dialNum4:
			writeNum(showPlayNumBox, 4 + "");
			break;
		case R.id.dialNum5:
			writeNum(showPlayNumBox, 5 + "");
			break;
		case R.id.dialNum6:
			writeNum(showPlayNumBox, 6 + "");
			break;
		case R.id.dialNum7:
			writeNum(showPlayNumBox, 7 + "");
			break;
		case R.id.dialNum8:
			writeNum(showPlayNumBox, 8 + "");
			break;
		case R.id.dialNum9:
			writeNum(showPlayNumBox, 9 + "");
			break;
		case R.id.dialNumJ:
			writeNum(showPlayNumBox, "#");
			break;
		case R.id.dialNumX:
			writeNum(showPlayNumBox, "*");
			break;
        case R.id.iv_add:
            CharSequence phone = showPlayNumBox.getText();

            if (phone.length() > 0) {
                PlayPhoneUtils.hinePlayNumBox();
                PlayPhoneUtils.hinePlayPhone();
                latelyLinkeMan = new LatelyLinkeMan();
                latelyLinkeMan.setLinkeManPhone(String.valueOf(phone));
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet();
            }else{
                ToastUtils.Errortoast(getActivity(),"号码为空");
            }


            break;
		}
	}



    class PhoneAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return latelyLinkeManList.size();
		}

		@Override
		public LatelyLinkeMan getItem(int position) {
			return latelyLinkeManList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.item_listview_playphone, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			final LatelyLinkeMan item = getItem(position);
			String linkeManName = item.getLinkeManName();
			if (TextUtils.isEmpty(linkeManName)) {
				holder.tv_name.setText(item.getLinkeManPhone());
				holder.tv_phone.setVisibility(View.GONE);
                holder.iv_phone.setVisibility(View.VISIBLE);
			} else {
				holder.tv_phone.setVisibility(View.VISIBLE);
                holder.iv_phone.setVisibility(View.GONE);
				holder.tv_name.setText(item.getLinkeManName());
				holder.tv_phone.setText(item.getLinkeManPhone());
			}

			holder.tv_time.setText(TimeUtil.getTimeLong(item.getLatelyTime()));
//            holder.ll_phone.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					showActionSheet(new String[] { item.getLinkeManPhone() },
//							item);
//
//				}
//			});
            holder.iv_phone.setImageResource(R.drawable.icon_phone_add);
            holder.iv_phone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//                    if (item.getCid() == 0) {
                        getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                        latelyLinkeMan = item;
                        showActionSheet();

                }
            });
			return convertView;
		}

		class ViewHolder {
			ImageView iv_phone;
			MyTextView tv_name;
			MyTextView tv_phone;
			MyTextView tv_time;
            LinearLayout ll_phone;
			public ViewHolder(View view) {
				iv_phone = (ImageView) view.findViewById(R.id.phoneImg);
				tv_name = (MyTextView) view.findViewById(R.id.phoneName);
				tv_time = (MyTextView) view.findViewById(R.id.phoneTime);
				tv_phone = (MyTextView) view.findViewById(R.id.phoneNum);
                ll_phone = (LinearLayout) view.findViewById(R.id.ll_phone);
				view.setTag(this);
			}
		}
	}

	public void showActionSheet(final String[] phone, final BaseBean item) {
		// mActivity.setTheme(R.style.ActionSheetStyleIOS7);
		// ActionSheet
		// .createBuilder(mActivity, mActivity.getSupportFragmentManager())
		// .setCancelButtonTitle("取消")
		// .setOtherButtonTitles(phone)
		// // 设置颜色 必须一一对应
		// .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
		// .setCancelableOnTouchOutside(true)
		// .setListener(new ActionSheetListener() {
		//
		// @Override
		// public void onOtherButtonClick(ActionSheet actionSheet,
		// int index) {

		LatelyLinkeMan linkeMan = null;
		if (item instanceof LatelyLinkeMan) {

			LatelyLinkeMan l = (LatelyLinkeMan) item;
			PhoneUtils.makeCall(l.getCid(),l.getLinkeManName(), phone[0], mActivity);
		} else {
			PhoneInfo l = (PhoneInfo) item;
			PhoneUtils.makeCall(l.getContacts_id(),l.getName(), phone[0], mActivity);
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				onRefresh();
			}
		}, 1000);
		// }
		//
		// @Override
		// public void onDismiss(ActionSheet actionSheet,
		// boolean isCancel) {
		// // TODO Auto-generated method stub
		//
		// }
		// }).show();
	}

	class LinkedPhoneAdapter extends BaseAdapter {

		private ArrayList<PhoneInfo> inlinkePhones;

		public LinkedPhoneAdapter(ArrayList<PhoneInfo> inlinkePhones) {
			super();
			this.inlinkePhones = inlinkePhones;
		}

		@Override
		public int getCount() {
			return linkePhones.size();
		}

		@Override
		public PhoneInfo getItem(int position) {
			return linkePhones.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.item_listview_playphone, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			final PhoneInfo item = getItem(position);
			holder.tv_name.setText(item.getName());
			holder.tv_phone.setText(item.getPhone());
			holder.tv_time.setVisibility(View.GONE);
            holder.iv_phone.setVisibility(View.GONE);
            holder.ll_phone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showActionSheet(new String[] { item.getPhone() }, item);
				}
			});

//            holder.iv_phone.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Contacts cs = dbUtil.findFirst(Selector.from(
//                                Contacts.class).where("id", "=",
//                                item.getContacts_id()));
//                        if (cs.getGroup_id().equals("10000")) {
//                            Intent intent = new Intent(mActivity,
//                                    ActivityNumberTrainDetail.class);
//                            int id = cs.getId() - 1000000;
//                            intent.putExtra("id", id + "");
//                            startActivity(intent);
//                            mActivity.overridePendingTransition(
//                                    R.anim.in_from_right, R.anim.out_to_left);
//                        }else {
//                            Intent intent = new Intent(mActivity,
//                                    ActivityContactsInfo.class);
//                            intent.putExtra("contacts", cs);
//                            startActivityForResult(intent,
//                                    AndroidConfig.ContactsFragmentRequestCode);
//                            mActivity.overridePendingTransition(
//                                    R.anim.in_from_right, R.anim.out_to_left);
//                        }
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            });


			return convertView;
		}

		class ViewHolder {
			ImageView iv_phone;
			MyTextView tv_name;
			MyTextView tv_phone;
			MyTextView tv_time;
            LinearLayout ll_phone;

			public ViewHolder(View view) {
				iv_phone = (ImageView) view.findViewById(R.id.phoneImg);
				tv_name = (MyTextView) view.findViewById(R.id.phoneName);
				tv_time = (MyTextView) view.findViewById(R.id.phoneTime);
				tv_phone = (MyTextView) view.findViewById(R.id.phoneNum);
                ll_phone = (LinearLayout) view.findViewById(R.id.ll_phone);
				view.setTag(this);
			}
		}
	}

	@Override
	public void onRefresh() {
		initDate();
		numPlay.performClick();
	}

	@Override
	public boolean onLongClick(View arg0) {
		int id = arg0.getId();
		switch (id) {
		case R.id.dialdel:
			final CharSequence text = showPlayNumBox.getText();
			if (text.length() > 0) {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						for (int i = 0; i < text.length(); i++) {
							Message msg = new Message();
							msg.what = SIGN;
							handler.sendMessage(msg);
						}
					}
				}, 500);
			}
			break;
		}
		return false;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == SIGN) {
				CharSequence text = showPlayNumBox.getText();
				if (text.length() > 0) {
					showPlayNumBox.setText(text.subSequence(0,
							text.length() - 1));
				}
			}
		};
	};

    public void showActionSheet() {
        ActionSheet
                .createBuilder(getActivity(), getActivity().getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("新建联系人", "保存至现有联系人")
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF", "#1E82FF").setCancelableOnTouchOutside(true)
                .setListener(PlayPhoneFragment.this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
//                inputDialog = new InputDialog(getActivity(), R.style.MyDialogStyle);
//                inputDialog.setContent("添加联系人", "请输入联系人备注", "添加", "取消");
//                inputDialog.setCancleListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        inputDialog.dismiss();
//                    }
//                });
//                inputDialog.setOKListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//                        final String pecketName = inputDialog.getInputText();
//                        if (CommonUtils.isEmpty(pecketName)) {
//                            ToastUtils.Infotoast(getActivity(), "请输入联系人备注");
//                            return;
//                        }
//                        if (!CommonUtils.StringIsSurpass2(pecketName, 2, 12)) {
//                            ToastUtils.Errortoast(getActivity(), "联系人备注限制在1—12字之内");
//                            return;
//                        }
//                        inputDialog.dismiss();
//                        final LodingDialog lodingDialog = new LodingDialog(getActivity());
//                        lodingDialog.show();
//                        InteNetUtils.getInstance(getActivity()).contactsSynchro(latelyLinkeMan.getLinkeManPhone()+"::"+ pecketName,
//                                new RequestCallBack<String>() {
//                                    @Override
//                                    public void onSuccess(
//                                            ResponseInfo<String> arg0) {
//                                        lodingDialog.dismiss();
//                                        JSONObject jsonObject = null;
//                                        try {
//                                            jsonObject = new JSONObject(
//                                                    arg0.result);
//                                            inputDialog.dismiss();
//                                            try {
//                                                ContactsObject contactsObject = new ContactsObject();
//                                                contactsObject = contactsObject
//                                                        .contactsSynchroparseJSON(jsonObject);
//
//                                                final ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
//                                                for (Contacts contacts : contactsObject.getmContactss()) {
//                                                    ArrayList<PhoneInfo> phones = contacts.getPhones();
//                                                    mPhoneInfos.addAll(contacts.getPhones());
//                                                    latelyLinkeMan.setCid(contacts.getId());
//                                                    latelyLinkeMan.setLinkeManName(contacts.getName());
//                                                }
////                                                if(latelyLinkeMan.getId()!=0) {
////                                                    dbUtil.saveOrUpdate(latelyLinkeMan);
////                                                    dbUtil.update();
////                                                }
//                                                dbUtil.update(latelyLinkeMan,
//                                                        WhereBuilder.b("linkeManPhone","=",latelyLinkeMan.getLinkeManPhone()),"cid","linkeManName");
//
//                                                dbUtil.saveOrUpdateAll(contactsObject.getmContactss());
//                                                dbUtil.saveOrUpdateAll(mPhoneInfos);
//
//
//                                                ToastUtils.Infotoast(getActivity(), "保存成功!");
//                                                getActivity().sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
//                                                initDate();
//
//                                            } catch (NetRequestException e) {
//                                                ToastUtils.Infotoast(getActivity(), "保存失败!");
//                                                e.printStackTrace();
//                                            } catch (DbException e) {
//                                                ToastUtils.Infotoast(getActivity(), "保存失败!");
//                                                e.printStackTrace();
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(HttpException arg0,
//                                                          String arg1) {
//                                        lodingDialog.dismiss();
//                                        ToastUtils.Errortoast(getActivity(), "网络不可用");
//                                    }
//                                });
//                    }
//                });
//                inputDialog.show();

                Intent intentNew = new Intent(mActivity,ActivityAddNewFriend.class);
                intentNew.putExtra("latelyLinkeMan", latelyLinkeMan);
                startActivityForResult(intentNew,
                        AndroidConfig.ContactsFragmentRequestCode);
                mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case 1:
                Intent intent = new Intent(mActivity,ActivityAllContacts.class);
                intent.putExtra("latelyLinkeMan", latelyLinkeMan);
                startActivityForResult(intent,
                        AndroidConfig.ContactsFragmentRequestCode);
                mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AndroidConfig.writeFriendRefreshResultCode) {
            getActivity().sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
            initDate();
        }
    }
}
