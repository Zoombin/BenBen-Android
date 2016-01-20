package com.xunao.test.ui.item;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.util.VoiceRecorder;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.base.IA.CrashApplication;
import com.xunao.test.bean.FriendUnion;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.bean.User;
import com.xunao.test.dialog.LodingDialog;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.Bimp;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.FileUtils;
import com.xunao.test.utils.ImageItem;
import com.xunao.test.utils.PixelUtil;
import com.xunao.test.utils.PublicWay;
import com.xunao.test.utils.Res;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.utils.click.XunAoVoicePlayClickListener;
import com.xunao.test.view.ActionSheet;
import com.xunao.test.view.ActionSheet.ActionSheetListener;
import com.xunao.test.view.ContainsEmojiEditText;
import com.xunao.test.view.MyTextView;

public class ActivityWriteSmallMake extends FragmentActivity implements
		OnClickListener {

	private static final int RECORD = 1;
	private static final int TEXTIMG = 0;
	private int statueType;

	private RelativeLayout curTouchTab;
	private RelativeLayout prerecord_tab_one;
	private RelativeLayout prerecord_tab_three;
	private RelativeLayout public_choice_address;
	private MyTextView tv_choice_address;
	private String[] addressId;
	private View[] but;
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private View back;
	private View send;
	private LinearLayout ll_popup;
	public static Bitmap bimap;
	private RelativeLayout public_smallbrocast;
	private ImageView item_select;
	private static final int TRUMPET_RESULT = 1;
	private MyTextView tv_send;
	private String friendUnion;
	private String areas;
    private TextView tv_record_time;

	// 录音

	private TextView recordingHint;
	private ImageView micImage;
	private Drawable[] micImages;
	View recordingContainer;
	private VoiceRecorder voiceRecorder;
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};

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
		parentView = getLayoutInflater().inflate(
				R.layout.activity_publicsmall_make, null);

		setContentView(parentView);
		initrecord();
		Init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CrashApplication.getInstance().removeActivity(this);
	}

	private void initrecord() {
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "xunao");
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingContainer = findViewById(R.id.recording_container);
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] {
				getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

	}

	public void Init() {

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

		but = new View[2];
		prerecord_tab_one = (RelativeLayout) findViewById(R.id.prerecord_tab_one);
		prerecord_tab_three = (RelativeLayout) findViewById(R.id.prerecord_tab_three);
		but[0] = prerecord_tab_one;
		but[1] = prerecord_tab_three;
		prerecord_tab_one.setOnClickListener(this);
		prerecord_tab_three.setOnClickListener(this);
		initBox();
		content = (ContainsEmojiEditText) findViewById(R.id.content);
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
					Intent intent = new Intent(ActivityWriteSmallMake.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}

		});
		prerecord_tab_one.performClick();
		tv_send = (MyTextView) findViewById(R.id.tv_send);
		public_smallbrocast = (RelativeLayout) findViewById(R.id.public_smallbrocast);

		DbUtils dbUtil = (DbUtils) CrashApplication.getInstance().getDb();
		try {
			User user = dbUtil.findFirst(User.class);
			if (user.getLeague() == 1) {
				public_smallbrocast.setVisibility(View.VISIBLE);
			} else {
				public_smallbrocast.setVisibility(View.GONE);
			}

			public_smallbrocast.setVisibility(View.GONE);

		} catch (DbException e) {
			e.printStackTrace();
		}
		public_smallbrocast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(getApplicationContext(),
						ActivityChoiceFriendUnion.class), TRUMPET_RESULTS);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		item_select = (ImageView) findViewById(R.id.item_select);
		item_select.setVisibility(View.GONE);

		item_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (friendUnions != null && friendUnions.size() > 0) {
					friendUnions.removeAll(friendUnions);
					tv_send.setText("同步发送小喇叭");
					item_select.setVisibility(View.GONE);
				}
			}
		});

		public_choice_address = (RelativeLayout) findViewById(R.id.public_choice_address);
		tv_choice_address = (MyTextView) findViewById(R.id.tv_choice_address);

		public_choice_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						ActivityChoiceAddress.class);
				intent.putExtra("level", "3");
				startActivityForResult(intent, CHOICE_ADDRESS);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

	}

	private void initBox() {

		vedio_box = findViewById(R.id.vedio_box);
		vedio = findViewById(R.id.vedio);
		delete = findViewById(R.id.delete);
		record_vedio = findViewById(R.id.record_vedio);
		record_vedio_text = (MyTextView) findViewById(R.id.record_vedio_text);
        tv_record_time = (TextView)findViewById(R.id.tv_record_time);
		record_vedio.setOnTouchListener(new PressToSpeakListen());

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vedio.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null) {
					voiceRecorder.discardRecording();
					voiceRecorder = null;
				}
				record_vedio_text.setText("按住按钮添加录音");

			}
		});

	}

	// 发布微创作
	protected void send() {

		final LodingDialog dialog = new LodingDialog(this);
		dialog.setContent("发送中");

		RequestCallBack callBack = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
//				ToastUtils.Errortoast(getApplicationContext(), "发送失败请重试!");
				dialog.dismiss();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dialog.dismiss();
				SuccessMsg msg = new SuccessMsg();

				try {
					JSONObject jsonObject = new JSONObject(arg0.result);
					msg.parseJSON(jsonObject);
					Bimp.tempSelectBitmap.clear();
					CrashApplication.getInstance().isAttenRefresh = true;
//					ActivityWriteSmallMake.this.finish();
//					ActivityWriteSmallMake.this.overridePendingTransition(
//							R.anim.in_from_left, R.anim.out_to_right);
				} catch (JSONException e) {
//					ToastUtils.Errortoast(getApplicationContext(), "发送失败请重试!");
					e.printStackTrace();
				} catch (NetRequestException e) {
					e.getError().print(ActivityWriteSmallMake.this);
					e.printStackTrace();
				}

			}
		};

		String con = content.getText().toString();

		switch (statueType) {
		case RECORD:// 发送录音
			if (TextUtils.isEmpty(con) && !isrecordOk) {
				ToastUtils.Errortoast(this, "内容不可为空");
				return;
			}
            if (!CommonUtils.StringIsSurpass2(con, 0, 200)) {
                ToastUtils.Errortoast(this, "内容限制在200个字之间!");
                return;
            }
			if (voiceRecorder != null) {
				if (CommonUtils.isNetworkAvailable(ActivityWriteSmallMake.this)) {
					dialog.show();
					InteNetUtils.getInstance(ActivityWriteSmallMake.this)
							.publicSmallMake(statueType, con, friendUnion,
									null, voiceRecorder.getVoiceFilePath(),
									addressId, callBack);

                    dialog.dismiss();
                    CrashApplication.getInstance().isAttenRefresh = true;
                    ActivityWriteSmallMake.this.finish();
                    ActivityWriteSmallMake.this.overridePendingTransition(
                            R.anim.in_from_left, R.anim.out_to_right);
                    ToastUtils.Errortoast(ActivityWriteSmallMake.this, "微创作已发送");
				}
			} else {
				ToastUtils.Errortoast(ActivityWriteSmallMake.this, "音频不可为空");
			}

			break;
		case TEXTIMG:// 发送图文
			if (TextUtils.isEmpty(con) && Bimp.tempSelectBitmap.size() <= 0) {
				ToastUtils.Errortoast(this, "内容不可为空");
				return;
			}
            if (!CommonUtils.StringIsSurpass2(con, 0, 200)) {
                ToastUtils.Errortoast(this, "内容限制在200个字之间!");
                return;
            }
			int size = Bimp.tempSelectBitmap.size();
			String[] images = new String[size];
			for (int i = 0; i < size; i++) {
				images[i] = Bimp.tempSelectBitmap.get(i).getImagePath();
			}
			if (CommonUtils.isNetworkAvailable(ActivityWriteSmallMake.this)) {
				dialog.show();
				InteNetUtils.getInstance(ActivityWriteSmallMake.this)
						.publicSmallMake(statueType, con, friendUnion, images,
								null, addressId, callBack);
                dialog.dismiss();
                CrashApplication.getInstance().isAttenRefresh = true;
                ActivityWriteSmallMake.this.finish();
                ActivityWriteSmallMake.this.overridePendingTransition(
                        R.anim.in_from_left, R.anim.out_to_right);
                ToastUtils.Errortoast(ActivityWriteSmallMake.this, "微创作已发送");
			}
			break;
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
									ActivityWriteSmallMake.this,
									ImageFile.class);
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
		private int mWidth = (mScreenWidth - PixelUtil.dp2px(100)) / 4;

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
			if (Bimp.tempSelectBitmap.size() == 6) {
				return 6;
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
				
//				decodeFile = PhotoUtils.rotaingImageView(
//						PhotoUtils.readPictureDegree(image.getAbsolutePath()),
//						image.getAbsolutePath());
				
				holder.delete.setVisibility(View.GONE);
				if (position == 6) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
						.getBitmap());
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
	private static final int TRUMPET_RESULTS = 0x000002;
	private static final int CHOICE_ADDRESS = 0x000003;
	private ContainsEmojiEditText content;
	private int mScreenWidth;
	private int mScreenHeight;
	// 录音界面
	private View vedio_box;
	// 录音文件
	private View vedio;
	// 删除录音文件
	private View delete;
	// 录音按钮
	private View record_vedio;
	private MyTextView record_vedio_text;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TRUMPET_RESULTS:
			if (data != null) {
				friendUnions = new ArrayList<>();
				friendUnions = (ArrayList<FriendUnion>) data
						.getSerializableExtra("friendUnions");

				String str = "";
				friendUnion = "";

				for (FriendUnion fUnion : friendUnions) {
					str += fUnion.getName() + " ";
					friendUnion += fUnion.getId() + ",";
				}
				friendUnion = friendUnion
						.substring(0, friendUnion.length() - 1);

				tv_send.setText("同步发送到：" + str);
				item_select.setVisibility(View.VISIBLE);
			}
			break;
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
		case CHOICE_ADDRESS:
			if (data != null) {
				addressId = null;
				addressId = data.getStringArrayExtra("addressId");
				tv_choice_address.setText(data.getStringExtra("address"));
			}
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
		Bimp.tempSelectBitmap.clear();
		this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.prerecord_tab_one:// 图文
			statueType = TEXTIMG;
			setChecked(prerecord_tab_one, false);
			setChecked(prerecord_tab_three, false);
			curTouchTab = prerecord_tab_one;
			setChecked(prerecord_tab_one, true);
			noScrollgridview.setVisibility(View.VISIBLE);
			vedio_box.setVisibility(View.GONE);
			// 删除选择图片
			Bimp.tempSelectBitmap.clear();
			adapter.notifyDataSetChanged();
			break;
		case R.id.prerecord_tab_three:// 音频

			statueType = RECORD;
			setChecked(prerecord_tab_one, false);
			setChecked(prerecord_tab_three, false);
			curTouchTab = prerecord_tab_three;
			setChecked(prerecord_tab_three, true);
			noScrollgridview.setVisibility(View.GONE);
			vedio_box.setVisibility(View.VISIBLE);
			isrecordOk = false;
			vedio.setVisibility(View.INVISIBLE);
			if (voiceRecorder != null) {
				voiceRecorder.discardRecording();
				voiceRecorder = null;
			}
			record_vedio_text.setText("按住按钮添加录音");
			break;
		}
	}

	private PowerManager.WakeLock wakeLock;
	private String TAG = "record";
	private boolean isrecordOk;
	private ArrayList<FriendUnion> friendUnions;

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(
							R.string.Send_voice_need_sdcard_support);
					ToastUtils.Infotoast(ActivityWriteSmallMake.this, st4);
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (XunAoVoicePlayClickListener.isPlaying)
                        XunAoVoicePlayClickListener.currentPlayListener
								.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);

					if (voiceRecorder == null) {
						voiceRecorder = new VoiceRecorder(micImageHandler);
					}

					voiceRecorder.startRecording(null, TAG,
							getApplicationContext());
                    timer.start();
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					ToastUtils.Infotoast(ActivityWriteSmallMake.this,
							"录音失败，请重试！");
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint
							.setText(getString(R.string.release_to_cancel));
					recordingHint
							.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(
							R.string.Recording_without_permission);
					String st2 = getResources().getString(
							R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(
							R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							if (length >= 60) {
								Toast.makeText(getApplicationContext(),
										"录音时间不能超过60秒", Toast.LENGTH_SHORT)
										.show();
							} else {
								vedio.setVisibility(View.VISIBLE);
								vedio.setOnClickListener(new XunAoVoicePlayClickListener(
										voiceRecorder.getVoiceFilePath(),
										ActivityWriteSmallMake.this));
								record_vedio_text.setText("重新录制");
								isrecordOk = true;
							}
							// sendVoice(voiceRecorder.getVoiceFilePath(),
							// voiceRecorder
							// .getVoiceFileName(TAG),
							// Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2,
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtils.Errortoast(ActivityWriteSmallMake.this, st3);
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	private void setChecked(RelativeLayout view, boolean isCheck) {
		RadioButton tab_RB = (RadioButton) view.findViewById(R.id.tab_RB);
		tab_RB.setChecked(isCheck);

	}

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            int last = 60-length;
            int last = (int) (millisUntilFinished/1000);
            tv_record_time.setText("剩余"+last+"s");
        }

        @Override
        public void onFinish() {
            record_vedio.setPressed(false);
            recordingContainer.setVisibility(View.INVISIBLE);
            if (wakeLock.isHeld())
                wakeLock.release();

            // stop recording and send voice file
            String st1 = getResources().getString(
                    R.string.Recording_without_permission);
            String st2 = getResources().getString(
                    R.string.The_recording_time_is_too_short);
            String st3 = getResources().getString(
                    R.string.send_failure_please);
            try {
                int length = voiceRecorder.stopRecoding();
                if (length > 0) {
//                    recordLength = length;
                    vedio.setVisibility(View.VISIBLE);
                    vedio.setOnClickListener(new XunAoVoicePlayClickListener(
                            voiceRecorder.getVoiceFilePath(),
                            ActivityWriteSmallMake.this));
                    record_vedio_text.setText("重新录制");
                    isrecordOk = true;

                } else if (length == EMError.INVALID_FILE) {
                    Toast.makeText(getApplicationContext(), st1,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), st2,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.Errortoast(ActivityWriteSmallMake.this, st3);
            }
        }
    };

}
