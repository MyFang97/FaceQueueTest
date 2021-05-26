package com.visualdeep.libs.bean;

public class FeatherBean {
  private float[] feathers;
  private long ts;

  public FeatherBean(float[] feathers, long ts) {
    this.feathers = feathers;
    this.ts = ts;
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
}
