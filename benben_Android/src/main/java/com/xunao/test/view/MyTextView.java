package com.xunao.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFontStyle();
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFontStyle();
	}

	public MyTextView(Context context) {
		super(context);
		initFontStyle();
	}

	private void initFontStyle() {
//		setTypeface(CrashApplication.getInstance().getTf());// 设置字体
	}
}
