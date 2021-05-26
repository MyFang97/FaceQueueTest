package com.visualdeep.libs.bean;

import com.visualdeep.libs.process.FaceListener;
import com.visualdeep.libs.process.RectListener;
import com.visualdeep.libs.util.DrawHelper;

public class FaceConfig {

    //基本参数
    public boolean isCompare = false;   //是否开启黑明单比对
    public int detectQueueSize = 6;     //1秒2帧，缓存3秒 = 6
    public int faceQueueSize = 20;      //1秒3个人脸，缓存20人脸
    public int featherQueueSize = 20;   //特征点人脸缓存
    public boolean traceMode = true;   //调试模式

    public int workSleep = 50;          //ms
    public int noworkSleep = 300;       //ms

    //人脸过滤参数
    public FaceListener faceListener = null;        //黑名单比较回调
    public float faceCredible = 0.999f;             //人脸置信度
    public int minFaceSize = 48;                    //小于此像素的头像，直接过滤掉（px）
    public int cachedFaceTime = 8000;               //缓存人脸，超过此时间的人脸，从短期比对中抛弃掉（ms）
    public float featherSimilarity = 0.75f;         //特征点去重相似度
    public float cmpImgSimilarity = 0.30f;          //快速去重相似度 0.64f
    public float blurVal = 30;                      //模糊比值
    public int detectThreadNum = 8;                 //检测线程数
    public RectListener rectListener = null;
    public DrawHelper drawHelper = null;

    public FaceConfig(boolean traceMode, FaceListener faceListener , boolean isCompare, float faceCredible ) {
        this.traceMode = traceMode;
        this.faceListener = faceListener;
        this.isCompare = isCompare;
        this.faceCredible = faceCredible;
    }

}

