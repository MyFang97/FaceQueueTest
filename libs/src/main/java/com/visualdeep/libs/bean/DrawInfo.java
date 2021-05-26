package com.visualdeep.libs.bean;

import android.graphics.Rect;

public class DrawInfo {
    private Rect rect;
    private String name = null;

    public DrawInfo(Rect rect, String name) {
        this.rect = rect;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

}
