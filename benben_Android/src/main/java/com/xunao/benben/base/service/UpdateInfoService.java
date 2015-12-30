package com.xunao.benben.base.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.News;
import com.xunao.benben.bean.NewsList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.XunaoLog;

public class UpdateInfoService extends Service {

	boolean isRuning;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isRuning = true;
		new UpdateInfoThread().start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isRuning = false;
	}

	class UpdateInfoThread extends Thread {

		@Override
		public void run() {

			while (isRuning) {
				DbUtils db = CrashApplication.getInstance().getDb();
				long lastTime = CrashApplication.getInstance().getSpUtil()
						.getLastTime();
				InteNetUtils.getInstanceNo(getApplicationContext())
						.getUpdateInfo(lastTime + "", requestCallBack);
				SystemClock.sleep(1000 * 5 * 60);
			}

		}

		private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {

				try {
					JSONObject jsonObject = new JSONObject(arg0.result);
					NewsList alllist = new NewsList();

					try {
						alllist.parseJSON(jsonObject);

						if (alllist != null && alllist.getmNewsList() != null) {
							News news = alllist.getmNewsList().get(0);
							CrashApplication.getInstance().getSpUtil()
									.setLastTime(news.getCreated_time());
							DbUtils db = CrashApplication.getInstance().getDb();
							try {
								for (News n : alllist.getmNewsList()) {
									db.save(n);
								}
							} catch (DbException e) {
								e.printStackTrace();
							}
						}
						sendBroadcast(new Intent(AndroidConfig.refreshNews));

					} catch (NetRequestException e) {
						e.printStackTrace();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Intent intent = new Intent();
				intent.setAction("hasMessage");
				sendBroadcast(intent);

			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				XunaoLog.yLog().i("请求失败");
				// CrashApplication.getInstance().getSpUtil()
				// .setLastTime(System.currentTimeMillis() / 1000);
				// Intent intent = new Intent();
				// intent.setAction("hasMessage");
				// sendBroadcast(intent);

			}
		};
	}
}
