package com.qstech.pierglass;

import android.app.Application;

/**
 * Created by admin on 2016/9/9.
 */
public class App extends Application {

    public static int MEDIA_ADD = 65535;

    public static int MEDIA_ALTER = 65534;

    public static String serverIP = "";  //ap的IP地址

    public static int blockSize = 0xffff;  //传输文件时的块大小

    private static App instance;

    @Override
    public void onCreate() {

        super.onCreate();

        if (instance == null) {
            instance = this;
        }
    }

    public static App getInstance() {
        return instance;
    }
}
