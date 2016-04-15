package com.xunao.benben.ui.promotion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.ui.shareselect.ActivityShareSelectFriend;
import com.xunao.benben.ui.shareselect.ActivityShareSelectTalkGroup;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;

import org.json.JSONObject;

/**
 * 促销详情
 */

public class ActivityPromotionDetail extends BaseActivity {

	private WebView webView;
	private TextView tv_search,tv_sendmsg,tv_call,tv_buynow;
	private LodingDialog lodingDialog;
    private String[] promotionId= new String[0];
    private int position;

    private LinearLayout ll_bottom;
    private String hxName="";
    private String train_id="";
    private String tel = "";
    private String type = "";


	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_web);
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		webView = (WebView) findViewById(R.id.webView);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		tv_search = (TextView) findViewById(R.id.tv_search);
		tv_sendmsg = (TextView) findViewById(R.id.tv_sendmsg);
		tv_call = (TextView) findViewById(R.id.tv_call);
		tv_buynow = (TextView) findViewById(R.id.tv_buynow);
        tv_buynow.setVisibility(View.GONE);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUserAgentString("benben");

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int mDensity = metrics.densityDpi;
		if (mDensity == 240) {
			webSettings.setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == 160) {
			webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
		} else if (mDensity == 120) {
			webSettings.setDefaultZoom(ZoomDensity.CLOSE);
		} else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
			webSettings.setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == DisplayMetrics.DENSITY_TV) {
			webSettings.setDefaultZoom(ZoomDensity.FAR);
		}
		MyWebClinet myWebClinet = new MyWebClinet();
		webView.setWebViewClient(myWebClinet);
		webView.addJavascriptInterface(new MyJsInterface(), "benben");

		initTitle_Right_Left_bar("促销详情", "", "", R.drawable.icon_com_title_left, R.drawable.icon_share);
		
	//	progressBar = findViewById(R.id.progressBar);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		if(!TextUtils.isEmpty(url)){
		}else{
			String ids = intent.getStringExtra("ids");
			promotionId = ids.split(";");
			position = intent.getIntExtra("position", 0);
			url = AndroidConfig.NETHOST + "/promotion/promotiondetail/key/android?promotionid=" + promotionId[position];
		}
        webView.loadUrl(url);
		
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnimFinsh();
			}
		});

        setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setTheme(R.style.ActionSheetStyleIOS7);
				showMoreActionSheet();
			}
		});
		tv_sendmsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				webView.loadUrl("javascript:window.benben.sendMsg(document.getElementsByName('huanxin_username')[0].value)");
                startAnimActivity2Obj(ChatActivity.class, "userId",hxName);
			}
		});
		tv_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				webView.loadUrl("javascript:window.benben.search(document.getElementsByName('train_id')[0].value)");
                Intent intent = new Intent(ActivityPromotionDetail.this, ActivityNumberTrainDetail.class);
                intent.putExtra("id", train_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

			}
		});
		tv_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				webView.loadUrl("javascript:window.benben.getTel(document.getElementsByName('tel')[0].value)");
                setTheme(R.style.ActionSheetStyleIOS7);
                String[] phones = tel.split("#");
                showActionSheet(phones);
			}
		});
		tv_buynow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				webView.loadUrl("javascript:window.benben.bynow(document.getElementsByName('type')[0].value)");
			}
		});
	}


    private void showActionSheet(final String[] phones) {

        ActionSheet.createBuilder(mContext, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(phones)
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        PhoneUtils.makeCall(Integer.parseInt(train_id), "",
                                phones[index], mContext);

                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {
                    }
                }).show();
    }

    public void showMoreActionSheet() {
        ActionSheet
        .createBuilder(this, getSupportFragmentManager())
        .setCancelButtonTitle("取消")
        .setOtherButtonTitles("收藏", "分享给好友","分享到群组")
                // 设置颜色 必须一一对应
        .setOtherButtonTitlesColor("#1E82FF", "#1E82FF", "#1E82FF")
        .setCancelableOnTouchOutside(true)
        .setListener(new ActionSheet.ActionSheetListener() {

            @Override
            public void onOtherButtonClick(ActionSheet actionSheet,
                                           int index) {
                switch (index) {
                    case 0:
                        if(CommonUtils.isNetworkAvailable(mContext)){
                            InteNetUtils.getInstance(mContext).AddCollect(promotionId[position],"0",mRequestCallBack);
                        }else{
                            ToastUtils.Infotoast(mContext,"当前网络不可用");
                        }
                        break;
//                    case 1:
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/plain");
//                        Intent.createChooser(intent, "促销分享");
//                        intent.putExtra(Intent.EXTRA_TEXT,  AndroidConfig.NETHOST+"/promotion/promotiondetail/key/android?promotionid="+promotionId[position]);
//                        startActivity(intent);
////                        // 设置弹出框标题
////                        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
////                            startActivity(Intent.createChooser(intent, dlgTitle));
////                        } else { // 系统默认标题
////                            startActivity(intent);
////                        }
//                        break;

                    case 1:
                        Intent intent = new Intent(ActivityPromotionDetail.this, ActivityShareSelectFriend.class);
                        intent.putExtra("url",AndroidConfig.NETHOST+"/promotion/promotiondetail/key/android?promotionid="+promotionId[position]);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        break;
                    case 2:
                        Intent groupintent = new Intent(ActivityPromotionDetail.this, ActivityShareSelectTalkGroup.class);
                        groupintent.putExtra("url",AndroidConfig.NETHOST+"/promotion/promotiondetail/key/android?promotionid="+promotionId[position]);
                        startActivity(groupintent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onDismiss(ActionSheet actionSheet,
                                  boolean isCancel) {
                // TODO Auto-generated method stub

            }
        }).show();
    }

	@Override
	protected void onHttpStart() {
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
        if(jsonObject.optInt("ret_num")==0){
            ToastUtils.Infotoast(mContext,"收藏成功");
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub
        ToastUtils.Infotoast(mContext,"收藏失败!");
	}

	@Override
	public void onBackPressed() {

		if (webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回webView的上一页面
		} else {
			AnimFinsh();
		}

	}

	private class MyWebClinet extends WebViewClient {

        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            position++;
            if(position==promotionId.length){
                position--;
                ToastUtils.Infotoast(mContext,"已无更多促销");
            }else{
                view.loadUrl( AndroidConfig.NETHOST+"/promotion/promotiondetail/key/android?promotionid="+promotionId[position]);
            }

//            view.loadUrl(url);


            //如果不需要其他对点击链接事件的处理返回true，否则返回false

            return true;

        }


		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			//progressBar.setVisibility(View.VISIBLE);
			
			if (isShowLoding) {
				if (lodingDialog == null) {
					lodingDialog = new LodingDialog(mContext);
				}
				lodingDialog.show();
			}
			
			

		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			//progressBar.setVisibility(View.GONE);
			if (lodingDialog != null && lodingDialog.isShowing()) {
				lodingDialog.dismiss();
			}
			ToastUtils.Errortoast(mContext, "网络延时!页面无法访问");
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			//progressBar.setVisibility(View.GONE);
			if (lodingDialog != null && lodingDialog.isShowing()) {
				lodingDialog.dismiss();
			}
            webView.loadUrl("javascript:window.benben.sendMsg(document.getElementsByName('huanxin_username')[0].value)");
            webView.loadUrl("javascript:window.benben.search(document.getElementsByName('train_id')[0].value)");
            webView.loadUrl("javascript:window.benben.getTel(document.getElementsByName('tel')[0].value)");
            webView.loadUrl("javascript:window.benben.bynow(document.getElementsByName('type')[0].value)");
		}

	}

	public class MyJsInterface {
		@JavascriptInterface
		public void getTel(String info) {
			Log.d("TTT",info);
            tel = info;
		}
		@JavascriptInterface
		public void bynow(String info) {
			Log.d("TTT",info);
            type = info;
		}
		@JavascriptInterface
		public void search(String info) {
			Log.d("TTT",info);
            train_id = info;
		}
		@JavascriptInterface
		public void sendMsg(String info) {
			Log.d("TTT",info);
            hxName = info;
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(hxName.equals(user.getHuanxin_username())){
                        ll_bottom.setVisibility(View.GONE);
                    }else{
                        ll_bottom.setVisibility(View.VISIBLE);
                    }
                }
            });


		}
	}
}
