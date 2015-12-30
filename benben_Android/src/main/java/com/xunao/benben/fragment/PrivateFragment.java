package com.xunao.benben.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.platform.comapi.map.m;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.sitech.oncon.barcode.core.CaptureActivity;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseFragment;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.NewMsgAboutMe;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.ui.item.ActivityFriend;
import com.xunao.benben.ui.item.ActivityHappy;
import com.xunao.benben.ui.item.ActivityMySelfBuy;
import com.xunao.benben.ui.item.ActivityMySmallMake;
import com.xunao.benben.ui.item.ActivitySmallMake;
import com.xunao.benben.ui.item.ActivitymyBuy;
import com.xunao.benben.ui.item.TallGroup.ActivityTalkGroup;
import com.xunao.benben.utils.ToastUtils;

public class PrivateFragment extends BaseFragment implements OnClickListener {

	private View view;
    private TextView num_friend,num_friend_about_me,num_creation,num_creation_about_me;
    private TextView tab_four_num,tab_four_hint;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_private, null);

		super.onCreateView(inflater, container, savedInstanceState);
		return view;

	}

	@Override
	protected void initView() {
        tab_four_num = (TextView) getActivity().findViewById(R.id.tab_four_num);
        tab_four_hint = (TextView) getActivity().findViewById(R.id.tab_four_hint);
        num_friend = (TextView) view.findViewById(R.id.num_friend);
        num_creation = (TextView) view.findViewById(R.id.num_creation);
        num_friend_about_me = (TextView) view.findViewById(R.id.num_friend_about_me);
        num_creation_about_me = (TextView) view.findViewById(R.id.num_creation_about_me);

		view.findViewById(R.id.layout_friend).setOnClickListener(this);
		view.findViewById(R.id.layout_creation).setOnClickListener(this);
		view.findViewById(R.id.layout_mybuy).setOnClickListener(this);
		view.findViewById(R.id.layout_happy).setOnClickListener(this);
		view.findViewById(R.id.layout_twocode).setOnClickListener(this);
		View scroll_view = view.findViewById(R.id.scroll_view);

		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, mActivity.mScreenHeight);

		scroll_view.setLayoutParams(layoutParams2);

	}


	@Override
	public void onClick(View v) {

		int id = v.getId();

		switch (id) {
		case R.id.layout_friend:// 朋友圈
			mActivity.startAnimActivity(ActivityFriend.class);
			break;
		case R.id.layout_creation:// 微创作
			if (CrashApplication.getInstance().user.getCreation_disable() == 0) {
				mActivity.startAnimActivity(ActivitySmallMake.class);
			} else if (CrashApplication.getInstance().user
					.getCreation_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getCreation_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}
			
			
			
			//mActivity.startAnimActivity(ActivitySmallMake.class);
			break;
		case R.id.layout_mybuy:// 我要买
//			if(mActivity.user.getBuy_disable() == 0){
//				mActivity.startAnimActivity(ActivitymyBuy.class);
//			}else if(mActivity.user.getBuy_disable() == 1){
//				ToastUtils.Errortoast(mActivity, "功能被永久禁用");
//			}else{
//				String date = mActivity.user.getBuy_disable() + "000";
//				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");  
//				String sd = sdf.format(new Date(Long.parseLong(date)));
//				ToastUtils.Errortoast(mActivity, "功能被禁用,将于" + sd + "解禁");
//			}
			
			if (CrashApplication.getInstance().user.getBuy_disable() == 0) {
				mActivity.startAnimActivity(ActivitymyBuy.class);
			} else if (CrashApplication.getInstance().user
					.getBuy_disable() == 1) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能已被永久禁用");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			} else {
				String beginDate = CrashApplication.getInstance().user
						.getBuy_disable() + "000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String sd = sdf.format(new Date(Long.parseLong(beginDate)));

				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mActivity,
						R.style.MyDialog1);
				hint.setContent("功能被禁用,将于" + sd + "解禁");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});

				hint.show();
			}
			
			
//			mActivity.startAnimActivity(ActivitymyBuy.class);
			break;
		case R.id.layout_happy:// 开心一刻
			mActivity.startAnimActivity(ActivityHappy.class);
			break;
		case R.id.layout_twocode:// 扫一扫
			mActivity.startAnimActivity(CaptureActivity.class);
			break;
		}

	}

	@Override
	protected void initLinstener() {

	}

	@Override
	protected void initDate() {

	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject t) {

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }


    public void refresh(){

        if(dbUtil!=null) {
            int friendAboutMe = 0;
            int creationAboutMe = 0;
            try {

                List<NewMsgAboutMe> friendAboutMeLists = dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type", "=", 1).and("is_new", "=", true).and("benben_id", "=", CrashApplication.getInstance().user.getBenbenId()));

                if (friendAboutMeLists != null) {
                    friendAboutMe = friendAboutMeLists.size();
                }
                List<NewMsgAboutMe> creationAboutMeLists = dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type", "=", 2).and("is_new", "=", true).and("benben_id", "=", CrashApplication.getInstance().user.getBenbenId()));
                if (friendAboutMeLists != null) {
                    creationAboutMe = creationAboutMeLists.size();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }

            int friendNum = CrashApplication.getInstance().friendNum;
//        int friendAboutMe = CrashApplication.getInstance().friendAboutMe;
            int creationNum = CrashApplication.getInstance().creationNum;
//        int creationAboutMe= CrashApplication.getInstance().creationAboutMe;
            int allNumAboutMe = friendAboutMe + creationAboutMe;
            int allNum = creationNum + friendNum;
            //底部红点显示
            if (allNumAboutMe <= 0) {
                tab_four_num.setVisibility(View.GONE);
                if (allNum <= 0) {
                    tab_four_hint.setVisibility(View.GONE);
                } else {
                    tab_four_hint.setVisibility(View.VISIBLE);
                }
            } else if (allNumAboutMe <= 99) {
                tab_four_num.setText(allNumAboutMe + "");
                tab_four_num.setVisibility(View.VISIBLE);
            } else {
                tab_four_num.setText("99+");
                tab_four_num.setVisibility(View.VISIBLE);
            }

            //朋友圈提示
            if (friendAboutMe <= 0) {
                num_friend_about_me.setVisibility(View.GONE);
            } else if (friendAboutMe <= 99) {
                num_friend_about_me.setText(friendAboutMe + "");
                num_friend_about_me.setVisibility(View.VISIBLE);
            } else {
                num_friend_about_me.setText("99+");
                num_friend_about_me.setVisibility(View.VISIBLE);
            }
            if (friendNum <= 0) {
                num_friend.setVisibility(View.GONE);
            } else {
                num_friend.setVisibility(View.VISIBLE);
            }
            //微创作
            if (creationAboutMe <= 0) {
                num_creation_about_me.setVisibility(View.GONE);
            } else if (creationAboutMe <= 99) {
                num_creation_about_me.setText(creationAboutMe + "");
                num_creation_about_me.setVisibility(View.VISIBLE);
            } else {
                num_creation_about_me.setText("99+");
                num_creation_about_me.setVisibility(View.VISIBLE);
            }
            if (creationNum <= 0) {
                num_creation.setVisibility(View.GONE);
            } else {
                num_creation.setVisibility(View.VISIBLE);
            }
        }
    }
}
