package com.xunao.benben.utils;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.easemob.chat.EMChatConfig.EMEnvMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;

public class Core {

	// 生成二维码
	public static void twoDimensionCode(User user, ImageView qr_image,
			int mScreenWidth, int mScreenHeight) {
		StringBuffer sBuffer = new StringBuffer(AndroidConfig.NETHOST)
				.append(AndroidConfig.MemberInfo).append("benbenid/")
				.append(user.getBenbenId()).append("/phone/")
				.append(user.getPhone()).append("/name/")
				.append(user.getName()).append("/nick_name/")
				.append(user.getUserNickname());

		Core.createImage(sBuffer.toString(),
				mScreenWidth - PixelUtil.dp2px(35),
				mScreenWidth - PixelUtil.dp2px(35), qr_image);
	}

	public static void createImage(String text, int QR_WIDTH, int QR_HEIGHT,
			ImageView qr_image) {
		try {
			if (text == null || "".equals(text) || text.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} 

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			qr_image.setImageBitmap(bitmap);

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	public static void createImageAndColor(String text, int QR_WIDTH,
			int QR_HEIGHT, ImageView qr_image, int color) {
		try {
			if (text == null || "".equals(text) || text.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = color;
					} else {
						pixels[y * QR_WIDTH + x] = 0xFFFFFFFF;
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			qr_image.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
}
