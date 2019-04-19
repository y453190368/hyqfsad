package com.jlinc.android.hyqfsad.base;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //bugly异常上报
        CrashReport.initCrashReport(this, "bfe0be0987", true);
        //初始化okgo
        initOkGo();
    }

    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);
        OkGo.getInstance().init(this)
        .setOkHttpClient(builder.build());
    }
}
