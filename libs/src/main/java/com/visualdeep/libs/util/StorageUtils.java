package com.visualdeep.libs.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageUtils {

    private static final String TAG = "StorageUtils";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS" );
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd" );

    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     */
    public static String saveBitmap(Bitmap bitmap , String payload ) {

        long timeStamp = System.currentTimeMillis();
        String sd = sdf.format(new Date(timeStamp));
        String dic = sdf1.format(new Date(timeStamp));

        //生成路径
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirName = "face-images/" + dic;
        File appDir = new File(root , dirName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        //文件名为时间
        String fileName = sd + "_" + payload + ".jpg";

        //获取文件
        String imgUrl = appDir + "/" + fileName;
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            //通知系统相册刷新
//            MyApplication.getInstance().getContext()..this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                    Uri.fromFile(new File(file.getPath()))));
            return imgUrl;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

}
