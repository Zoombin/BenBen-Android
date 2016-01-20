package com.xunao.test.ui.item;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.BxContacts;
import com.xunao.test.bean.Contacts;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.dialog.InfoSimpleMsgHint;
import com.xunao.test.dialog.InputDialog;
import com.xunao.test.dialog.LodingDialog;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.ui.ActivityWeb;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.RegexUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.utils.ViewHolderUtil;

public class ActivityInviteFriendToBx extends BaseActivity implements
		OnClickListener {
	private Contacts contacts;
	// private ArrayList<Contacts> arrayList;
	private ArrayList<BxContacts> bxArrayList = new ArrayList<BxContacts>();
	// private ArrayList<PhoneInfo> phoneArrayList;

	private LodingDialog lodingDialog;
	private boolean isCheck;
	private ListView listView;
	private myAdapter adapter;
	private static final int CHOCE_ADDRESS = 4;
	private ArrayList<BxContacts> bxContactsArrayList = new ArrayList<>();
	private HashMap<Integer, String> mNameMap = new HashMap<Integer, String>();
	private InputDialog inputDialog;
	String pecketName;
	
	private ArrayList<Integer> minganNumArrayList = new ArrayList<Integer>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_invite_friend_to_bx);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("将好友加入百姓网", "", "完成",
				R.drawable.icon_com_title_left, 0);

		listView = (ListView) findViewById(R.id.lv_friend);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		Intent intent = getIntent();
		bxArrayList = (ArrayList<BxContacts>) intent
				.getSerializableExtra("contacts");

		int index = 0;
		for (BxContacts b : bxArrayList) {
			mNameMap.put(b.getId(), b.getName());
			
			if(RegexUtils.minganciCheck3(b.getName())){
				minganNumArrayList.add(index);
			}
			index += 1;
		}

		adapter = new myAdapter();
		listView.setAdapter(adapter);
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

//				if (!isCheck) {
//					ToastUtils.Errortoast(mContext, "请先修改红色非法文字");
//					return;
//				}
				
				if (bxContactsArrayList.size() > 0) {
					listView.setSelection(minganNumArrayList.get(0));
					ToastUtils.Errortoast(mContext, "请先修改红色非法文字");
					return;
				}
				String invite = "";
				for (int i = 0; i < bxArrayList.size(); i++) {
					if (TextUtils.isEmpty(bxArrayList.get(i).getAddress())) {
						ToastUtils.Infotoast(mContext, "请选择地区！");
						return;
					} else if (TextUtils.isEmpty(bxArrayList.get(i).getName())) {
						ToastUtils.Infotoast(mContext, "请填写姓名！");
						return;
					} else {
						invite += bxArrayList.get(i).getName() + "::"
								+ bxArrayList.get(i).getPhone() + "::"
								+ bxArrayList.get(i).getAddressId()[0] + "::"
								+ bxArrayList.get(i).getAddressId()[1] + "::"
								+ bxArrayList.get(i).getAddressId()[2] + "::"
								+ bxArrayList.get(i).getAddressId()[3] + "|";
					}
				}

				if (invite.length() > 0)
					invite = invite.substring(0, invite.length() - 1);
				if (CommonUtils.isNetworkAvailable(mContext))
					InteNetUtils.getInstance(mContext).inviteFriendToBx(invite,
							mRequestCallBack);
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
		if (lodingDialog != null && lodingDialog.isShowing()) {
			lodingDialog.dismiss();
		}
		String ret_num = jsonObject.optString("ret_num");
		if ("0".equals(ret_num)) {

			final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
					R.style.MyDialog1);
			hint.setContent("我们已收到您的申请，将会尽快处理!");
			hint.show();
			hint.setOKListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hint.dismiss();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent();
							intent.putExtra("success", "success");
							setResult(1000, intent);
							AnimFinsh();
						}
					}, 500);
				}
			});

		} else {
			ToastUtils.Infotoast(mContext, "邀请好友加入百姓网失败！");
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "邀请好友加入百姓网失败！");
	}

	@Override
	public void onClick(View arg0) {
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return bxArrayList.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return bxArrayList.get(arg0);
		}

		@Override
		public int getItemViewType(int position) {
			return position <= bxArrayList.size() - 1 ? 0 : 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (getItemViewType(position) == 0) {

				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.activity_invite_friend_to_bx_item, null);
				}

				TextView tv_phone = ViewHolderUtil.get(convertView,
						R.id.tv_phone);
				final TextView tv_name = ViewHolderUtil.get(convertView,
						R.id.tv_name);
				RelativeLayout rl_area = ViewHolderUtil.get(convertView,
						R.id.rl_area);
				TextView tv_area = ViewHolderUtil
						.get(convertView, R.id.tv_area);

				final BxContacts bxContacts = bxArrayList.get(position);
				tv_name.setText(Html.fromHtml(RegexUtils.minganciCheck2(bxContacts.getName())));
				
				if(RegexUtils.minganciCheck3(bxContacts.getName())){
					if(!bxContactsArrayList.contains(bxContacts)){
						bxContactsArrayList.add(bxContacts);
					}
				}
				
				tv_phone.setText(bxContacts.getPhone());

				if (!TextUtils.isEmpty(bxContacts.getAddress())) {
					tv_area.setText(bxContacts.getAddress());
					tv_area.setTextColor(Color.BLACK);
				} else {
					tv_area.setText("请选择所在地区");
					tv_area.setTextColor(Color.parseColor("#848484"));
				}

				tv_name.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						inputDialog = new InputDialog(mContext,
								R.style.MyDialogStyle);
						inputDialog.setContent("修改姓名", "请填写姓名", "确认", "取消");
						inputDialog.setEditContent(bxContacts.getName());
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
								
								if("".equals(pecketName)){
									ToastUtils.Errortoast(mContext, "请输入姓名");
									return;
								}

//								if (RegexUtils.minganciCheck(pecketName)) {
//									tv_name.setTextColor(Color.RED);
//									isCheck = false;
//								} else {
//									isCheck = true;
//									tv_name.setTextColor(Color
//											.parseColor("#000000"));
//
//								}
								
								if(RegexUtils.minganciCheck3(pecketName)){
									if(!bxContactsArrayList.contains(bxContacts)){
										bxContactsArrayList.add(bxContacts);
									}
									
									if(!minganNumArrayList.contains(new Integer(position))){
										minganNumArrayList.add(new Integer(position));
									}
									
									
								}else{
									if(bxContactsArrayList.contains(bxContacts)){
										bxContactsArrayList.remove(bxContacts);
									}
									
									if(minganNumArrayList.contains(new Integer(position))){
										minganNumArrayList.remove(new Integer(position));
										if(minganNumArrayList.size() > 0){
											listView.setSelection(minganNumArrayList.get(0));
										}
										
									}
								}
								
								bxContacts.setName(pecketName);
								tv_name.setText(Html.fromHtml(RegexUtils.minganciCheck2(pecketName)));
								inputDialog.dismiss();
							}
						});
						inputDialog.show();
					}
				});

				// tv_name.addTextChangedListener(new TextWatcher() {
				//
				// @Override
				// public void onTextChanged(CharSequence s, int start,
				// int before, int count) {
				// // TODO Auto-generated method stub
				//
				// }
				//
				// @Override
				// public void beforeTextChanged(CharSequence s, int start,
				// int count, int after) {
				// // TODO Auto-generated method stub
				//
				// }
				//
				// @Override
				// public void afterTextChanged(Editable s) {
				// bxContacts.setName(s.toString());
				// }
				// });

				rl_area.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						switch (arg0.getId()) {
						case R.id.rl_area:
							startAnimActivityForResult3(
									ActivityChoiceAddress.class, CHOCE_ADDRESS,
									"position", position, "level", "3");
							break;
						default:
							break;
						}
					}
				});
			} else {
				convertView = getLayoutInflater().inflate(
						R.layout.mytextview_item, null);
				TextView register_point = ViewHolderUtil.get(convertView,
						R.id.register_point);

				// 修改部分自体颜色
				SpannableStringBuilder builder = new SpannableStringBuilder(
						register_point.getText().toString());
				ForegroundColorSpan greenSpan = new ForegroundColorSpan(
						Color.parseColor("#3b96ca"));
				builder.setSpan(greenSpan, 11, 22,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				register_point.setText(builder);

				register_point.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						startAnimActivity2Obj(ActivityWeb.class, "url",
								AndroidConfig.NETHOST3 + AndroidConfig.Setting
										+ "key/android/type/2", "title",
								"东阳百姓网入网声明");

					}
				});

			}
			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHOCE_ADDRESS:
			if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
				String addressname = data.getStringExtra("address");
				int position = data.getIntExtra("position", 0);
				bxArrayList.get(position).setAddress(addressname);
				bxArrayList.get(position).setAddressId(
						data.getStringArrayExtra("addressId"));
				adapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
