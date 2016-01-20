package com.xunao.test.ui.item.TallGroup;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.TalkGroup;
import com.xunao.test.bean.TalkGroupList;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

public class ActivityFindTalkGroup extends BaseActivity implements
		OnClickListener {

	private ArrayList<TalkGroup> mTalkGroups = new ArrayList<TalkGroup>();

	private EditText search_edittext;
	private LinearLayout ll_seach_icon;
	private String searchKey = "";

	private int pagerNum = 0;
	private boolean isMoreData = true;
	private boolean enterNum = false;

	private ListView listView;
	private MyAdapter myAdapter;

	private View nodota;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_find_talkgroup);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				if (cubeImageView != null) {
					cubeImageView.setImageResource(R.drawable.ic_group_poster);
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
					imageView.setImageResource(R.drawable.ic_group_poster);
				}
			}
		});

	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("查找群", "", "取消",
				R.drawable.icon_com_title_left, 0);

		search_edittext = (EditText) findViewById(R.id.search_edittext);
		((TextView) findViewById(R.id.searchName)).setText("群组号/群名称");
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		nodota = findViewById(R.id.nodota);

		listView = (ListView) findViewById(R.id.listview);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		search_edittext.setFocusable(true);
		search_edittext.setFocusableInTouchMode(true);
		search_edittext.requestFocus();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager =

				(InputMethodManager) search_edittext.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(search_edittext, 0);
			}

		}, 200);
		// TODO Auto-generated method stub
		myAdapter = new MyAdapter();
		listView.setAdapter(myAdapter);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

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
				} else {
					ll_seach_icon.setVisibility(View.VISIBLE);
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
					if (!TextUtils.isEmpty(searchKey)) {
						isLoadMore = false;
						pagerNum = 0;
						enterNum = false;
						InteNetUtils.getInstance(mContext).findTalkGroup(
								searchKey, mRequestCallBack);
					} else {
						ToastUtils.Infotoast(mContext, "搜索关键字不可为空");
					}
					return true;
				}
				return false;
			}
		});

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
		try {

			TalkGroupList talkGroupList = new TalkGroupList();
			talkGroupList.parseJSON(jsonObject);
			ArrayList<TalkGroup> talkGroups = talkGroupList.getTalkGroups();

			if (talkGroups != null && talkGroups.size() > 0) {
				mTalkGroups = talkGroups;
				nodota.setVisibility(View.GONE);
				myAdapter.notifyDataSetChanged();
			} else {
				mTalkGroups.clear();
				nodota.setVisibility(View.VISIBLE);
			}

		} catch (NetRequestException e) {
			mTalkGroups.clear();
			e.getError().print(mContext);
			nodota.setVisibility(View.VISIBLE);
		}
		myAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		nodota.setVisibility(View.VISIBLE);
		mTalkGroups.clear();
		nodota.setVisibility(View.GONE);
		ToastUtils.Errortoast(mContext, "搜索失败");
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
			search_edittext.setText("");
			CommonUtils.hideSoftInputFromWindow(mContext);
			nodota.setVisibility(View.GONE);
			mTalkGroups.clear();
			myAdapter.notifyDataSetChanged();
			AnimFinsh();
			break;

		default:
			break;
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTalkGroups == null ? 0 : mTalkGroups.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {
			// TODO Auto-generated method stub

			ItemHolder itemHolder;
			final TalkGroup tG = mTalkGroups.get(position);

			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.item_activity_find_group, null);
				itemHolder = new ItemHolder();
				itemHolder.talk_group_poster = (CubeImageView) converView
						.findViewById(R.id.talk_group_poster);
				itemHolder.talk_group_name = (TextView) converView
						.findViewById(R.id.talk_group_name);
				itemHolder.talk_group_level = (TextView) converView
						.findViewById(R.id.talk_group_level);
				itemHolder.talk_group_address = (TextView) converView
						.findViewById(R.id.talk_group_address);
				converView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) converView.getTag();
			}

			CommonUtils.startImageLoader(cubeimageLoader, tG.getPoster(),
					itemHolder.talk_group_poster);

			itemHolder.talk_group_name.setText(tG.getName());
			itemHolder.talk_group_level.setText(tG.getNumber() + "人     "
					+ tG.getDescription());

			itemHolder.talk_group_address.setVisibility(View.VISIBLE);
			itemHolder.talk_group_address.setText(tG.getPro_city());

			converView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					startAnimActivityForResult(ActivityTalkGroupInfo.class,
							"FIND", tG.getHuanxin_groupid(),
							AndroidConfig.writeFriendResultCode);

				}
			});

			return converView;
		}

	}

	public void startAnimActivityForResult(Class<?> cla, String key,
			String baseBean, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, baseBean);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	class ItemHolder {
		CubeImageView talk_group_poster;
		TextView talk_group_name;
		TextView talk_group_level;
		TextView talk_group_address;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

	}

}
