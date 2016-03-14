package com.xunao.benben.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.bean.User;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class BuyDialog extends AbsDialog {

	private MyTextView name;
	private EditText price;
	private EditText info;
	private Button submit;
	private int buyId;
	private BaseActivity context;
	private CharSequence charSequence;
	

	public int getBuyId() {
		return buyId;
	}

	public void setBuyId(int buyId) {
		this.buyId = buyId;
	}

	public BuyDialog(Context context) {
		super(context);
		this.context = (BaseActivity) context;
		init();
	}

	public BuyDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public BuyDialog(Context context, int theme) {
		super(context, theme);
		this.context = (BaseActivity) context;
		init();
	}

	public void init() {
		setContentView(R.layout.dialog_buy);
		Window dialogWindow = this.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int)(display.getWidth()*3/4); //设置宽度
        dialogWindow.setAttributes(lp);
	}

	@Override
	protected void initView() {
		name = (MyTextView) findViewById(R.id.name);
		price = (EditText) findViewById(R.id.price);
		info = (EditText) findViewById(R.id.info);
		submit = (Button) findViewById(R.id.submit);
		
		price.setInputType(EditorInfo.TYPE_CLASS_PHONE);  

	}

	@Override
	protected void initData() {
		try {
			User findFirst = CrashApplication.getInstance().getDb()
					.findFirst(User.class);
			name.setText(findFirst.getZhiShortName());
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void setListener() {
		price.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						
						price.setText(s);
						price.setSelection(s.length());
					}
					
					if(s.toString().indexOf(".") > 8){
					//	ToastUtils.Errortoast(context, s.toString().indexOf(".") +"");
						s = s.toString().subSequence(1,
								s.toString().length());
						price.setText(s);
						//price.setSelection(s.length());
					}
					
					

				}else{
					if(s.length() > 8){
						s = s.toString().subSequence(0,
								8);
						price.setText(s);
						price.setSelection(s.length());
					}
					charSequence = s;
					
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
		
		
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String sName = name.getText().toString();
				final String sPrice = price.getText().toString();
				final String sInfo = info.getText().toString();

				if (TextUtils.isEmpty(sPrice)) {
					ToastUtils.Errortoast(getContext(), "报价不可为空");
					return;
				}
                if (!CommonUtils.StringIsSurpass2(sInfo, 0, 200)) {
                    ToastUtils.Errortoast(getContext(), "备注限制在200个字之间!");
                    return;
                }

                final MsgDialog msgDialog = new MsgDialog(context, R.style.MyDialogStyle);
                msgDialog.setContent("确定报价吗？", "", "确定", "取消");
                msgDialog.setCancleListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                    }
                });
                msgDialog.setOKListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                        if (CommonUtils.isNetworkAvailable(context)) {
                            String[] images = new String[0];
                            InteNetUtils.getInstance(getContext()).submitBuyPrice(
                                    buyId, sPrice, sInfo,images,"","",
                                    new RequestCallBack<String>() {
                                        @Override
                                        public void onFailure(HttpException arg0,
                                                              String arg1) {
                                            ToastUtils.Errortoast(getContext(),
                                                    "网络提交失败");
                                        }

                                        @Override
                                        public void onSuccess(ResponseInfo<String> arg0) {
                                            JSONObject jsonObject;
                                            try {
                                                jsonObject = new JSONObject(arg0.result);
                                                SuccessMsg msg = new SuccessMsg();
                                                msg.parseJSON(jsonObject);
                                                if (successLinstener != null) {
                                                    successLinstener.onSuccess(sName,
                                                            sPrice, sInfo);
                                                }
                                                ToastUtils.Infotoast(getContext(),
                                                        "报价成功");
                                                BuyDialog.this.dismiss();
                                            } catch (NetRequestException e) {
                                                e.getError().print(context);
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                ToastUtils.Errortoast(getContext(),
                                                        "网络提交失败");
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }

                    }

                });
                msgDialog.show();


			}
		});
	}

	private onSuccessLinstener successLinstener;

	public void setSuccessLinstener(onSuccessLinstener successLinstener) {
		this.successLinstener = successLinstener;
	}

	public interface onSuccessLinstener {

		void onSuccess(String name, String pricr, String info);

	}

}
