/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunao.benben.hx.chatuidemo.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.easemob.chat.EMChatConfig;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.xunao.benben.R;
import com.xunao.benben.hx.chatuidemo.task.LoadLocalBigImgTask;
import com.xunao.benben.hx.chatuidemo.utils.ImageCache;
import com.xunao.benben.hx.chatuidemo.widget.photoview.PhotoView;
import com.xunao.benben.ui.item.TallGroup.ActivityCreatedTallGroup;
import com.xunao.benben.ui.item.TallGroup.ActivityFindTalkGroup;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends BaseActivity implements ActionSheetListener {

	private ProgressDialog pd;
	private PhotoView image;
	private int default_res = R.drawable.default_image;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private ProgressBar loadLocalPb;
	private Uri uri;
	private String remotepath;

	private File saveImage(boolean show) {

		if (localFilePath == null) {
			localFilePath = uri.getPath();
		}
		File image = new File(localFilePath);

		if (image != null && image.exists()) {
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/benbenImage";

			String urlToFileName = CommonUtils.UrlToFileName(localFilePath);

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
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = new FileInputStream(image);
				fos = new FileOutputStream(saveImage);
				byte[] buffer = new byte[1024];
				int read = 0;
				while ((read = fis.read(buffer)) != -1) {
					fos.write(buffer);
				}
				fos.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (fis != null)
						fis.close();
					if (fos != null)
						fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (show)
				ToastUtils.Infotoast(getApplicationContext(), "图片保存成功"
						+ saveImage.getAbsolutePath());
			return saveImage;
		} else {
			if (show)
				ToastUtils.Infotoast(getApplicationContext(), "图片保存失败");

			return null;
		}

	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);

		image = (PhotoView) findViewById(R.id.image);
		loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
		default_res = getIntent().getIntExtra("default_image",
				R.drawable.default_avatar);
		uri = getIntent().getParcelableExtra("uri");
		remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");
		System.err.println("show big image uri:" + uri + " remotepath:"
				+ remotepath);

		// 本地存在，直接显示本地的图片
		if (uri != null && new File(uri.getPath()).exists()) {
			System.err.println("showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = ImageCache.getInstance().get(uri.getPath());
			if (bitmap == null) {
				LoadLocalBigImgTask task = new LoadLocalBigImgTask(this,
						uri.getPath(), image, loadLocalPb,
						ImageUtils.SCALE_IMAGE_WIDTH,
						ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			} else {
				image.setImageBitmap(bitmap);
			}
		} else if (remotepath != null) { // 去服务器下载图片
			System.err.println("download remote image");
			Map<String, String> maps = new HashMap<String, String>();
			if (!TextUtils.isEmpty(secret)) {
				maps.put("share-secret", secret);
			}
			downloadImage(remotepath, maps);
		} else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

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
				CommonUtils.sharePicture(ShowBigImage.this, saveImage);
			} else {
				ToastUtils.Infotoast(ShowBigImage.this, "分享失败");
			}

			break;
		default:
			break;
		}

	}

	/**
	 * 通过远程URL，确定下本地下载后的localurl
	 * 
	 * @param remoteUrl
	 * @return
	 */
	public String getLocalFilePath(String remoteUrl) {
		String localPath;
		if (remoteUrl.contains("/")) {
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath()
					+ "/" + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
		} else {
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath()
					+ "/" + remoteUrl;
		}
		return localPath;
	}

	/**
	 * 下载图片
	 * 
	 * @param remoteFilePath
	 */
	private void downloadImage(final String remoteFilePath,
			final Map<String, String> headers) {
		String str1 = getResources().getString(R.string.Download_the_pictures);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		localFilePath = getLocalFilePath(remoteFilePath);
		final HttpFileManager httpFileMgr = new HttpFileManager(this,
				EMChatConfig.getInstance().getStorageUrl());
		final CloudOperationCallback callback = new CloudOperationCallback() {
			public void onSuccess(String resultMsg) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(
								metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath,
								screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							ImageCache.getInstance().put(localFilePath, bitmap);
							isDownloaded = true;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(String msg) {
				Log.e("###", "offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if (file.exists() && file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						image.setImageResource(default_res);
					}
				});
			}

			public void onProgress(final int progress) {
				Log.d("ease", "Progress: " + progress);
				final String str2 = getResources().getString(
						R.string.Download_the_pictures_new);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				httpFileMgr.downloadFile(remoteFilePath, localFilePath,
						headers, callback);
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}
}
