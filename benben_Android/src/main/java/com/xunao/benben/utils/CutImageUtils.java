package com.xunao.benben.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

public class CutImageUtils {
	public static String getImagePath(Context context, InputStream stream){
		File mCurrentPhotoFile = null;
		try {
			FileOutputStream fileOutputStream = null;
			Bitmap decodeFile = null;
			mCurrentPhotoFile = CommonUtils.getImagePath(context, getPhotoFileName());
			fileOutputStream = new FileOutputStream(mCurrentPhotoFile);

			int length = 0;
			byte[] buf = new byte[1024 * 1024];

			while ((length = stream.read(buf)) != -1) {
				fileOutputStream.write(buf);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
		
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtils.Errortoast(context, "获取图片异常，请重新尝试。");
		}
		
		return mCurrentPhotoFile.getAbsolutePath();
	}
	
	// 用当前时间给取得的图片命名
		public static String getPhotoFileName() {
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"'IMG'_yyyyMMdd_HHmmss");
			return dateFormat.format(date) + ".jpg";
		}

		public static Intent getTakePickIntent(File f) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			return intent;
		}
		
		
		public static Bitmap getBitMap(Context context, Uri data, BitmapFactory.Options opts, View v) {

			ContentResolver resolver = context.getContentResolver();
			Bitmap myBitmap = null;
			try {
				byte[] mContent = readStream(resolver.openInputStream(Uri
						.parse(data.toString())));
				myBitmap = getPicFromBytes(mContent, opts, v);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return myBitmap;
		}
		
		public static byte[] readStream(InputStream inStream) throws Exception {
			byte[] buffer = new byte[1024];
			int len = -1;
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			byte[] data = outStream.toByteArray();
			outStream.close();
			inStream.close();
			return data;

		}
		
		public static Bitmap getPicFromBytes(byte[] bytes,
				BitmapFactory.Options opts, View v) {
			if (bytes != null)
				if (opts != null) {
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
					int outWidth = opts.outWidth;
					int width = v.getWidth();
					if (outWidth > width) {
						opts.inSampleSize = outWidth / width;
					}
					opts.inJustDecodeBounds = false;
					return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
							opts);
				} else
					return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			return null;
		}

		
		public static Bitmap convertToBitmap(String path, int w, int h) {
	        BitmapFactory.Options opts = new BitmapFactory.Options();
	        // 设置为ture只获取图片大小
	        opts.inJustDecodeBounds = true;
	        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
	        // 返回为空
	        BitmapFactory.decodeFile(path, opts);

	        int width = opts.outWidth;
	        int height = opts.outHeight;
	        float scaleWidth = 0.f, scaleHeight = 0.f;
	        if (width > w || height > h) {
	            // 缩放
	            scaleWidth = ((float) width) / w;
	            scaleHeight = ((float) height) / h;
	        }
	        opts.inJustDecodeBounds = false;
	        float scale   = Math.max(scaleWidth, scaleHeight);
	        opts.inSampleSize = (int) scale;
	        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));

			return weak.get();
		}
		
		
		
}
