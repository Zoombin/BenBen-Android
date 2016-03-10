package com.xunao.benben.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;

import com.xunao.benben.R;
import com.xunao.benben.view.MyTextView;

public class InfobulletinHint extends Dialog {

//	private MyTextView dialog_info_content;
	private MyTextView dialog_info_content_,dialog_info_time;
	private Button dialog_info_ok;

	public InfobulletinHint(Context context) {
		super(context);
		init();
	}

	public InfobulletinHint(Context context, boolean cancelable,
                            OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public InfobulletinHint(Context context, int theme) {
		super(context, theme);
		init();
	}

	public void init() {
		setContentView(R.layout.dialog_bulletin_hint);
		initView();
		initData();
		setListener();
	}

	protected void initView() {
//		dialog_info_content = (MyTextView) findViewById(R.id.dialog_info_content);
		dialog_info_content_ = (MyTextView) findViewById(R.id.dialog_info_content_);
        dialog_info_content_.setMovementMethod(new ScrollingMovementMethod());
        dialog_info_time = (MyTextView) findViewById(R.id.dialog_info_time);
		dialog_info_ok = (Button) findViewById(R.id.dialog_info_OK);
	}

	protected void initData() {

	}

	protected void setListener() {

	}

	public void setContent(String content) {
		dialog_info_content_.setText(content);

	}

	public void setContent(String content, String time) {
		dialog_info_content_.setText(content);
        dialog_info_time.setText(time);
	}
	
	public void setBtnContent(String content){
		dialog_info_ok.setText(content);
	}

	public void setOKListener(android.view.View.OnClickListener clickListener) {
		dialog_info_ok.setOnClickListener(clickListener);
	}

}
