package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.AndriodVersion;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.News;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.ActivityRegisterThree;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivitySetting extends BaseActivity implements OnClickListener,
		ActionSheetListener, UmengUpdateListener {
	private RelativeLayout rl_cogradient;
    private RelativeLayout rl_phone_update;
	private RelativeLayout rl_check_update;
	private RelativeLayout rl_complain;
	private RelativeLayout rl_about_benben;
	private RelativeLayout rl_logout;
	private RelativeLayout rl_update_pwd;
	private RelativeLayout del_cache;
	private RelativeLayout rl_binding_phone;
    private RelativeLayout message_notice;

    private ImageView iv_hint;

	protected LodingDialog lodingDialog;
	protected boolean isShowLoding = true;
	private int doWhat = 1;

	// data 数据
	private ArrayList<ContactsGroup> mContactsGroups = new ArrayList<ContactsGroup>();
	private HashMap<String, ContactsGroup> mapGroup = new HashMap<String, ContactsGroup>();
	private ArrayList<PhoneInfo> mPhoneInfos = new ArrayList<PhoneInfo>();

	private ContactsObject contactsObject;

	private WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("设置", "", "", R.drawable.icon_com_title_left,
				0);

		rl_cogradient = (RelativeLayout) findViewById(R.id.rl_cogradient);
        rl_phone_update = (RelativeLayout) findViewById(R.id.rl_phone_update);
		rl_check_update = (RelativeLayout) findViewById(R.id.rl_check_update);
		rl_complain = (RelativeLayout) findViewById(R.id.rl_complain);
		rl_about_benben = (RelativeLayout) findViewById(R.id.rl_about_benben);
		rl_logout = (RelativeLayout) findViewById(R.id.rl_logout);
		rl_update_pwd = (RelativeLayout) findViewById(R.id.rl_update_pwd);
		del_cache = (RelativeLayout) findViewById(R.id.del_cache);
		rl_binding_phone = (RelativeLayout) findViewById(R.id.rl_binding_phone);
        message_notice = (RelativeLayout) findViewById(R.id.message_notice);
        iv_hint = (ImageView) findViewById(R.id.iv_hint);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
        if(CrashApplication.getInstance().updateFlag){
            iv_hint.setVisibility(View.VISIBLE);
        }else{
            iv_hint.setVisibility(View.GONE);
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

		rl_cogradient.setOnClickListener(this);
        rl_phone_update.setOnClickListener(this);
		rl_check_update.setOnClickListener(this);
		rl_complain.setOnClickListener(this);
		rl_about_benben.setOnClickListener(this);
		rl_logout.setOnClickListener(this);
		rl_update_pwd.setOnClickListener(this);
		del_cache.setOnClickListener(this);
		rl_binding_phone.setOnClickListener(this);
        message_notice.setOnClickListener(this);

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
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		if (lodingDialog != null && lodingDialog.isShowing()) {
			lodingDialog.dismiss();
		}
		ToastUtils.Infotoast(mContext, "同步通讯录失败！");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rl_cogradient:
			startAnimActivity(ActivityContactsSynchro.class);
			break;
        case R.id.rl_phone_update:
//            final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
//            msgDialog.setContent("确定同步通讯录至本地联系人吗？", "同步需较长时间", "确认", "取消");
//            msgDialog.setCancleListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    msgDialog.dismiss();
//                }
//            });
//            msgDialog.setOKListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startAnimActivity(ActivityContactsUpdate.class);
//                    msgDialog.dismiss();
//                }
//            });
//            msgDialog.show();
            showActionContactSheet();

            break;
		case R.id.rl_check_update:
			chekVersionUpdate();
			break;
		case R.id.rl_complain:
			startAnimActivity(ActivityComplain.class);
			break;
		case R.id.rl_about_benben:
			startAnimActivity(ActivityAboutBenben.class);
			break;
		case R.id.rl_logout:
			showActionSheet("退出登录");
			doWhat = 1;
			break;
		case R.id.rl_update_pwd:
			startAnimActivity(ActivityUpdatePassword.class);
			break;
		case R.id.del_cache:
			showActionSheet("清除所有聊天记录和缓存");
			doWhat = 2;
			break;
		case R.id.rl_binding_phone:
			startAnimActivity(ActivityPhoneBinding.class);
			break;
        case R.id.message_notice:
            startAnimActivity(ActivityMessageNotice.class);
            break;

		}
	}

	private void showActionSheet(String content) {
		setTheme(R.style.ActionSheetStyleIOS7);
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消").setOtherButtonTitles(content)
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

    public void showActionContactSheet() {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet
                .createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("与手机通讯录合并", "将手机通讯录覆盖")
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF", "#1E82FF").setCancelableOnTouchOutside(true)
                .setListener(new ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0:
//                                final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
//                                msgDialog.setContent("确定与手机通讯录合并吗？", "合并需较长时间", "确认", "取消");
//                                msgDialog.setCancleListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        msgDialog.dismiss();
//                                    }
//                                });
//                                msgDialog.setOKListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        startAnimActivity(ActivityContactsUpdate.class);
//                                        msgDialog.dismiss();
//                                    }
//                                });
//                                msgDialog.show();
                                startAnimActivity(ActivityContactsUpdate.class);
                                break;
                            case 1:
                                final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                                msgDialog.setContent("是否要覆盖手机通讯录?", "", "确认", "取消");
                                msgDialog.setCancleListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        msgDialog.dismiss();
                                    }
                                });
                                msgDialog.setOKListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        msgDialog.dismiss();
                                        coverMail();

                                    }
                                });
                                msgDialog.show();


                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

	// 检查更新
	private void chekVersionUpdate() {
        if(CommonUtils.isNetworkAvailable(mContext)) {
            showLoding("");
            UmengUpdateAgent.setUpdateOnlyWifi(false); //false 则非wifi也检测更新
            UmengUpdateAgent.update(this);
            UmengUpdateAgent.setUpdateListener(this);
        }

//		setShowLoding(false);
//		if (CommonUtils.isNetworkAvailable(mContext)) {
//			InteNetUtils.getInstance(mContext).checkVersion(
//					new RequestCallBack<String>() {
//						@Override
//						public void onSuccess(ResponseInfo<String> json) {
//							AndriodVersion andriodVersion = new AndriodVersion();
//							JSONObject object = null;
//							try {
//								object = new JSONObject(json.result);
//								andriodVersion.parseJSON(object);
//							} catch (Exception e) {
//								e.printStackTrace();
//								ToastUtils.Errortoast(mContext, "服务器数据有误!");
//								return;
//							}
//
//							boolean versionCompare = CommonUtils
//									.versionCompare(mContext,
//											andriodVersion.getAndriodVersion());
//
//							if (!versionCompare) {
//								CommonUtils.showUpdateDialog(mContext,
//										andriodVersion.getUrl(),
//										andriodVersion.getUpdateContent(),
//										new Handler());
//							} else {
//								ToastUtils.Infotoast(mContext, "当前版本已是最新版本!");
//							}
//						}
//
//						@Override
//						public void onFailure(HttpException arg0, String arg1) {
//							ToastUtils.Infotoast(mContext, "服务器出错!");
//						}
//					});
//		}
	}

    //覆盖手机通讯录
    private void coverMail(){
        try {
            final List<Contacts> myContacts = dbUtil.findAll(Selector
                    .from(Contacts.class)
                    .where("group_id", "!=", 10000).orderBy("pinyin", false));
            final List<Contacts> newmyContacts = dbUtil.findAll(Selector
                    .from(Contacts.class)
                    .where("group_id", "!=", 10000).orderBy("pinyin", false));
            if(myContacts!=null) {
                showLoding("覆盖中");

                new Thread() {
                    public void run() {
//                        ContentResolver resolver = getContentResolver();
//                        resolver.delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,"true").build(), null, null);
                        for (int i = 0; i < myContacts.size(); i++) {
                            Contacts contacts = myContacts.get(i);
                            List<PhoneInfo> phoneInfos = null;
                            try {
                                phoneInfos = dbUtil.findAll(Selector
                                        .from(PhoneInfo.class)
                                        .where("contacts_id", "=", contacts.getId()));
                                List<PhoneInfo> newPhoneInfos = dbUtil.findAll(Selector
                                        .from(PhoneInfo.class)
                                        .where("contacts_id", "=", contacts.getId()));
                                if (phoneInfos != null && phoneInfos.size()>0) {
                                    for(int j=0;j<phoneInfos.size();j++){
                                        if(phoneInfos.get(j).getPhone().equals("")){
                                            newPhoneInfos.remove(phoneInfos.get(j));
                                        }
                                    }
                                }
//                                if(newPhoneInfos.size()>0){
//                                    addContact(contacts.getName(),newPhoneInfos);
//                                }
                                for(int j=0;j<newmyContacts.size();j++){
                                    if(newmyContacts.get(j).getId()==contacts.getId()){
                                        if(newPhoneInfos.size()<=0){
                                            newmyContacts.remove(j);
                                        }else{
                                            newmyContacts.get(j).setPhones((ArrayList<PhoneInfo>) newPhoneInfos);
                                        }
                                    }
                                }



                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                        }

                        if(newmyContacts!=null && newmyContacts.size()>0){
                            try {
                                BatchAddContact(newmyContacts);
                                mContext.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        dissLoding();
                                        ToastUtils.Infotoast(mContext,"覆盖成功！");
                                    }
                                });
                            } catch (RemoteException e) {
                                dissLoding();
                                ToastUtils.Infotoast(mContext,"覆盖失败！");
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                dissLoding();
                                ToastUtils.Infotoast(mContext,"覆盖失败！");
                                e.printStackTrace();
                            }
                        }else{
                            dissLoding();
                            ToastUtils.Infotoast(mContext,"当前无联系人覆盖！");
                        }


                    };
                }.start();

            }else{
                ToastUtils.Infotoast(mContext, "当前无联系人覆盖！");
            }

        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    /**
     * 单条联系人添加通讯录
     *
     * @throws android.content.OperationApplicationException
     * @throws android.os.RemoteException
     */
    public void addContact(String name, List<PhoneInfo> phoneInfo) {

        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(
                ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        // 向data表插入数据
        if (name != "") {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }
        // 向data表插入电话号码
        for(int i=0;i<phoneInfo.size();i++){
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneInfo.get(i).getPhone());
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }
    }

    /**
     * 批量添加通讯录
     *
     * @throws android.content.OperationApplicationException
     * @throws android.os.RemoteException
     */
    public  void BatchAddContact(List<Contacts> list) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

//        resolver.delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,"true").build(), null, null);
//       Uri uri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,"true").build();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).build());

        int rawContactInsertIndex = 0;
        for (Contacts contact : list) {
            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true).build());

            // 添加姓名
            ops.add(ContentProviderOperation
                    .newInsert(
                            android.provider.ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                    .withYieldAllowed(true).build());
            // 添加号码

            for(int i=0;i<contact.getPhones().size();i++) {

                ops.add(ContentProviderOperation
                        .newInsert(
                                android.provider.ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(i).getPhone())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "").withYieldAllowed(true).build());
            }
        }
        if (ops != null) {
            // 真正添加
            ContentProviderResult[] results = getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);

        }
    }

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index) {
		case 0:
			switch (doWhat) {
			case 1:
				startAnimActivity(ActivityLogin.class);
				mApplication.logout();
				mApplication.getSpUtil().setLastTimeLogin(false);
				break;

			case 2:
				if (CommonUtils.deleteDir(CommonUtils
						.getParentImagePath(mContext))) {
					EMChatManager.getInstance().deleteAllConversation();
					ToastUtils.Infotoast(mContext, "缓存删除成功!");
				}else{
					EMChatManager.getInstance().deleteAllConversation();
					ToastUtils.Infotoast(mContext, "缓存删除成功!");
				}
				break;
			}
			break;
		default:
			break;
		}
	}


    @Override
    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
        dissLoding();
        if(!updateResponse.hasUpdate){
            ToastUtils.Infotoast(this,"当前已是最新版本！");
        }
    }
}
