package com.xunao.benben.ui.promotion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

public class ActivityUpdateAlbumName extends BaseActivity {
	private EditText et_new_nick_name;
    private int id;
    private String name;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_update_nick_name);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("修改名称", "", "提交",
				R.drawable.icon_com_title_left, 0);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		et_new_nick_name = (EditText) findViewById(R.id.et_new_nick_name);

		Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        name = intent.getStringExtra("name");
        et_new_nick_name.setText(name);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				name = et_new_nick_name.getText().toString().trim();
				if (TextUtils.isEmpty(name)) {
					ToastUtils.Infotoast(mContext, "名称不能为空!");
				} else {
					if (!CommonUtils.StringIsSurpass(name, 1, 8)) {
						ToastUtils.Infotoast(mContext, "昵称不可超过8字");
						return;
					}
                    InteNetUtils.getInstance(mContext).Editalbum(id, name, null, mRequestCallBack);
				}
			}
		});
	}

	@Override
	protected void onHttpStart() {
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String ret_num = jsonObject.optString("ret_num");
		String ret_msg = jsonObject.optString("ret_msg");
		if (ret_num.equals("0")) {
			ToastUtils.Infotoast(mContext, "修改成功！");
            Intent intent = new Intent();
            intent.putExtra("name",name);
            setResult(RESULT_OK, intent);
            AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, "修改失败！");
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext, "修改失败！");
	}

}
