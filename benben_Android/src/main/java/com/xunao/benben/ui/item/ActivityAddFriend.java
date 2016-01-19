package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.BuyInfo;
import com.xunao.benben.bean.BuyInfolist;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.FriendUser;
import com.xunao.benben.bean.FriendUserlist;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.BuyDialog.onSuccessLinstener;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.MyTextView;

public class ActivityAddFriend extends BaseActivity implements OnClickListener,
		OnRefreshListener<ListView>, OnLastItemVisibleListener {

	private EditText search_edittext;
	private ImageView iv_search_content_delect;
	private LinearLayout ll_seach_icon;
	private RelativeLayout nodota;
	private PullToRefreshListView listview;
	private String searchKey;
	private ArrayList<FriendUser> mFriendUsers;
	private FriendAdapter friendAdapter;
    private InputDialog inputDialog;
    private ListView lv_recommend_friend;
    private TextView tv_recommend_num;
    private RecommendFriendAdapter recommendFriendAdapter;
    private int recommendNum=0;
    private List<Map<String,String>> recommendList = new ArrayList<>();
    private LinearLayout ll_recommend;
    private ImageView iv_arrow;
    private boolean isShow = false ;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_addfriend);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				Boolean ispost = (Boolean) cubeImageView
						.getTag(R.string.ispost);
				if (cubeImageView != null) {
					cubeImageView.setImageResource(R.drawable.default_face);
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
					Boolean ispost = (Boolean) imageView
							.getTag(R.string.ispost);
					imageView.setImageResource(R.drawable.default_face);
				}
			}
		});
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "手动添加", 0,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						startAnimActivity(ActivityAddFriendBySelf.class);
					}
				}, "", R.drawable.ic_back, new OnClickListener() {

					@Override
					public void onClick(View v) {
						mContext.AnimFinsh();
					}
				}, "添加好友", 0);
		chanageTitle(mode);

		search_edittext = (EditText) findViewById(R.id.search_edittext);
		((TextView) findViewById(R.id.searchName)).setText("奔犇号/昵称");

		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);

        ll_recommend = (LinearLayout) findViewById(R.id.ll_recommend);
        ll_recommend.setOnClickListener(this);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        lv_recommend_friend = (ListView) findViewById(R.id.lv_recommend_friend);
        tv_recommend_num = (TextView) findViewById(R.id.tv_recommend_num);
        recommendFriendAdapter = new RecommendFriendAdapter();
        lv_recommend_friend.setAdapter(recommendFriendAdapter);

		nodota = (RelativeLayout) findViewById(R.id.nodota);
		listview = (PullToRefreshListView) findViewById(R.id.listview);

		listview.setOnRefreshListener(this);
		listview.setOnLastItemVisibleListener(this);
		iv_search_content_delect.setOnClickListener(this);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        if (CommonUtils.isNetworkAvailable(mContext)) {
            mContext.showLoding("");
            InteNetUtils.getInstance(mContext).recommendFriends( new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                            mContext.dissLoding();
                            try {
                                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                if (jsonObject.optInt("ret_num") == 0) {
                                    recommendList.clear();
                                    recommendNum = jsonObject.optInt("data_count");
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for(int i=0;i<jsonArray.length();i++) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("member_id", jsonArray.getJSONObject(i).optString("member_id"));
                                        map.put("poster", jsonArray.getJSONObject(i).optString("poster"));
                                        map.put("nick_name", jsonArray.getJSONObject(i).optString("nick_name"));
                                        map.put("huanxin_username", jsonArray.getJSONObject(i).optString("huanxin_username"));
                                        map.put("is_friend", "0");
                                        recommendList.add(map);
                                    }
                                }
                                tv_recommend_num.setText("好友推荐 （"+recommendNum+"人)");
                                recommendFriendAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                        @Override
                        public void onFailure(HttpException e, String s) {
                            mContext.dissLoding();
                            ToastUtils.Errortoast(mContext,"获取推荐好友失败");
                        }
                    });
        }else{
            ToastUtils.Errortoast(this, "当前网络不可用");
        }

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		search_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getApplicationContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					// 显示键盘
					imm.showSoftInput(search_edittext, 0);
				}
			}
		});

		search_edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (arg0.length() > 0) {
					ll_seach_icon.setVisibility(View.GONE);
					iv_search_content_delect.setVisibility(View.VISIBLE);
					searchKey = search_edittext.getText().toString().trim();
				} else {
					ll_seach_icon.setVisibility(View.VISIBLE);
					iv_search_content_delect.setVisibility(View.GONE);
					searchKey = "";
				}
			}
		});

		search_edittext.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 先隐藏键盘
					((InputMethodManager) search_edittext.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(mContext.getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					// 更新关键字
					searchKey = search_edittext.getText().toString().trim();
					isLoadMore = false;
					enterNum = false;
					if (CommonUtils.isNetworkAvailable(mContext))
						InteNetUtils.getInstance(mContext).buySearchFriend("",
								searchKey, mRequestCallBack);
					return true;
				}
				return false;
			}
		});
	}

    @Override
    protected void onResume() {
        super.onResume();
        if (search_edittext != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    showKeyBoard(search_edittext);
                }
            }, 200);
        }

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
		listview.onRefreshComplete();
		FriendUserlist mFriendUserlist = new FriendUserlist();
		try {
			mFriendUserlist.parseJSON(jsonObject);
			ArrayList<FriendUser> mFriendUsers = mFriendUserlist
					.getmFriendUser();
			addData(mFriendUsers);
		} catch (NetRequestException e) {
			e.getError().print(mContext);
			e.printStackTrace();
		}
	}

	private void addData(ArrayList<FriendUser> mFriendUsers) {
        isShow = false;
        iv_arrow.setImageResource(R.drawable.ico_arrow_down_blue);
        lv_recommend_friend.setVisibility(View.GONE);
		if (!isLoadMore) {
			if (mFriendUsers != null && mFriendUsers.size() > 0) {
				this.mFriendUsers = mFriendUsers;
				if (mFriendUsers.size() < AndroidConfig.DataNUM) {
					isMoreData = false;
					enterNum = false;
				} else {
					isMoreData = true;
					enterNum = true;
				}
				if (friendAdapter == null) {
					friendAdapter = new FriendAdapter();
					listview.setAdapter(friendAdapter);
				} else {
					friendAdapter.notifyDataSetChanged();
				}
				nodota.setVisibility(View.GONE);
			} else {

				if (this.mFriendUsers != null)
					this.mFriendUsers.clear();
				this.mFriendUsers = null;
				friendAdapter = null;
				listview.setAdapter(null);
				nodota.setVisibility(View.VISIBLE);
			}

		} else {
			if (mFriendUsers.size() < AndroidConfig.DataNUM) {
				isMoreData = false;
			} else {
				isMoreData = true;
			}
			enterNum = true;
			this.mFriendUsers.addAll(mFriendUsers);
			friendAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用,请重试");
	}

	@Override
	public void onLastItemVisible() {
		isLoadMore = true;
		if (CommonUtils.isNetworkAvailable(mContext))
			InteNetUtils.getInstance(mContext).buySearchFriend(
					mFriendUsers.get(mFriendUsers.size() - 1).getCreated_time()
							+ "", searchKey, mRequestCallBack);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		isLoadMore = false;
		if (CommonUtils.isNetworkAvailable(mContext))
			InteNetUtils.getInstance(mContext).buySearchFriend("", searchKey,
					mRequestCallBack);
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch (id) {
            case R.id.iv_search_content_delect:// 删除搜索条件
                search_edittext.setText("");
                break;
            case R.id.ll_recommend:
                if(isShow){
                    isShow = false;
                    iv_arrow.setImageResource(R.drawable.ico_arrow_down_blue);
                    lv_recommend_friend.setVisibility(View.GONE);
                }else{
                    isShow = true;
                    iv_arrow.setImageResource(R.drawable.ico_arrow_up_blue);
                    lv_recommend_friend.setVisibility(View.VISIBLE);
                }
                break;
		}
	}


    class RecommendFriendAdapter extends BaseAdapter {

        public int getCount() {
            if (recommendList != null) {
                return recommendList.size();
            }
            return 0;
        }

        public Object getItem(int position) {
            if (recommendList != null) {
                return recommendList.get(position);
            }
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder localViewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(ActivityAddFriend.this);
            if (convertView == null) {
                localViewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_recommend_friend, null);
                localViewHolder.item_name = (MyTextView) convertView.findViewById(R.id.item_name);
                localViewHolder.addFriend = (Button) convertView.findViewById(R.id.addFriend);
                localViewHolder.item_iv = (RoundedImageView) convertView.findViewById(R.id.item_iv);
                convertView.setTag(localViewHolder);
            } else {
                localViewHolder = (ViewHolder) convertView.getTag();
            }
            final Map<String,String> map = recommendList.get(position);
            CommonUtils.startImageLoader(cubeimageLoader, map.get("poster"),
                    localViewHolder.item_iv);
            localViewHolder.item_name.setText(map.get("nick_name"));
            if(map.get("is_friend").equals("0")){
                localViewHolder.addFriend.setText("加为好友");
                localViewHolder.addFriend.setBackgroundResource(R.drawable.but_bg_findfriend);
                localViewHolder.addFriend.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            mContext.showLoding("");
                            InteNetUtils.getInstance(mContext).addRecommendFriend(map.get("member_id"), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    mContext.dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                        if (jsonObject.optInt("ret_num") == 0) {
                                            Contacts contacts = new Contacts();
                                            contacts.parseJSONSingle3(jsonObject);
                                            if(contacts.getName()==null || contacts.getName().equals("")){
                                                contacts.setName(contacts.getNick_name());
                                            }
                                            dbUtil.saveOrUpdate(contacts);
                                            ArrayList<PhoneInfo> phones = contacts
                                                    .getPhones();
                                            if (phones != null)
                                                dbUtil.saveOrUpdateAll(phones);

                                            map.put("is_friend","1");
                                            recommendFriendAdapter.notifyDataSetChanged();

                                            sendBroadcast(new Intent(
                                                    AndroidConfig.ContactsRefresh));
                                        }else{
                                            ToastUtils.Errortoast(mContext, "添加推荐好友失败");
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    } catch (NetRequestException e) {
                                        e.printStackTrace();
                                    }

                                }


                                @Override
                                public void onFailure(HttpException e, String s) {
                                    mContext.dissLoding();
                                    ToastUtils.Errortoast(mContext, "添加推荐好友失败");
                                }
                            });
                        }else{
                            ToastUtils.Errortoast(mContext, "当前网络不可用");
                        }
                    }
                });
            }else{
                localViewHolder.addFriend.setText("已添加");
                localViewHolder.addFriend.setBackgroundResource(R.drawable.but_bg_public_agree);
                localViewHolder.addFriend.setOnClickListener(null);
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityAddFriend.this, ActivityContactsInfo.class);
                    intent.putExtra("username", map.get("huanxin_username"));
                    startActivityForResult(intent, AndroidConfig.ContactsFragmentRequestCode);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private MyTextView item_name;
            private Button addFriend;
            private RoundedImageView item_iv;

        }
    }

	class FriendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFriendUsers.size() + 1;
		}

		@Override
		public FriendUser getItem(int arg0) {
			// TODO Auto-generated method stub
			return mFriendUsers.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {

			if (position >= mFriendUsers.size()) {
				return 1;
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			if (itemViewType == 0) {
				if (convertView == null) {
					convertView = View.inflate(mContext,
							R.layout.item_findfriend, null);
				}
				final FriendUser item = getItem(position);

				RoundedImageView item_iv = ViewHolderUtil.get(convertView,
						R.id.item_iv);
				MyTextView item_name = ViewHolderUtil.get(convertView,
						R.id.item_name);
				MyTextView item_content = ViewHolderUtil.get(convertView,
						R.id.item_friend_content);
				Button addFriend = ViewHolderUtil.get(convertView,
						R.id.addFriend);
				MyTextView item_friend_short_phone = ViewHolderUtil.get(
						convertView, R.id.item_friend_short_phone);

				CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
						item_iv);
				item_name.setText(item.getNickName());
				item_content.setText("奔犇号: " + item.getBenben_id());

				addFriend.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

                        Intent intent = new Intent(mContext, ActivityAddFriendDetail.class);
                        mContext.overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_to_left);
                        intent.putExtra("nick_name", item.getNickName());
                        intent.putExtra("from_huanxin", user.getHuanxin_username());
                        intent.putExtra("to_huanxin", item.getHuanxin_username());
                        startActivityForResult(intent, AndroidConfig.writeFriendRequestCode);

						// 加好友
						// 参数为要添加的好友的username和添加理由

//                        inputDialog = new InputDialog(mContext,
//                                R.style.MyDialogStyle);
//                        inputDialog.setContent("添加好友", "请输入留言",
//                                "确认", "取消");
//                        inputDialog
//                                .setCancleListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        inputDialog.dismiss();
//                                    }
//                                });
//                        inputDialog
//                                .setOKListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        String message = inputDialog
//                                                .getInputText();
//                                        if(!CommonUtils.StringIsSurpass2(message, 0, 25)){
//                                            ToastUtils.Errortoast(mContext, "留言限制在25字之间");
//                                            return;
//                                        }
//
//                                        try {
//                                            EMContactManager.getInstance().addContact(
//                                                    item.getHuanxin_username(), message);
//                                            final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
//                                                    mContext, R.style.MyDialog1);
//                                            hint.show();
//                                            hint.setOKListener(new OnClickListener() {
//
//                                                @Override
//                                                public void onClick(View v) {
//                                                    hint.dismiss();
//                                                }
//                                            });
//                                        } catch (EaseMobException e) {
//                                            ToastUtils.Errortoast(mContext, "添加好友失败");
//                                            e.printStackTrace();
//                                        }
//
//                                        inputDialog.dismiss();
//                                    }
//                                });
//                        inputDialog.show();




					}
				});
                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ActivityAddFriend.this, ActivityContactsInfo.class);
                        intent.putExtra("username", item.getHuanxin_username());
                        startActivityForResult(intent, AndroidConfig.ContactsFragmentRequestCode);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });

			} else {

				if (isMoreData) {
					convertView = getLayoutInflater().inflate(
							R.layout.item_load_more, null);
					LinearLayout load_more = ViewHolderUtil.get(convertView,
							R.id.load_more);
					if (enterNum) {
						load_more.setVisibility(View.VISIBLE);
					} else {
						load_more.setVisibility(View.GONE);
					}
				} else {
					convertView = getLayoutInflater().inflate(
							R.layout.item_no_load_more, null);
				}

			}
			return convertView;
		}
	}
}
