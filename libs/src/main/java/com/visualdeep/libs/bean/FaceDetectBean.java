package com.visualdeep.libs.bean;

public class FaceDetectBean {

  private float[] faceInfo;

  private byte[] rgba;

  private int width;

  private int height;

  private String payload;

  public FaceDetectBean(float[] faceInfo, byte[] rgba, int width, int height, String payload) {
    this.faceInfo = faceInfo;
    this.rgba = rgba;
    this.width = width;
    this.height = height;
    this.payload = payload;
  }

  public String getPayload() {
    return payload;
}

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public float[] getFaceInfo() {
    return faceInfo;
  }

  public void setFaceInfo(float[] faceInfo) {
    this.faceInfo = faceInfo;
  }

  public byte[] getRgba() {
    return rgba;
  }

  public void setRgba(byte[] rgba) {
    this.rgba = rgba;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }
}
