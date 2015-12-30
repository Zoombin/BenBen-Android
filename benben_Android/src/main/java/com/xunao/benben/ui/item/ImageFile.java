package com.xunao.benben.ui.item;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xunao.benben.R;
import com.xunao.benben.base.adapter.FolderAdapter;
import com.xunao.benben.utils.AlbumHelper;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.ImageBucket;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.Res;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:06
 */
public class ImageFile extends Activity {

	private FolderAdapter folderAdapter;
	private ImageView bt_cancel;
	private Context mContext;
	private AlbumHelper helper;
	private exitBroadCast cast;
	public static List<ImageBucket> contentList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Res.getLayoutID("plugin_camera_image_file"));
		PublicWay.activityList.add(this);
		mContext = this;
		bt_cancel = (ImageView) findViewById(Res.getWidgetID("back"));
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(Res
				.getWidgetID("fileGridView"));
		
		cast=new exitBroadCast();
		
		registerReceiver(cast, new IntentFilter("ImageFile"));
//		TextView textView = (TextView) findViewById(Res
//				.getWidgetID("headerTitle"));
//		textView.setText(Res.getString("photo"));

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		contentList = helper.getImagesBucketList(true);

		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			// // 清空选择的图片
			// Bimp.tempSelectBitmap.clear();
			onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(cast);
	}
	
	@Override
	public void onBackPressed() {
        Bimp.tempSelectBitmap.clear();
		this.finish();
		this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

	}
	
	class exitBroadCast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
		
	}

}
