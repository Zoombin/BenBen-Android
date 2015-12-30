package com.xunao.benben.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;

import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.utils.ToastUtils;

public class ActivityWeb extends BaseActivity {

	private WebView webView;
	private View progressBar;
	private LodingDialog lodingDialog;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_web);
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

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
	
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		initTitle_Right_Left_bar(title, "", "", R.drawable.icon_com_title_left, 0);
		
	//	progressBar = findViewById(R.id.progressBar);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		
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
	}

	@Override
	protected void onHttpStart() {
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject t) {

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

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
		}

	}
}
