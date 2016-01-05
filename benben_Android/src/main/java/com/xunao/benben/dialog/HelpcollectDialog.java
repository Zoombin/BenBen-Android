package com.xunao.benben.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;

/**
 * Created by LSD on 15/12/31.
 * 我的号码直通车弹窗
 */
public class HelpcollectDialog extends AbsDialog {
    BaseActivity context;
    HelpcollectListener viewListener;
    private TextView tvnum;
    private TextView tvpercent;
    private Button iknow;
    private Button continuer;

    public HelpcollectDialog(BaseActivity context) {
        super(context);
        this.context = context;
        init();
    }
    public HelpcollectDialog(BaseActivity context, int theme) {
        super(context, theme);
        this.context = context;
        init();
    }

    public void init() {
        setContentView(R.layout.dialog_helpcollect);
//        Window dialogWindow = this.getWindow();
//        dialogWindow.setGravity(Gravity.CENTER);
//        WindowManager windowManager = context.getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = (int)(display.getWidth()*3/4); //设置宽度
//        dialogWindow.setAttributes(lp);
    }

    @Override
    protected void initView() {
        tvnum = (TextView) findViewById(R.id.tv_num);
        tvpercent = (TextView) findViewById(R.id.tv_percent);
        iknow = (Button) findViewById(R.id.iknow);
        continuer = (Button) findViewById(R.id.continuer);
    }

    @Override
    protected void initData() {
    }

    public void setData(String views,String over_rate ){
        tvnum.setText(views+"人");
        tvpercent.setText(over_rate);
    }

    @Override
    protected void setListener() {
        iknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(viewListener != null){
                    viewListener.iknow();
                }
            }
        });
        continuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(viewListener != null){
                    viewListener.continuer();
                }
            }
        });
    }
    public void setHelpcollectListener(HelpcollectListener viewListener){
        this.viewListener = viewListener;
    }
    public interface HelpcollectListener{
        void iknow();
        void continuer();
    }
}
