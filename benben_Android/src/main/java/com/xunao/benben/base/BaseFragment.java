package com.xunao.benben.base;

import in.srain.cube.image.ImageLoaderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.activity.MainActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.utils.ToastUtils;

public abstract class BaseFragment extends Fragment {

	protected CrashApplication crashApplication;
	protected MainActivity mActivity;
	protected LodingDialog lodingDialog;
	protected boolean isShowLoding = true;
	protected long lastThreadTimeMillis;
	protected boolean isLoadMore;
	protected DbUtils dbUtil;
	protected in.srain.cube.image.ImageLoader cubeimageLoader;

	// 页面重现时刷新
	public abstract void onRefresh();

	public boolean isLoadMore() {
		return isLoadMore;
	}

	public void setLoadMore(boolean isLoadMore) {
		this.isLoadMore = isLoadMore;
	}

	public boolean isShowLoding() {
		return isShowLoding;
	}

	public void setShowLoding(boolean isShowLoding) {
		this.isShowLoding = isShowLoding;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = (MainActivity) getActivity();
		crashApplication = CrashApplication.getInstance();
		dbUtil = crashApplication.getDb();
		lodingDialog = mActivity.getLodingDialog();

		cubeimageLoader = mActivity.cubeimageLoader;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initLinstener();
		initDate();
	}

	public RequestCallBack<String> mRequestCallBack = new RequestCallBack<String>() {

		public void onStart() {
			if (isShowLoding) {
				// lodingDialog.show();
				if (lodingDialog == null) {
					lodingDialog = new LodingDialog(mActivity);
				}
				lodingDialog.show();
			}
			BaseFragment.this.onHttpStart();

		};

		@Override
		public void onSuccess(ResponseInfo<String> mResponseInfo) {

			if (isShowLoding) {
				isShowLoding = false;
			}

			mActivity.dissLoding();

			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(mResponseInfo.result);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (jsonObject != null) {
				BaseFragment.this.onSuccess(jsonObject);
			} else {

				ToastUtils.Errortoast(mActivity, "抱歉!服务器数据有误");
			}
		}

		public void onLoading(long total, long current, boolean isUploading) {

			BaseFragment.this.onLoading(total, current, isUploading);

		};

		@Override
		public void onFailure(HttpException exception, String strMsg) {

			if (isShowLoding) {
				isShowLoding = false;
			}

			lodingDialog.dismiss();
			// mActivity.dissmissLoding();
			// if (lodingDialog != null && lodingDialog.isShowing()) {
			// lodingDialog.dismiss();
			// }
			ToastUtils.Errortoast(mActivity, "网络不给力!!!");
			BaseFragment.this.onFailure(exception, strMsg);
		}
	};

	/**
	 * 初始化视图
	 */
	protected abstract void initView();

	/**
	 * 设置事件监听
	 */
	protected abstract void initLinstener();

	/**
	 * 加载数据
	 */
	protected abstract void initDate();

	/**
	 * http连接开始
	 */
	protected abstract void onHttpStart();

	/**
	 * http连接中
	 */
	protected abstract void onLoading(long count, long current,
			boolean isUploading);

	/**
	 * http连接成功
	 */
	protected abstract void onSuccess(JSONObject t);

	/**
	 * http连接失败
	 */
	protected abstract void onFailure(HttpException exception, String strMsg);

	// /**
	// * 处理提醒事项
	// *
	// * @param all
	// */
	// public abstract void setUpdateInfo(All all);

}
