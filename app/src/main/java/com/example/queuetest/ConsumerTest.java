package com.example.queuetest;

import android.util.Log;

public class ConsumerTest extends Thread {

    private static ConfigTest configTest = ConfigTest.getConfigTest();

    public ConsumerTest(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String res = configTest.faceQueue.take();
                Log.d("consumer", "抛出一个:+" + res + ",当前queue：" + String.valueOf(configTest.faceQueue));
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
