package com.xunao.benben.ui.item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Type;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.FriendUnion;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;
import com.xunao.benben.view.MyEditView;

public class ActivityCreatedFriendUnion extends BaseActivity implements
		OnClickListener, ActionSheetListener {
	private RoundedImageView friend_union_poster;
	private MyEditView friend_union_name;
	private MyEditView friend_union_info;
	private MyTextView friend_union_area;
	private String imageName;
	private Intent intent;
	private String imagePath;
	private File file;
	private FriendUnion friendUnion;
	private FriendUnion friendUnion2;
	private String imageNames;
	private boolean isUpdate = false;
	private String[] addressId;

	private static final int PIC_REQUEST_CODE_WITH_DATA = 1; // 标识获取图片数据
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 2; // 标识请求照相功能的activity
	private static final int PIC_Select_CODE_ImageFromLoacal = 3;// 标识请求相册取图功能的activity
	protected static final int CHOICE_ADDRESS = 1000;

	private int friendUnionType = 0;
	private RelativeLayout rl_work_union;
	private RelativeLayout rl_lol_union;
	private ImageView iv_work;
	private ImageView iv_lol;
	private MsgDialog inputDialog;
	private MyTextView union_type_title;
	private RelativeLayout friend_union_rl;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_created_friend_union);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		if (friendUnion2 != null) {
			initTitle_Right_Left_bar("更新好友联盟", "", "完成",
					R.drawable.icon_com_title_left, 0);
		} else {
			initTitle_Right_Left_bar("创建好友联盟", "", "完成",
					R.drawable.icon_com_title_left, 0);
		}

		friend_union_poster = (RoundedImageView) findViewById(R.id.friend_union_poster);
		friend_union_name = (MyEditView) findViewById(R.id.friend_union_name);
		friend_union_info = (MyEditView) findViewById(R.id.friend_union_info);
		friend_union_area = (MyTextView) findViewById(R.id.friend_union_area);
		friend_union_rl = (RelativeLayout) findViewById(R.id.friend_union_rl);

		rl_work_union = (RelativeLayout) findViewById(R.id.rl_work_union);
		rl_lol_union = (RelativeLayout) findViewById(R.id.rl_lol_union);
		iv_work = (ImageView) findViewById(R.id.iv_work);
		iv_lol = (ImageView) findViewById(R.id.iv_lol);
		union_type_title = (MyTextView) findViewById(R.id.union_type_title);

		friend_union_area.setFocusable(true);

		friend_union_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult2(ActivityChoiceAddress.class,
						CHOICE_ADDRESS, "level", "3");
			}
		});
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		friendUnion2 = (FriendUnion) intent.getSerializableExtra("friendUnion");

		if (friendUnion2 != null) {
			initTitle_Right_Left_bar("更新好友联盟", "", "完成",
					R.drawable.icon_com_title_left, 0);
			isUpdate = true;
			friend_union_name.setText(friendUnion2.getName());
			friend_union_info.setText(friendUnion2.getInfo());
			friend_union_area.setText(friendUnion2.getFullArea());
			rl_work_union.setVisibility(View.GONE);
			rl_lol_union.setVisibility(View.GONE);
			union_type_title.setText("");

			CommonUtils.startImageLoader(cubeimageLoader,
					friendUnion2.getPoster(), friend_union_poster);

		}
		
		if (friendUnion2 != null) {
			initTitle_Right_Left_bar(friendUnion2.getName(), "", "完成",
					R.drawable.icon_com_title_left, 0);
		} else {
			initTitle_Right_Left_bar("创建好友联盟", "", "完成",
					R.drawable.icon_com_title_left, 0);
		}

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String name = friend_union_name.getText().toString().trim();
				String info = friend_union_info.getText().toString().trim();

				if (TextUtils.isEmpty(name)) {
					ToastUtils.Infotoast(mContext, "请填写好友联盟名称!");
					return;
				}
				
				if (!CommonUtils.StringIsSurpass2(name, 4, 10)) {
					ToastUtils.Errortoast(mContext, "联盟名称限制在2—10字");
					return;
				}
				
				
				if (TextUtils.isEmpty(info)) {
					ToastUtils.Infotoast(mContext, "请填写好友联盟简介");
					return;
				}
                if (!CommonUtils.StringIsSurpass2(info, 1, 150)) {
                    ToastUtils.Errortoast(mContext, "简介限制在150个字之间!");
                    return;
                }

				if (!isUpdate) {
					if (file == null) {
						ToastUtils.Infotoast(mContext, "请上传头像!");
						return;
					}
				}

				friendUnion = new FriendUnion();
				friendUnion.setName(name);
				friendUnion.setInfo(info);

				if (isUpdate) {
					friendUnion.setNumber(friendUnion2.getNumber());
				} else {
					friendUnion.setNumber(1);
				}

				friendUnion.setBitMap(true);
				friendUnion.setCreatedId(user.getId() + "");

				if (isUpdate) {
					InteNetUtils.getInstance(mContext).updateFriendUnion(
							friendUnion2.getId(), name, info, addressId, "",
							file, mRequestCallBack);
				} else {
					
					if (addressId == null) {
						ToastUtils.Infotoast(mContext, "请选择所在地区");
						return;
					}
					
					if(friendUnionType == 0){
						ToastUtils.Infotoast(mContext, "请选择联盟类型");
						return;
					}
					
					InteNetUtils.getInstance(mContext).createdFriendUnion(name,
							info, addressId, file, friendUnionType + "",
							mRequestCallBack);
				}
			}
		});

		friend_union_poster.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showActionSheet();
			}
		});

		rl_work_union.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("工作联盟", "创建联盟后不能更改", "确认", "取消");
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						iv_work.setImageResource(R.drawable.icon_checkbox_select);
						iv_lol.setImageResource(R.drawable.icon_checkbox_noselect);
						friendUnionType = 1;
						inputDialog.dismiss();
					}
				});
				inputDialog.show();
			}
		});

		rl_lol_union.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("英雄联盟", "创建联盟后不能更改", "确认", "取消");
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						iv_work.setImageResource(R.drawable.icon_checkbox_noselect);
						iv_lol.setImageResource(R.drawable.icon_checkbox_select);
						friendUnionType = 2;
						inputDialog.dismiss();
					}
				});
				inputDialog.show();
			}
		});

	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String ret_num = jsonObject.optString("ret_num");

		if (ret_num.equals("0")) {
			if (isUpdate) {
				ToastUtils.Infotoast(mContext, "修改好友联盟成功!");
				sendBroadcast(new Intent(AndroidConfig.refreshFriendUnion));
				FriendUnion friendUnion = new FriendUnion();
				try {
					Intent intent = new Intent();
					
					JSONObject object = jsonObject.optJSONObject("enterprise_info");
					intent.putExtra("friendUnion", friendUnion.parseJSON(object));
					setResult(100001, intent);
					AnimFinsh();
				} catch (NetRequestException e) {
					e.printStackTrace();
				}
				
			} else {
				ToastUtils.Infotoast(mContext, "创建好友联盟成功!");
				user.setSysLeague(2);
			}
			user.setLeague(1);
			try {
				dbUtil.saveOrUpdate(user);
				sendBroadcast(new Intent(AndroidConfig.refreshFUBroadCasting));
			} catch (DbException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent();
			intent.putExtra("friendUnion", friendUnion);
			intent.putExtra("imageName", imageNames);
			intent.setData(Uri.parse("file://" + imagePath));
			setResult(1, intent);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	@Override
	public void onClick(View arg0) {

	}

	private void showActionSheet() {
		setTheme(R.style.ActionSheetStyleIOS7);
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("拍摄新图片", "从相册选择")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index) {
		case 0:
			imageName = getPhotoFileName();
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(CommonUtils.getImagePath(mContext, imageName)));
			startActivityForResult(intent, PIC_REQUEST_CODE_SELECT_CAMERA);
			break;
		case 1:
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PIC_REQUEST_CODE_WITH_DATA:
			if (data != null) {
				Uri uri = data.getData();
				imageNames = data.getStringExtra("imageName");
				file = CommonUtils.getImagePath(mContext, imageNames);

				BitmapFactory.Options options = new Options();
				options.inJustDecodeBounds = false;
				Bitmap bitmap = CutImageUtils.getBitMap(mContext, uri, options,
						friend_union_poster);
				friend_union_poster.setImageBitmap(bitmap);

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
		case CHOICE_ADDRESS:
			if (data != null) {
				addressId = null;
				friend_union_area.setText(data.getStringExtra("address"));
				addressId = data.getStringArrayExtra("addressId");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
