//package com.xunao.benben.fragment;
//
//import in.srain.cube.image.CubeImageView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.WeakHashMap;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
//import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
//import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnFocusChangeListener;
//import android.view.View.OnLongClickListener;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.TextView.OnEditorActionListener;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.SectionIndexer;
//import android.widget.TextView;
//
//import com.lidroid.xutils.db.sqlite.Selector;
//import com.lidroid.xutils.db.sqlite.WhereBuilder;
//import com.lidroid.xutils.exception.DbException;
//import com.lidroid.xutils.exception.HttpException;
//import com.lidroid.xutils.http.ResponseInfo;
//import com.lidroid.xutils.http.callback.RequestCallBack;
//import com.xunao.benben.R;
//import com.xunao.benben.base.BaseActivity;
//import com.xunao.benben.base.BaseFragment;
//import com.xunao.benben.bean.Contacts;
//import com.xunao.benben.bean.ContactsGroup;
//import com.xunao.benben.bean.ContactsObject;
//import com.xunao.benben.bean.PhoneInfo;
//import com.xunao.benben.config.AndroidConfig;
//import com.xunao.benben.dialog.LodingDialog;
//import com.xunao.benben.exception.NetRequestException;
//import com.xunao.benben.net.InteNetUtils;
//import com.xunao.benben.ui.ActivityNumberTrain;
//import com.xunao.benben.ui.item.ActivityContactsInfo;
//import com.xunao.benben.ui.item.ActivityTalkGroup;
//import com.xunao.benben.ui.item.Activity_Packet_Management;
//import com.xunao.benben.utils.CommonUtils;
//import com.xunao.benben.utils.PhoneUtils;
//import com.xunao.benben.utils.PixelUtil;
//import com.xunao.benben.utils.ToastUtils;
//import com.xunao.benben.utils.XunaoLog;
//import com.xunao.benben.view.ActionSheet;
//import com.xunao.benben.view.ActionSheet.ActionSheetListener;
//
//public class CopyOfContactsFragment extends BaseFragment implements
//		OnClickListener {
//
//	private View view;
//	private TestBaseAdapter mTestBaseAdapter;
//
//	private LinearLayout contacts_layout_zqtxl;
//	private LinearLayout contacts_layout_qun;
//	private LinearLayout contacts_layout_hmztc;
//
//	private PopupWindow mPopupWindow;
//	private ArrayList<Contacts> adapterList;
//
//	private ExpandableStickyListHeadersListView mListView;
//	private WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();
//
//	// data 数据
//	private List<Contacts> searContacts;
//	private ArrayList<Contacts> mContacts = new ArrayList<Contacts>();
//	private ArrayList<ContactsGroup> mContactsGroups = new ArrayList<ContactsGroup>();
//	private HashMap<String, ContactsGroup> mapGroup = new HashMap<String, ContactsGroup>();
//	private ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();
//	private String searchKey = "";
//	// 内容提供器
//	private ContentResolver contentResolver;
//
//	/**
//	 * 0显示所有的人,1 显示搜索的人;
//	 */
//	private int ContactsShowType = 0;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
//		view = inflater.inflate(R.layout.fragment_contacts, null);
//		contentResolver = mActivity.getContentResolver();
//		return view;
//	}
//
//	@Override
//	protected void initView() {
//		mListView = (ExpandableStickyListHeadersListView) view
//				.findViewById(R.id.list);
//		mListView.setDividerHeight(0);
//
//		nodata = view.findViewById(R.id.nodata);
//		refreshBut = view.findViewById(R.id.refreshBut);
//		nodata_contacts_layout_zqtxl = (LinearLayout) view
//				.findViewById(R.id.contacts_layout_zqtxl);
//		nodata_contacts_layout_qun = (LinearLayout) view
//				.findViewById(R.id.contacts_layout_qun);
//		nodata_contacts_layout_hmztc = (LinearLayout) view
//				.findViewById(R.id.contacts_layout_hmztc);
//
//		headView = LayoutInflater.from(mActivity).inflate(
//				R.layout.contactsfragment_head, null);
//
//		iv_search_content_delect = (ImageView) headView
//				.findViewById(R.id.iv_search_content_delect);
//		search_edittext = (EditText) headView
//				.findViewById(R.id.search_edittext);
//		ll_seach_icon = (LinearLayout) headView
//				.findViewById(R.id.ll_seach_icon);
//		contacts_layout_zqtxl = (LinearLayout) headView
//				.findViewById(R.id.contacts_layout_zqtxl);
//		contacts_layout_qun = (LinearLayout) headView
//				.findViewById(R.id.contacts_layout_qun);
//		contacts_layout_hmztc = (LinearLayout) headView
//				.findViewById(R.id.contacts_layout_hmztc);
//
//		mListView.addHeaderView(headView);
//	}
//
//	@Override
//	protected void initLinstener() {
//
//		searchLinstener();
//
//		refreshBut.setOnClickListener(this);
//		mListView
//				.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
//
//					@Override
//					public void onHeaderClick(StickyListHeadersListView l,
//							View header, int itemPosition, long headerId,
//							boolean currentlySticky) {
//
//						ContactsGroup contactsGroup = mapGroup.get(adapterList
//								.get(itemPosition).getGroup_id());
//
//						int position = 0;
//
//						ArrayList<Contacts> curlist = ContactsShowType == 0 ? mContacts
//								: (ArrayList<Contacts>) searContacts;
//
//						for (int i = 0; i < mContactsGroups.size(); i++) {
//
//							if (mContactsGroups.get(i).getId()
//									.equalsIgnoreCase(contactsGroup.getId())) {
//								position = i + 1;
//								break;
//							}
//
//						}
//
//						contactsGroup.setOpen(!contactsGroup.isOpen());
//
//						ArrayList<Contacts> nContacts = new ArrayList<Contacts>();
//						for (ContactsGroup cg : mContactsGroups) {
//							if (cg.isOpen()) {
//								for (Contacts cs : curlist) {
//									if (cg.getId().equals(cs.getGroup_id())) {
//										nContacts.add(cs);
//									}
//								}
//							}
//						}
//						ImageView status_img = (ImageView) header
//								.findViewById(R.id.status_img);
//						if (mListView.isHeaderCollapsed(headerId)) {
//							mListView.expand(headerId);
//							status_img
//									.setImageResource(R.drawable.icon_contacts_single_down);
//						} else {
//							mListView.collapse(headerId);
//							status_img
//									.setImageResource(R.drawable.icon_contacts_single_right);
//						}
//						mTestBaseAdapter = new TestBaseAdapter(mActivity,
//								nContacts);
//						mListView.setAdapter(mTestBaseAdapter);
//
//						mListView.setSelection(position);
//
//					}
//				});
//
//		nodata_contacts_layout_zqtxl.setOnClickListener(this);
//		nodata_contacts_layout_qun.setOnClickListener(this);
//		nodata_contacts_layout_hmztc.setOnClickListener(this);
//		contacts_layout_zqtxl.setOnClickListener(this);
//		contacts_layout_qun.setOnClickListener(this);
//		contacts_layout_hmztc.setOnClickListener(this);
//	}
//
//	private void searchLinstener() {
//
//		search_edittext.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1,
//					int arg2, int arg3) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable arg0) {
//				if (arg0.length() > 0) {
//					ll_seach_icon.setVisibility(View.GONE);
//					iv_search_content_delect.setVisibility(View.VISIBLE);
//					searchKey = search_edittext.getText().toString().trim();
//				} else {
//					ll_seach_icon.setVisibility(View.VISIBLE);
//					iv_search_content_delect.setVisibility(View.GONE);
//					searchKey = "";
//					ContactsShowType = 0;
//					for (int i = 0; i < mContactsGroups.size(); i++) {
//						ContactsGroup contactsGroup = mContactsGroups.get(i);
//						mListView.collapse(Long.valueOf(contactsGroup.getId()));
//					}
//					mTestBaseAdapter = new TestBaseAdapter(mActivity,
//							(ArrayList) searContacts);
//					mListView.setAdapter(mTestBaseAdapter);
//
//				}
//			}
//		});
//
//		search_edittext.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView arg0, int actionId,
//					KeyEvent arg2) {
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//					// 先隐藏键盘
//					((InputMethodManager) search_edittext.getContext()
//							.getSystemService(Context.INPUT_METHOD_SERVICE))
//							.hideSoftInputFromWindow(mActivity
//									.getCurrentFocus().getWindowToken(),
//									InputMethodManager.HIDE_NOT_ALWAYS);
//					// 更新关键字
//					searchKey = search_edittext.getText().toString().trim();
//
//					try {
//						searContacts = dbUtil.findAll(Selector.from(
//								Contacts.class).where(
//								WhereBuilder.b("name", "like", "%" + searchKey
//										+ "%")));
//						ContactsShowType = 1;
//
//						for (int i = 0; i < mContactsGroups.size(); i++) {
//							ContactsGroup contactsGroup = mContactsGroups
//									.get(i);
//							for (Contacts c : searContacts) {
//								if (c.getGroup_id().equalsIgnoreCase(
//										contactsGroup.getId())) {
//									contactsGroup.setOpen(true);
//									mListView.expand(Long.valueOf(contactsGroup
//											.getId()));
//									break;
//								}
//							}
//						}
//
//						mTestBaseAdapter = new TestBaseAdapter(mActivity,
//								(ArrayList) searContacts);
//						mListView.setAdapter(mTestBaseAdapter);
//
//					} catch (DbException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					return true;
//				}
//				return false;
//			}
//		});
//	}
//
//	@Override
//	protected void initDate() {
//
//		if (mActivity.getFrom().equals("login")) {
//			if (CommonUtils.isNetworkAvailable(mActivity)) {
//				InteNetUtils.getInstance(mActivity).GetContact(
//						getContactCallBack);
//			} else {
//				initlocakData();
//			}
//		} else if (mActivity.getFrom().equals("register")) {
//			// 匹配通讯录 由于耗时长 所以另开线程
//			mActivity.showLoding("匹配通讯录...");
//			new Thread() {
//				public void run() {
//
//					// 获取手机内的联系人信息
//					ContentResolver contentResolver = mActivity
//							.getContentResolver();
//					// mContactsGroups = (ArrayList<ContactsGroup>)
//					// PhoneUtils
//					// .getAllGroupInfo(contentResolver);
//					// if (mContactsGroups != null
//					// && mContactsGroups.size() > 0) {
//					// ArrayList<Contacts> cots = null;
//					// for (int i = 0; i < mContactsGroups.size(); i++) {
//					// cots = new ArrayList<Contacts>();
//					// cots = (ArrayList<Contacts>) PhoneUtils
//					// .getAllContactsByGroupId(Integer
//					// .valueOf(mContactsGroups.get(i)
//					// .getId()),
//					// contentResolver);
//					// mContacts.addAll(cots);
//					// mapGroup.put(mContactsGroups.get(i).getId(),
//					// mContactsGroups.get(i));
//					//
//					// // sim信息 属于未分组
//					// if (mContactsGroups.get(i).getName()
//					// .equals("未分组")) {
//					// ArrayList<Contacts> conts = (ArrayList<Contacts>)
//					// PhoneUtils
//					// .getSIMContacts(
//					// contentResolver,
//					// Integer.valueOf(mContactsGroups
//					// .get(i).getId()));
//					// mContacts.addAll(conts);
//					// }
//					// }
//					// }
//					//
//					// // 按照格式提交到服务器
//					// String group = "";
//					// String phone = "";
//					// for (int i = 0; i < mContactsGroups.size(); i++) {
//					// group += mContactsGroups.get(i).getName() + ",";
//					// for (int j = 0; j < mContacts.size(); j++) {
//					// String phone2 = "";
//					// if (mContacts.get(j).getGroup_id()
//					// .equals(mContactsGroups.get(i).getId())) {
//					// ArrayList<PhoneInfo> phoneInfos = mContacts
//					// .get(j).getPhones();
//					// for (int k = 0; k < phoneInfos.size(); k++) {
//					// phone2 += phoneInfos.get(k).getPhone()
//					// + "#";
//					// }
//					// phone2 = phone2.substring(0,
//					// phone2.length() - 1);
//					// phone += phone2 + "::"
//					// + mContacts.get(j).getName() + "|";
//					// }
//					// }
//					// phone = phone.substring(0, phone.length() - 1);
//					// phone += ",";
//					// }
//					//
//					// if (phone.length() > 0) {
//					// phone = phone.substring(0, phone.length() - 1);
//					// phone += ",";
//					// }
//					//
//					// if (group.length() > 0) {
//					// group = group.substring(0, group.length() - 1);
//					// }
//					// if (phone.length() > 0) {
//					// phone = phone.substring(0, phone.length() - 1);
//					// }
//
//					// 仅获取联系人
//					String phone = PhoneUtils.getOnlyContacts(contentResolver);
//					InteNetUtils.getInstance(mActivity).PhoneMatch("", phone,
//							mRequestCallBack);
//
//				};
//			}.start();
//
//		}
//
//	}
//
//	// 读取本地数据库数据
//	private void initlocakData() {
//		try {
//			if (dbUtil.tableIsExist(Contacts.class)
//					&& dbUtil.tableIsExist(ContactsGroup.class)) {
//				mContactsGroups = (ArrayList<ContactsGroup>) dbUtil
//						.findAll(ContactsGroup.class);
//				mContacts = (ArrayList<Contacts>) dbUtil
//						.findAll(Contacts.class);
//
//				int size = mContactsGroups.size();
//				ContactsGroup unGroup = null;
//				for (int i = 0; i < size; i++) {
//					if (mContactsGroups.get(i).getName()
//							.equalsIgnoreCase("未分组")) {
//						unGroup = mContactsGroups.remove(i);
//						break;
//					}
//				}
//				if (unGroup != null) {
//					mContactsGroups.add(unGroup);
//				}
//
//				ArrayList<Contacts> nContacts = new ArrayList<Contacts>();
//				mTestBaseAdapter = new TestBaseAdapter(mActivity, nContacts);
//				mListView.setAdapter(mTestBaseAdapter);
//
//				// 获得所有数据 方便传值
//				crashApplication.contactsObject = new ContactsObject();
//				crashApplication.contactsObject.setmContacts(mContacts);
//				crashApplication.contactsObject
//						.setmContactsGroups(mContactsGroups);
//
//				// 关闭所有item
//				for (int i = 0; i < mContactsGroups.size(); i++) {
//					mListView.collapse(Long.valueOf(mContactsGroups.get(i)
//							.getId()));
//				}
//			}
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	protected void onHttpStart() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	protected void onLoading(long count, long current, boolean isUploading) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	protected void onSuccess(JSONObject jsonObject) {
//		mActivity.dissLoding();
//		getData(jsonObject);
//
//	}
//
//	@Override
//	protected void onFailure(HttpException exception, String strMsg) {
//		nodata.setVisibility(View.VISIBLE);
//	}
//
//	public class TestBaseAdapter extends BaseAdapter implements
//			StickyListHeadersAdapter, SectionIndexer {
//
//		private final Context context;
//		private int[] mSectionIndices;
//		private String[] mSectionLetters;
//
//		private LayoutInflater mInflater;
//
//		public TestBaseAdapter(Context context, ArrayList<Contacts> contacts) {
//			this.context = context;
//			mInflater = LayoutInflater.from(context);
//			adapterList = getNewContacts(contacts, mContactsGroups);
//			mSectionIndices = getSectionIndices();
//			mSectionLetters = getSectionLetters();
//		}
//
//		public void init(ArrayList<Contacts> contacts) {
//			adapterList = getNewContacts(contacts, mContactsGroups);
//			// mSectionIndices = getSectionIndices();
//			// mSectionLetters = getSectionLetters();
//		}
//
//		private int[] getSectionIndices() {
//			ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
//			// char lastFirstChar = mCountries[0].charAt(0);
//			if (adapterList == null || adapterList.size() < 1) {
//				return new int[0];
//			}
//			String lastgroupid = adapterList.get(0).getGroup_id();
//			sectionIndices.add(0);
//			for (int i = 1; i < adapterList.size(); i++) {
//				if (!(lastgroupid.equals(adapterList.get(i).getGroup_id()))) {
//					lastgroupid = adapterList.get(i).getGroup_id();
//					sectionIndices.add(i);
//				}
//			}
//			int[] sections = new int[sectionIndices.size()];
//			for (int i = 0; i < sectionIndices.size(); i++) {
//				sections[i] = sectionIndices.get(i);
//			}
//			return sections;
//		}
//
//		private String[] getSectionLetters() {
//			String[] letters = new String[mSectionIndices.length];
//			for (int i = 0; i < mSectionIndices.length; i++) {
//				letters[i] = mapGroup.get(
//						adapterList.get(mSectionIndices[i]).getGroup_id())
//						.getName();
//			}
//			return letters;
//		}
//
//		@Override
//		public int getCount() {
//			return adapterList.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return adapterList.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			final Contacts cs = adapterList.get(position);
//			// if (convertView == null || !(convertView instanceof
//			// LinearLayout)) {
//			holder = new ViewHolder();
//			convertView = mInflater.inflate(R.layout.contacts_list_item_layout,
//					parent, false);
//			holder.item_phone_poster = (CubeImageView) convertView
//					.findViewById(R.id.item_phone_poster);
//			holder.item_phone_name = (TextView) convertView
//					.findViewById(R.id.item_phone_name);
//			holder.item_phone_single_bb = (ImageView) convertView
//					.findViewById(R.id.item_phone_single_bb);
//			holder.item_phone_single_bxw = (ImageView) convertView
//					.findViewById(R.id.item_phone_single_bxw);
//			holder.item_phone_single_call = (ImageView) convertView
//					.findViewById(R.id.item_phone_single_call);
//			convertView.setTag(holder);
//			// } else {
//			// holder = (ViewHolder) convertView.getTag();
//			// }
//
//			String poster = cs.getPoster();
//			holder.item_phone_poster.setImageResource(R.drawable.default_face);
//			if (!TextUtils.isEmpty(poster)) {
//				CommonUtils.startImageLoader(cubeimageLoader, poster,
//						holder.item_phone_poster);
//			}
//
//			if (cs.getId().equals("-1")) {
//				convertView.setLayoutParams(new LayoutParams(0, 0));
//			} else {
//				convertView.setLayoutParams(new LayoutParams(
//						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//				holder.item_phone_name.setText(cs.getName());
//			}
//
//			if ("0".equals(cs.getIs_baixing())) {
//				holder.item_phone_single_bxw.setVisibility(View.GONE);
//			} else {
//				holder.item_phone_single_bxw.setVisibility(View.VISIBLE);
//			}
//
//			if ("0".equals(cs.getIs_benben())) {
//				holder.item_phone_single_bb.setVisibility(View.GONE);
//			} else {
//				holder.item_phone_single_bb.setVisibility(View.VISIBLE);
//			}
//
//			holder.item_phone_single_call
//					.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View arg0) {
//							makeCall(cs.getId());
//						}
//					});
//
//			convertView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					Intent intent = new Intent(mActivity,
//							ActivityContactsInfo.class);
//					intent.putExtra("contacts", cs);
//					startActivityForResult(intent,
//							AndroidConfig.ContactsFragmentRequestCode);
//					mActivity.overridePendingTransition(R.anim.in_from_right,
//							R.anim.out_to_left);
//
//				}
//			});
//
//			return convertView;
//		}
//
//		@Override
//		public View getHeaderView(int position, View convertView,
//				ViewGroup parent) {
//			XunaoLog.yLog().d("刷新HEADView" + position);
//			final HeaderViewHolder holder;
//			final Contacts contacts = adapterList.get(position);
//
//			if (convertView == null) {
//				holder = new HeaderViewHolder();
//				convertView = mInflater.inflate(R.layout.contacts_header,
//						parent, false);
//				holder.group_name = (TextView) convertView
//						.findViewById(R.id.group_name);
//				holder.status_img = (ImageView) convertView
//						.findViewById(R.id.status_img);
//				holder.group_num = (TextView) convertView
//						.findViewById(R.id.group_num);
//				convertView.setTag(holder);
//			} else {
//				holder = (HeaderViewHolder) convertView.getTag();
//			}
//			if (mContactsGroups != null && mContactsGroups.size() > 0) {
//				holder.status_img
//						.setImageResource(mapGroup.get(
//								adapterList.get(position).getGroup_id())
//								.isOpen() ? R.drawable.icon_contacts_single_down
//								: R.drawable.icon_contacts_single_right);
//			}
//			// set header text as first char in name
//			// CharSequence headerChar = mCountries[position].subSequence(0, 1);
//			CharSequence headerChar = mapGroup.get(contacts.getGroup_id())
//					.getName();
//			holder.group_name.setText(headerChar);
//			holder.group_num.setText(mapGroup.get(contacts.getGroup_id())
//					.getProportion());
//			final View headview = convertView;
//
//			// 长按弹出管理分组
//			convertView.setOnLongClickListener(new OnLongClickListener() {
//
//				@Override
//				public boolean onLongClick(View v) {
//					// int[] location = new int[2] ;
//					// view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
//					// view.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
//
//					getPopupWindowInstance();
//					mPopupWindow.showAsDropDown(headview, 0,
//							0 - PixelUtil.dp2px(94));
//
//					// 取消长按后的单击事件
//					return true;
//				}
//			});
//
//			// //延迟处理
//			// new Handler().postDelayed(new Runnable() {
//			//
//			// @Override
//			// public void run() {
//			// holder.status_img.setImageResource(R.drawable.icon_contacts_single_right);
//			// }
//			// },500);
//			//
//			return convertView;
//		}
//
//		/**
//		 * Remember that these have to be static, postion=1 should always return
//		 * the same Id that is.
//		 */
//		@Override
//		public long getHeaderId(int position) {
//			// return the first character of the country as ID because this is
//			// what
//			// headers are based upon
//			// return mCountries[position].subSequence(0, 1).charAt(0);
//			return Long.valueOf(adapterList.get(position).getGroup_id());
//		}
//
//		@Override
//		public int getPositionForSection(int section) {
//			if (mSectionIndices.length == 0) {
//				return 0;
//			}
//
//			if (section >= mSectionIndices.length) {
//				section = mSectionIndices.length - 1;
//			} else if (section < 0) {
//				section = 0;
//			}
//			return mSectionIndices[section];
//		}
//
//		@Override
//		public int getSectionForPosition(int position) {
//			for (int i = 0; i < mSectionIndices.length; i++) {
//				if (position < mSectionIndices[i]) {
//					return i - 1;
//				}
//			}
//			return mSectionIndices.length - 1;
//		}
//
//		@Override
//		public Object[] getSections() {
//			return mSectionLetters;
//		}
//
//		public void clear() {
//			// mCountries = new String[0];
//			mContacts = new ArrayList<Contacts>();
//			mSectionIndices = new int[0];
//			mSectionLetters = new String[0];
//			notifyDataSetChanged();
//		}
//
//		public void restore() {
//			adapterList = getNewContacts(mContacts, mContactsGroups);
//			mSectionIndices = getSectionIndices();
//			mSectionLetters = getSectionLetters();
//			notifyDataSetChanged();
//		}
//
//		class HeaderViewHolder {
//			TextView group_name;
//			ImageView status_img;
//			TextView group_num;
//		}
//
//		class ViewHolder {
//			CubeImageView item_phone_poster;
//			TextView item_phone_name;
//			ImageView item_phone_single_bb;
//			ImageView item_phone_single_bxw;
//			ImageView item_phone_single_call;
//		}
//
//	}
//
//	// animation executor
//	// class AnimationExecutor implements
//	// ExpandableStickyListHeadersListView.IAnimationExecutor {
//	//
//	// @Override
//	// public void executeAnim(final View target, final int animType) {
//	// if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType
//	// && target.getVisibility() == View.VISIBLE) {
//	// return;
//	// }
//	// if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType
//	// && target.getVisibility() != View.VISIBLE) {
//	// return;
//	// }
//	// if (mOriginalViewHeightPool.get(target) == null) {
//	// mOriginalViewHeightPool.put(target, target.getHeight());
//	// }
//	// final int viewHeight = mOriginalViewHeightPool.get(target);
//	// float animStartY = animType ==
//	// ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f
//	// : viewHeight;
//	// float animEndY = animType ==
//	// ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight
//	// : 0f;
//	// final ViewGroup.LayoutParams lp = target.getLayoutParams();
//	// ValueAnimator animator = ValueAnimator
//	// .ofFloat(animStartY, animEndY);
//	// // 动画 时间 原200
//	// animator.setDuration(0);
//	// target.setVisibility(View.VISIBLE);
//	// animator.addListener(new Animator.AnimatorListener() {
//	// @Override
//	// public void onAnimationStart(Animator animator) {
//	// }
//	//
//	// @Override
//	// public void onAnimationEnd(Animator animator) {
//	// if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
//	// target.setVisibility(View.VISIBLE);
//	// } else {
//	// target.setVisibility(View.GONE);
//	// }
//	// target.getLayoutParams().height = viewHeight;
//	// }
//	//
//	// @Override
//	// public void onAnimationCancel(Animator animator) {
//	//
//	// }
//	//
//	// @Override
//	// public void onAnimationRepeat(Animator animator) {
//	//
//	// }
//	// });
//	// animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//	// @Override
//	// public void onAnimationUpdate(ValueAnimator valueAnimator) {
//	// lp.height = ((Float) valueAnimator.getAnimatedValue())
//	// .intValue();
//	// target.setLayoutParams(lp);
//	// target.requestLayout();
//	// }
//	// });
//	// animator.start();
//	//
//	// }
//	// }
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.contacts_layout_zqtxl:
//			ToastUtils.Infotoast(mActivity, "contacts_layout_zqtxl");
//			break;
//		case R.id.contacts_layout_qun:
//			mActivity.startAnimActivity(ActivityTalkGroup.class);
//			break;
//		case R.id.contacts_layout_hmztc:
//			((BaseActivity) getActivity())
//					.startAnimActivity(ActivityNumberTrain.class);
//			break;
//		case R.id.refreshBut:
//			// 刷新
//			if (CommonUtils.isNetworkAvailable(mActivity)) {
//				mActivity.showLoding("请稍等...");
//				InteNetUtils.getInstance(mActivity).GetContact(
//						getContactCallBack);
//			}
//			break;
//		// 长按
//		case R.id.management_packet:
//			Intent intent = new Intent(mActivity,
//					Activity_Packet_Management.class);
//			intent.putExtra("contactsObject", crashApplication.contactsObject);
//			startActivityForResult(intent,
//					AndroidConfig.ContactsFragmentRequestCode);
//			mActivity.overridePendingTransition(R.anim.in_from_right,
//					R.anim.out_to_left);
//			// getParentFragment().startAnimActivityForResult(
//			// Activity_Packet_Management.class, "contactsObject",
//			// contactsObject, AndroidConfig.ContactsFragment_RequestCode);
//			mPopupWindow.dismiss();
//			break;
//		default:
//			break;
//		}
//	}
//
//	// 处理联系人 每个分组末尾加个空的联系人 用来现实分组 并且得到mapgroup
//	public ArrayList<Contacts> getNewContacts(ArrayList<Contacts> contacts,
//			ArrayList<ContactsGroup> contactsGroups) {
//
//		// 清空mapgroup
//		mapGroup.clear();
//
//		int fenzi = 0;
//		int fenmu = 0;
//
//		ArrayList<Contacts> all = new ArrayList<Contacts>();
//		ArrayList<Contacts> common = new ArrayList<Contacts>();
//		ArrayList<Contacts> isbenben = new ArrayList<Contacts>();
//		ArrayList<Contacts> total = new ArrayList<Contacts>();
//
//		for (int i = 0; i < contactsGroups.size(); i++) {
//			String groupid = contactsGroups.get(i).getId();
//
//			for (Contacts contacts2 : contacts) {
//				if (contacts2.getGroup_id().equals(groupid)) {
//					all.add(contacts2);
//				}
//			}
//
//			for (Contacts contacts2 : all) {
//				if ("0".equals(contacts2.getIs_benben())) {
//					common.add(contacts2);
//				} else {
//					isbenben.add(contacts2);
//				}
//			}
//			isbenben.addAll(common);
//			// 统计 几分之急
//			for (Contacts contacts3 : mContacts) {
//				if (contacts3.getGroup_id().equals(groupid)) {
//					fenmu++;
//					if (!("0".equals(contacts3.getIs_benben()))) {
//						fenzi++;
//					}
//				}
//			}
//
//			ContactsGroup cg = contactsGroups.get(i);
//			cg.setProportion(fenzi + "/" + fenmu);
//			mapGroup.put(groupid, cg);
//			fenzi = 0;
//			fenmu = 0;
//
//			Contacts cont = new Contacts();
//			cont.setGroup_id(groupid);
//			cont.setId("-1");
//			isbenben.add(cont);
//			total.addAll(isbenben);
//			all.clear();
//			isbenben.clear();
//			common.clear();
//		}
//
//		return total;
//	}
//
//	/*
//	 * 获取PopupWindow实例
//	 */
//	private void getPopupWindowInstance() {
//		if (null != mPopupWindow) {
//			mPopupWindow.dismiss();
//			return;
//		} else {
//			initPopuptWindow();
//		}
//	}
//
//	/*
//	 * 创建PopupWindow
//	 */
//	private void initPopuptWindow() {
//		LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
//		View popupWindow = layoutInflater.inflate(R.layout.popup_packet, null);
//		ImageView management_packet = (ImageView) popupWindow
//				.findViewById(R.id.management_packet);
//		management_packet.setOnClickListener(this);
//
//		// 创建一个PopupWindow
//		// 参数1：contentView 指定PopupWindow的内容
//		// 参数2：width 指定PopupWindow的width
//		// 参数3：height 指定PopupWindow的height
//		mPopupWindow = new PopupWindow(popupWindow,
//				ViewGroup.LayoutParams.MATCH_PARENT,
//				ViewGroup.LayoutParams.WRAP_CONTENT);
//		// 设置点击外围空间 popup消失
//		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//		mPopupWindow.setOutsideTouchable(true);
//		// 获取屏幕和PopupWindow的width和height
//		// mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
//		// mScreenWidth = getWindowManager().getDefaultDisplay().getHeight();
//		// mPopupWindowWidth = mPopupWindow.getWidth();
//		// mPopupWindowHeight = mPopupWindow.getHeight();
//	}
//
//	private RequestCallBack<String> getContactCallBack = new RequestCallBack<String>() {
//		@Override
//		public void onSuccess(ResponseInfo<String> arg0) {
//			mActivity.dissLoding();
//			JSONObject jsonObject = null;
//			try {
//				jsonObject = new JSONObject(arg0.result);
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
//			lodingDialog.dismiss();
//		}
//	};
//	private View headView;
//	private View nodata;
//	private View refreshBut;
//	private LinearLayout nodata_contacts_layout_zqtxl;
//	private LinearLayout nodata_contacts_layout_qun;
//	private LinearLayout nodata_contacts_layout_hmztc;
//	private EditText search_edittext;
//	private LinearLayout ll_seach_icon;
//	private ImageView iv_search_content_delect;
//
//	public void onActivityResult(int requestCode, int resultCode,
//			android.content.Intent data) {
//		if (resultCode == AndroidConfig.ContactsFragmentResultCode) {
//
//			mContacts = crashApplication.contactsObject.getmContacts();
//			mContactsGroups = crashApplication.contactsObject
//					.getmContactsGroups();
//			// 消除开闭合状态
//			for (int i = 0; i < mContactsGroups.size(); i++) {
//				mContactsGroups.get(i).setOpen(false);
//			}
//			mTestBaseAdapter = new TestBaseAdapter(mActivity, mContacts);
//			mListView.setAdapter(mTestBaseAdapter);
//			// 关闭所有item
//			for (int i = 0; i < mContactsGroups.size(); i++) {
//				mListView
//						.collapse(Long.valueOf(mContactsGroups.get(i).getId()));
//			}
//		}
//	}
//
//	/**
//	 * 拨打电话 dialog
//	 */
//	private void makeCall(String uid) {
//
//		List<PhoneInfo> phonesArrayList = null;
//		try {
//			phonesArrayList = dbUtil.findAll(Selector.from(PhoneInfo.class)
//					.where("contacts_id", "=", uid));
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		ArrayList<String> pString = new ArrayList<String>();
//		if (phonesArrayList != null && phonesArrayList.size() > 0) {
//			for (int i = 0; i < phonesArrayList.size(); i++) {
//				String phoneString = phonesArrayList.get(i).getPhone();
//				if (!("".equals(phoneString))) {
//					pString.add(phoneString);
//				}
//				String is_baixing = phonesArrayList.get(i).getIs_baixing();
//				if (!("0".equals(is_baixing))) {
//					pString.add(is_baixing);
//				}
//			}
//		}
//
//		if (pString.size() == 0) {
//			ToastUtils.Errortoast(mActivity, "该联系人无号码！");
//		} else {
//			final String[] phs = new String[pString.size()];
//			for (int i = 0; i < pString.size(); i++) {
//				phs[i] = pString.get(i);
//			}
//			mActivity.setTheme(R.style.ActionSheetStyleIOS7);
//			showActionSheet(phs);
//		}
//
//	}
//
//	public void showActionSheet(final String[] phone) {
//		ActionSheet
//				.createBuilder(mActivity, mActivity.getSupportFragmentManager())
//				.setCancelButtonTitle("取消")
//				.setOtherButtonTitles(phone)
//				// 设置颜色 必须一一对应
//				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
//				.setCancelableOnTouchOutside(true)
//				.setListener(new ActionSheetListener() {
//
//					@Override
//					public void onOtherButtonClick(ActionSheet actionSheet,
//							int index) {
//
//						PhoneUtils.makeCall(phone[index], mActivity);
//
//					}
//
//					@Override
//					public void onDismiss(ActionSheet actionSheet,
//							boolean isCancel) {
//						// TODO Auto-generated method stub
//
//					}
//				}).show();
//	}
//
//	/**
//	 * 获取服务器拉取过来的数据
//	 * 
//	 * @param jsonObject
//	 */
//	private void getData(JSONObject jsonObject) {
//		try {
//			crashApplication.contactsObject = new ContactsObject();
//			crashApplication.contactsObject = crashApplication.contactsObject
//					.parseJSON(jsonObject);
//
//			mContacts.clear();
//			mContactsGroups.clear();
//			mPhoneInfos.clear();
//
//			mContacts = crashApplication.contactsObject.getmContacts();
//			mContactsGroups = crashApplication.contactsObject
//					.getmContactsGroups();
//			int size = mContactsGroups.size();
//			// 对组群排序,未分组放在后面
//			ContactsGroup unGroup = null;
//			for (int i = 0; i < size; i++) {
//				if (mContactsGroups.get(i).getName().equalsIgnoreCase("未分组")) {
//					unGroup = mContactsGroups.remove(i);
//					break;
//				}
//			}
//			if (unGroup != null) {
//				mContactsGroups.add(unGroup);
//			}
//			// 记录所有的phone信息
//			for (Contacts contacts : mContacts) {
//
//				ArrayList<PhoneInfo> phones = contacts.getPhones();
//
//				mPhoneInfos.addAll(contacts.getPhones());
//			}
//			// 传一个空的数据 防止刚开始切换的卡顿
//			ArrayList<Contacts> nContacts = new ArrayList<Contacts>();
//			mTestBaseAdapter = new TestBaseAdapter(mActivity, nContacts);
//			mListView.setAdapter(mTestBaseAdapter);
//			// 关闭所有item
//			for (int i = 0; i < mContactsGroups.size(); i++) {
//				mListView
//						.collapse(Long.valueOf(mContactsGroups.get(i).getId()));
//			}
//
//			// 持久化一个环信与本地数据的hashmap
//			HashMap<String, Object> huanXinMap = new HashMap<String, Object>();
//			if (mContacts != null && mContacts.size() > 0) {
//				for (Contacts cs : mContacts) {
//					if (!"0".equals(cs.getIs_benben())) {
//						huanXinMap.put(cs.getHuanxin_username(), cs);
//					}
//				}
//			}
//			crashApplication.getInstance().setHuanXinMap(huanXinMap);
//
//			// 保存数据
//			dbUtil.deleteAll(ContactsGroup.class);
//			dbUtil.deleteAll(Contacts.class);
//			dbUtil.deleteAll(PhoneInfo.class);
//
//			dbUtil.saveAll(mContactsGroups);
//			dbUtil.saveAll(mContacts);
//			dbUtil.saveAll(mPhoneInfos);
//			nodata.setVisibility(View.GONE);
//			if (lodingDialog != null) {
//				lodingDialog.dismiss();
//			}
//
//		} catch (NetRequestException e) {
//			nodata.setVisibility(View.VISIBLE);
//			// e.getError().print(mActivity);
//			// initlocakData();
//		} catch (DbException e) {
//			nodata.setVisibility(View.VISIBLE);
//			// e.printStackTrace();
//			// initlocakData();
//		}
//	}
//}
