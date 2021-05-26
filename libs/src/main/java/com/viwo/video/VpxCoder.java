package com.viwo.video;

public class VpxCoder
{
	private static VpxCoder vpxCoder = null;
	
	public static int viwo_mode = 8;
	
	private VpxCoder()
	{
	}
	
	
	public static VpxCoder getInstance()
	{
		if(vpxCoder == null)
		{
		    vpxCoder = new VpxCoder();
		}
		return vpxCoder;
	}
	
	public void close264()
	{
	    vpxCoder = null;
	}
	
	
	public native void video_encode_init(int width,int height) ;

	public native void video_encode_end();

	public native int video_encode(byte[] rgb, byte[] yuv,int inLen, byte[] out);

	public native int video_encode_scale(byte[] rgb ,byte[]  dst_rgb,int width,int height,float scale);

	public native void video_decode_end() ;

	public native void video_decode_init(int width,int height);

	public native int video_decode(byte[] in,int inLen,byte[] out);
	
	public native int video_decodeI(byte[] in,int inLen,int[] out);
	
	public native int encodeSendData(int sock,byte[] nv21,int length,int width,int height);
	public native int decodeRecvData(int sock,int[] rgb,int[] out_ints);
	
	public native void VP8EncodeInit();
	public native void VP8EncodeRelease();
	public native int VP8Encode(byte[] _in_pBuff , int _in_length , int _in_width , int _in_height , int _in_bitcount,byte[] _out_pBuff , int[] _out_lengh , float _fScale);
	
	public native int VP8EncodeY(byte[] _in_pBuff , int _in_length , int _in_width , int _in_height , int _in_bitcount,byte[] _out_pBuff , int[] _out_lengh , float _fScale);

	public native int VP8DecodeInit();
	public native void VP8DecodeRelease();
	public native int VP8Decode( byte[] _pBuff , int _nLength, int _nBitCount , byte[] _out_pBuff , int _out_length , int _out_width , int _out_height , float _fScale);
	public native int VP8DecodeI( byte[] _pBuff , int _nLength, int _nBitCount , int[] _out_pBuff , int[] out_ints , float _fScale);
	
	
	public native int YUV4202RGB(byte[] yuv,byte[] rgb,int bitcount,int width,int height);
	public native int NV212RGB24(byte[] yuv,byte[] rgb,int bitcount,int width,int height);

	public native int YUV420ToRGB24(byte[] yuv,byte[] rgb,int bitcount,int width,int height);


	public native int YUV4202RGBI(byte[] yuv,int[] rgb,int bitcount,int width,int height);
	public native int RGB2YUV420(byte[] rgb,byte[] yuv,int bitcount,int width,int height);
	public native int NV21ToI420( byte[] nv21,byte[] yuv,int width,int height) ;


	public native int YUVRotate(byte[] yuv ,byte[] yuvrotate, int width, int height,int rotate);
		
	
	public native int connect(byte[] ip,int port);
	public native int recv(int sock,byte[] data,int length);
	public native int send(int sock,byte[] data,int length);
	public native int close(int sock);
	
    static 
    {
//        System.loadLibrary("VpxCoder");
		System.loadLibrary("NeonCodec");
    }
}
