package com.xunao.benben.utils;

import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.xunao.benben.R;
import com.xunao.benben.activity.MainActivity;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.PhoneInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class PlayPhoneUtils {

	private static PopupWindow popupWindowPhone;
	private static PopupWindow popupWindowNum;
	private static View vP;
	private static View vN;

	// 显示拨号盘
	public static void showPlayPhone(final MainActivity mMainActivity,
			View parnt, OnClickListener listener, final DbUtils dbUtils, OnLongClickListener longClickListener) {

		if (popupWindowPhone == null || vP == null) {
			LayoutInflater inflater = LayoutInflater.from(mMainActivity);

			vP = inflater.inflate(R.layout.fragment_playphone_num, null);
			popupWindowPhone = new PopupWindow(vP,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			popupWindowPhone.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#ffffff")));
			popupWindowPhone.setAnimationStyle(R.style.popwin_anim_style);
		}
		vP.findViewById(R.id.dialclose).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						hinePlayPhone();
					}
				});
		vP.findViewById(R.id.dialplay).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (vN != null) {
							TextView phoneNumBox = (TextView) vN
									.findViewById(R.id.phoneNumBox);
							if (phoneNumBox != null) {
								String text = phoneNumBox.getText().toString();

								if (!TextUtils.isEmpty(text)) {

									Intent in2 = new Intent();
									in2.setAction(Intent.ACTION_CALL);// 指定意图动作
									in2.setData(Uri.parse("tel:" + text));// 指定电话号码
									mMainActivity.startActivity(in2);

									try {
										PhoneInfo findFirst = dbUtils
												.findFirst(Selector.from(
														PhoneInfo.class).where(
														WhereBuilder.b("phone",
																"=", text)));

										if (findFirst != null) {
											LatelyLinkeMan linkeMan = new LatelyLinkeMan(
                                                    findFirst.getContacts_id(),
													findFirst.getName(),
													findFirst.getPhone(),
													System.currentTimeMillis());
											dbUtils.saveOrUpdate(linkeMan);
										} else {
											findFirst = dbUtils
													.findFirst(Selector
															.from(PhoneInfo.class)
															.where("is_baixing",
																	"like",
																	"%"
																			+ text
																			+ "%"));
											if (findFirst != null && findFirst.getPhone().equals(text)) {
												LatelyLinkeMan linkeMan = new LatelyLinkeMan(
                                                        findFirst.getContacts_id(),
														findFirst.getName(),
														findFirst
																.getIs_baixing(),
														System.currentTimeMillis());
												dbUtils.saveOrUpdate(linkeMan);

											} else {
												LatelyLinkeMan linkeMan = new LatelyLinkeMan(
                                                        0,
														"",
														text,
														System.currentTimeMillis());
												dbUtils.saveOrUpdate(linkeMan);
												mMainActivity
														.refreshPlayPhone();

											}

										}

									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								} else {
									ToastUtils.Errortoast(mMainActivity,
											"请输入号码");
								}
							}
						}
					}
				});
		vP.findViewById(R.id.dialdel).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum0).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum1).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum2).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum3).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum4).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum5).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum6).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum7).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum8).setOnClickListener(listener);
		vP.findViewById(R.id.dialNum9).setOnClickListener(listener);
		vP.findViewById(R.id.dialNumJ).setOnClickListener(listener);
		vP.findViewById(R.id.dialNumX).setOnClickListener(listener);
		
		vP.findViewById(R.id.dialdel).setOnLongClickListener(longClickListener);

		popupWindowPhone.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mMainActivity.hineBlack();
				hinePlayPhone();
			}
		});

		if (!popupWindowPhone.isShowing()) {
			// 设置点击窗口外边窗口消失
			popupWindowPhone.setOutsideTouchable(true);
			popupWindowPhone.showAtLocation(parnt, Gravity.LEFT
					| Gravity.BOTTOM, 0, PixelUtil.dp2px(47));
			mMainActivity.showBlack();
		}

	}

	// 隐藏拨号盘
	public static void hinePlayPhone() {
		if (popupWindowPhone != null) {
			popupWindowPhone.dismiss();
			TextView phoneNumBox = (TextView) vN.findViewById(R.id.phoneNumBox);
			if (phoneNumBox.getText().length() <= 0) {
				hinePlayNumBox();
			}
		}

	}

	//
	public static TextView showPlayNumBox(MainActivity mMainActivity,
			View parnt, OnClickListener listener) {

		if (popupWindowNum == null || vP == null) {
			LayoutInflater inflater = LayoutInflater.from(mMainActivity);

			vN = inflater.inflate(R.layout.fragement_playphone_eidet, null);

			popupWindowNum = new PopupWindow(vN,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			popupWindowNum.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#ffffff")));
			// 设置点击窗口外边窗口消失
			popupWindowPhone.setOutsideTouchable(false);

		}
		if (!popupWindowNum.isShowing())
			popupWindowNum.showAsDropDown(parnt, 0, -(int) mMainActivity
					.getResources().getDimension(R.dimen.titleBar));

		final TextView phoneNumBox = (TextView) vN
				.findViewById(R.id.phoneNumBox);
		vN.findViewById(R.id.phoneNumclance).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						phoneNumBox.setText("");
						if (!popupWindowPhone.isShowing()) {
							popupWindowNum.dismiss();
						}

					}
				});
        vN.findViewById(R.id.iv_add).setOnClickListener(listener);
		return phoneNumBox;
	}

	public static void hinePlayNumBox() {
		if (popupWindowNum != null && !popupWindowPhone.isShowing()) {
			TextView phoneNumBox = (TextView) vN.findViewById(R.id.phoneNumBox);
			phoneNumBox.setText("");
			popupWindowNum.dismiss();
		}
	}

}
