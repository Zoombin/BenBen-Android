package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Attachment;
import com.xunao.benben.bean.BroadCasting;
import com.xunao.benben.bean.BroadCastingList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.click.VoiceCastPlayClickListener;
import com.xunao.benben.utils.click.VoicelistSelfPlayClickListener;
import com.xunao.benben.view.NoScrollGridView;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

public class ActivitySmallPublics extends BaseActivity {
	private ListView listview;
	private myAdapter adapter;
	private ArrayList<BroadCasting> broadCastings = new ArrayList<>();
	private BroadCastingList broadCasting;
	private Button btn_new_broadCasting;
	private MsgDialog inputDialog;
	private myBroadCast myBroadCast;
    public String playMsgId;


    @Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_small_publics);
		myBroadCast = new myBroadCast();
		registerReceiver(myBroadCast, new IntentFilter(
				AndroidConfig.refrashBroadCasting));
        cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
                mContext) {
            @Override
            public void onLoading(ImageTask imageTask,
                                  CubeImageView cubeImageView) {

                Boolean ispost = (Boolean) cubeImageView
                        .getTag(R.string.ispost);
                if (cubeImageView != null) {
                    if (ispost != null && ispost) {
                        cubeImageView.setImageResource(R.drawable.default_face);
                    } else {
                        cubeImageView.setImageResource(R.drawable.loading);
                    }
                }

            }

            @Override
            public void onLoadFinish(ImageTask imageTask,
                                     CubeImageView cubeImageView, BitmapDrawable drawable) {
                if (cubeImageView != null
                        && imageTask.getIdentityUrl().equalsIgnoreCase(
                        (String) cubeImageView.getTag())) {

                    Boolean issuofang = (Boolean) cubeImageView
                            .getTag(R.string.issuofang);

                    String urlToFileFormat = CommonUtils
                            .UrlToFileFormat(imageTask.getOriginUrl());
                    if (issuofang != null && issuofang) {
                        // Bitmap bitmap = drawable.getBitmap();
                        // int width = bitmap.getWidth();
                        // int height = bitmap.getHeight();
                        // float scal = 1;
                        // if (width > height) {
                        // scal = (width * 1.0f / height * 1.0f);
                        //
                        // if (width > mScreenWidth - PixelUtil.dp2px(75)) {
                        // width = mScreenWidth - PixelUtil.dp2px(75);
                        // height = (int) (width / scal);
                        // } else if (height > maxheight) {
                        // height = maxheight;
                        // width = (int) (height * scal);
                        // } else {
                        // height = (int) (width / scal);
                        // }
                        //
                        // } else {
                        // scal = (height * 1.0f / width * 1.0f);
                        //
                        // if (width > mScreenWidth - PixelUtil.dp2px(75)) {
                        // width = mScreenWidth - PixelUtil.dp2px(75);
                        // height = (int) (width * scal);
                        // } else if (height > maxheight) {
                        // height = maxheight;
                        // width = (int) (height / scal);
                        // } else {
                        // width = (int) (height / scal);
                        // }
                        //
                        // }
                        // cubeImageView.getLayoutParams().width = width;
                        // cubeImageView.getLayoutParams().height = height;
                    }

                    cubeImageView.setVisibility(View.VISIBLE);
                    cubeImageView.setImageDrawable(drawable);

                }
            }

            @Override
            public void onLoadError(ImageTask imageTask,
                                    CubeImageView imageView, int errorCode) {
                if (imageView != null) {
                    Boolean ispost = (Boolean) imageView
                            .getTag(R.string.ispost);
                    if (ispost != null && ispost) {
                        imageView.setImageResource(R.drawable.default_face);
                    } else {
                        imageView.setImageResource(R.drawable.icon_message_error);
                    }
                }
            }
        });
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("小喇叭", "", "", R.drawable.icon_com_title_left,
				R.drawable.icon_delete_01);

		listview = (ListView) findViewById(R.id.listview);
		adapter = new myAdapter();
		listview.setAdapter(adapter);

		btn_new_broadCasting = (Button) findViewById(R.id.btn_new_broadCasting);

		btn_new_broadCasting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(broadCasting!=null) {
                    if (broadCasting.getAuthority() == 0) {
                        final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
                                ActivitySmallPublics.this, R.style.MyDialog1);
                        hint.setContent("本月小喇叭已经用完");
                        hint.setBtnContent("知道了");
                        hint.show();
                        hint.setOKListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                hint.dismiss();
                            }
                        });

                        hint.show();
                    } else {
                        startAnimActivity(ActivitySmallPublic.class);
                    }
                }else{
                    ToastUtils.Infotoast(mContext,"当前网络不可用");
                }
				
			}
		});
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).broadCastinglist(
					mRequestCallBack);
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
				inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("清除小喇叭", "是否清除所有小喇叭", "确认", "取消");
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						InteNetUtils.getInstance(mContext).deleteBroadCasting(
								"", "1", new RequestCallBack<String>() {
									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										String result = arg0.result;
										try {
											JSONObject jsonObject = new JSONObject(
													result);
											String ret_num = jsonObject
													.optString("ret_num");
											String ret_msg = jsonObject
													.optString("ret_msg");

											if ("0".equals(ret_num)) {
												ToastUtils.Infotoast(mContext,
														"小喇叭清除成功");
												new Handler().postDelayed(
														new Runnable() {

															@Override
															public void run() {
																broadCastings
																		.removeAll(broadCastings);
																adapter.notifyDataSetChanged();
															}
														}, 300);
												return;
											} else {
												ToastUtils.Infotoast(mContext,
														ret_msg);
												return;
											}
										} catch (JSONException e) {
											e.printStackTrace();
											ToastUtils.Infotoast(mContext,
													"小喇叭清除失败!");
											return;
										}
									}

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils.Errortoast(mContext,
												"网络不可用!");
									}
								});
						inputDialog.dismiss();
					}
				});
				inputDialog.show();
			}
		});

//		listview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				startAnimActivity5Obj(ActivityBroadCasting.class,
//						"broadCasting", broadCastings.get(arg2), "lastNum",
//						broadCasting.getAuthority());
//			}
//		});

//		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					final int position, long arg3) {
//
//				inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
//				inputDialog.setContent("删除小喇叭", "是否删除本条小喇叭", "确认", "取消");
//				inputDialog.setCancleListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						inputDialog.dismiss();
//					}
//				});
//				inputDialog.setOKListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						InteNetUtils.getInstance(mContext).deleteBroadCasting(
//								broadCastings.get(position).getId(), "0",
//								new RequestCallBack<String>() {
//									@Override
//									public void onSuccess(
//											ResponseInfo<String> arg0) {
//										String result = arg0.result;
//										try {
//											JSONObject jsonObject = new JSONObject(
//													result);
//											String ret_num = jsonObject
//													.optString("ret_num");
//											String ret_msg = jsonObject
//													.optString("ret_msg");
//
//											if ("0".equals(ret_num)) {
//												ToastUtils.Infotoast(mContext,
//														"小喇叭删除成功");
//												new Handler().postDelayed(
//														new Runnable() {
//
//															@Override
//															public void run() {
//																if (broadCastings
//																		.size() - 1 > position) {
//																	broadCastings
//																			.remove(position);
//																	adapter.notifyDataSetChanged();
//
//																}
//															}
//														}, 300);
//												return;
//											} else {
//												ToastUtils.Infotoast(mContext,
//														ret_msg);
//												return;
//											}
//										} catch (JSONException e) {
//											e.printStackTrace();
//											ToastUtils.Infotoast(mContext,
//													"小喇叭删除失败!");
//											return;
//										}
//									}
//
//									@Override
//									public void onFailure(HttpException arg0,
//											String arg1) {
//										ToastUtils.Errortoast(mContext,
//												"网络不可用!");
//									}
//								});
//						inputDialog.dismiss();
//					}
//				});
//				inputDialog.show();
//				return true;
//			}
//		});
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
        Log.d("ltf","jsonObject============"+jsonObject);
        try {
			broadCastings.clear();
			broadCasting = new BroadCastingList();
			broadCasting = broadCasting.parseJSON(jsonObject);
			broadCastings = broadCasting.getBroadCasting();

			btn_new_broadCasting.setText("新建小喇叭(" + broadCasting.getAuthority()
					+ ")");
		} catch (NetRequestException e) {
			e.printStackTrace();
		}
		if (broadCastings.size() > 0 && broadCastings != null) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用!");
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return broadCastings.size();
		}

		@Override
		public BroadCasting getItem(int arg0) {
			return broadCastings.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup arViewGroup) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_small_public_item, null);
			}

			TextView tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			TextView tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			TextView tv_senders = (TextView) convertView
					.findViewById(R.id.tv_senders);
            CubeImageView item_friend_singleImg = (CubeImageView) convertView
                    .findViewById(R.id.item_friend_singleImg);
            NoScrollGridView item_friend_gridView = (NoScrollGridView) convertView
                    .findViewById(R.id.item_friend_gridView);
            ImageView item_friend_voice = (ImageView) convertView
                    .findViewById(R.id.item_friend_voice);
            View item_friend_voice_box = convertView
                    .findViewById(R.id.item_friend_voice_box);
            View item_friend_voice_loding = convertView
                    .findViewById(R.id.item_friend_voice_loding);
            View item_friend_voice_error = convertView
                    .findViewById(R.id.item_friend_voice_error);

			tv_time.setText(broadCastings.get(position).getCreatedTime());
            String content = broadCastings.get(position).getContentdetail();
            if(content.contains("groupBuy/groupbuyDetail") || content.contains("promotion/promotiondetail")){
                content = content.substring(0,content.indexOf("http"));
            }

			tv_content.setText(content);
			tv_senders.setText(broadCastings.get(position)
					.getShortDescription());

            item_friend_singleImg.setVisibility(View.GONE);
            item_friend_gridView.setVisibility(View.GONE);
            item_friend_voice_box.setVisibility(View.GONE);
            final BroadCasting item = broadCastings.get(position);
            String images = item.getImages();
            if (item.getType() == 1) {// 图文
                if (!TextUtils.isEmpty(images)) {
                    String[] split = images.split("\\^");
//                    int length = split.length;
//                    if (length > 1) {
                        // 多图用GridView
                        item_friend_gridView
                                .setVisibility(View.VISIBLE);
                        MyGridViewAdapter gridAdapter = new MyGridViewAdapter(
                                split);
                        item_friend_gridView
                                .setAdapter(gridAdapter);
                        item_friend_gridView
                                .setOnItemClickListener(new OnItemClickListener() {

                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                                        startActivity2StringAndPosition(
                                                ActivityContentPicSet.class,
                                                "IMAGES", item.getImages(),
                                                arg2);
                                    }
                                });



                    item_friend_gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                            inputDialog.setContent("删除小喇叭", "是否删除本条小喇叭", "确认", "取消");
                            inputDialog.setCancleListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    inputDialog.dismiss();
                                }
                            });
                            inputDialog.setOKListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    InteNetUtils.getInstance(mContext).deleteBroadCasting(
                                            broadCastings.get(position).getId(), "0",
                                            new RequestCallBack<String>() {
                                                @Override
                                                public void onSuccess(
                                                        ResponseInfo<String> arg0) {
                                                    String result = arg0.result;
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(
                                                                result);
                                                        String ret_num = jsonObject
                                                                .optString("ret_num");
                                                        String ret_msg = jsonObject
                                                                .optString("ret_msg");

                                                        if ("0".equals(ret_num)) {
                                                            ToastUtils.Infotoast(mContext,
                                                                    "小喇叭删除成功");
                                                            new Handler().postDelayed(
                                                                    new Runnable() {

                                                                        @Override
                                                                        public void run() {
                                                                            if (broadCastings
                                                                                    .size() - 1 > position) {
                                                                                broadCastings
                                                                                        .remove(position);
                                                                                adapter.notifyDataSetChanged();

                                                                            }
                                                                        }
                                                                    }, 300);
                                                            return;
                                                        } else {
                                                            ToastUtils.Infotoast(mContext,
                                                                    ret_msg);
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        ToastUtils.Infotoast(mContext,
                                                                "小喇叭删除失败!");
                                                        return;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(HttpException arg0,
                                                                      String arg1) {
                                                    ToastUtils.Errortoast(mContext,
                                                            "网络不可用!");
                                                }
                                            });
                                    inputDialog.dismiss();
                                }
                            });
                            inputDialog.show();
                            return true;
                        }
                    });
//                    } else {
//                        // 单图
//                        item_friend_singleImg
//                                .setVisibility(View.VISIBLE);
//
//                        item_friend_singleImg
//                                .getLayoutParams().width = item
//                                .getSingImageW();
//                        item_friend_singleImg
//                                .getLayoutParams().height = item
//                                .getSingImageH();
//                        CommonUtils.startImageLoader(cubeimageLoader,
//                                split[0],
//                                item_friend_singleImg);
//                        item_friend_singleImg
//                                .setOnClickListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        startActivity2StringAndPosition(
//                                                ActivityContentPicSet.class,
//                                                "IMAGES", item.getImages(),
//                                                0);
//                                    }
//                                });
//
//                    }
                }
            } else {// 音频
                item_friend_voice_box
                        .setVisibility(View.VISIBLE);
                if (images != null) {
                    item_friend_voice_box
                            .setOnClickListener(new VoiceCastPlayClickListener(
                                    item,
                                    item_friend_voice,
                                    item_friend_voice_error,
                                    item_friend_voice_loding,
                                    mContext));
                }
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
                            ActivitySmallPublics.this, R.style.MyDialog1);
                    hint.setContent(broadCastings.get(position)
                            .getDescription());
                    hint.show();
                    hint.setOKListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            hint.dismiss();
                        }
                    });
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                    inputDialog.setContent("删除小喇叭", "是否删除本条小喇叭", "确认", "取消");
                    inputDialog.setCancleListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputDialog.dismiss();
                        }
                    });
                    inputDialog.setOKListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InteNetUtils.getInstance(mContext).deleteBroadCasting(
                                    broadCastings.get(position).getId(), "0",
                                    new RequestCallBack<String>() {
                                        @Override
                                        public void onSuccess(
                                                ResponseInfo<String> arg0) {
                                            String result = arg0.result;
                                            try {
                                                JSONObject jsonObject = new JSONObject(
                                                        result);
                                                String ret_num = jsonObject
                                                        .optString("ret_num");
                                                String ret_msg = jsonObject
                                                        .optString("ret_msg");

                                                if ("0".equals(ret_num)) {
                                                    ToastUtils.Infotoast(mContext,
                                                            "小喇叭删除成功");
                                                    new Handler().postDelayed(
                                                            new Runnable() {

                                                                @Override
                                                                public void run() {
                                                                    if (broadCastings
                                                                            .size() - 1 > position) {
                                                                        broadCastings
                                                                                .remove(position);
                                                                        adapter.notifyDataSetChanged();

                                                                    }
                                                                }
                                                            }, 300);
                                                    return;
                                                } else {
                                                    ToastUtils.Infotoast(mContext,
                                                            ret_msg);
                                                    return;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                ToastUtils.Infotoast(mContext,
                                                        "小喇叭删除失败!");
                                                return;
                                            }
                                        }

                                        @Override
                                        public void onFailure(HttpException arg0,
                                                              String arg1) {
                                            ToastUtils.Errortoast(mContext,
                                                    "网络不可用!");
                                        }
                                    });
                            inputDialog.dismiss();
                        }
                    });
                    inputDialog.show();
                    return false;
                }
            });

            return convertView;
		}

	}

	class myBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				InteNetUtils.getInstance(mContext).broadCastinglist(
						mRequestCallBack);
			}
		}

	}

    private class MyGridViewAdapter extends BaseAdapter {

        String[] images;

        public MyGridViewAdapter(String[] images) {
            super();
            this.images = images;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return images.length;
        }

        @Override
        public String getItem(int position) {
            // TODO Auto-generated method stub
            return images[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_friend_gridview, null);
            }

            convertView
                    .setLayoutParams(new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT, PixelUtil
                            .dp2px(60)));
            String poster = getItem(position);
            if (!TextUtils.isEmpty(poster)) {
                CommonUtils.startImageLoader(cubeimageLoader,
                        getItem(position), ((CubeImageView) convertView));
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
                        ((CubeImageView) convertView));
            }
            return convertView;
        }

    }

    public void startActivity2StringAndPosition(Class<?> cla, String key,
                                                String value, int position) {
        Intent intent = new Intent(this, cla);
        intent.putExtra(key, value);
        intent.putExtra("POSITION", position);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(VoiceCastPlayClickListener.currentPlayListener.isPlaying)
            VoiceCastPlayClickListener.currentPlayListener.stopPlayVoice();
    }
}
