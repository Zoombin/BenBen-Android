package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.Attention;
import com.xunao.benben.bean.AttentionList;
import com.xunao.benben.bean.SmallMakeData;
import com.xunao.benben.bean.SmallMakeDataComment;
import com.xunao.benben.bean.SmallSelfMakeData;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.bean.User;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.MyTextView;

@SuppressLint("ViewHolder")
public class ActivityMyttention extends BaseActivity {

	private ListView listview;
	private View nodota;
	private boolean hasLocalData;
	private ArrayList<Attention> mAttentions;
	private MyAdapter adapter;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_myattention);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitleView();

		TitleMode mode = new TitleMode("#068cd9", "", 0, new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 我的关注
//				startAnimActivity(ActivityMyttention.class);
			}
		}, "", R.drawable.ic_back, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.AnimFinsh();
			}
		}, "我的关注", 0);
		chanageTitle(mode);

		listview = (ListView) findViewById(R.id.listview);
		nodota = findViewById(R.id.nodota);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		initLocalDate();
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getMyAttention(mRequestCallBack);
		} else {
			if (hasLocalData) {
				nodota.setVisibility(View.GONE);
			} else {
				nodota.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

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
		AttentionList attentionList = new AttentionList();
		try {
			attentionList.parseJSON(jsonObject);

			ArrayList<Attention> getmAttentions = attentionList
					.getmAttentions();

			addData(getmAttentions);
			saveData(getmAttentions);

		} catch (NetRequestException e) {
			if (hasLocalData) {
				nodota.setVisibility(View.GONE);
			} else {
				nodota.setVisibility(View.VISIBLE);
			}
			e.getError().print(mContext);

		}

	}

	private void addData(ArrayList<Attention> getmAttentions) {
		mAttentions = getmAttentions;
		if (getmAttentions != null) {
			nodota.setVisibility(View.GONE);
			if (adapter == null) {
				adapter = new MyAdapter();
				listview.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		} else {
			if (mAttentions != null) {
				mAttentions.clear();
			}
			listview.setAdapter(null);
			adapter = null;
			nodota.setVisibility(View.VISIBLE);

		}

	}

	private void saveData(ArrayList<Attention> getmAttentions) {

		try {
			dbUtil.deleteAll(Attention.class);
			if (getmAttentions != null)
				dbUtil.saveAll(getmAttentions);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initLocalDate() {

		try {
			List<Attention> findAll = dbUtil.findAll(Attention.class);
			if (findAll != null && findAll.size() > 0) {
				hasLocalData = true;
				addData((ArrayList<Attention>) findAll);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mAttentions.size();
		}

		@Override
		public Attention getItem(int position) {
			// TODO Auto-generated method stub
			return mAttentions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Attention item = getItem(position);
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.item_attention,
						null);
			}

			RoundedImageView item_poster = ViewHolderUtil.get(convertView,
					R.id.item_poster);
			MyTextView item_name = ViewHolderUtil.get(convertView,
					R.id.item_name);
			MyTextView cancleAttention = ViewHolderUtil.get(convertView,
					R.id.cancleAttention);

			if (!TextUtils.isEmpty(item.getPoster())) {
				item_poster.setTag(R.string.ispost, true);
				CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
						item_poster);
			} else {
				item_poster.setTag(R.string.ispost, true);
				CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
						item_poster);
			}

			item_name.setText(item.getNickName());

			cancleAttention.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 取消关注
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).cnacleAttention(
								item.getId(), new RequestCallBack<String>() {

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										try {
											JSONObject jsonObject = new JSONObject(
													arg0.result);
											SuccessMsg msg = new SuccessMsg();
											try {
												msg.parseJSON(jsonObject);
												if (position <= mAttentions
														.size() - 1) {
													mAttentions
															.remove(position);
													if (mAttentions.size() <= 0) {
														nodota.setVisibility(View.VISIBLE);
													}
													mApplication.isAttenRefresh = true;
													adapter.notifyDataSetChanged();
												}

											} catch (NetRequestException e) {
												e.getError().print(mContext);
											}
										} catch (JSONException e) {
											ToastUtils.Errortoast(mContext,
													"取消失败,请重试");
										}

									}

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils.Errortoast(mContext,
												"取消失败,请重试");
									}
								});
					}

				}
			});

			return convertView;
		}
	}

}
