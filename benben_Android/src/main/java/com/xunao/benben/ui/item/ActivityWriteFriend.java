package com.xunao.benben.ui.item;

import java.io.IOException;
import java.lang.ref.SoftReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.FileUtils;
import com.xunao.benben.utils.ImageItem;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.Res;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;
import com.xunao.benben.view.ContainsEmojiEditText;

public class ActivityWriteFriend extends FragmentActivity {

	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private View back;
	private View send;
	private LinearLayout ll_popup;
	public static Bitmap bimap;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Res.init(this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		CrashApplication.getInstance().addActivity(this);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
		PublicWay.activityList.add(this);
		parentView = getLayoutInflater().inflate(R.layout.activity_selectimg,
				null);
		setContentView(parentView);
		Init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CrashApplication.getInstance().removeActivity(this);
	}

	public void Init() {

		content = (ContainsEmojiEditText) findViewById(R.id.content);
		send = findViewById(R.id.activity_selectimg_send);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
		back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					if (Bimp.tempSelectBitmap.size() < 6) {
						changeImage();
					}
				} else {
					Intent intent = new Intent(ActivityWriteFriend.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

	// 发布朋友圈
	protected void send() {

		String con = content.getText().toString();

		if (TextUtils.isEmpty(con.trim()) && Bimp.tempSelectBitmap.size() <= 0) {
			ToastUtils.Errortoast(this, "内容不可为空");
			return;
		}
        if (!CommonUtils.StringIsSurpass2(con.trim(), 0, 200)) {
            ToastUtils.Errortoast(this, "内容限制在200个字之间!");
            return;
        }
		int size = Bimp.tempSelectBitmap.size();
		String[] images = new String[size];
		for (int i = 0; i < size; i++) {
			images[i] = Bimp.tempSelectBitmap.get(i).getImagePath();
		}
		if (CommonUtils.isNetworkAvailable(ActivityWriteFriend.this)) {
			final LodingDialog dialog = new LodingDialog(this);
			dialog.setContent("发送中");
			dialog.show();
			InteNetUtils.getInstance(ActivityWriteFriend.this).publicFrient(
					"0", con, images, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
//							ToastUtils.Errortoast(getApplicationContext(),
//									"发送失败请重试!");
							dialog.dismiss();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								SuccessMsg msg = new SuccessMsg();
								try {
									msg.parseJSON(jsonObject);
									Bimp.tempSelectBitmap.clear();
									content.setText("");
									adapter.notifyDataSetChanged();
//									setResult(AndroidConfig.writeFriendRefreshResultCode);
//									ActivityWriteFriend.this.finish();
//									ActivityWriteFriend.this
//											.overridePendingTransition(
//													R.anim.in_from_left,
//													R.anim.out_to_right);
								} catch (NetRequestException e) {
									e.getError()
											.print(ActivityWriteFriend.this);
								}
							} catch (JSONException e) {
								ToastUtils.Errortoast(getApplicationContext(),
										"网络不可用,请重试");
							}

							dialog.dismiss();

						}
					});
            dialog.dismiss();
            ToastUtils.Errortoast(ActivityWriteFriend.this, "内容已发送");
            setResult(AndroidConfig.writeFriendRefreshResultCode);
            ActivityWriteFriend.this.finish();
            ActivityWriteFriend.this
                    .overridePendingTransition(
                            R.anim.in_from_left,
                            R.anim.out_to_right);
		}
	}

	// 显示拍照选照片 弹窗
	private void changeImage() {
		setTheme(R.style.ActionSheetStyleIOS7);
		showActionSheet();
	}

	public void showActionSheet() {
		ActionSheet
				.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("拍摄新图片", "从相册选择")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {
						switch (index) {
						case 0:
							photo();
							break;
						case 1:
							Intent intent = new Intent(
									ActivityWriteFriend.this, ImageFile.class);
							startActivity(intent);
							overridePendingTransition(
									R.anim.activity_translate_in,
									R.anim.activity_translate_out);
							break;
						default:
							break;
						}
					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;
		private int mWidth = (mScreenWidth - PixelUtil.dp2px(80)) / 4;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				View v = convertView.findViewById(R.id.box);
				holder.delete = (ImageView) convertView
						.findViewById(R.id.delete);
				convertView.setTag(holder);

				v.setLayoutParams(new RelativeLayout.LayoutParams(mWidth
						+ PixelUtil.dp2px(10), mWidth + PixelUtil.dp2px(10)));
				holder.image.getLayoutParams().width = mWidth;
				holder.image.getLayoutParams().height = mWidth;

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				holder.delete.setVisibility(View.GONE);
				if (position == 6) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
                String imagePath = Bimp.tempSelectBitmap.get(position).getImagePath();
                try {
                    SoftReference<Bitmap> bitmap = new SoftReference<Bitmap>(
                            Bimp.revitionImageSize(imagePath));
                    holder.image.setImageBitmap(CommonUtils.readPictureDegree(
                            imagePath,bitmap.get()));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }



				holder.delete.setVisibility(View.VISIBLE);
				holder.delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bimp.tempSelectBitmap.remove(position);
						adapter.notifyDataSetChanged();
					}
				});
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
			public ImageView delete;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			adapter.notifyDataSetChanged();
			// new Thread(new Runnable() {
			// public void run() {
			// while (true) {
			// if (Bimp.max == Bimp.tempSelectBitmap.size()) {
			// Message message = new Message();
			// message.what = 1;
			// handler.sendMessage(message);
			// break;
			// } else {
			// Bimp.max += 1;
			// Message message = new Message();
			// message.what = 1;
			// handler.sendMessage(message);
			// }
			// }
			// }
			// }).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	private static final int TAKE_PICTURE = 0x000001;
	private ContainsEmojiEditText content;
	private int mScreenWidth;
	private int mScreenHeight;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
				String fileName = String.valueOf(System.currentTimeMillis());
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				String saveBitmap = FileUtils.saveBitmapPhoto(bm, fileName);

				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(new SoftReference<Bitmap>(bm));
				takePhoto.setImagePath(saveBitmap);
				Bimp.tempSelectBitmap.add(takePhoto);
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(AndroidConfig.writeFriendResultCode);
		Bimp.tempSelectBitmap.clear();
		this.finish();
		this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}
