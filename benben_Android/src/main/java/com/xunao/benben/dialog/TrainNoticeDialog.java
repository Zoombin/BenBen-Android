package com.xunao.benben.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xunao.benben.R;

public class TrainNoticeDialog extends Dialog {
	private TextView tv_notice;
	private Button btn_confirm;

	public TrainNoticeDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		init();
	}


	public TrainNoticeDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init() {
		setContentView(R.layout.train_notice_dialog);
		initView();
		initData();
		setListener();
	}

	private void initView() {
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void setListener() {
		// TODO Auto-generated method stub

	}

	public void setContent(String notice) {
        tv_notice.setText(notice);

	}


}
