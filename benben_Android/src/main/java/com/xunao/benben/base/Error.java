package com.xunao.benben.base;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.xunao.benben.R;
import com.xunao.benben.activity.MainActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.item.ActivityEnterpriseMember;
import com.xunao.benben.ui.item.ActivitySearchEnterprise;
import com.xunao.benben.utils.ToastUtils;

public class Error extends BaseBean<Error> {

	public static final String COMERRORINFO = "网络未知错误!请稍候!";

	private int errorId;
	private String errorInfo;

	public int getErrorId() {
		return errorId;
	}

	public void setErrorId(int errorId) {
		this.errorId = errorId;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public Error() {
		super();
	}

	public Error(String errorInfo) {
		super();
		this.errorInfo = errorInfo;
	}

	public Error(int errorId, String errorInfo) {
		super();
		this.errorId = errorId;
		this.errorInfo = errorInfo;
	}

	public void print(Activity context) {

		if (errorId == 2001) {// 用户登录失效
			CrashApplication.getInstance().logout();
			context.startActivity(new Intent(context, ActivityLogin.class));
			ToastUtils.Errortoast(context, "用户登录超时");
			return;
		}else if(errorId == 2015){
			final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
					context, R.style.MyDialog1);
			hint.setContent("奔犇账号在其他手机登录");
			hint.setBtnContent("确定");
			hint.show();
			hint.setOKListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hint.dismiss();
				}
			});

			hint.show();
			
			
//			ToastUtils.InfotoastLong(context, "奔犇账号在其他手机登录");
			CrashApplication.getInstance().setExit(true);
			CrashApplication.getInstance().logout();
			context.startActivity(new Intent(context, ActivityLogin.class));
		}else{
			ToastUtils.Errortoast(context, errorInfo);
		}
	}

	@Override
	public Error parseJSON(JSONObject jsonObj) {
		errorId = jsonObj.optInt("ret_num");
		errorInfo = jsonObj.optString("ret_msg");
		
		return this;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
