package com.xunao.benben.utils;

import static android.os.Environment.MEDIA_MOUNTED;
import in.srain.cube.cache.DiskFileUtils;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.xunao.benben.R;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.dialog.LogoutDialog;
import com.xunao.benben.dialog.QrCodeDialog;
import com.xunao.benben.dialog.UpdateVersionDialog;
import com.xunao.benben.hx.chatuidemo.Constant;

@SuppressLint("NewApi")
public class CommonUtils {

	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			boolean available = info.isAvailable();
			if (!available) {
				ToastUtils.ErrorToastNoNet(context);
			}
			return available;
		}
		ToastUtils.ErrorToastNoNet(context);
		return false;
	}


    /** 检查是否有网络,不提示 */
    public static boolean isNetworkAvailableNoShow(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            boolean available = info.isAvailable();
            if (!available) {
                ToastUtils.ErrorToastNoNet(context);
            }
            return available;
        }
        return false;
    }

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	public static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/**
	 * 读取照片exif信息中的旋转角度
	 * 
	 * @param path
	 *            照片路径
	 * @return角度
	 */
	public static Bitmap readPictureDegree(String path, Bitmap bmp)
			throws OutOfMemoryError {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			if (exifInterface != null) {
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
				return adjustPhotoRotation(bmp, degree, 2);
			} else {
				return bmp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}

	private static Bitmap adjustPhotoRotation(Bitmap bm,
			final int orientationDegree, int inSampleSize)
			throws OutOfMemoryError {
		Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
				bm.getHeight(), m, true);
		return bm1;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static void showUpdateDialog(final Context context,
			final String url, String content, final Handler handler) {

		UpdateVersionDialog dialog = new UpdateVersionDialog(context);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.dialog_updateversion);
		ProgressDialog pBar = new ProgressDialog(context);
		pBar.setTitle("正在下载");
		pBar.setMessage("请稍候...");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// pBar.setCanceledOnTouchOutside(false);
		dialog.init(context, url, pBar, handler);
		dialog.setContent(content);
		dialog.show();
	}

	public static void showLogoutDialog(final Context context,
			CrashApplication application) {
		LogoutDialog dialog = new LogoutDialog(context);
		dialog.setCancelable(false);
		dialog.init(application);
		dialog.setContentView(R.layout.dialog_logout);
		dialog.setContent("您确定退出登录吗?");
		dialog.show();
	}

	public static void showQrCode(Context context, String url,
			ImageLoader imageLoader) {
		QrCodeDialog codeDialog = new QrCodeDialog(context);
		codeDialog.setCancelable(false);
		codeDialog.setCanceledOnTouchOutside(true);
		codeDialog.init(imageLoader);
		codeDialog.setContent(url);
		codeDialog.setContentView(R.layout.dialog_qrcode);
		codeDialog.show();
	}

	public static void showPoster(Context context, String url,
			ImageLoader imageLoader) {
		final QrCodeDialog codeDialog = new QrCodeDialog(context);
		codeDialog.setCancelable(true);
		codeDialog.setCanceledOnTouchOutside(true);
		codeDialog.init(imageLoader);
		codeDialog.setContent(url);
		codeDialog.setContentView(R.layout.dialog_show_poster);
		codeDialog.show();
	}

	// 开始下载更新
	public static void startUpdateAPK(final Context context, final String url,
			final ProgressDialog pBar, final Handler handler) {
		pBar.show();
		final File file = getAPKPath(context);
		if (file.exists()) {
			file.delete();
		} else {
			file.mkdirs();
		}

		new Thread() {
			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							if (length > 0) {
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down(context, handler, pBar);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static void down(final Context context, Handler handler,
			final ProgressDialog pBar) {

		handler.post(new Runnable() {
			@Override
			public void run() {
				pBar.cancel();
				update(context);
			}
		});
	}

	private static void update(Context context) {

		String apkPath = getAPKPath(context).getAbsolutePath();
		root(apkPath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkPath),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static void root(String path) {
		try {
			Runtime.getRuntime().exec("chmod 777 " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File getAPKPath(Context context) {

		File externalStorageDirectory = null;

		if (isExitsSdcard()) {
			externalStorageDirectory = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"benbenApk");

		} else {
			externalStorageDirectory = new File(context.getCacheDir(),
					"benbenApk");
		}
		if (externalStorageDirectory.exists()) {
			externalStorageDirectory.mkdirs();

		}
		return new File(externalStorageDirectory, "android_benben.apk");
	}

	/**
	 * 获取缓存地址
	 * 
	 */
	public static File getImageCachePath(Context context, String path) {
		return DiskFileUtils.getDiskCacheDir(context, path, 0).path;
	}

	public static boolean versionCompare(Context context, int version) {

		int versionCode = getVersionCode(context);

		if (versionCode >= version) {// 一样不需要更新
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/*
	 * MD5加密
	 */
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String toHexString(byte[] b) { // String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取系统可用内存
	 * 
	 */
	public static long getSystemMemoryInfo(Context context) {
		ActivityManager myActivityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		// 然后获得MemoryInfo类型对象
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		// 然后，使用getMemoryInfo(memoryInfo)方法获得系统可用内存，此方法将内存大小保存在memoryInfo对象上
		myActivityManager.getMemoryInfo(memoryInfo);
		// 然后，memoryInfo对象上的availmem值即为所求
		return memoryInfo.availMem;
	}

	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context
				.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	// 根据图片名称获取图片
	public static File getImagePath(Context mContext, String photoFileName) {
		return new File(getImageCachePath(mContext, "temp"), photoFileName);
	}

	// 根据获取父级地址
	public static File getParentImagePath(Context mContext) {
		return getImageCachePath(mContext, "temp").getParentFile();
	}

	/**
	 * Returns specified application cache directory. Cache directory will be
	 * created on SD card by defined path if card is mounted and app has
	 * appropriate permission. Else - Android defines cache directory on
	 * device's file system.
	 * 
	 * @param context
	 *            Application context
	 * @param cacheDir
	 *            Cache directory path (e.g.: "AppCacheDir",
	 *            "AppDir/cache/images")
	 * @return Cache {@link File directory}
	 */
	public static File getOwnCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&& hasExternalStoragePermission(context)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(),
					cacheDir);
		}
		if (appCacheDir == null
				|| (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	/**
	 * 将URL中文件名分析出来 为防止冲突 取倒数第二个斜杠
	 * 
	 * @param url
	 * @return
	 */
	public static String UrlToFileName(String url) {
		if (url.length() > 0) {
			String[] strs = url.split("/");
			return strs[strs.length - 2] + strs[strs.length - 1];
		} else {
			return "";
		}
	}

	/**
	 * 将URL中各式取出
	 * 
	 * @param url
	 * @return
	 */
	public static String UrlToFileFormat(String url) {
		if (url.length() > 0) {
			int lastIndexOf = url.lastIndexOf('.');
			return url.substring(lastIndexOf + 1, url.length());
		} else {
			return "";
		}
	}

	/**
	 * 取得视频大小
	 * 
	 * @return
	 */
	// public static File getVideoFile(Context context, Content_News
	// content_News) {
	//
	// File videoFile = new File(getVideoCachePath(context),
	// UrlToFileName(content_News.getMp4url()));
	// if (videoFile.exists()) {
	// return videoFile;
	// } else {
	// return null;
	// }
	// }

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "no_version";
		}
	}

	/**
	 * 将数字转化为固定格式
	 * 
	 * @param str
	 * @return
	 */
	public static String NumFormat(String str) {
		NumberFormat nf = new DecimalFormat("###,###,###");
		String str_result = nf.format(Integer.valueOf(str));
		return str_result;
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static void startImageLoader(ImageLoader imageLoadert, String url,
			CubeImageView imageView) {

		// String urlToFileFormat = UrlToFileFormat(url);
		// if (!"gif".equalsIgnoreCase(urlToFileFormat) ) {
		// int lastIndexOf = url.lastIndexOf('.');
		// if (lastIndexOf == -1)
		// return;
		// url = url.substring(0, lastIndexOf);
		// url += ".webp";
		// }

		if (!TextUtils.isEmpty(url)) {
			imageView.setTag(url);
			imageView.loadImage(imageLoadert, url, 0);
		} else {
			// 防止加载图片BUG
			// imageView.clearLoadTask
			imageView.loadImage(imageLoadert, "www.baidu.com", 0);
			// imageView.setImageResource(R.drawable.default_face);
		}

	}

	/*
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				// 从sdk中提到了ui中，使用更简单不犯错的获取string方法
				// digest = EasyUtils.getAppResourceString(context,
				// "location_recv");
				digest = getStrng(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				// digest = EasyUtils.getAppResourceString(context,
				// "location_prefix");
				digest = getStrng(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			digest = getStrng(context, R.string.picture);
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			if (!message.getBooleanAttribute(
					Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			} else {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call)
						+ txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	static String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	// 计算年龄
	public static String getAge(Context context, Date birthday) {
		try {
			Date now = new Date();
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");

			if (now.getTime() < birthday.getTime()) {
				ToastUtils.Infotoast(context, "生日不能大于当前日期！");
				return null;
			}

			long days = (now.getTime() - birthday.getTime())
					/ (1000 * 60 * 60 * 24);
			String years = new DecimalFormat("#").format(days / 365f);
			return years;
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtils.Infotoast(context, e.getMessage());
			return null;
		}
	}

	// 计算年龄
	public static String getAge2(Context context, String birthday) {
		try {
			Date now = new Date();
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date born = myFormatter.parse(birthday);

			if (now.getTime() < born.getTime()) {
				ToastUtils.Infotoast(context, "生日不能大于当前日期！");
				return null;
			}

			long days = (now.getTime() - born.getTime())
					/ (1000 * 60 * 60 * 24);
			String years = new DecimalFormat("#").format(days / 365f);
			return years;
		} catch (ParseException e) {
			e.printStackTrace();
			ToastUtils.Infotoast(context, e.getMessage());
			return null;
		}
	}

	// 时间戳转为日期
	public static Date changeLongDateToDate(long longDate)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Long time = new Long(longDate);

		return format.parse(format.format(time));
	}

	// 换算时间
	public static String getDate(Context context, String timeLong) {
		try {
			Date now = new Date();
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date born = myFormatter.parse(timeLong);

			if (now.getTime() > born.getTime()) {
				ToastUtils.Infotoast(context, "时间不能小于当前时间");
				return null;
			}

			return timeLong;
		} catch (ParseException e) {
			e.printStackTrace();
			ToastUtils.Infotoast(context, e.getMessage());
			return null;
		}
	}

	/**
	 * 汉字转拼音的方法
	 * 
	 * @param name
	 *            汉字
	 * @return 拼音
	 */
	public static String hanYuToPinyin(String name) {
		try {
			name = name.replaceAll(" ", "");
			name = name.replaceAll("[^\u4e00-\u9fa5a-zA-Z0-9]", "");
			String firstName = name.substring(0, 1);

			char[] nameChar = name.toCharArray();
			Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");
			Matcher m = p_str.matcher(firstName);

			if (!(m.find() && m.group(0).equals(firstName))) {
				return firstName.toUpperCase();
			}

			String pinyinName = "";
			HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
			defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			for (int i = 0; i < nameChar.length; i++) {
				if (nameChar[i] > 128) {
					try {
						pinyinName += PinyinHelper.toHanyuPinyinStringArray(
								nameChar[i], defaultFormat)[0];
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
						return "#";
					}

				}
			}

			if (pinyinName.equals("")) {
				return "#";
			} else {
				return pinyinName.toUpperCase();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "#";
		}

	}

	// 压缩图片到指定大小 size kb单位
	public static InputStream compressImage(File image, int size,
			int inSampleSize, InputStream... bm) {
		Bitmap decodeFile = null;
		InputStream isBm = null;
		if (bm != null && bm.length > 0) {
			isBm = bm[0];
		}
		// try {
		decodeFile = PhotoUtils.rotaingImageView(
				PhotoUtils.readPictureDegree(image.getAbsolutePath()),
				image.getAbsolutePath());

		if (decodeFile != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			decodeFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			baos.reset();// 重置baos即清空baos
			decodeFile.compress(Bitmap.CompressFormat.JPEG, options, baos);
			while (baos.toByteArray().length / 1024 > size) { //
				// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				decodeFile.compress(Bitmap.CompressFormat.JPEG, options, baos);//
				// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			decodeFile.recycle();
			decodeFile = null;
			isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			return isBm;
		}
		// } catch (OutOfMemoryError e) {
		// if (decodeFile != null) {
		// decodeFile.recycle();
		// decodeFile = null;
		// }
		// }
		return isBm;
	}

	public static void showSoftInput(Activity mActivity, EditText edittext) {
		InputMethodManager imm = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示键盘
		imm.showSoftInput(edittext, 0);
	}

	public static void hideSoftInputFromWindow(Activity mActivity) {
		InputMethodManager imm = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示键盘
		imm.hideSoftInputFromWindow(mActivity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static boolean StringIsSurpass(String inputStr, int num) {
		int orignLen = inputStr.length();
		int resultLen = 0;
		String temp = null;
		for (int i = 0; i < orignLen; i++) {
			temp = inputStr.substring(i, i + 1);
			try {// 3 bytes to indicate chinese word,1 byte to indicate english
					// word ,in utf-8 encode
				if (temp.getBytes("utf-8").length == 3) {
					resultLen += 2;
				} else {
					resultLen++;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (resultLen > (num * 2)) {
				return true;
			}
		}
		return false;
	}

	public static boolean StringIsSurpass(String inputStr, int minnum,
			int maxnum) {
		int orignLen = inputStr.length();
		int resultLen = 0;
		String temp = null;
		for (int i = 0; i < orignLen; i++) {
			temp = inputStr.substring(i, i + 1);
			try {// 3 bytes to indicate chinese word,1 byte to indicate english
					// word ,in utf-8 encode
				if (temp.getBytes("utf-8").length == 3) {
					resultLen += 2;
				} else {
					resultLen++;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (resultLen >= (minnum * 0.5) && (maxnum * 2 + 0.5) >= resultLen) {
			return true;
		}else{
			return false;
		}
		
	}
	
	public static boolean StringIsSurpass2(String inputStr, int minnum,
			int maxnum) {
		int orignLen = inputStr.length();
		int resultLen = 0;
		String temp = null;
		for (int i = 0; i < orignLen; i++) {
			temp = inputStr.substring(i, i + 1);
			try {// 3 bytes to indicate chinese word,1 byte to indicate english
					// word ,in utf-8 encode
				if (temp.getBytes("utf-8").length == 3) {
					resultLen += 2;
				} else {
					resultLen++;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (resultLen >= (minnum * 1) && (maxnum * 2 + 0.5) >= resultLen) {
			return true;
		}else{
			return false;
		}
		
	}

	public static boolean isEmpty(String str) {
		if (str != null) {
			String regex = "\\s+";
			str = str.replaceAll(regex, "");
			return TextUtils.isEmpty(str.trim());
		} else {
			return true;
		}
	}

	/**
	 * 将Drawable保存到本地
	 * 
	 * @param drawable
	 * @param path
	 * @throws IOException
	 */
	public static void drawableTofile(Drawable drawable, String path)
			throws IOException {
		// Log.i(TAG, "drawableToFile:"+path);
		File file = new File(path);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100 /* ignored for PNG */, bos);
		byte[] bitmapdata = bos.toByteArray();

		// write the bytes in file
		FileOutputStream fos;
		fos = new FileOutputStream(file);
		fos.write(bitmapdata);


	}

	/**
	 * 集合去重复方法
	 * 
	 * @param li
	 * @return
	 */
	public static List<Object> getNewList(List<Object> li) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < li.size(); i++) {
			Object o = li.get(i); // 获取传入集合对象的每一个元素
			if (!list.contains(o)) { // 查看新集合中是否有指定的元素，如果没有则加入
				list.add(o);
			}
		}
		return list; // 返回集合
	}

	// 共享一张图片
	public static void sharePicture(Activity content, File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		intent.setType("image/jpeg");
		Intent.createChooser(intent, "共享一张图片");
		content.startActivity(intent);
	}


    public static String getString(InputStream inputStream) {  
        InputStreamReader inputStreamReader = null;  
        try {  
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        }  
        BufferedReader reader = new BufferedReader(inputStreamReader);  
        StringBuffer sb = new StringBuffer("");  
        String line;  
        try {  
            while ((line = reader.readLine()) != null) {  
                sb.append(line);  
                sb.append("\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return sb.toString();  
    }  
	
}
