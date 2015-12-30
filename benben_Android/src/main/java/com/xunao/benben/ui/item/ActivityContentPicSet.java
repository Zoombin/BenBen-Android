package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract.Constants;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ShowBigImage;
import com.xunao.benben.hx.chatuidemo.widget.photoview.PhotoView;
import com.xunao.benben.hx.chatuidemo.widget.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.xunao.benben.hx.chatuidemo.widget.photoview.PhotoViewAttacher.OnViewTapListener;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityContentPicSet extends BaseActivity implements
		OnClickListener, OnPageChangeListener, ActionSheetListener {

	private ViewPager vp;
	private LinearLayout point_box;
	private String[] splits;
	private int position;
	private ArrayList<View> points;
	private HashMap<Integer, PhotoView> photoViews;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_pic_set);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null
						&& imageTask.getIdentityUrl().equalsIgnoreCase(
								(String) cubeImageView.getTag())) {

					View tag2 = (View) cubeImageView.getTag(R.string.pb);

					Boolean issuofang = (Boolean) cubeImageView
							.getTag(R.string.issuofang);

					String urlToFileFormat = CommonUtils
							.UrlToFileFormat(imageTask.getOriginUrl());
					if (tag2 != null) {
						tag2.setVisibility(View.GONE);
					}
					cubeImageView.setVisibility(View.VISIBLE);
					cubeImageView.setImageDrawable(drawable);

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
			}
		});

	}

	private File saveImage(boolean show) {

		PhotoView photoView = photoViews.get(position);

		Drawable drawable = photoView.getDrawable();

		if (drawable != null) {
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/benbenImage";

			String urlToFileName = CommonUtils.UrlToFileName(splits[position]);

			urlToFileName = urlToFileName.split("\\.")[0];

			File imgPra = new File(path);
			final File saveImage = new File(imgPra, urlToFileName + ".jpg");

			if (!imgPra.exists()) {
				imgPra.mkdirs();
			}
			if (saveImage.exists()) {
				saveImage.delete();
			}
			try {
				saveImage.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				CommonUtils.drawableTofile(drawable,
						saveImage.getAbsolutePath());
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            saveImage.getAbsolutePath(), urlToFileName + ".jpg", null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 最后通知图库更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(saveImage.getPath()))));
				if (show)
                    ToastUtils.Infotoast(getApplicationContext(), "图片保存至相册成功");
//					ToastUtils.Infotoast(mContext,
//							"图片保存成功" + saveImage.getAbsolutePath());


				return saveImage;
			} catch (IOException e) {
				ToastUtils.Infotoast(getApplicationContext(), "图片保存失败");
				e.printStackTrace();
			}

		} else {
			if (show)
				ToastUtils.Infotoast(getApplicationContext(), "图片保存失败");
		}
		return null;
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		vp = (ViewPager) findViewById(R.id.vp);
		point_box = (LinearLayout) findViewById(R.id.point_box);
		findViewById(R.id.saveImage).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTheme(R.style.ActionSheetStyleIOS7);
				showActionSheet();
			}
		});
	}

	public void showActionSheet() {
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消").setOtherButtonTitles("保存", "分享")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {

		switch (index) {
		case 0:
			saveImage(true);
			break;
		case 1:
			File saveImage = saveImage(false);
			if (saveImage != null) {
				CommonUtils.sharePicture(mContext, saveImage);
			} else {
				ToastUtils.Infotoast(mContext, "分享失败");
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		Intent intent = getIntent();
		String stringExtra = intent.getStringExtra("IMAGES");
		position = intent.getIntExtra("POSITION", 0);

		if (!TextUtils.isEmpty(stringExtra)) {
			splits = stringExtra.split("\\^");
			initTopImageAndPoint(splits.length);
		}
		photoViews = new HashMap<Integer, PhotoView>();

		points.get(position).setBackgroundResource(R.drawable.point_red);
		vp.setOnPageChangeListener(this);
		vp.setAdapter(new MyVpAdapter());
		vp.setCurrentItem(position);

	}

	// 初始化Viewpager的image与point
	private void initTopImageAndPoint(int size) {
		android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				PixelUtil.dp2px(6), PixelUtil.dp2px(6));
		layoutParams.rightMargin = PixelUtil.dp2px(4);
		points = new ArrayList<View>();
		View point;
		for (int i = 0; i < size; i++) {
			point = new View(mContext);
			point.setLayoutParams(layoutParams);
			point.setBackgroundResource(R.drawable.point_gray);
			points.add(point);
			point_box.addView(point, layoutParams);
		}

	}

	private class MyVpAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			if (splits != null) {
				return splits.length;
			} else {
				return 0;
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = getLayoutInflater().inflate(
					R.layout.item_activity_picset, null);
			final PhotoView photoView = (PhotoView) view
					.findViewById(R.id.img_photo_view);
			final LinearLayout pb_box = (LinearLayout) view
					.findViewById(R.id.pb_box);
			// 加载图片
			CommonUtils.startImageLoader(cubeimageLoader, splits[position],
					photoView);
			photoView.setTag(R.string.pb, pb_box);
			photoViews.put(position, photoView);
			view.setTag(photoView);
			container.addView(view);

			photoView.setOnViewTapListener(new OnViewTapListener() {

				@Override
				public void onViewTap(View view, float x, float y) {
					onBackPressed();
				}
			});

			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {

	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		for (View v : points) {
			v.setBackgroundResource(R.drawable.point_gray);
		}
		position = arg0;
		points.get(arg0).setBackgroundResource(R.drawable.point_red);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		this.overridePendingTransition(R.anim.in_from_nochange,
				R.anim.in_from_big2small);

	}
}
