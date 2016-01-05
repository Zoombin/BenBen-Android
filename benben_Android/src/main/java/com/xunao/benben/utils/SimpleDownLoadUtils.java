package com.xunao.benben.utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleDownLoadUtils {
    public interface DownloadListener {
        public void DownLoadComplete(String url, String outPath);

        public void DownLoadFailed(String url, String outPath);
    }

    public static void download(final String strUrl, final String outPath, final DownloadListener downloadListener) {
        new Thread() {
            public void run() {

                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5 * 1000);
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                        throw new Exception();

                    // 获取输入流
                    InputStream is = connection.getInputStream();
                    // 创建文件输出流
                    FileOutputStream fos = new FileOutputStream(outPath);
                    byte buffer[] = new byte[1024 * 4];

                    // 获取文件总长
                    float filesize = connection.getContentLength();
                    // 记录已下载长度
                    float temp = 0;
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        // 将字节写入文件输出流
                        fos.write(buffer, 0, len);
                        temp += len;
                    }
                    if (downloadListener != null)
                        downloadListener.DownLoadComplete(strUrl, outPath);
                    fos.flush();
                    fos.close();
                    is.close();
                    connection.disconnect();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    if (downloadListener != null)
                        downloadListener.DownLoadFailed(strUrl, outPath);
                }
            }

            ;
        }.start();
    }
}
