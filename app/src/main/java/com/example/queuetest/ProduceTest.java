package com.example.queuetest;

import android.util.Log;

public class ProduceTest extends Thread {

    private ConfigTest configTest = ConfigTest.getConfigTest();

    ProduceTest(String name) {
        super(name);
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 10) {
            try {
                configTest.faceQueue.put(String.valueOf(i));
                Log.d("producer", "放进去一个a,当前queue：" + String.valueOf(configTest.faceQueue));
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
