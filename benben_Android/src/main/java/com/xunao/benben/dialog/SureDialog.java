package com.xunao.benben.dialog;

import com.xunao.benben.R;

import android.content.Context;
import android.widget.TextView;

public class SureDialog extends AbsDialog{
	private TextView tv_content;
	private TextView tv_content2;
	public SureDialog(Context context) {
		super(context, R.style.MyDialog1);
		init();
	}
	
	public SureDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public SureDialog(Context context, int theme) {
		super(context, theme);
		init();
	}
	
	private void init() {
		setContentView(R.layout.dialog_msg);
		initView();
		initData();
		setListener();		
	}

	
	@Override
	protected void initView() {
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content2 = (TextView) findViewById(R.id.tv_content2);
	}
	
	public void setOneListener(android.view.View.OnClickListener clickListener) {
		tv_content.setOnClickListener(clickListener);
	}
	
	public void setTwoListener(
			android.view.View.OnClickListener clickListener) {
		tv_content2.setOnClickListener(clickListener);
	}

	@Override
	protected void initData() {
		
	}

	@Override
	protected void setListener() {
		
	}
	
}
