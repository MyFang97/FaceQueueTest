package com.visualdeep.libs.bean;

public class FaceBean {

    private int left;
    private int top;
    private int right;
    private int bottom;
    private int width;
    private int height;
    private float credible;
    private int[] facePoints;
    private byte[] rgbai;
    private float[] feathers;
    private long ts;
    private String payload;

    public FaceBean(int left, int top, int right, int bottom, int width, int height, float credible, int[] facePoints, byte[] rgbai,String payload) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.width = width;
        this.height = height;
        this.credible = credible;
        this.facePoints = facePoints;
        this.rgbai = rgbai;
        this.payload = payload;
        this.ts = System.currentTimeMillis();
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
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

    public float getCredible() {
        return credible;
    }

    public void setCredible(float credible) {
        this.credible = credible;
    }

    public int[] getFacePoints() {
        return facePoints;
    }

    public void setFacePoints(int[] facePoints) {
        this.facePoints = facePoints;
    }

    public byte[] getRgbai() {
        return rgbai;
    }

    public void setRgbai(byte[] rgbai) {
        this.rgbai = rgbai;
    }

    public float[] getFeathers() {
        return feathers;
    }

    public void setFeathers(float[] feathers) {
        this.feathers = feathers;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
