package com.visualdeep.libs.util;

public class FaceUtils {

    // 正脸：1, 2
    // 侧脸 3, 4
    // 鼻子在外边：5
    // 有点在小图外边：6
    // |  4  |  3  |  2  |  1  |  1  |  2  |  3  |  4  |
    // |     |     |     |     |     |     |     |     |
    // -------------------------------------------------
    // 0     1     2     3     4     5     6     7     8
    public static int side_face( int left , int top , int width , int height , int[] facePoints ) {

        int zyx = facePoints[0] ;
        int yyx = facePoints[1] ;
        int bzx = facePoints[2] ;
        int zzx = facePoints[3] ;
        int yzx = facePoints[4] ;
        int zyy = facePoints[5] ;
        int yyy = facePoints[6] ;
        int bzy = facePoints[7] ;
        int zzy = facePoints[8] ;
        int yzy = facePoints[9] ;

        // 判断鼻子在不在眼睛嘴中间
        if ( bzx - zyx > 0 && bzx - zzx > 0 &&
                yyx - bzx > 0 && yzx - bzx > 0 ) {
            double sideface = bzx / (width / 8);
            if ( sideface > 3 && sideface < 5 ) {
                return 1;
            }

            if (sideface > 2 && sideface < 6 ) {
                return 2;
            }

            if (sideface > 1 && sideface < 7 ) {
                return 3;
            }
            return 4;

        } else {
            return 5;
        }
    }

    // [抬头, 低头] < 0.6
    public static double[] bow_upward_face( int left , int top , int width , int height , int[] facePoints  ) {

        int zyx = facePoints[0] ;
        int yyx = facePoints[1] ;
        int bzx = facePoints[2] ;
        int zzx = facePoints[3] ;
        int yzx = facePoints[4] ;
        int zyy = facePoints[5] ;
        int yyy = facePoints[6] ;
        int bzy = facePoints[7] ;
        int zzy = facePoints[8] ;
        int yzy = facePoints[9] ;

        // 眼睛到鼻子距离
        double aa = bzy - ((zyy + yyy) / 2);
        // 鼻子到嘴的距离
        double bb = ((zzy + yzy) / 2) - bzy;
        double TT = 0.0;
        double DT = 0.0;
        if ( bb <= 0 ) {
            TT = 0.0;
        } else {
            TT = aa / bb;
        }

        if ( aa <= 0 ) {
            DT = 0.0;
        } else {
            DT = bb / aa;
        }
        return new double[]{TT, DT};
    }


    // 倾斜 0.9
    public static double tilt_face( int left , int top , int width , int height ,  int[] facePoints  ) {

        int zyx = facePoints[0] ;
        int yyx = facePoints[1] ;
        int bzx = facePoints[2] ;
        int zzx = facePoints[3] ;
        int yzx = facePoints[4] ;
        int zyy = facePoints[5] ;
        int yyy = facePoints[6] ;
        int bzy = facePoints[7] ;
        int zzy = facePoints[8] ;
        int yzy = facePoints[9] ;

        int aa = yyy - zyy;
        int bb = zyy - yyy;
        double tilt = 0;
        if ( aa < bb ) {
            tilt = bb - aa;
        } else {
            tilt = aa - bb;
        }
        return tilt / height;
    }

}