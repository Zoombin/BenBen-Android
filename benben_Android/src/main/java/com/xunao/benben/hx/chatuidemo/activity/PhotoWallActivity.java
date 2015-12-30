package com.xunao.benben.hx.chatuidemo.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.xunao.benben.R;
import com.xunao.benben.hx.chatuidemo.adapter.PhotoWallAdapter;
import com.xunao.benben.hx.chatuidemo.utils.ScreenUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

import java.io.File;
import java.util.ArrayList;

import static com.xunao.benben.hx.chatuidemo.utils.Utility.isImage;


/**
 * 选择照片页面
 */
public class PhotoWallActivity extends BaseActivity {
    private ImageView com_title_bar_left_bt;
    private MyTextView com_title_bar_left_tv;
    private ImageView com_title_bar_right_bt;
    private MyTextView com_title_bar_right_tv;
    private MyTextView com_title_bar_content;
    private ArrayList<String> list;
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;

    /**
     * 当前文件夹路径
     */
    private String currentFolder = null;
    /**
     * 当前展示的是否为最近照片
     */
    private boolean isLatest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_wall);
        //获取屏幕像素
        ScreenUtils.initScreen(this);
        // 头部
        com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
        com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
        com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
        com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);
        com_title_bar_content = (MyTextView) findViewById(R.id.com_title_bar_content);
        com_title_bar_left_bt.setImageResource(R.drawable.icon_com_title_left);
        com_title_bar_content.setText("图片选择");
        com_title_bar_right_tv.setText("完成");

        mPhotoWall = (GridView) findViewById(R.id.photo_wall_grid);
        list = getLatestImagePaths(100);
        adapter = new PhotoWallAdapter(this, list);
        mPhotoWall.setAdapter(adapter);

        com_title_bar_left_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimFinsh();
            }
        });

        //选择照片完成
        com_title_bar_right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择图片完成,回到起始页面
                ArrayList<String> paths = getSelectImagePaths();
                if(paths==null || paths.size()==0 ){
                    ToastUtils.Errortoast(PhotoWallActivity.this,"请选择图片");
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("code", paths);
                    setResult(RESULT_OK, intent);
                    PhotoWallActivity.this.finish();
                }

//                Intent intent = new Intent(PhotoWallActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("code", paths != null ? 100 : 101);
//                intent.putStringArrayListExtra("paths", paths);
//                startActivity(intent);
            }
        });

    }





    /**
     * 根据图片所属文件夹路径，刷新页面
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        adapter.clearSelectionMap();
        adapter.notifyDataSetChanged();

        if (code == 100) {   //某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            list.addAll(getAllImagePathsByFolder(folderPath));
        } else if (code == 200) {  //最近照片
            list.addAll(getLatestImagePaths(100));
        }

        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            //滚动至顶部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }


    /**
     * 获取指定路径下的所有图片文件。
     */
    private ArrayList<String> getAllImagePathsByFolder(String folderPath) {
        File folder = new File(folderPath);
        String[] allFileNames = folder.list();
        if (allFileNames == null || allFileNames.length == 0) {
            return null;
        }

        ArrayList<String> imageFilePaths = new ArrayList<String>();
        for (int i = allFileNames.length - 1; i >= 0; i--) {
            if (isImage(allFileNames[i])) {
                imageFilePaths.add(folderPath + File.separator + allFileNames[i]);
            }
        }

        return imageFilePaths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            //从最新的图片开始读取.
            //当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    latestImagePaths.add(path);
                    if (!cursor.moveToPrevious()) {
                        break;
                    }

//                    if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
//                        break;
//                    }
                }
            }


            cursor.close();
        }

        return latestImagePaths;
    }

    //获取已选择的图片路径
    private ArrayList<String> getSelectImagePaths() {
        SparseBooleanArray map = adapter.getSelectionMap();
        if (map.size() == 0) {
            return null;
        }

        ArrayList<String> selectedImageList = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            if (map.get(i)) {
                selectedImageList.add(list.get(i));
            }
        }

        return selectedImageList;
    }

    //从相册页面跳转至此页
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //动画
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        int code = intent.getIntExtra("code", -1);
        if (code == 100) {
            //某个相册
            String folderPath = intent.getStringExtra("folderPath");
            if (isLatest || (folderPath != null && !folderPath.equals(currentFolder))) {
                currentFolder = folderPath;
                updateView(100, currentFolder);
                isLatest = false;
            }
        } else if (code == 200) {
            //“最近照片”
            if (!isLatest) {
                updateView(200, null);
                isLatest = true;
            }
        }
    }
}
