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

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.VoiceMessageBody;
import com.xunao.test.R;

public class XunAoVoicePlayClickListener implements View.OnClickListener {

	VoiceMessageBody voiceBody;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	Activity activity;
	private ChatType chatType;

	private String path;

	public static boolean isPlaying = false;
	public static XunAoVoicePlayClickListener currentPlayListener = null;

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
	public XunAoVoicePlayClickListener(String path, Activity activity) {
		this.activity = activity;
		this.path = path;
	}

	public void stopPlayVoice() {
		// voiceAnimation.stop();
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
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
		// voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		// voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		String st = activity.getResources().getString(
				R.string.Is_download_voice_click_later);
		if (isPlaying) {
			currentPlayListener.stopPlayVoice();
			return;
		}

		// for sent msg, we will try to play the voice file directly
		playVoice(path);
	}
}