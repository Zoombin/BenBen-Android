package com.xunao.benben.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.NewMsgAboutMe;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.TimeUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/11/20.
 */
public class ActivityNewAboutMe extends BaseActivity{
    private int type=1;
    private ListView lv_message;
    private List<NewMsgAboutMe> newMsgAboutMes = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_about_me);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("消息列表", "", "",
                R.drawable.icon_com_title_left, 0);
        lv_message = (ListView) findViewById(R.id.lv_message);
        myAdapter = new MyAdapter();
        lv_message.setAdapter(myAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        type = getIntent().getIntExtra("type",1);
        try {
            newMsgAboutMes = dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type","=",type).and("is_new","=",true).and("benben_id","=", CrashApplication.getInstance().user.getBenbenId()).orderBy("id",true));
            myAdapter.notifyDataSetChanged();
            List<NewMsgAboutMe> updateMessages = new ArrayList<>();
            updateMessages = newMsgAboutMes;
            for(int i=0;i<updateMessages.size();i++){
                updateMessages.get(i).setIs_new(false);
            }
            dbUtil.updateAll(updateMessages);

        } catch (DbException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimFinsh();
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

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return newMsgAboutMes.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View converView, ViewGroup arg2) {

            ItemHolder itemHolder;
            NewMsgAboutMe newMsgAboutMe = newMsgAboutMes.get(position);

            if (converView == null) {
                converView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_new_about_me, null);
                itemHolder = new ItemHolder();
                itemHolder.item_poster = (CubeImageView) converView
                        .findViewById(R.id.item_poster);
                itemHolder.item_name = (TextView) converView
                        .findViewById(R.id.item_name);
                itemHolder.item_review = (TextView) converView
                        .findViewById(R.id.item_review);
                itemHolder.item_time = (TextView) converView
                        .findViewById(R.id.item_time);
                itemHolder.item_description = (TextView) converView
                        .findViewById(R.id.item_description);
                converView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) converView.getTag();
            }

            CommonUtils.startImageLoader(cubeimageLoader, newMsgAboutMe.getPoster(),
                    itemHolder.item_poster);

            itemHolder.item_name.setText(newMsgAboutMe.getNick_name());
            itemHolder.item_review.setText(newMsgAboutMe.getReview());
            itemHolder.item_time.setText(TimeUtil
                    .getDescriptionTimeFromTimestamp(newMsgAboutMe.getCreated_time() * 1000));
            itemHolder.item_description.setText(newMsgAboutMe.getDescription());

            converView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if(type==1){
                        startAnimActivity(ActivityMyDynamic.class);
                    }else if(type==2){
                        if (CrashApplication.getInstance().user.getCreation_disable() == 0) {
                            startAnimActivity(ActivityMySmallMake.class);
                        } else if (CrashApplication.getInstance().user
                                .getCreation_disable() == 1) {
                            final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
                                    R.style.MyDialog1);
                            hint.setContent("功能已被永久禁用");
                            hint.setBtnContent("确定");
                            hint.show();
                            hint.setOKListener(new View.OnClickListener() {

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

                            final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
                                    R.style.MyDialog1);
                            hint.setContent("功能被禁用,将于" + sd + "解禁");
                            hint.setBtnContent("确定");
                            hint.show();
                            hint.setOKListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    hint.dismiss();
                                }
                            });

                            hint.show();
                        }
                    }


                }
            });

            return converView;
        }

    }

    class ItemHolder {
        CubeImageView item_poster;
        TextView item_name;
        TextView item_review;
        TextView item_time;
        TextView item_description;
    }
}
