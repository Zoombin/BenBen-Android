package com.xunao.benben.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.xunao.benben.R;
import com.xunao.benben.view.MyTextView;

public class LodingDialog extends Dialog {

	private String content;

	public LodingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public LodingDialog(Context context, int theme) {
		super(context, theme);
	}

	public LodingDialog(Context context) {
		super(context, R.style.MyDialogStyle);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.loding);
		if (!TextUtils.isEmpty(content)) {
			MyTextView findViewById = (MyTextView) findViewById(R.id.loding_content);
			findViewById.setText(content);
		}
	}

	public void setContent(String content) {
		this.content = content;
	}

}
