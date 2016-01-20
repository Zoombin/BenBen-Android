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
package com.xunao.test.utils.click;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.bean.SmallMakeData;
import com.xunao.test.bean.SmallSelfMakeData;
import com.xunao.test.ui.item.ActivitySmallMake;
import com.xunao.test.utils.CommonUtils;

public class VoicelistSelfPlayClickListener implements View.OnClickListener {

	SmallSelfMakeData samllData;
	File voiceFile;
	ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	View error;
	View loding;
	Activity activity;
	private BaseAdapter adapter;

	public static boolean isPlaying = false;
	public static VoicelistSelfPlayClickListener currentPlayListener = null;
	private HttpUtils utils;

	/**
	 * 
	 * @param message
	 * @param v
	 * @param iv_read_status
	 * @param context
	 * @param activity
	 * @param user
	 * @param chatType
	 */
	public VoicelistSelfPlayClickListener(SmallSelfMakeData samllData, ImageView v,
			View error, View loding, Activity activity) {
		this.samllData = samllData;
		this.voiceFile = new File(CommonUtils.getImageCachePath(activity,
				"Voice"), CommonUtils.md5(samllData.getImages()));
		this.loding = loding;
		this.error = error;
		this.adapter = adapter;
		voiceIconView = v;
		this.activity = activity;
		utils = new HttpUtils();

	}

	public void stopPlayVoice() {
		voiceAnimation.stop();
		voiceAnimation.selectDrawable(0);
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		((ActivitySmallMake) activity).playMsgId = null;
		// adapter.notifyDataSetChanged();
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			samllData.setStatu(SmallMakeData.DOWNLOADBAD);
			return;
		}
		AudioManager audioManager = (AudioManager) activity
				.getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		if (EMChatManager.getInstance().getChatOptions().getUseSpeaker()) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(true);// 关闭扬声器
			// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
			audioManager.setMode(AudioManager.MODE_NORMAL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mediaPlayer.release();
							mediaPlayer = null;
							stopPlayVoice(); // stop animation
						}

					});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();

		} catch (Exception e) {
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		String st = activity.getResources().getString(
				R.string.Is_download_voice_click_later);
		if (isPlaying) {
			currentPlayListener.stopPlayVoice();
			if (currentPlayListener == VoicelistSelfPlayClickListener.this) {
				return;
			}
		}

		if (voiceFile.exists()) {
			samllData.setStatu(SmallMakeData.DOWNLOADED);
		}

		switch (samllData.getStatu()) {
		case SmallMakeData.UNDOWNLOAD:
			// 没有下载,联网下载
			HttpHandler handler = utils.download(samllData.getImages(),
					voiceFile.getAbsolutePath(), true, false,
					new RequestCallBack<File>() {
						@Override
						public void onStart() {
							super.onStart();
							error.setVisibility(View.GONE);
							loding.setVisibility(View.VISIBLE);
						}

						@Override
						public void onSuccess(ResponseInfo<File> arg0) {

							// 下载成功,当前播放还是自己就播放
							playVoice(voiceFile.getAbsolutePath());
							samllData.setStatu(SmallMakeData.DOWNLOADED);
							error.setVisibility(View.GONE);
							loding.setVisibility(View.GONE);
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							samllData.setStatu(SmallMakeData.DOWNLOADBAD);
							error.setVisibility(View.VISIBLE);
							loding.setVisibility(View.GONE);
						}
					});

			break;
		case SmallMakeData.DOWNLOADED:
			// 下载完成播放
			// 下载成功,当前播放还是自己就播放
			playVoice(voiceFile.getAbsolutePath());
			break;
		case SmallMakeData.DOWNLOADBAD:
			// 下载失败删除重新下载
			voiceFile.delete();
			samllData.setStatu(SmallMakeData.UNDOWNLOAD);
			onClick(v);
			break;

		}

	}
}