package com.visualdeep.libs.process;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.visualdeep.libs.bean.DrawInfo;
import com.visualdeep.libs.bean.FaceDetectBean;
import com.visualdeep.libs.bean.FeatherBean;
import com.visualdeep.libs.util.FaceUtils;
import com.visualdeep.libs.util.StorageUtils;
import com.viwo.android.FaceDetect;
import com.visualdeep.libs.bean.FaceBean;
import com.visualdeep.libs.bean.FaceConfig;
import com.visualdeep.libs.util.BitmapUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class FaceProcess {

    private FaceProcess(){}
    public static FaceProcess getInst()
    {
        if(faceProcess == null)
        {
            faceProcess = new FaceProcess();
        }
        return faceProcess;
    }

    public boolean start() {
        isRunning = true;
        if( procThread == null )
        {
            procThread = new ProcThread( "AProcThread" );
            procThread.start();
        }
        if( compareThread == null )
        {
            compareThread = new CompareThread( "CompareThread" );
            compareThread.start();
        }
        return isRunning;
    }

    public boolean stop()
    {
        isRunning = false;
        //清除缓存
        if( mFaceQueue != null )
            mFaceQueue.clear();
        if( mDetectQueue != null )
            mDetectQueue.clear();
        return isRunning;
    }

    public void init( FaceConfig faceConfig )
    {
        this.mFaceConfig = faceConfig;
        this.mDetectQueue = new ArrayBlockingQueue<FaceDetectBean>( mFaceConfig.detectQueueSize );
        this.mFaceQueue = new ArrayBlockingQueue<FaceBean>( mFaceConfig.faceQueueSize);
        this.mFeatherQueue = new ArrayBlockingQueue<FeatherBean>( mFaceConfig.featherQueueSize);
        this.mTraceMode = faceConfig.traceMode;
        start();
    }

    public List<DrawInfo> detectFaceEx( byte[] rgba , byte[] rgb , int w , int h , int channel , int faceSize , String payload ) {
        //人脸检测
        List<DrawInfo> list = null;
        long t1 = System.currentTimeMillis();
        float[] faceInfo = getDetect(rgb, w, h, channel, faceSize , mFaceConfig.detectThreadNum );
        long t2 = System.currentTimeMillis() - t1 ;
        Log.d( TAG, "ts=" + t2);
        if (faceInfo != null && faceInfo.length > 1) {
            list = new ArrayList();
            int faceNum = (int) faceInfo[0];
            for (int i = 0; i < faceNum; i++) {
                float left, top, right, bottom, width, height;
                left = faceInfo[1 + 15 * i];
                top = faceInfo[2 + 15 * i];
                right = faceInfo[3 + 15 * i];
                bottom = faceInfo[4 + 15 * i];

                Rect rect = new Rect( (int)left , (int)top  , (int)right  , (int)bottom  );
                DrawInfo drawInfo = new DrawInfo( rect , "" );
                if( mFaceConfig.rectListener != null )
                    drawInfo = mFaceConfig.rectListener.process( drawInfo );

                list.add( drawInfo );
            }

            if( mFaceConfig.isCompare ) {
                FaceDetectBean fdBean = new FaceDetectBean( faceInfo , rgba , w , h , payload );
                if( mDetectQueue.remainingCapacity() == 0 )
                    mDetectQueue.poll();
                mDetectQueue.add( fdBean );
            }
            trace("[队列统计] 队列情况, 检测=" + mDetectQueue.size() + ",人脸=" + mFaceQueue.size()  + ",特征点人脸=" + mFeatherQueue.size() + ",ts=" + t2 );
        }
        return list;
    }

    public List<DrawInfo> detectFace(byte[] rgba , int w , int h , int channel , int faceSize , String payload ) {
        //人脸检测
        List<DrawInfo> list = null;
        float[] faceInfo = getDetect(rgba, w, h, channel, faceSize , mFaceConfig.detectThreadNum );
        if (faceInfo != null && faceInfo.length > 1) {
            list = new ArrayList();
            int faceNum = (int) faceInfo[0];
            for (int i = 0; i < faceNum; i++) {
                float left, top, right, bottom, width, height;
                left = faceInfo[1 + 15 * i];
                top = faceInfo[2 + 15 * i];
                right = faceInfo[3 + 15 * i];
                bottom = faceInfo[4 + 15 * i];

                Rect rect = new Rect( (int)left , (int)top  , (int)right  , (int)bottom  );
                DrawInfo drawInfo = new DrawInfo( rect , "" );
                list.add( drawInfo );
            }

            if( mFaceConfig.isCompare ) {
                FaceDetectBean fdBean = new FaceDetectBean( faceInfo , rgba , w , h , payload );
                if( mDetectQueue.remainingCapacity() == 0 )
                    mDetectQueue.poll();
                mDetectQueue.add( fdBean );
            }
            trace("[队列统计] 队列情况, 检测=" + mDetectQueue.size() + ",人脸=" + mFaceQueue.size()  + ",特征点人脸=" + mFeatherQueue.size() );
        }
        return list;
    }

    public float[] getFeather( byte[] rgba , int w , int h , int[] facePoints )
    {
        float[] f2 = FaceDetect.faceUtil.getFeature(rgba, w, h, facePoints );
        return f2;
    }

    public float[] getDetect( byte[] imageData, int imageWidth , int imageHeight, int imageChannel, int minFaceSize , int nThreadNum  )
    {
        float[] faceInfo = FaceDetect.faceUtil.FaceDetectF( imageData, imageWidth , imageHeight, imageChannel, minFaceSize , nThreadNum );
        return faceInfo;
    }

    public float getSimilar( float[] f1 , float[] f2 )
    {
        float similar = FaceDetect.faceUtil.calcSimilar(f1, f2);
        return similar;
    }

    public List<FaceBean> detectFace( Bitmap bitmap , byte[] rgba , int w , int h , int channel , int faceSize ) {
        //人脸检测
        List<FaceBean> list = null;
        if( bitmap == null )
            bitmap = BitmapUtils.getPicFromBytes( rgba, w , h );
        float[] faceInfo = FaceDetect.faceUtil.FaceDetectF( rgba, w, h, channel, faceSize , 8 );
        if (faceInfo != null && faceInfo.length > 1) {
            list = new ArrayList<FaceBean>();
            int faceNum = (int) faceInfo[0];
            for (int i = 0; i < faceNum; i++) {
                float left, top, right, bottom, width, height;
                float credible;
                left = faceInfo[1 + 15 * i];
                top = faceInfo[2 + 15 * i];
                right = faceInfo[3 + 15 * i];
                bottom = faceInfo[4 + 15 * i];
                credible = faceInfo[15 + 15 * i];

                width = right - left;
                height = bottom - top;

                int[] facePoints = new int[10];
                facePoints[0] = (int) (faceInfo[5 + 15 * i] - left);
                facePoints[1] = (int) (faceInfo[6 + 15 * i] - left);
                facePoints[2] = (int) (faceInfo[7 + 15 * i] - left);
                facePoints[3] = (int) (faceInfo[8 + 15 * i] - left);
                facePoints[4] = (int) (faceInfo[9 + 15 * i] - left);
                facePoints[5] = (int) (faceInfo[10 + 15 * i] - top);
                facePoints[6] = (int) (faceInfo[11 + 15 * i] - top);
                facePoints[7] = (int) (faceInfo[12 + 15 * i] - top);
                facePoints[8] = (int) (faceInfo[13 + 15 * i] - top);
                facePoints[9] = (int) (faceInfo[14 + 15 * i] - top);

                Bitmap nBitmap = Bitmap.createBitmap( bitmap,(int)left,(int)top,(int)width,(int)height);
                byte[] rgbai = BitmapUtils.getBitmapBytes(nBitmap);

                FaceBean faceBean = new FaceBean( (int)left, (int)top, (int)right, (int)bottom, (int)width , (int)height , credible, facePoints, rgbai , "" );

                if( credible <= mFaceConfig.faceCredible ) {
                    continue;
                }

                if( width < mFaceConfig.minFaceSize || height < mFaceConfig.minFaceSize ) {
                    continue;
                }

                list.add( faceBean );
            }
        }
        return list;
    }


    public class ProcThread extends Thread {

        public ProcThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            Log.d(TAG, Thread.currentThread().getName());
            while( isRunning )
            {
                //检测逻辑
                if( !mDetectQueue.isEmpty() && mFaceConfig.isCompare )
                {

                    long t1 = System.currentTimeMillis();
                    FaceDetectBean faceDetectorBean = mDetectQueue.poll();
                    byte[] rgba = faceDetectorBean.getRgba();
                    int w = faceDetectorBean.getWidth();
                    int h = faceDetectorBean.getHeight();
                    int faceNum = (int) faceDetectorBean.getFaceInfo()[0];
                    float[] faceInfo = faceDetectorBean.getFaceInfo();
                    String payload = faceDetectorBean.getPayload();

                    if (faceNum == 0)
                        continue;

                    Bitmap bitmap = BitmapUtils.getPicFromBytes( rgba, w ,h );

                    for(int i=0;i<faceNum;i++) {
                        count++;
                        int left, top, right, bottom,width , height;
                        float credible;
                        int[] facePoints2 = new int[10];

                        if (1+15*i == faceInfo.length)
                            continue;

                        left = (int) faceInfo[1+15*i];
                        top = (int) faceInfo[2+15*i];
                        right = (int) faceInfo[3+15*i];
                        bottom = (int) faceInfo[4+15*i];
                        credible = faceInfo[15+15*i];
                        width = right - left;
                        height = bottom - top;

                        if (width < mFaceConfig.minFaceSize || height < mFaceConfig.minFaceSize){
                            trace("[过滤]人脸大小不符合," +width+","+height );
                            continue;
                        }

                        if (credible < mFaceConfig.faceCredible){
                            trace("[过滤]人脸相似度低," +credible );
                            continue;
                        }

                        facePoints2[0] = (int) (faceInfo[5+15*i]-left);
                        facePoints2[1] = (int) (faceInfo[6+15*i]-left);
                        facePoints2[2] = (int) (faceInfo[7+15*i]-left);
                        facePoints2[3] = (int) (faceInfo[8+15*i]-left);
                        facePoints2[4] = (int) (faceInfo[9+15*i]-left);
                        facePoints2[5] = (int) (faceInfo[10+15*i]-top);
                        facePoints2[6] = (int) (faceInfo[11+15*i]-top);
                        facePoints2[7] = (int) (faceInfo[12+15*i]-top);
                        facePoints2[8] = (int) (faceInfo[13+15*i]-top);
                        facePoints2[9] = (int) (faceInfo[14+15*i]-top);



                        int val1 = FaceUtils.side_face( left , top , width , height , facePoints2 );
                        if( val1 >2   )
                        {
                            trace("[过滤]人脸姿态[X轴]不符合," +val1 );
                            continue;
                        }

//                        double[] val2 = FaceUtils.bow_upward_face( left , top , width , height , facePoints2 );
//                        if( val2[0] < 0.6 || val2[1] < 0.6 )
//                        {
//                            trace("[过滤]人脸姿态[Y轴]不符合," +val2[0]+","+val2[1] );
//                            //continue;
//                        }

                        double val3 = FaceUtils.tilt_face( left , top , width , height , facePoints2 );
                        if(  val3 > 0.1   )
                        {
                            trace("[过滤]人脸姿态[Z轴]不符合," +val3 );
                            continue;
                        }

                        Bitmap nBitmap = Bitmap.createBitmap( bitmap , left, top, width, height);
                        byte[] rgbai = BitmapUtils.getBitmapBytes( nBitmap );

                        double blur = FaceDetect.faceUtil.getBlur(rgbai,width,height);
                        if (blur < mFaceConfig.blurVal ){
                            trace( "[过滤]人脸清晰度不符合"+ blur);
                            continue;
                        }

                        boolean isFilter = false;

                        if ( bitmaps != null && faceInfos!= null){

                            for (int j=0;j< faceNums;j++){

                                int leftj, topj, rightj, bottomj,widthj , heightj;
                                float crediblej;
                                int[] facePointsj = new int[10];

                                leftj = (int) faceInfos[1+15*j];
                                topj = (int) faceInfos[2+15*j];
                                rightj = (int) faceInfos[3+15*j];
                                bottomj = (int) faceInfos[4+15*j];
                                crediblej = faceInfos[15+15*j];
                                widthj = rightj - leftj;
                                heightj = bottomj - topj;

                                if (width < mFaceConfig.minFaceSize || height < mFaceConfig.minFaceSize){
                                    trace("[过滤]人脸大小不符合," +width+","+height );
                                    continue;
                                }

                                if (crediblej < mFaceConfig.faceCredible){
                                    trace("[过滤]人脸相似度低," +crediblej );
                                    continue;
                                }

                                facePointsj[0] = (int) (faceInfos[5+15*j]-leftj);
                                facePointsj[1] = (int) (faceInfos[6+15*j]-leftj);
                                facePointsj[2] = (int) (faceInfos[7+15*j]-leftj);
                                facePointsj[3] = (int) (faceInfos[8+15*j]-leftj);
                                facePointsj[4] = (int) (faceInfos[9+15*j]-leftj);
                                facePointsj[5] = (int) (faceInfos[10+15*j]-topj);
                                facePointsj[6] = (int) (faceInfos[11+15*j]-topj);
                                facePointsj[7] = (int) (faceInfos[12+15*j]-topj);
                                facePointsj[8] = (int) (faceInfos[13+15*j]-topj);
                                facePointsj[9] = (int) (faceInfos[14+15*j]-topj);

                                int val11 = FaceUtils.side_face( leftj , topj , widthj , heightj , facePointsj );
                                if( val11 > 4  )
                                {
                                    trace("[过滤]人脸姿态[X轴]不符合," +val11 );
                                    continue;
                                }

//                        double[] val22 = FaceUtils.bow_upward_face( left , top , width , height , facePoints2 );
//                        if( val22[0] < 0.6 || val22[1] < 0.6 )
//                        {
//                            trace("[过滤]人脸姿态[Y轴]不符合," +val22[0]+","+val22[1] );
//                            //continue;
//                        }

                                double val33 = FaceUtils.tilt_face( leftj , topj , widthj , heightj , facePointsj );
                                if(  val33 > 0.15   )
                                {
                                    trace("[过滤]人脸姿态[Z轴]不符合," +val33 );
                                    continue;
                                }

                                Bitmap nBitmapS = Bitmap.createBitmap( bitmap ,leftj,topj,widthj,heightj );
                                byte[] rgbaj = BitmapUtils.getBitmapBytes( nBitmapS );

                                double blurs = FaceDetect.faceUtil.getBlur(rgbaj,widthj,heightj);
                                if (blurs < mFaceConfig.blurVal ){
                                    trace( "[过滤]人脸清晰度不符合"+ blurs );
                                    continue;
                                }

                                double d = FaceDetect.faceUtil.cmpImgData( rgbai , rgbaj , width, height, widthj, heightj ,4);
//                                StorageUtils.saveBitmap( nBitmap , count + "_" + FormatUtils.format(d) + "_" + credible );
//                                StorageUtils.saveBitmap( nBitmapS , count + "_" + FormatUtils.format(d) + "_" + crediblej );
                                if (d > mFaceConfig.cmpImgSimilarity ) {
                                    //if (credible < crediblej){
                                    isFilter = true;
                                    trace("[过滤]快速相似度" + d + "," + mFaceConfig.cmpImgSimilarity );
                                    //}
                                } else {
                                    trace("[未过滤]快速相似度" + d + "," + mFaceConfig.cmpImgSimilarity );
                                }

                            }

                        }

                        if ( isFilter ){
                            continue;
                        }

                        hitCount++;
                        long ts = System.currentTimeMillis() - t1;
                        trace("[命中统计" + ts + "] 缓存:" + mDetectQueue.size() + ",命中：" + hitCount + ",全部:" + count + ",比率:" + ((float)hitCount/(float)count));

                        FaceBean faceBean = new FaceBean( left, top, right, bottom, width , height , credible, facePoints2, rgbai , payload );

                        if( mFaceQueue.remainingCapacity() == 0 )
                            mFaceQueue.poll();
                        mFaceQueue.add( faceBean );

                    }

                    bitmaps = bitmap;
                    faceInfos = faceInfo;
                    faceNums = faceNum;

                    try {
                        Thread.sleep( mFaceConfig.workSleep );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    try {
                        Thread.sleep( mFaceConfig.noworkSleep );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    public class CompareThread extends Thread {

        private final static String NAME = "CompareThread";

        public CompareThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            Log.d(TAG, Thread.currentThread().getName());
            while( isRunning )
            {
                if( mFaceConfig.isCompare && !mFaceQueue.isEmpty() )
                {
                    final FaceBean faceBean = mFaceQueue.poll();
                    float[] feathers = getFeather(faceBean.getRgbai(), faceBean.getWidth(), faceBean.getHeight() , faceBean.getFacePoints() );
                    trace("[ CompareThread ] 计算人脸特征点," + feathers.length + ",[" + mFaceQueue.size() + "]");
                    faceBean.setFeathers( feathers );

                   //一段时间内的有效人脸比对
                    float similar = 0.0f;
                    boolean isOldFace = false;
                    FeatherBean featherBean = new FeatherBean( feathers , System.currentTimeMillis() );
                    Iterator<FeatherBean> it = mFeatherQueue.iterator();
                    long currTs = System.currentTimeMillis();
                    while (it.hasNext()) {
                        FeatherBean oldFeatherBean = it.next();

                        //过期的放到结果队列里
                        long ts = currTs - oldFeatherBean.getTs();
                        if (ts >= mFaceConfig.cachedFaceTime ) {
                            mFeatherQueue.remove( oldFeatherBean );
                            continue;
                        }

                        similar = FaceDetect.faceUtil.calcSimilar( feathers, oldFeatherBean.getFeathers() );
                        if ( similar >= mFaceConfig.featherSimilarity) {
                            isOldFace = true;
                        }

                        if( isOldFace ) {
                            break;
                        }
                    }

                    if( isOldFace ) {
                        trace( "[ CompareThread ] 老人脸数据，精准相似度," + similar );
                    }
                    else
                    {
                        trace( "[ CompareThread ] 新人脸数据，开始比较黑名单数据," + similar );
                    }

                    if( mFeatherQueue.remainingCapacity() == 0 )
                        mFeatherQueue.poll();
                    mFeatherQueue.add( featherBean );

                    if ( !isOldFace && mFaceConfig.faceListener != null) {
                        //Bitmap bitmap = BitmapUtils.getPicFromBytes( faceBean.getRgbai() , faceBean.getWidth() , faceBean.getHeight() );
                        //StorageUtils.saveBitmap( bitmap , "有效人脸" );
                        mFaceConfig.faceListener.compareFace( faceBean );
                    }

                    try {
                        Thread.sleep( mFaceConfig.workSleep );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        Thread.sleep( mFaceConfig.noworkSleep );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void trace( String payload )
    {
        if(mTraceMode) {
            Log.d( TAG, "payload=" + payload);
        }
    }

    private void trace( byte[] rgba , int width , int height , boolean isStorage , String payload )
    {
        if(mTraceMode) {
            Log.d( TAG, "isStorage=" + isStorage + " , payload=" + payload);
            if( isStorage ) {
                Bitmap bitmap = BitmapUtils.getPicFromBytes( rgba , width , height );
                StorageUtils.saveBitmap( bitmap, payload);
            }
        }
    }

    private FaceConfig mFaceConfig;

    private long count = 0;
    private long hitCount = 0;
    private boolean mTraceMode = false;
    private static final String TAG = "FaceProcess";
    private static FaceProcess faceProcess = null;
    private boolean isRunning = false;

    private ArrayBlockingQueue <FaceDetectBean> mDetectQueue = null;
    private ArrayBlockingQueue <FaceBean> mFaceQueue = null;
    private ArrayBlockingQueue <FeatherBean> mFeatherQueue = null;

    private ProcThread procThread = null;
    private CompareThread compareThread = null;
    private Bitmap bitmaps;
    private float[] faceInfos;
    private int faceNums;

}
