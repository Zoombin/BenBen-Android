package com.xunao.benben.ui.item;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.chat.EMChatConfig;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.ChatImages;
import com.xunao.benben.hx.chatuidemo.task.LoadLocalBigImgTask;
import com.xunao.benben.hx.chatuidemo.utils.ImageCache;
import com.xunao.benben.hx.chatuidemo.widget.photoview.PhotoView;
import com.xunao.benben.hx.chatuidemo.widget.photoview.PhotoViewAttacher.OnViewTapListener;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

public class ActivityChatPicSet extends BaseActivity implements
		OnClickListener, OnPageChangeListener, ActionSheetListener {

	private ViewPager vp;
//	private LinearLayout point_box;
	private int position;
//	private ArrayList<View> points;
	private HashMap<Integer, PhotoView> photoViews;
    private List<ChatImages> chatImageses = new ArrayList<>();
    private TextView tv_num;
    private ProgressBar loadLocalPb;
    private ProgressDialog pd;
    private String localFilePath;
    private int default_res = R.drawable.default_image;
    private boolean isDownloaded;

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

        if (localFilePath == null) {
            localFilePath = chatImageses.get(position).getFilePath();
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
//                ToastUtils.Infotoast(getApplicationContext(), "图片保存成功"
//                        + saveImage.getAbsolutePath());
            return saveImage;
        } else {
            if (show)
                ToastUtils.Infotoast(getApplicationContext(), "图片保存失败");

            return null;
        }

    }

	@Override
	public void initView(Bundle savedInstanceState) {
		vp = (ViewPager) findViewById(R.id.vp);
//		point_box = (LinearLayout) findViewById(R.id.point_box);
        tv_num = (TextView) findViewById(R.id.tv_num);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
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
        chatImageses = (List<ChatImages>) getIntent().getSerializableExtra("chatImageses");
        position = intent.getIntExtra("position", 0);
//        initTopImageAndPoint(chatImageses.size());
        tv_num.setText((position+1)+"/"+chatImageses.size());

		photoViews = new HashMap<Integer, PhotoView>();

//		points.get(position).setBackgroundResource(R.drawable.point_red);
		vp.setOnPageChangeListener(this);
		vp.setAdapter(new MyVpAdapter());
		vp.setCurrentItem(position);

	}

//	// 初始化Viewpager的image与point
//	private void initTopImageAndPoint(int size) {
//		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//				PixelUtil.dp2px(6), PixelUtil.dp2px(6));
//		layoutParams.rightMargin = PixelUtil.dp2px(4);
//		points = new ArrayList<View>();
//		View point;
//		for (int i = 0; i < size; i++) {
//			point = new View(mContext);
//			point.setLayoutParams(layoutParams);
//			point.setBackgroundResource(R.drawable.point_gray);
//			points.add(point);
//			point_box.addView(point, layoutParams);
//		}
//
//	}

	private class MyVpAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			if (chatImageses != null) {
				return chatImageses.size();
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
//			CommonUtils.startImageLoader(cubeimageLoader, splits[position],
//					photoView);
            // 本地存在，直接显示本地的图片
            ChatImages chatImages = chatImageses.get(position);
            File file = new File(chatImages.getFilePath());
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
            }
            if (chatImages.getFilePath() != null && new File(chatImages.getFilePath()).exists()) {
                System.err.println("showbigimage file exists. directly show it");
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                // int screenWidth = metrics.widthPixels;
                // int screenHeight =metrics.heightPixels;
                Bitmap bitmap = ImageCache.getInstance().get(chatImages.getFilePath());
                if (bitmap == null) {
                    LoadLocalBigImgTask task = new LoadLocalBigImgTask(ActivityChatPicSet.this,
                            chatImages.getFilePath(), photoView, loadLocalPb,
                            ImageUtils.SCALE_IMAGE_WIDTH,
                            ImageUtils.SCALE_IMAGE_HEIGHT);
                    if (android.os.Build.VERSION.SDK_INT > 10) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        task.execute();
                    }
                } else {
                    photoView.setImageBitmap(bitmap);
                }
            } else if (chatImages.getRemotePath() != null) { // 去服务器下载图片
//                System.err.println("download remote image");
                Map<String, String> maps = new HashMap<String, String>();
                if (!TextUtils.isEmpty(chatImages.getSecret())) {
                    maps.put("share-secret", chatImages.getSecret());
                }
                downloadImage(photoView,chatImages.getRemotePath(), maps);
            } else {
                photoView.setImageResource(default_res);
            }




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
//		for (View v : points) {
//			v.setBackgroundResource(R.drawable.point_gray);
//		}
		position = arg0;
//		points.get(arg0).setBackgroundResource(R.drawable.point_red);
        tv_num.setText((arg0+1)+"/"+chatImageses.size());
	}

	@Override
	public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
		this.finish();
		this.overridePendingTransition(R.anim.in_from_nochange,
				R.anim.in_from_big2small);

	}

    /**
     * 下载图片
     *
     * @param remoteFilePath
     */
    private void downloadImage(final PhotoView image ,final String remoteFilePath,
                               final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
//        pd.show();
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

                        Bitmap bitmap = ImageUtils.decodeScaleImage(localFilePath,
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


}
