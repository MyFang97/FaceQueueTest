package com.viwo.android;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

public class FaceDetect {

    public native boolean FaceModelInit(String faceDetectionModelPath,String passcode);

    public native float[] FaceDetectF(byte[] imageData, int imageWidth , int imageHeight, int imageChannel, int minFaceSize , int nThreadNum );

    public native float[] getFeature(byte[] faceData1,int w1,int h1,int[] landmark1);

    public native float calcSimilar(float[] ft1,float[] ft2);

    public native double getBlur(byte[] faceData1,int width,int height);

    //简单比较
    public native double cmpImgData(byte[] faceData1,byte[] faceData2_,int w1,int h1,int w2,int h2,int channel);

    static {
        System.loadLibrary("ArmFace");
    }

    public static void copyBigDataToSD(String strOutFileName , Context context) throws IOException {
        Log.i(TAG, "start copy file " + strOutFileName);

        File sdDir = Environment.getExternalStorageDirectory();
        File file = new File(sdDir.toString()+"/face/");
        if (!file.exists()) {
            file.mkdir();
        }

        String tmpFile = sdDir.toString()+"/face/" + strOutFileName;
        File f = new File(tmpFile);
        if (f.exists()) {
            Log.i(TAG, "file exists " + strOutFileName);
            return;
        }
        InputStream myInput;
        java.io.OutputStream myOutput = new FileOutputStream(sdDir.toString()+"/face/"+ strOutFileName);
        myInput = context.getAssets().open(strOutFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
        Log.i(TAG, "end copy file " + strOutFileName);

    }


    public static FaceDetect faceUtil = new FaceDetect();

    public static boolean isInit = false;

    public static FaceDetect getFaceUtil( Context context , String passcode )
    {
        if(isInit == false)
        {
            try
            {
                copyBigDataToSD("det1.bin",context);
                copyBigDataToSD("det2.bin",context);
                copyBigDataToSD("det3.bin",context);
                copyBigDataToSD("det1.param",context);
                copyBigDataToSD("det2.param",context);
                copyBigDataToSD("det3.param",context);
                copyBigDataToSD("mf.bin",context);
                copyBigDataToSD("mf.param",context);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            File sdDir =  Environment.getExternalStorageDirectory();
            String sdPath = sdDir.toString() + "/face/";
            boolean state = faceUtil.FaceModelInit(sdPath,passcode);
            isInit = true;
        }
        return faceUtil;
    }
}
