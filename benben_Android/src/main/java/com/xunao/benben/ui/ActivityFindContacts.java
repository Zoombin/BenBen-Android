package com.xunao.benben.ui;

import in.srain.cube.image.CubeImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityContactsInfo;
import com.xunao.benben.ui.item.ActivityMyNumberTrain;
import com.xunao.benben.ui.item.ActivityMyNumberTrainDetail;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityFindContacts extends BaseActivity {
	private myAdapter adapter;
	private ListView listview;
	private String searchKey = "";
	private LinearLayout ll_seach_icon;
	private EditText search_edittext;
	private LinearLayout no_data;
	private ImageView iv_search_content_delect;
	private ArrayList<Contacts> contactsList = new ArrayList<Contacts>();
	private ArrayList temp = new ArrayList();

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_find_contacts);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (search_edittext != null) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					showKeyBoard(search_edittext);
				}
			}, 200);
		}

	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("查找联系人", "", "",
				R.drawable.icon_com_title_left, 0);

		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		search_edittext = (EditText) findViewById(R.id.search_edittext);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		no_data = (LinearLayout) findViewById(R.id.no_data);

		listview = (ListView) findViewById(R.id.listview);
		adapter = new myAdapter();
		listview.setAdapter(adapter);

		no_data.setVisibility(View.VISIBLE);
		iv_search_content_delect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_edittext.setText("");
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (contactsList.get(position).getGroup_id().equals("10000")) {
                    if(contactsList.get(position).getHuanxin_username().equals(user.getHuanxin_username())){
                        Intent intent = new Intent(mContext,
                                ActivityMyNumberTrainDetail.class);
                        int id = contactsList.get(position).getId() - 1000000;
                        intent.putExtra("id", id + "");
                        startActivity(intent);
                        mContext.overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_to_left);
                    }else {
                        Intent intent = new Intent(mContext,
                                ActivityNumberTrainDetail.class);
                        int id = contactsList.get(position).getId() - 1000000;
                        intent.putExtra("id", id + "");
                        startActivity(intent);
                        mContext.overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_to_left);
                    }



				} else {
					Intent intent = new Intent(mContext,
							ActivityContactsInfo.class);
//					intent.putExtra("contacts", contactsList.get(position));
//                    intent.putExtra("username", contactsList.get(position).getHuanxin_username());
                    intent.putExtra("infoid", contactsList.get(position).getId());
					startActivityForResult(intent,
							AndroidConfig.ContactsFragmentRequestCode);
					mContext.overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
//					AnimFinsh();
				}

			}
		});
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
	}

	int curSearchNum;

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		search_edittext.setImeOptions(EditorInfo.IME_ACTION_DONE);

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
					s = arg0.toString();
					new Thread() {
						public void run() {
							if (s.length() > 0) {
								// 更新关键字
								contactsList.clear();
								temp.clear();
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
															"%" + s + "%"))
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
																			+ "%")));

									for (PhoneInfo p : list2) {
										list.addAll(dbUtil.findAll(Selector
												.from(Contacts.class)
												.where(WhereBuilder.b("id",
														"=", p.getContacts_id()))));
									}

									list = CommonUtils.getNewList(list);

									for (Contacts c : (List<Contacts>) list) {
										if (c.getIs_benben().equals("0")) {
											temp.add(c);
										}
									}

									list.removeAll(temp);
									list.addAll(temp);

									if (last == curSearchNum) {
										contactsList = (ArrayList<Contacts>) list;
										mContext.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												if (contactsList.size() > 0) {
													adapter.notifyDataSetChanged();
													no_data.setVisibility(View.GONE);
													searchKey = s;
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

				} else {
					contactsList.clear();
					adapter.notifyDataSetChanged();
					ll_seach_icon.setVisibility(View.VISIBLE);
					iv_search_content_delect.setVisibility(View.GONE);
					searchKey = "";
				}

			}
		});

		iv_search_content_delect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_edittext.setText("");
			}
		});

		// search_edittext.setOnEditorActionListener(new
		// OnEditorActionListener() {
		// @Override
		// public boolean onEditorAction(TextView arg0, int actionId,
		// KeyEvent arg2) {
		// if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		// // 先隐藏键盘
		// ((InputMethodManager) search_edittext.getContext()
		// .getSystemService(Context.INPUT_METHOD_SERVICE))
		// .hideSoftInputFromWindow(mContext.getCurrentFocus()
		// .getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);

		// // 更新关键字
		// searchKey = "";
		// searchKey = search_edittext.getText().toString().trim();
		// contactsList.clear();
		// try {
		// List list = new ArrayList<Contacts>();
		// list = dbUtil.findAll(Selector
		// .from(Contacts.class)
		// .where(WhereBuilder.b("name", "like", "%"
		// + searchKey + "%"))
		// .orderBy("pinyin", false));
		//
		// contactsList = (ArrayList<Contacts>) list;
		// if (contactsList.size() > 0) {
		// adapter.notifyDataSetChanged();
		// no_data.setVisibility(View.GONE);
		// } else {
		// no_data.setVisibility(View.VISIBLE);
		// }
		//
		// } catch (DbException e) {
		// e.printStackTrace();
		// }

		// return true;
		// }
		// return false;
		// }
		// });

	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return contactsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return contactsList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final Contacts contacts = contactsList.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.contacts_list_item_layout, null);
			}
			CubeImageView item_phone_poster = ViewHolderUtil.get(convertView,
					R.id.item_phone_poster);
			TextView item_phone_name = ViewHolderUtil.get(convertView,
					R.id.item_phone_name);
			ImageView item_phone_single_bb = ViewHolderUtil.get(convertView,
					R.id.item_phone_single_bb);
			ImageView item_phone_single_bxw = ViewHolderUtil.get(convertView,
					R.id.item_phone_single_bxw);
			ImageView item_phone_single_call = ViewHolderUtil.get(convertView,
					R.id.item_phone_single_call);
            ImageView item_phone_single_ztc = ViewHolderUtil.get(convertView,
                    R.id.item_phone_single_ztc);

			CommonUtils.startImageLoader(cubeimageLoader, contacts.getPoster(),
					item_phone_poster);
			item_phone_name.setText(contacts.getName());

//			if (contacts.getIs_baixing().equals("0")) {
//				item_phone_single_bxw.setVisibility(View.GONE);
//			} else {
//				item_phone_single_bxw.setVisibility(View.VISIBLE);
//			}

			if (contacts.getIs_benben().equals("0") || contacts.getGroup_id().equals("10000")) {
				item_phone_single_bb.setVisibility(View.GONE);
			} else {
				item_phone_single_bb.setVisibility(View.VISIBLE);
			}

            List<PhoneInfo> phonesArrayList = null;
            try {
                phonesArrayList = dbUtil.findAll(Selector.from(PhoneInfo.class)
                        .where("contacts_id", "=", contacts.getId()));
            } catch (DbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            boolean ztcFlag = false;
            boolean baixingFlag = false;
            ArrayList<String> pString = new ArrayList<String>();
            if (phonesArrayList != null && phonesArrayList.size() > 0) {
                for (int i = 0; i < phonesArrayList.size(); i++) {
                    String phoneString = phonesArrayList.get(i).getPhone();
                    if (!("".equals(phoneString))) {
                        pString.add(phoneString);
                    }
                    String train_id = phonesArrayList.get(i).getTrain_id();
                    if(train_id!=null && !train_id.equals("") && !train_id.equals("0")){
                        ztcFlag = true;
                    }
                    String bx = phonesArrayList.get(i).getIs_baixing();
                    if(!bx.equals("0") && !phoneString.equals("")){
                        baixingFlag = true;
                    }
                }
            }
            if (baixingFlag && !contacts.getGroup_id().equals("10000")) {
                item_phone_single_bxw.setVisibility(View.VISIBLE);
            } else {
                item_phone_single_bxw.setVisibility(View.GONE);
            }
            if(ztcFlag && !contacts.getGroup_id().equals("10000")){
                item_phone_single_ztc.setVisibility(View.VISIBLE);
            }else{
                item_phone_single_ztc.setVisibility(View.GONE);
            }
            if (pString.size() == 0) {
                item_phone_single_call.setVisibility(View.GONE);
            } else {
                item_phone_single_call.setVisibility(View.VISIBLE);
            }


			item_phone_single_call.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					makeCall(contacts.getId() + "");
				}
			});

			return convertView;
		}
	}

	/**
	 * 拨打电话 dialog
	 */
	private void makeCall(String uid) {

		List<PhoneInfo> phonesArrayList = null;
		try {
			phonesArrayList = dbUtil.findAll(Selector.from(PhoneInfo.class)
					.where("contacts_id", "=", uid));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> pString = new ArrayList<String>();
		if (phonesArrayList != null && phonesArrayList.size() > 0) {
			for (int i = 0; i < phonesArrayList.size(); i++) {
				String phoneString = phonesArrayList.get(i).getPhone();
				if (!("".equals(phoneString))) {
					pString.add(phoneString);
				}
				String is_baixing = phonesArrayList.get(i).getIs_baixing();
				if (!("0".equals(is_baixing))) {
					pString.add(is_baixing);
				}
			}
		}

		if (pString.size() == 0) {
			ToastUtils.Errortoast(mContext, "该联系人无号码！");
		} else {
			final String[] phs = new String[pString.size()];
			for (int i = 0; i < pString.size(); i++) {
				phs[i] = pString.get(i);
			}
			mContext.setTheme(R.style.ActionSheetStyleIOS7);
			showActionSheet(phs,phonesArrayList.get(0).getContacts_id(), phonesArrayList.get(0).getName());
		}

	}

	public void showActionSheet(final String[] phone, final int contact_id, final String name) {
		ActionSheet
				.createBuilder(mContext, mContext.getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles(phone)
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {
						PhoneUtils.makeCall(contact_id,name, phone[index], mContext);

					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == AndroidConfig.ContactsFragmentResultCode) {
			sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
			AnimFinsh();
		}

        new Thread() {
            public void run() {
                if (searchKey.length() > 0) {
                    // 更新关键字
                    contactsList.clear();
                    temp.clear();
                    try {
                        List list = new ArrayList<Contacts>();
                        list = dbUtil.findAll(Selector
                                .from(Contacts.class)
                                .where(WhereBuilder
                                        .b("name", "like",
                                                "%" + searchKey + "%")
                                        .or("pinyin", "like",
                                                "%" + searchKey + "%")
                                        .or("is_baixing", "like",
                                                "%" + searchKey + "%")
                                        .or("is_benben", "like",
                                                "%" + searchKey + "%"))
                                .orderBy("pinyin", false));

                        List<PhoneInfo> list2 = dbUtil
                                .findAll(Selector
                                        .from(PhoneInfo.class)
                                        .where(WhereBuilder
                                                .b("phone",
                                                        "like",
                                                        "%"
                                                                + searchKey
                                                                + "%")
                                                .or("is_baixing",
                                                        "like",
                                                        "%"
                                                                + searchKey
                                                                + "%")
                                                .or("is_benben",
                                                        "like",
                                                        "%"
                                                                + searchKey
                                                                + "%")));

                        for (PhoneInfo p : list2) {
                            list.addAll(dbUtil.findAll(Selector
                                    .from(Contacts.class)
                                    .where(WhereBuilder.b("id",
                                            "=", p.getContacts_id()))));
                        }

                        list = CommonUtils.getNewList(list);

                        for (Contacts c : (List<Contacts>) list) {
                            if (c.getIs_benben().equals("0")) {
                                temp.add(c);
                            }
                        }

                        list.removeAll(temp);
                        list.addAll(temp);

                            contactsList = (ArrayList<Contacts>) list;
                            mContext.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (contactsList.size() > 0) {
                                        adapter.notifyDataSetChanged();
                                        no_data.setVisibility(View.GONE);
                                    } else {
                                        no_data.setVisibility(View.VISIBLE);
                                    }

                                }
                            });

                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();

		super.onActivityResult(requestCode, resultCode, data);
	}
}
