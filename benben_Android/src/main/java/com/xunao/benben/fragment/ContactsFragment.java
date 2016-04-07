package com.xunao.benben.fragment;

import in.srain.cube.image.CubeImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseFragment;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityFindContacts;
import com.xunao.benben.ui.ActivityNumberTrain;
import com.xunao.benben.ui.item.ActivityContactsInfo;
import com.xunao.benben.ui.item.ActivityEnterprisesContacts;
import com.xunao.benben.ui.item.ActivityMyNumberTrain;
import com.xunao.benben.ui.item.ActivityMyNumberTrainDetail;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.ui.item.ActivitySmallPublic;
import com.xunao.benben.ui.item.ContectManagement.ActivityPacketManagement;
import com.xunao.benben.ui.item.TallGroup.ActivityTalkGroup;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.XunaoLog;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ContactsFragment extends BaseFragment implements OnClickListener {

	private View view;

	private LinearLayout contacts_layout_zqtxl;
	private LinearLayout contacts_layout_qun;
	private LinearLayout contacts_layout_hmztc;

	private PopupWindow mPopupWindow;
	private ArrayList<Contacts> adapterList;

	private HashMap<String, Integer> groupIdAndPosition = new HashMap<String, Integer>();

	// data 数据
	private List<Contacts> searContacts;
	private ArrayList<ContactsGroup> mContactsGroups;
	private String searchKey = "";
	// 内容提供器
	private ContentResolver contentResolver;
    Dialog dialog;
    /**
	 * 0显示所有的人,1 显示搜索的人;
	 */
	private int ContactsShowType = 0;
    private boolean isNew = false;
    private String snapshot;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setShowLoding(false);
		view = inflater.inflate(R.layout.fragment_contacts, null);
		contentResolver = mActivity.getContentResolver();
        if(getHintState("help1")) {
            showHintDiaLog();
        }
		return view;
	}

	@Override
	protected void initView() {

		listView = (FloatingGroupExpandableListView) view
				.findViewById(R.id.list);
		listView.setGroupIndicator(null);
		nodata = view.findViewById(R.id.nodata);
		refreshBut = view.findViewById(R.id.refreshBut);
		nodata_contacts_layout_zqtxl = (LinearLayout) view
				.findViewById(R.id.contacts_layout_zqtxl);
		nodata_contacts_layout_qun = (LinearLayout) view
				.findViewById(R.id.contacts_layout_qun);
		nodata_contacts_layout_hmztc = (LinearLayout) view
				.findViewById(R.id.contacts_layout_hmztc);

		headView = LayoutInflater.from(mActivity).inflate(
				R.layout.contactsfragment_head, null);

		iv_search_content_delect = (ImageView) headView
				.findViewById(R.id.iv_search_content_delect);
		search_edittext = (EditText) headView
				.findViewById(R.id.search_edittext);
		ll_seach_icon = (LinearLayout) headView
				.findViewById(R.id.ll_seach_icon);
		contacts_layout_zqtxl = (LinearLayout) headView
				.findViewById(R.id.contacts_layout_zqtxl);
		contacts_layout_qun = (LinearLayout) headView
				.findViewById(R.id.contacts_layout_qun);
		contacts_layout_hmztc = (LinearLayout) headView
				.findViewById(R.id.contacts_layout_hmztc);
		listView.addHeaderView(headView);
	}

	@Override
	protected void initLinstener() {

		searchLinstener();

		refreshBut.setOnClickListener(this);

		nodata_contacts_layout_zqtxl.setOnClickListener(this);
		nodata_contacts_layout_qun.setOnClickListener(this);
		nodata_contacts_layout_hmztc.setOnClickListener(this);
		contacts_layout_zqtxl.setOnClickListener(this);
		contacts_layout_qun.setOnClickListener(this);
		contacts_layout_hmztc.setOnClickListener(this);
		iv_search_content_delect.setOnClickListener(this);
	}

	private void searchLinstener() {
		search_edittext.setFocusable(false);
		search_edittext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mActivity.startAnimActivity(ActivityFindContacts.class);
			}
		});
	}

	@Override
	protected void initDate() {
        snapshot =  CrashApplication.getInstance().getSpUtil().getSnapshot();
        if(snapshot.equals("1")){
            isNew = true;
        }



		if (AndroidConfig.AUTOLOGIN.equals(mActivity.getFrom())) {
			initlocakData();
			if (CommonUtils.isNetworkAvailable(mActivity)) {
//				InteNetUtils.getInstance(mActivity).GetContact(
//						getContactCallBack);
                InteNetUtils.getInstance(mActivity).AddressBook(snapshot, getContactBack);
			}

		} else if (mActivity.getFrom().equals("login")) {
			if (CommonUtils.isNetworkAvailable(mActivity)) {
				mActivity.showLoding("请稍后...");
//				InteNetUtils.getInstance(mActivity).GetContact(
//						getContactCallBack);
                InteNetUtils.getInstance(mActivity).AddressBook(snapshot, getContactBack);
			} else {
				initlocakData();
			}
		} else if (mActivity.getFrom().equals("register")) {
            CrashApplication.getInstance().getSpUtil().setSnapshot("1");
			// 匹配通讯录 由于耗时长 所以另开线程
			mActivity.showLoding("匹配通讯录...");
			new Thread() {
				public void run() {

					// 获取手机内的联系人信息
					ContentResolver contentResolver = mActivity
							.getContentResolver();
					// 仅获取联系人
					String phone = PhoneUtils.getOnlyContacts(contentResolver);
					InteNetUtils.getInstance(mActivity).PhoneMatch("", phone,
							mRequestCallBack);

				};
			}.start();

		}

	}

	// 读取本地数据库数据
    public void initlocakData() {
        try {
//			mContactsGroups = (ArrayList<ContactsGroup>) dbUtil
//					.findAll(ContactsGroup.class);
            mContactsGroups = new ArrayList<>();
            List<ContactsGroup> list =  dbUtil
                    .findAll(Selector.from(ContactsGroup.class).orderBy("sort"));
            for(int i=0;i<list.size();i++){
                mContactsGroups.add(list.get(i));
            }

            ArrayList<Contacts> getmContacts = new ArrayList<>();
            ArrayList<Contacts> com = new ArrayList<Contacts>();
            ArrayList<Contacts> baixing = new ArrayList<Contacts>();
            for (ContactsGroup cg : mContactsGroups) {
                getmContacts = cg.getmContacts();
                getmContacts.clear();
                com.clear();
                baixing.clear();
                List<Contacts> benbenContacts = dbUtil.findAll(Selector
                        .from(Contacts.class)
                        .where("group_id", "=", cg.getId())
                        .and("is_benben", "!=", "0")
                        .orderBy("pinyin", false));
                int benbenSize=0;
                if (benbenContacts != null) {
                    benbenSize = benbenContacts.size();
                    getmContacts.addAll(benbenContacts);
                }

                List<Contacts> otherContacts = dbUtil.findAll(Selector
                        .from(Contacts.class)
                        .where("group_id", "=", cg.getId())
                        .and("is_benben", "=", "0")
                        .orderBy("pinyin", false));
                if(otherContacts!=null && otherContacts.size()>0) {
                    for(int i=0;i<otherContacts.size();i++) {
                        List<PhoneInfo> phonesArrayList = null;
                        try {
                            phonesArrayList = dbUtil.findAll(Selector.from(PhoneInfo.class)
                                    .where("contacts_id", "=", otherContacts.get(i).getId()));
                        } catch (DbException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        boolean baixingFlag = false;
                        if (phonesArrayList != null && phonesArrayList.size() > 0) {
                            for (int j = 0; j < phonesArrayList.size(); j++) {
                                String phoneString = phonesArrayList.get(j).getPhone();
                                String bx = phonesArrayList.get(j).getIs_baixing();
                                if (!bx.equals("0") && !phoneString.equals("")) {
                                    baixingFlag = true;
                                    break;
                                }
                            }
                        }
                        if(baixingFlag){
                            baixing.add(otherContacts.get(i));
                        }else{
                            com.add(otherContacts.get(i));
                        }
                    }
                }
                getmContacts.addAll(baixing);
                getmContacts.addAll(com);

//				List<Contacts> findAll = dbUtil.findAll(Selector
//						.from(Contacts.class)
//						.where("group_id", "=", cg.getId())
//						.and("is_benben", "!=", "0")
//						.and("is_baixing", "!=", "0").orderBy("pinyin", false));
//
//				List<Contacts> isbenben = dbUtil.findAll(Selector
//						.from(Contacts.class)
//						.where("group_id", "=", cg.getId())
//						.and("is_benben", "!=", "0")
//						.and("is_baixing", "=", "0").orderBy("pinyin", false));
//
//				List<Contacts> isbaixing = dbUtil.findAll(Selector
//						.from(Contacts.class)
//						.where("group_id", "=", cg.getId())
//						.and("is_benben", "=", "0")
//						.and("is_baixing", "!=", "0").orderBy("pinyin", false));
//
//				List<Contacts> common = dbUtil.findAll(Selector
//						.from(Contacts.class)
//						.where("group_id", "=", cg.getId())
//						.and("is_benben", "=", "0").and("is_baixing", "=", "0")
//						.orderBy("pinyin", false));
//
//				if (findAll != null) {
//					getmContacts.addAll(findAll);
//				}
//
//				if (isbenben != null) {
//					getmContacts.addAll(isbenben);
//				}
//
//				if (isbaixing != null) {
//					getmContacts.addAll(isbaixing);
//				}
//				if (common != null) {
//					getmContacts.addAll(common);
//				}
//
//				// if (findAll != null) {
//
//				com.clear();
//				benben.clear();
//
//				for (Contacts contacts : getmContacts) {
//
//					// PhoneInfo firstPhone =
//					// dbUtil.findFirst(Selector.from(
//					// PhoneInfo.class).where("contacts_id", "=",
//					// contacts.getId()));
//					// if (firstPhone != null) {
//					// contacts.setIs_baixing(firstPhone.getIs_baixing());
//					// contacts.setIs_benben(firstPhone.getIs_benben());
//					// } else {
//					// contacts.setIs_baixing("0");
//					// contacts.setIs_benben("0");
//					// }
//					if (!contacts.getIs_benben().equals("0")) {
//						benben.add(contacts);
//					} else {
//						com.add(contacts);
//					}
//				}
//				// Collections.sort(com);
//				// Collections.sort(benben);
//				// }
//				getmContacts.clear();
//				getmContacts.addAll(benben);
//				getmContacts.addAll(com);
                cg.setProportion(benbenSize+ "/" + getmContacts.size());
            }
            ContactsObject contactsObject = new ContactsObject();
            contactsObject.setmContactsGroups(mContactsGroups);
            crashApplication.setContactsObject(contactsObject);

            groupOrderBy();

            if (wrapperAdapter == null) {
                ContatctsAdapter adapter = new ContatctsAdapter();
                wrapperAdapter = new WrapperExpandableListAdapter(adapter);
                listView.setAdapter(wrapperAdapter);
            } else {
                wrapperAdapter.notifyDataSetChanged();
            }
            // 获得所有数据 方便传值
            crashApplication.contactsObject = new ContactsObject();
            crashApplication.contactsObject.setmContactsGroups(mContactsGroups);

        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
	protected void onSuccess(JSONObject jsonObject) {
//		mActivity.dissLoding();
        InteNetUtils.getInstance(mActivity).AddressBook(snapshot, getContactBack);
//        getMatchData(jsonObject);

	}

    /**
     * 获取服务器拉取过来的数据
     *
     * @param jsonObject
     */
    private void getMatchData(JSONObject jsonObject) {
        final ContactsObject contactsObject = new ContactsObject();
        try {
//			XunaoLog.yLog().i(jsonObject.toString());

            contactsObject.parseJSON(jsonObject);
            crashApplication.setContactsObject(contactsObject);
            mContactsGroups = crashApplication.contactsObject
                    .getmContactsGroups();


            final ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
            for (Contacts contacts : contactsObject.getmContactss()) {
                ArrayList<PhoneInfo> phones = contacts.getPhones();
                mPhoneInfos.addAll(contacts.getPhones());
            }

            new Thread() {
                @Override
                public void run() {
                    groupOrderBy();

                    ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
                    // ArrayList<Contacts> com = new ArrayList<Contacts>();
                    // ArrayList<Contacts> benben = new ArrayList<Contacts>();
                    for (ContactsGroup cg : mContactsGroups) {
                        // 记录所有的phone信息
                        ArrayList<Contacts> mContacts = cg.getmContacts();
                        // com.clear();
                        // benben.clear();

                        for (Contacts contacts : mContacts) {
                            // if (!contacts.getIs_benben().equals("0")) {
                            // benben.add(contacts);
                            // } else {
                            // com.add(contacts);
                            // }
                            ArrayList<PhoneInfo> phones = contacts.getPhones();
                            mPhoneInfos.addAll(contacts.getPhones());
                        }
                    }

                    // // 持久化一个环信与本地数据的hashmap
                    // HashMap<String, Object> huanXinMap = new HashMap<String,
                    // Object>();
                    // if (mContacts != null && mContacts.size() > 0) {
                    // for (Contacts cs : mContacts) {
                    // if (!"0".equals(cs.getIs_benben())) {
                    // huanXinMap.put(cs.getHuanxin_username(), cs);
                    // }
                    // }
                    // }
                    // crashApplication.getInstance().setHuanXinMap(huanXinMap);

                    try {

                        dbUtil.deleteAll(ContactsGroup.class);
                        dbUtil.deleteAll(Contacts.class);
                        dbUtil.deleteAll(PhoneInfo.class);

                        dbUtil.saveOrUpdateAll(mContactsGroups);
                        dbUtil.saveOrUpdateAll(contactsObject.getmContactss());
                        // }
                        dbUtil.saveOrUpdateAll(mPhoneInfos);

                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                initlocakData();
                                mActivity.dissLoding();
                                nodata.setVisibility(View.GONE);
                                if (lodingDialog != null) {
                                    lodingDialog.dismiss();
                                }
                            }
                        });

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (NetRequestException e) {
            e.getError().print(mActivity);
            nodata.setVisibility(View.VISIBLE);
            // initlocakData();
        }
    }

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		mActivity.dissLoding();
		nodata.setVisibility(View.VISIBLE);
	}

	// animation executor
	// class AnimationExecutor implements
	// ExpandableStickyListHeadersListView.IAnimationExecutor {
	//
	// @Override
	// public void executeAnim(final View target, final int animType) {
	// if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType
	// && target.getVisibility() == View.VISIBLE) {
	// return;
	// }
	// if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType
	// && target.getVisibility() != View.VISIBLE) {
	// return;
	// }
	// if (mOriginalViewHeightPool.get(target) == null) {
	// mOriginalViewHeightPool.put(target, target.getHeight());
	// }
	// final int viewHeight = mOriginalViewHeightPool.get(target);
	// float animStartY = animType ==
	// ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f
	// : viewHeight;
	// float animEndY = animType ==
	// ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight
	// : 0f;
	// final ViewGroup.LayoutParams lp = target.getLayoutParams();
	// ValueAnimator animator = ValueAnimator
	// .ofFloat(animStartY, animEndY);
	// // 动画 时间 原200
	// animator.setDuration(0);
	// target.setVisibility(View.VISIBLE);
	// animator.addListener(new Animator.AnimatorListener() {
	// @Override
	// public void onAnimationStart(Animator animator) {
	// }
	//
	// @Override
	// public void onAnimationEnd(Animator animator) {
	// if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
	// target.setVisibility(View.VISIBLE);
	// } else {
	// target.setVisibility(View.GONE);
	// }
	// target.getLayoutParams().height = viewHeight;
	// }
	//
	// @Override
	// public void onAnimationCancel(Animator animator) {
	//
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animator animator) {
	//
	// }
	// });
	// animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	// @Override
	// public void onAnimationUpdate(ValueAnimator valueAnimator) {
	// lp.height = ((Float) valueAnimator.getAnimatedValue())
	// .intValue();
	// target.setLayoutParams(lp);
	// target.requestLayout();
	// }
	// });
	// animator.start();
	//
	// }
	// }

	protected InfoMsgHint hint;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contacts_layout_zqtxl:

			if (CrashApplication.getInstance().user.getEnterprise_disable() == 0) {
				((BaseActivity) getActivity())
						.startAnimActivity(ActivityEnterprisesContacts.class);
			} else if (CrashApplication.getInstance().user
					.getEnterprise_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getEnterprise_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}

			break;
		case R.id.iv_search_content_delect:
			search_edittext.setText("");
			break;
		case R.id.contacts_layout_qun:
			if (CrashApplication.getInstance().user.getGroup_disable() == 0) {
				mActivity.startAnimActivity(ActivityTalkGroup.class);
			} else if (CrashApplication.getInstance().user
					.getGroup_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getGroup_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}

			break;
		case R.id.contacts_layout_hmztc:
			((BaseActivity) getActivity())
					.startAnimActivity(ActivityNumberTrain.class);
			break;
		case R.id.refreshBut:
			// 刷新
			if (CommonUtils.isNetworkAvailable(mActivity)) {
				mActivity.showLoding("请稍等...");
				refreshData();
			}
			break;
		// 长按
		case R.id.management_packet:

			mActivity.startAnimActivity(ActivityPacketManagement.class);

			mPopupWindow.dismiss();
			break;
		default:
			break;
		}
	}

	public void refreshData() {
        InteNetUtils.getInstance(mActivity).AddressBook(snapshot, getContactBack);
//		InteNetUtils.getInstance(mActivity).GetContact(getContactCallBack);
	}

	/*
	 * 获取PopupWindow实例
	 */
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
		LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
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

//	private RequestCallBack<String> getContactCallBack = new RequestCallBack<String>() {
//		@Override
//		public void onSuccess(ResponseInfo<String> arg0) {
//			JSONObject jsonObject = null;
//			try {
//				jsonObject = new JSONObject(arg0.result);
//                Log.d("ltf","jsonObject====2======="+jsonObject);
//				getData(jsonObject);
//			} catch (JSONException e) {
//				initlocakData();
//				e.printStackTrace();
//			}
//
//		}
//
//		@Override
//		public void onFailure(HttpException arg0, String arg1) {
//			ToastUtils.Errortoast(mActivity, "服务器数据匹配失败");
//			initlocakData();
//			mActivity.dissLoding();
//		}
//	};

    private RequestCallBack<String> getContactBack = new RequestCallBack<String>() {
        @Override
        public void onSuccess(ResponseInfo<String> arg0) {
            mActivity.dissLoding();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(arg0.result);
                Log.d("ltf","jsonObject====1======="+jsonObject);
                getData(jsonObject);
            } catch (JSONException e) {
                initlocakData();
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(HttpException arg0, String arg1) {
            ToastUtils.Errortoast(mActivity, "服务器数据匹配失败");
            initlocakData();
            mActivity.dissLoding();
        }
    };
	private View headView;
	private View nodata;
	private View refreshBut;
	private LinearLayout nodata_contacts_layout_zqtxl;
	private LinearLayout nodata_contacts_layout_qun;
	private LinearLayout nodata_contacts_layout_hmztc;
	private EditText search_edittext;
	private LinearLayout ll_seach_icon;
	private ImageView iv_search_content_delect;

	private FloatingGroupExpandableListView listView;

	private WrapperExpandableListAdapter wrapperAdapter;

	public void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		if (resultCode == AndroidConfig.ContactsFragmentResultCode) {
			// groupOrderBy();
			// ArrayList<Contacts> getmContacts = null;
			// ArrayList<Contacts> com = new ArrayList<Contacts>();
			// ArrayList<Contacts> benben = new ArrayList<Contacts>();
			// for (ContactsGroup cg : mContactsGroups) {
			// getmContacts = cg.getmContacts();
			// com.clear();
			// benben.clear();
			// for (Contacts contacts : getmContacts) {
			// if (!contacts.getIs_benben().equals("0")) {
			// benben.add(contacts);
			// } else {
			// com.add(contacts);
			// }
			// }
			// Collections.sort(com);
			// Collections.sort(benben);
			// getmContacts.clear();
			// getmContacts.addAll(benben);
			// getmContacts.addAll(com);
			// cg.setProportion(benben.size() + "/" + getmContacts.size());
			// }
			//
			// ContatctsAdapter adapter = new ContatctsAdapter();
			// wrapperAdapter = new WrapperExpandableListAdapter(adapter);
			// listView.setAdapter(wrapperAdapter);
			// // 获得所有数据 方便传值
			// crashApplication.contactsObject = new ContactsObject();
			// crashApplication.contactsObject.setmContactsGroups(mContactsGroups);

			initlocakData();

		}

	}

	@Override
	public void onResume() {
		super.onResume();

		if (mActivity.user.isUpdate()) {
			wrapperAdapter.notifyDataSetChanged();
			mActivity.user.setUpdate(false);
		} else if (CrashApplication.getInstance().isAddContacts) {
			CrashApplication.getInstance().isAddContacts = false;
			initlocakData();
		} else {
			if (wrapperAdapter != null) {
				wrapperAdapter.notifyDataSetChanged();
			}

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
                    String is_baixing = phonesArrayList.get(i).getIs_baixing();
                    if (!("0".equals(is_baixing))) {
                        pString.add(is_baixing);
                    }
				}

			}
		}

		if (pString.size() == 0) {
			ToastUtils.Errortoast(mActivity, "该联系人无号码！");
		} else {
			final String[] phs = new String[pString.size()];
			for (int i = 0; i < pString.size(); i++) {
				phs[i] = pString.get(i);
			}
			mActivity.setTheme(R.style.ActionSheetStyleIOS7);
			showActionSheet(phs,phonesArrayList.get(0).getContacts_id(), phonesArrayList.get(0).getName());
		}

	}

	public void showActionSheet(final String[] phone,final int contact_id, final String name) {
		ActionSheet
				.createBuilder(mActivity, mActivity.getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles(phone)
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {
						PhoneUtils.makeCall(contact_id, name, phone[index], mActivity);

					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	/**
	 * 获取服务器拉取过来的数据
	 * 
	 * @param jsonObject
	 */
	private void getData(JSONObject jsonObject) {
		final ContactsObject contactsObject = new ContactsObject();
		try {
//			XunaoLog.yLog().i(jsonObject.toString());
            JSONObject delJsonObject = jsonObject.getJSONObject("delete");
            if(delJsonObject!=null && !delJsonObject.equals("")){
                if(delJsonObject.has("group")) {
                    JSONArray delGroupJsonArray = delJsonObject.getJSONArray("group");
                    if (delGroupJsonArray != null && delGroupJsonArray.length() > 0) {
                        int[] groupIds = new int[delGroupJsonArray.length()];
                        for (int i = 0; i < delGroupJsonArray.length(); i++) {
                            int groupId = delGroupJsonArray.getInt(i);
                            groupIds[i] = groupId;
                        }
                        dbUtil.delete(ContactsGroup.class, WhereBuilder.b("id", "in", groupIds));
                    }
                }
                if(delJsonObject.has("contact")) {
                    JSONArray delContactJsonArray = delJsonObject.getJSONArray("contact");
                    if (delContactJsonArray != null && delContactJsonArray.length() > 0) {
                        int[] contactIds = new int[delContactJsonArray.length()];
                        for (int i = 0; i < delContactJsonArray.length(); i++) {
                            int contactId = delContactJsonArray.getInt(i);
                            contactIds[i] = contactId;
                        }
                        dbUtil.delete(Contacts.class, WhereBuilder.b("id", "in", contactIds));
                        dbUtil.delete(PhoneInfo.class, WhereBuilder.b("contacts_id", "in", contactIds));
                    }
                }
            }
            CrashApplication.getInstance().getSpUtil().setSnapshot(jsonObject.optString("snapshot"));
			contactsObject.parseJSON(jsonObject.getJSONObject("add"));
			crashApplication.setContactsObject(contactsObject);
			mContactsGroups = crashApplication.contactsObject
					.getmContactsGroups();


            final ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
            for (Contacts contacts : contactsObject.getmContactss()) {
                ArrayList<PhoneInfo> phones = contacts.getPhones();

                mPhoneInfos.addAll(contacts.getPhones());
            }

			new Thread() {
				@Override
				public void run() {
					groupOrderBy();

////					ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
//					// ArrayList<Contacts> com = new ArrayList<Contacts>();
//					// ArrayList<Contacts> benben = new ArrayList<Contacts>();
//					for (ContactsGroup cg : mContactsGroups) {
//						// 记录所有的phone信息
//						ArrayList<Contacts> mContacts = cg.getmContacts();
//						// com.clear();
//						// benben.clear();
//
//						for (Contacts contacts : mContacts) {
//							// if (!contacts.getIs_benben().equals("0")) {
//							// benben.add(contacts);
//							// } else {
//							// com.add(contacts);
//							// }
//							ArrayList<PhoneInfo> phones = contacts.getPhones();
//                            Log.d("ltf","phones======"+phones.size());
//							mPhoneInfos.addAll(contacts.getPhones());
//						}
//					}

					// // 持久化一个环信与本地数据的hashmap
					// HashMap<String, Object> huanXinMap = new HashMap<String,
					// Object>();
					// if (mContacts != null && mContacts.size() > 0) {
					// for (Contacts cs : mContacts) {
					// if (!"0".equals(cs.getIs_benben())) {
					// huanXinMap.put(cs.getHuanxin_username(), cs);
					// }
					// }
					// }
					// crashApplication.getInstance().setHuanXinMap(huanXinMap);

					try {
                        if(isNew) {
                            dbUtil.deleteAll(ContactsGroup.class);
                            dbUtil.deleteAll(Contacts.class);
                            dbUtil.deleteAll(PhoneInfo.class);
                            isNew = false;
                        }

						dbUtil.saveOrUpdateAll(mContactsGroups);
                        dbUtil.saveOrUpdateAll(contactsObject.getmContactss());
						// }
						dbUtil.saveOrUpdateAll(mPhoneInfos);

						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								initlocakData();
								mActivity.dissLoding();
								nodata.setVisibility(View.GONE);
								if (lodingDialog != null) {
									lodingDialog.dismiss();
								}
							}
						});

					} catch (DbException e) {
						e.printStackTrace();
					}
				}
			}.start();

		} catch (NetRequestException e) {
			e.getError().print(mActivity);
			nodata.setVisibility(View.VISIBLE);
			// initlocakData();
		} catch (JSONException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

	private void groupOrderBy() {
		int size = mContactsGroups.size();
		// 对组群排序,未分组放在后面
		ContactsGroup unGroup = null;
		ContactsGroup unGroup2 = null;
		for (int i = 0; i < size; i++) {
			if (mContactsGroups.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = mContactsGroups.remove(i);
				break;
			}

		}
		if (unGroup != null) {
			mContactsGroups.add(unGroup);
		}

		for (int i = 0; i < size; i++) {
			if (mContactsGroups.get(i).getName().equalsIgnoreCase("常用号码直通车")) {
				unGroup2 = mContactsGroups.remove(i);
				break;
			}
		}

		if (unGroup2 != null) {
			mContactsGroups.add(unGroup2);
		}

	}

	class ContatctsAdapter extends BaseExpandableListAdapter {

		public ContatctsAdapter() {
			super();
			groupIdAndPosition.clear();
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			groupIdAndPosition.clear();
		}

		@Override
		public Contacts getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return mContactsGroups.get(groupPosition).getmContacts()
					.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final Contacts cs = getChild(groupPosition, childPosition);
			holder = new ViewHolder();
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.contacts_list_item_layout, null);
				holder.item_phone_poster = (CubeImageView) convertView
						.findViewById(R.id.item_phone_poster);
				holder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				holder.item_phone_single_bb = (ImageView) convertView
						.findViewById(R.id.item_phone_single_bb);
				holder.item_phone_single_bxw = (ImageView) convertView
						.findViewById(R.id.item_phone_single_bxw);
                holder.item_phone_single_ztc = (ImageView) convertView
                        .findViewById(R.id.item_phone_single_ztc);
				holder.item_phone_single_call = (ImageView) convertView
						.findViewById(R.id.item_phone_single_call);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String poster = cs.getPoster();
            if(poster!=null && !poster.equals("")) {
                CommonUtils.startImageLoader(cubeimageLoader, poster,
                        holder.item_phone_poster);
            }else{
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
                        holder.item_phone_poster);
            }

			// if (cs.getId().equals("-1")) {
			// convertView.setLayoutParams(new LayoutParams(0, 0));
			// } else {
			// convertView.setLayoutParams(new AbsListView.LayoutParams(
			// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			holder.item_phone_name.setText(cs.getName());
			// }

			if ("0".equals(cs.getIs_benben()) || cs.getGroup_id().equals("10000")) {
				holder.item_phone_single_bb.setVisibility(View.GONE);
			} else {
				holder.item_phone_single_bb.setVisibility(View.VISIBLE);
			}


            List<PhoneInfo> phonesArrayList = null;
            try {
                phonesArrayList = dbUtil.findAll(Selector.from(PhoneInfo.class)
                        .where("contacts_id", "=", cs.getId()));
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

            if (baixingFlag && !cs.getGroup_id().equals("10000")) {
                holder.item_phone_single_bxw.setVisibility(View.VISIBLE);
            } else {
                holder.item_phone_single_bxw.setVisibility(View.GONE);
            }
            if(ztcFlag && !cs.getGroup_id().equals("10000")){
                holder.item_phone_single_ztc.setVisibility(View.VISIBLE);
            }else{
                holder.item_phone_single_ztc.setVisibility(View.GONE);
            }
            if (pString.size() == 0) {
                holder.item_phone_single_call.setVisibility(View.GONE);
            } else {
                holder.item_phone_single_call.setVisibility(View.VISIBLE);
            }
			holder.item_phone_single_call
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							makeCall(cs.getId() + "");
						}
					});

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (cs.getGroup_id().equals("10000")) {
                        if(cs.getHuanxin_username().equals(CrashApplication.getInstance().user.getHuanxin_username())){
                            Intent intent = new Intent(mActivity,
                                    ActivityMyNumberTrainDetail.class);
                            int id = cs.getId() - 1000000;
                            intent.putExtra("id", id + "");
                            startActivity(intent);
                            mActivity.overridePendingTransition(
                                    R.anim.in_from_right, R.anim.out_to_left);
                        }else {
                            Intent intent = new Intent(mActivity,
                                    ActivityNumberTrainDetail.class);
                            int id = cs.getId() - 1000000;
                            intent.putExtra("id", id + "");
                            startActivity(intent);
                            mActivity.overridePendingTransition(
                                    R.anim.in_from_right, R.anim.out_to_left);
                        }
					} else {
						Intent intent = new Intent(mActivity,
								ActivityContactsInfo.class);
//						intent.putExtra("contacts", cs);
//                        String hxName =  cs.getHuanxin_username();
//                        if(hxName!=null && !hxName.equals("")) {
//                            intent.putExtra("username", cs.getHuanxin_username());
//                        }else{
//                            intent.putExtra("contacts", cs);
//                        }
                        intent.putExtra("infoid", cs.getId());

						startActivityForResult(intent,
								AndroidConfig.ContactsFragmentRequestCode);
						mActivity.overridePendingTransition(
								R.anim.in_from_right, R.anim.out_to_left);
					}
				}
			});

			return convertView;

		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mContactsGroups.get(groupPosition).getmContacts().size();
		}

		@Override
		public ContactsGroup getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mContactsGroups.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mContactsGroups.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final HeaderViewHolder holder;
			final ContactsGroup mContactsGroup = getGroup(groupPosition);
			groupIdAndPosition.put(mContactsGroup.getId() + "", groupPosition);
			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = View.inflate(mActivity, R.layout.contacts_header,
						null);
				holder.group_name = (TextView) convertView
						.findViewById(R.id.group_name);
				holder.status_img = (ImageView) convertView
						.findViewById(R.id.status_img);
				holder.group_num = (TextView) convertView
						.findViewById(R.id.group_num);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
			CharSequence headerChar = mContactsGroup.getName();

			holder.group_name.setText(headerChar);
			if (mContactsGroup.getId() != 10000) {
				holder.group_num.setText(mContactsGroup.getProportion());
			} else {
				if (mContactsGroup.getmContacts().size() == 0) {
					holder.group_num.setText("0");
				} else {
					holder.group_num.setText(mContactsGroup.getmContacts()
							.size() + "");
				}
			}
			final View headview = convertView;

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			// 长按弹出管理分组
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {

					if (mContactsGroup.getId() != 10000) {
						getPopupWindowInstance();
						mPopupWindow
								.showAsDropDown(headview,
										(mActivity.mScreenWidth - PixelUtil
												.dp2px(95)) / 2, 0 - PixelUtil
												.dp2px(94));
					}
					return true;
				}
			});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (listView.isGroupExpanded(groupPosition)) {
						listView.collapseGroup(groupPosition);
						holder.status_img
								.setImageResource(R.drawable.icon_contacts_single_right);
					} else {
						listView.expandGroup(groupPosition,true);
						holder.status_img
								.setImageResource(R.drawable.icon_contacts_single_down);
					}

				}
			});

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	class HeaderViewHolder {
		TextView group_name;
		ImageView status_img;
		TextView group_num;
	}

	class ViewHolder {
		CubeImageView item_phone_poster;
		TextView item_phone_name;
		ImageView item_phone_single_bb;
		ImageView item_phone_single_bxw;
        ImageView item_phone_single_ztc;
		ImageView item_phone_single_call;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

    private void showHintDiaLog(){
        updatHintState("help1",false);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View hintdialogView = inflater.inflate(R.layout.help_hint_dialog, null);
        ImageView iv_hint_help = (ImageView) hintdialogView.findViewById(R.id.iv_hint_help);
        iv_hint_help.setImageResource(R.drawable.img_help1);
        iv_hint_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = new Dialog(getActivity(),R.style.hintDialog);
        // 设置它的ContentView
        dialog.setContentView(hintdialogView);
        Window dialogWindow = dialog.getWindow();
//        //设置在底部
//        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //设置对话框宽
        p.width = d.getWidth();
        p.height = d.getHeight();
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    //获取帮助是否提示
    public boolean getHintState(String key) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        if(sharedPreferences.contains(key)){
            return sharedPreferences.getBoolean(key, true);
        }else{
            return true;
        }
    }

    //修改帮助是否提示
    public void updatHintState(String key,boolean flag) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        //创建数据编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        //传递需要保存的数据
        editor.putBoolean(key, flag);
        //保存数据
        editor.commit();
    }
}
