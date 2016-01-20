package com.xunao.test.ui.item.TallGroup;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.TalkGroup;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.ui.item.ActivityChoiceAddress;
import com.xunao.test.ui.item.ActivityCutImage;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.CutImageUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.view.ActionSheet;
import com.xunao.test.view.ActionSheet.ActionSheetListener;

public class ActivityUpdateTallGroup extends BaseActivity implements
		OnClickListener, ActionSheetListener {
	// 记录了地区的id
	private String[] addressId;

	private RoundedImageView talkgroup_poster;
	private EditText talk_group_name;
	private RelativeLayout talk_group_address;
	private EditText talk_group_info;
	// private EditText talk_group_notice;

	private Bitmap decodeStream;
	private String imageName;
	private String imagePath = "";

	private static final int PIC_REQUEST_CODE_WITH_DATA = 1; // 标识获取图片数据
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 2; // 标识请求照相功能的activity
	private static final int PIC_Select_CODE_ImageFromLoacal = 3;// 标识请求相册取图功能的activity

	private TalkGroup mTalkGroup;

	private int maxuser;

	private TextView tv_choice_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_created_talkgroup);
		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				Boolean ispost = (Boolean) cubeImageView
						.getTag(R.string.ispost);
				if (cubeImageView != null) {
					cubeImageView
							.setImageResource(R.drawable.icon_default_grouppost);
				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null) {
					if (imageTask.getIdentityUrl().equalsIgnoreCase(
							(String) cubeImageView.getTag())) {
						cubeImageView.setVisibility(View.VISIBLE);
						cubeImageView.setImageDrawable(drawable);

					}

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					imageView
							.setImageResource(R.drawable.icon_default_grouppost);
				}
			}
		});

	}

	@Override
	public void initView(Bundle savedInstanceState) {

		talkgroup_poster = (RoundedImageView) findViewById(R.id.talkgroup_poster);
		talk_group_address = (RelativeLayout) findViewById(R.id.talk_group_address);
		tv_choice_address = (TextView) findViewById(R.id.tv_choice_address);

		talk_group_name = (EditText) findViewById(R.id.talk_group_name);
		talk_group_info = (EditText) findViewById(R.id.talk_group_info);
		// talk_group_notice = (EditText) findViewById(R.id.talk_group_notice);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		mTalkGroup = (TalkGroup) getIntent().getSerializableExtra("TalkGroup");
		maxuser = mTalkGroup.getMaxuser();
		initTitle_Right_Left_bar(mTalkGroup.getName(), "", "完成",
				R.drawable.icon_com_title_left, 0);

		setOnLeftClickLinester(ActivityUpdateTallGroup.this);
		setOnRightClickLinester(ActivityUpdateTallGroup.this);

		talkgroup_poster.setOnClickListener(ActivityUpdateTallGroup.this);
		talk_group_address.setOnClickListener(ActivityUpdateTallGroup.this);

		CommonUtils.startImageLoader(cubeimageLoader, mTalkGroup.getPoster(),
				talkgroup_poster);
		tv_choice_address.setText(mTalkGroup.getPro_city());
		talk_group_name.setText(mTalkGroup.getName());
		talk_group_info.setText(mTalkGroup.getDescription());
		// talk_group_notice.setText(mTalkGroup.getBulletin());
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
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
		dissLoding();
		try {

			String group_nick_name = mTalkGroup.getGroup_nick_name();
			mTalkGroup.checkJson(jsonObject);

			JSONObject optJSONObject = jsonObject.optJSONObject("group_info");

			mTalkGroup.parseJSON(optJSONObject);
			mTalkGroup.setMaxuser(maxuser);
			mTalkGroup.setMember_id(user.getId() + "");
			mTalkGroup.setIs_admin("1");
			mTalkGroup.setGroup_nick_name(group_nick_name);
			Intent intent = new Intent();
			intent.putExtra("TG", mTalkGroup);
			setResult(AndroidConfig.writeFriendRefreshResultCode, intent);
			AnimFinsh();
		} catch (NetRequestException e) {
			e.printStackTrace();
			e.getError().print(mContext);
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		dissLoding();
		ToastUtils.Errortoast(mContext, "网络不可用");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;

		// 右侧
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			File poster;
			if ("".equals(imagePath)) {
				poster = null;
			} else {
				poster = new File(imagePath);
			}
			String name = talk_group_name.getText().toString();
			String info = talk_group_info.getText().toString();

			if (!CommonUtils.StringIsSurpass2(name, 4, 10)) {
				ToastUtils.Errortoast(mContext, "群组名限制在2—10字");
				return;
			}

			// String notice = talk_group_notice.getText().toString();
			showLoding("请稍后...");
			InteNetUtils.getInstance(mContext).updateGroup(mTalkGroup.getId(),
					poster, name, addressId, info, "", mRequestCallBack);
			break;
		case R.id.talkgroup_poster:
			setTheme(R.style.ActionSheetStyleIOS7);
			showActionSheet();
			break;
		case R.id.talk_group_address:
			startAnimActivityForResult2(ActivityChoiceAddress.class, 111,
					"level", "3");
			break;
		default:
			break;
		}
	}

	public void showActionSheet() {
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("拍摄新图片", "从相册选择")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index) {
		case 0:
			imageName = getPhotoFileName();
			Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent2.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(CommonUtils.getImagePath(mContext, imageName)));
			startActivityForResult(intent2, PIC_REQUEST_CODE_SELECT_CAMERA);
			break;
		case 1:
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
			String addressname = data.getStringExtra("address");
			addressId = data.getStringArrayExtra("addressId");
			tv_choice_address.setText(addressname);
			tv_choice_address.setTextColor(Color.BLACK);
		}

		switch (requestCode) {
		case PIC_REQUEST_CODE_WITH_DATA:
			if (data != null) {
				Uri uri = data.getData();
				imagePath = data.getStringExtra("imagePath");
				BitmapFactory.Options options = new Options();
				options.inJustDecodeBounds = false;
				Bitmap bitmap = CutImageUtils.getBitMap(mContext, uri, options,
						talkgroup_poster);
				talkgroup_poster.setImageBitmap(bitmap);
			}
			break;
		case PIC_REQUEST_CODE_SELECT_CAMERA:
			File temp = CommonUtils.getImagePath(mContext, imageName);
			Intent intent = new Intent(this, ActivityCutImage.class);
			intent.putExtra("imagePath", temp.getAbsolutePath());
			startActivityForResult(intent, PIC_REQUEST_CODE_WITH_DATA);

			break;
		case PIC_Select_CODE_ImageFromLoacal:
			if (data != null) {
				if (data.getData() != null) {
					Uri uri = data.getData();
					boolean isSDCard = true;
					ContentResolver cr = getContentResolver();
					try {
						InputStream openInputStream = cr.openInputStream(uri);
						if (openInputStream != null) {
							imagePath = CutImageUtils.getImagePath(mContext,
									openInputStream);
						} else {
							ToastUtils.Infotoast(mContext, "请选择正确的图像资源");
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Intent intent2 = new Intent(this, ActivityCutImage.class);
					intent2.putExtra("imagePath", imagePath);
					startActivityForResult(intent2, PIC_REQUEST_CODE_WITH_DATA);
				}
			}
			break;
		default:
			break;
		}
	}

}
