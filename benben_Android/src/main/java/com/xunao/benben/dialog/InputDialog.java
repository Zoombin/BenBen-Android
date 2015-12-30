package com.xunao.benben.dialog;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.xunao.benben.R;
import com.xunao.benben.view.MyEditView;

public class InputDialog extends Dialog {
	private static final int SETSELECTION = 1;
	private TextView dialog_info_content;
	private TextView dialog_info_content_;
	private MyEditView dialog_info_input;
	private Button dialog_info_ok;
	private Button dialog_info_cancle;

	public InputDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyEditView getDialog_info_input() {
		return dialog_info_input;
	}

	protected InputDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
		init();
	}

	public InputDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init() {
		setContentView(R.layout.dialog_input);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		dialog_info_content = (TextView) findViewById(R.id.dialog_info_content);
		dialog_info_content_ = (TextView) findViewById(R.id.dialog_info_content_);
		dialog_info_ok = (Button) findViewById(R.id.dialog_info_OK);
		dialog_info_input = (MyEditView) findViewById(R.id.dialog_info_input);
		dialog_info_cancle = (Button) findViewById(R.id.dialog_info_cancle);
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void setListener() {
		// TODO Auto-generated method stub

	}

	public String getInputText() {
		return dialog_info_input.getText().toString();
	}

	public void setContent(String content_title, String content,
			String OKString, String cancleString) {
		dialog_info_content.setText(content_title);
		dialog_info_content_.setText(content);
		dialog_info_cancle.setText(cancleString);
		dialog_info_ok.setText(OKString);

	}

	public void setEditContent(String content) {
		dialog_info_input.setText(content);
		dialog_info_input.setSelection(content.length());

	}

	public void setOKListener(android.view.View.OnClickListener clickListener) {
		dialog_info_ok.setOnClickListener(clickListener);
	}

	public void setCancleListener(
			android.view.View.OnClickListener clickListener) {
		dialog_info_cancle.setOnClickListener(clickListener);
	}

	public void setInputType(String content) {
		dialog_info_input.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		dialog_info_input.setText(content);
		dialog_info_input.setSelection(content.length());
	}

	@Override
	public void show() {
		super.show();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (dialog_info_input != null) {
					// 设置可获得焦点
					dialog_info_input.setFocusable(true);
					dialog_info_input.setFocusableInTouchMode(true);
					// 请求获得焦点
					dialog_info_input.requestFocus();
					// 调用系统输入法
					InputMethodManager inputManager = (InputMethodManager) dialog_info_input
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(dialog_info_input, 0);
				}
			}
		}, 300);
	}

	
//	Handler handler = new Handler(){
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case SETSELECTION:
//				dialog_info_input.setSelection(content.length());
//				dialog_info_input.setText(content);
//				break;
//			}
//		};
//	};
}
