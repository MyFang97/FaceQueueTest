package com.example.queuetest;

import java.util.concurrent.ArrayBlockingQueue;

public class ConfigTest {
    public ArrayBlockingQueue<String> faceQueue = null;

    private static com.example.queuetest.ConfigTest configTest;

    private ConfigTest() {
    }

    public static synchronized com.example.queuetest.ConfigTest getConfigTest() {

        if (configTest == null) {
            configTest = new com.example.queuetest.ConfigTest();
        }
        return configTest;
    }

}
