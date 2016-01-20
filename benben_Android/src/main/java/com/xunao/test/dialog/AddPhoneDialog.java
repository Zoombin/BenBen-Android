package com.xunao.test.dialog;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.bean.Contacts;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.RegexUtils;
import com.xunao.test.utils.ToastUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;

public class AddPhoneDialog extends Dialog implements
		android.view.View.OnClickListener {
	private EditText edittext;
	private Button dialog_info_OK;
	private Button dialog_info_cancle;
	private ImageView dialog_iv_cancle;
	private Contacts mContacts;

	public AddPhoneDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		init();
	}

	public AddPhoneDialog(Context context, int theme, Contacts mContacts) {
		super(context, theme);
		this.mContacts = mContacts;
		init();
	}

	protected AddPhoneDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
		init();
	}

	public AddPhoneDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init() {
		setContentView(R.layout.dialog_addphone);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		edittext = (EditText) findViewById(R.id.edittext);
		dialog_info_OK = (Button) findViewById(R.id.dialog_info_OK);
		dialog_info_cancle = (Button) findViewById(R.id.dialog_info_cancle);
		dialog_iv_cancle = (ImageView) findViewById(R.id.dialog_iv_cancle);
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void setListener() {

		dialog_iv_cancle.setOnClickListener(this);
		dialog_info_OK.setOnClickListener(this);
		dialog_info_cancle.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		switch (id) {
		case R.id.dialog_iv_cancle:
			this.dismiss();
			break;
		case R.id.dialog_info_cancle:
			this.dismiss();
			break;
		case R.id.dialog_info_OK:
			// 添加号码

			final String string = edittext.getText().toString();

			if (RegexUtils.checkMobile(string)) {
				InteNetUtils.getInstance(getContext()).addPhone(string,
						mContacts.getId()+"","", new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								mOnFailureListener.onmFailure(arg0, arg1);
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								AddPhoneDialog.this.dismiss();
								mOnSuccessListener.onmSuccess(arg0, string);
							}
						});
			} else {
				ToastUtils.Errortoast(getContext(), "请填写正确地手机号码");
			}

			break;
		}

	}

	private OnFailureListener mOnFailureListener;
	private OnSuccessListener mOnSuccessListener;

	public void setmOnFailureListener(OnFailureListener mOnFailureListener) {
		this.mOnFailureListener = mOnFailureListener;
	}

	public void setmOnSuccessListener(OnSuccessListener mOnSuccessListener) {
		this.mOnSuccessListener = mOnSuccessListener;
	}

	public interface OnFailureListener {
		public void onmFailure(HttpException arg0, String arg1);
	}

	public interface OnSuccessListener {
		public void onmSuccess(ResponseInfo<String> arg0, String string);
	}

}
