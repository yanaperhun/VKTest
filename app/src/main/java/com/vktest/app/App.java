package com.vktest.app;

import android.app.Application;

import com.vk.sdk.VKSdk;

/**
 * Created by seishu on 25.05.18.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }



}
