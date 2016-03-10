package com.xunao.benben.ui.order;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.lidroid.xutils.exception.HttpException;
import com.sitech.oncon.barcode.camera.CameraManager;
import com.sitech.oncon.barcode.core.FinishListener;
import com.sitech.oncon.barcode.core.InactivityTimer;
import com.sitech.oncon.barcode.core.OrderCheckActivityHandler;
import com.sitech.oncon.barcode.core.ViewfinderView;
import com.sitech.oncon.barcode.executor.ResultHandler;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by ltf on 2015/12/22.
 */
public class ActivityOrderCheck extends BaseActivity implements View.OnClickListener,SurfaceHolder.Callback {
    private LinearLayout ll_input_order;
    private FrameLayout fl_scan_order;
    private TextView tv_order_sn;
    private ImageView iv_input_order,iv_scan_order;
    Button btn1,btn2,btn3,btn4,btn5,
            btn6,btn7,btn8,btn9,btn0,
            btnEnter,btnSharp;
    private String order_sn="";
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private CameraManager cameraManager;
    private ViewfinderView viewfinderView;
    private OrderCheckActivityHandler handler;
    private Result lastResult;
    private IntentSource source;
    private final int from_photo = 010;
    static final int PARSE_BARCODE_SUC = 3035;
    static final int PARSE_BARCODE_FAIL = 3036;
    ProgressDialog mProgress;
    private Collection<BarcodeFormat> decodeFormats;
    private String characterSet;
    private int currentTab=1;
    private MsgDialog inputDialog;

    enum IntentSource {

        ZXING_LINK, NONE

    }

//    Handler barHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case PARSE_BARCODE_SUC:
//                    // viewfinderView.setRun(false);
//                    showDialog((String) msg.obj);
//                    break;
//                case PARSE_BARCODE_FAIL:
//                    // showDialog((String) msg.obj);
//                    if (mProgress != null && mProgress.isShowing()) {
//                        mProgress.dismiss();
//                    }
//                    final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
//                            ActivityOrderCheck.this, R.style.MyDialog1);
//                    hint.setContent("扫描失败,请重试");
//                    hint.show();
//                    hint.setOKListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            hint.dismiss();
//                            restartPreviewAfterDelay(0L);
//                        }
//                    });
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//
//    };
//    private MsgDialog inputDialog;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    
    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_check);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("订单验证", "", "记录",
                R.drawable.icon_com_title_left, 0);
        ll_input_order = (LinearLayout) findViewById(R.id.ll_input_order);
        fl_scan_order = (FrameLayout) findViewById(R.id.fl_scan_order);
        tv_order_sn = (TextView) findViewById(R.id.tv_order_sn);
        iv_input_order = (ImageView) findViewById(R.id.iv_input_order);
        iv_scan_order = (ImageView) findViewById(R.id.iv_scan_order);
        iv_input_order.setOnClickListener(this);
        iv_scan_order.setOnClickListener(this);
        btn1 = (Button)findViewById(R.id.btn_1);
        btn2 = (Button)findViewById(R.id.btn_2);
        btn3 = (Button)findViewById(R.id.btn_3);
        btn4 = (Button)findViewById(R.id.btn_4);
        btn5 = (Button)findViewById(R.id.btn_5);
        btn6 = (Button)findViewById(R.id.btn_6);
        btn7 = (Button)findViewById(R.id.btn_7);
        btn8 = (Button)findViewById(R.id.btn_8);
        btn9 = (Button)findViewById(R.id.btn_9);
        btn0 = (Button)findViewById(R.id.btn_0);
        btnEnter = (Button)findViewById(R.id.btn_enter);
        btnSharp = (Button)findViewById(R.id.btn_sharp);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn0.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
        btnSharp.setOnClickListener(this);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        cameraManager = new CameraManager(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimFinsh();
            }
        });

        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimActivity(ActivityOrderCheckRecord.class);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_input_order:
                if(currentTab==2) {
                    currentTab = 1;
                    ll_input_order.setVisibility(View.VISIBLE);
                    fl_scan_order.setVisibility(View.GONE);
                    iv_input_order.setImageResource(R.drawable.icon_input_order_green);
                    iv_scan_order.setImageResource(R.drawable.icon_scan_order_grey);
                    stop();
                }
                break;
            case R.id.iv_scan_order:
                if(currentTab==1) {
                    currentTab = 2;
                    ll_input_order.setVisibility(View.GONE);
                    fl_scan_order.setVisibility(View.VISIBLE);
                    iv_input_order.setImageResource(R.drawable.icon_input_order_grey);
                    iv_scan_order.setImageResource(R.drawable.icon_scan_order_green);
                    initScan();
                }
                break;
            case R.id.btn_1:
                order_sn += "1";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_2:
                order_sn += "2";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_3:
                order_sn += "3";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_4:
                order_sn += "4";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_5:
                order_sn += "5";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_6:
                order_sn += "6";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_7:
                order_sn += "7";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_8:
                order_sn += "8";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_9:
                order_sn += "9";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_0:
                order_sn += "0";
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_sharp:
                if(order_sn.length()>0) {
                    order_sn = order_sn.substring(0, order_sn.length() - 1);
                }
                tv_order_sn.setText(order_sn);
                break;
            case R.id.btn_enter:
                startAnimActivity2Obj(ActivityOrderCheckDetail.class,"order_sn",order_sn);
                break;
        }
    }

    private void initScan() {
        handler = null;
        lastResult = null;
        resetStatusView();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        inactivityTimer.onResume();
        source = IntentSource.NONE;
        decodeFormats = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentTab==2)
            initScan();
    }

    @Override
    protected void onPause() {
        if(currentTab==2) {
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            inactivityTimer.onPause();
            cameraManager.closeDriver();
            if (!hasSurface) {
                SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                surfaceHolder.removeCallback(this);
            }
        }
        super.onPause();
    }

    private void stop(){
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        if (mProgress != null) {
            mProgress.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK)
                        && lastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 这里初始化界面，调用初始化相机
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    private static ParsedResult parseResult(Result rawResult) {
        return ResultParser.parseResult(rawResult);
    }

    // 解析二维码
    public void handleDecode(Result rawResult, Bitmap barcode) {
        inactivityTimer.onActivity();
        lastResult = rawResult;

        ResultHandler resultHandler = new ResultHandler(parseResult(rawResult));

        boolean fromLiveScan = barcode != null;
        if (barcode == null) {
            final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
                    ActivityOrderCheck.this, R.style.MyDialog1);
            hint.setContent("扫描失败,请重试");
            hint.show();
            hint.setOKListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    hint.dismiss();
                    restartPreviewAfterDelay(0L);
                }
            });
            return;
        } else {
            final String url = resultHandler.getDisplayContents().toString();
            if (url.startsWith("http")) {
                inputDialog = new MsgDialog(ActivityOrderCheck.this,
                        R.style.MyDialogStyle);
                inputDialog.setContent("检测到未知链接是否跳转", url, "跳转", "取消");
                inputDialog.setCancleListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.dismiss();
                    }
                });
                inputDialog.setOKListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.dismiss();
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                inputDialog.show();
            } else {
                startAnimActivity2Obj(ActivityOrderCheckDetail.class,"order_sn",url);
            }
        }
    }

    // 初始化照相机，CaptureActivityHandler解码
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new OrderCheckActivityHandler(this, decodeFormats,
                        characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.confirm, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }


}
