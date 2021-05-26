package com.example.queuetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import com.viwo.android.FaceDetect;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends AppCompatActivity {
    //F2
    private String passcode = "767bf722d0e76935ed963008b008cecf36bc3f4677f63b02c43e03a6a878d6381092289d916ae5ea732f79600361b5bf1cd159abd2203faa2220c36f2c9c2f0f3b21835fc2b66d24d097420b126b10c4";
    private String tag = "MainActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(tag, "" + getid(this));
        ConfigTest configTest = ConfigTest.getConfigTest();
        configTest.faceQueue = new ArrayBlockingQueue<>(50);
        ProduceTest produceTest = new ProduceTest("ProduceTest");
        produceTest.start();
        ConsumerTest consumerTest = new ConsumerTest("ConsumerTest");
        consumerTest.start();


//        new Thread(() -> {
//            init();
//            Log.d(tag, "" + FaceDetect.faceUtil);
//            Log.d(tag, "" + FaceDetect.isInit);
//            String url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20190123%2F0a49041e86624254ae1d81f4873d4c81.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1624586310&t=55048cf16603403202f4e6429d8209b2";
//            try {
//                Bitmap bitmapFact = BitmapFactory.decodeStream(getImageStream(url));
//                Log.d(tag, "" + bitmapFact);
//                Log.d(tag, "此处开始是向量化逻辑");
//                byte[] imgPix = getPixelsRGBA(bitmapFact);
//                Log.d(tag, "pix-->" + imgPix);
//                Log.d(tag, "此处开始获取特征点");
//                float[] faceInfo = FaceDetect.faceUtil.FaceDetectF(imgPix, bitmapFact.getWidth(), bitmapFact.getHeight(), 4, 48, Integer.parseInt(String.valueOf(Thread.currentThread().getId())));
//                Log.d(tag, "特征点" + Arrays.toString(faceInfo));
//                int[] facePoints = new int[10];
//                int left = 0, top = 0, right, bottom, width = 0, height = 0;
//                float credible;
//                left = (int) faceInfo[1];
//                top = (int) faceInfo[2];
//                right = (int) faceInfo[3];
//                bottom = (int) faceInfo[4];
//                credible = faceInfo[15 + 15];
//                width = right - left;
//                height = bottom - top;
//                facePoints[0] = (int) (faceInfo[5 + 15] - left);
//                facePoints[1] = (int) (faceInfo[6 + 15] - left);
//                facePoints[2] = (int) (faceInfo[7 + 15] - left);
//                facePoints[3] = (int) (faceInfo[8 + 15] - left);
//                facePoints[4] = (int) (faceInfo[9 + 15] - left);
//                facePoints[5] = (int) (faceInfo[10 + 15] - top);
//                facePoints[6] = (int) (faceInfo[11 + 15] - top);
//                facePoints[7] = (int) (faceInfo[12 + 15] - top);
//                facePoints[8] = (int) (faceInfo[13 + 15] - top);
//                facePoints[9] = (int) (faceInfo[14 + 15] - top);
//                // 向量化
//                float[] temp = FaceDetect.faceUtil.getFeature(imgPix, bitmapFact.getWidth(), bitmapFact.getHeight(), facePoints);
//                float[] temp2 = FaceDetect.faceUtil.getFeature(imgPix, bitmapFact.getWidth(), bitmapFact.getHeight(), facePoints);
//                Log.d(tag, "向量化" + Arrays.toString(temp));
//
//                Log.d(tag, "此处开始是简单比较逻辑");
////                byte[] rgbai = BitmapUtils.getBitmapBytes(bitmapFact);
////                byte[] rgbaj = BitmapUtils.getBitmapBytes(bitmapFact);
////                double d = FaceDetect.faceUtil.cmpImgData(rgbai, rgbaj, width, height, width, height, 4);
////                Log.d(tag, "简单比较逻辑结果" + d);
//                Log.d(tag, "此处开始是向量化比较逻辑");
//                float f = FaceDetect.faceUtil.calcSimilar(temp, temp2);
//                Log.d(tag, "向量化比较逻辑结果" + f);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }).start();
    }

    private void init() {
        FaceDetect.getFaceUtil(com.example.queuetest.MainActivity.this, passcode);
//        FaceConfig faceConfig = new FaceConfig(true, (FaceListener) this, true, 0.95f);
//        FaceProcess.getInst().init(faceConfig);
//        faceConfig.rectListener = (RectListener) this;
    }

    /**
     * 从网络上获取图片,并返回输入流
     *
     * @param path 图片的完整地址
     * @return InputStream
     * @throws Exception
     */
    public InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10 * 1000);
        conn.setConnectTimeout(10 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    //get pixels
    private static byte[] getPixelsRGBA(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        byte[] temp = null;
        try {
            if (buffer.array().length == bytes) {
                image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer
                temp = buffer.array(); // Get the underlying array containing the
            } else {
                Log.i("error", "BufferUnderflowException");
            }
        } catch (BufferUnderflowException e) {
        }
        return temp;
    }


    public synchronized String getid(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String ID = TelephonyMgr.getDeviceId();
        return ID;
    }

}