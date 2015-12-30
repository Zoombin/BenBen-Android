package com.xunao.benben.ui.item;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.edmodo.cropper.CropImageView;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.ui.item.ActivityMyNumberTrain;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityCutImage extends BaseActivity {
	private CropImageView CropImageView;

	private File file;

	private static final int PIC_REQUEST_CODE_WITH_DATA = 1; // 标识获取图片数据

	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
	private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
	private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

	private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
	private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

	private Bitmap bitMap;

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
		bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
	}

	@Override
	protected void onRestoreInstanceState(Bundle bundle) {

		super.onRestoreInstanceState(bundle);
		mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
		mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_cut_image);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("裁剪图片", "", "裁剪",
				R.drawable.icon_com_title_left, 0);
		CropImageView = (com.edmodo.cropper.CropImageView) findViewById(R.id.CropImageView);
		CropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES,
				DEFAULT_ASPECT_RATIO_VALUES);
		CropImageView.setFixedAspectRatio(true);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		String imagePath = intent.getStringExtra("imagePath");
		/** 
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
         */ 
        int degree = readPictureDegree(imagePath);  
         
        BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
        opts.inSampleSize=2;
        Bitmap cbitmap=BitmapFactory.decodeFile(imagePath,opts);
         
        /** 
         * 把图片旋转为正的方向 
         */ 
        Bitmap newbitmap = rotaingImageView(degree, cbitmap);  
        
        String imageName = getPhotoFileName();
		file = CommonUtils.getImagePath(ActivityCutImage.this,
				imageName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			newbitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (newbitmap != null) {
			newbitmap.recycle();
			newbitmap = null;
		}
        
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;

		bitMap = CutImageUtils.convertToBitmap(file.getAbsolutePath(), screenWidth,
				screenHeigh);
		CropImageView.setImageBitmap(bitMap);
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
				String imageName = getPhotoFileName();
				Bitmap bitmap = CropImageView.getCroppedImage();
				file = CommonUtils.getImagePath(ActivityCutImage.this,
						imageName);
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
							out = null;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (bitmap != null) {
					bitmap.recycle();
					bitmap = null;
				}
				String imagePath = file.getAbsolutePath();
				CommonUtils.root(imagePath);
				Intent intent = new Intent();
				intent.setData(Uri.parse("file://" + imagePath));
				intent.putExtra("imagePath", imagePath);
				intent.putExtra("imageName", imageName);
				setResult(PIC_REQUEST_CODE_WITH_DATA, intent);
				AnimFinsh();
			}
		});

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
	protected void onDestroy() {
		super.onDestroy();
		if (bitMap != null) {
			bitMap.recycle();
			bitMap = null;
		}
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
	 * 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

}
