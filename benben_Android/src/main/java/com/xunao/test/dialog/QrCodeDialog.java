package com.xunao.test.dialog;

import in.srain.cube.image.ImageLoader;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.IA.CrashApplication;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.PixelUtil;

import android.content.Context;
import android.widget.LinearLayout;

public class QrCodeDialog extends AbsDialog {
	private RoundedImageView imageView;

	public QrCodeDialog(Context context) {
		super(context, R.style.MyDialog1);
	}

	public QrCodeDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public QrCodeDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void initView() {
		imageView = (RoundedImageView) findViewById(R.id.iv_code);
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	protected void initData() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				CrashApplication.getInstance().mScreenWidth
						- PixelUtil.dp2px(20),
				CrashApplication.getInstance().mScreenWidth
						- PixelUtil.dp2px(20));
		imageView.setLayoutParams(params);
		imageView.setTag(R.string.type, "twocode");
		CommonUtils.startImageLoader(imageLoader, content, imageView);
	}

	@Override
	protected void setListener() {

	}

	ImageLoader imageLoader;
	String content;

	public void init(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}
}
