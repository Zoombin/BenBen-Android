package com.xunao.benben.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

public class ActivityMyWeb extends BaseActivity {

	private WebView webView;
	private LodingDialog lodingDialog;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_web);
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持javascript
        webSettings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
        //扩大比例的缩放
        webSettings.setUseWideViewPort(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
		MyWebClinet myWebClinet = new MyWebClinet();
		webView.setWebViewClient(myWebClinet);
	
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		initTitle_Right_Left_bar(title, "", "", R.drawable.icon_com_title_left, 0);

        webView.setWebChromeClient(new WebChromeClient()
        {
            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );
            }

        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;

            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
}
