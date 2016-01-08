package com.xunao.benben.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;

/**
 * Created by LSD on 15/12/31.
 * 转让我的号码直通车弹窗
 */
public class TransferMyNumberTrainDialog extends AbsDialog {
    BaseActivity context;
    DialogViewListener viewListener;
    private EditText etnumbenben;
    private EditText ettips;
    private Button btno;
    private Button btyes;

    public TransferMyNumberTrainDialog(BaseActivity context) {
        super(context, R.style.MyDialog1);
        this.context = context;
        init();
    }

    public void init() {
        setContentView(R.layout.dialog_transfer_mynumber_train);

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
        etnumbenben = (EditText) findViewById(R.id.et_num_benben);
        ettips = (EditText) findViewById(R.id.et_tips);
        btno = (Button) findViewById(R.id.bt_no);
        btyes = (Button) findViewById(R.id.bt_yes);
    }

    @Override
    protected void initData() {
    }

    public void setData(String views,String over_rate ){
    }

    @Override
    protected void setListener() {
        btno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(viewListener != null){
                    viewListener.onConfirm();
                }
            }
        });
    }
    public void setDialogListener(DialogViewListener viewListener){
        this.viewListener = viewListener;
    }

    public interface DialogViewListener {
        void onConfirm();
    }
}
