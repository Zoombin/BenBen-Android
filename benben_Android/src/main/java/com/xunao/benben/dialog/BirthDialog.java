package com.xunao.benben.dialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.widget.time.JudgeDate;
import com.widget.time.ScreenInfo;
import com.widget.time.WheelMain;
import com.xunao.benben.R;
import com.xunao.benben.view.MyTextView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BirthDialog extends Dialog {
	private Button chose_cancel;
	private Button chose_position;
	private WheelMain wheelMain;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private Calendar calendar;
	private String time = "";
	private String choiceTime = "";

	public BirthDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public BirthDialog(Context context, String choiceTime) {
		super(context, R.style.MyDialog2);
		this.choiceTime = choiceTime;
		init(context);
	}

	public BirthDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);

		chose_cancel = (Button) timepickerview.findViewById(R.id.chose_cancel);
		chose_position = (Button) timepickerview
				.findViewById(R.id.chose_position);

		ScreenInfo screenInfo = new ScreenInfo((Activity) context);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();

		Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置显示格式
		time = df.format(dt);

		if (TextUtils.isEmpty(choiceTime)) {
			time = df.format(dt);
		} else {
			time = choiceTime;
		}

		calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		wheelMain.initDateTimePicker(year, month, day);

		initView();
		initData();
		setListener();
		setContentView(timepickerview);
	}

	private void setListener() {

	}

	private void initData() {

	}

	private void initView() {

	}

	public String getWheelMain() {
		return wheelMain.getTime();
	}

	public void setOKListener(android.view.View.OnClickListener clickListener) {
		chose_position.setOnClickListener(clickListener);
	}

	public void setCancleListener(
			android.view.View.OnClickListener clickListener) {
		chose_cancel.setOnClickListener(clickListener);
	}

}
