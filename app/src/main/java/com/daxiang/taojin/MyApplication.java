package com.daxiang.taojin;

import android.app.Application;

import com.daxiang.android.http.executor.TaskExecutor;
import com.daxiang.android.http.executor.TaskExecutorConfiguration;
import com.daxiang.android.http.okhttp.OkHttpManager;
import com.daxiang.android.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daxiang on 2017/1/24.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TaskExecutorConfiguration configuration = new TaskExecutorConfiguration.Builder(
                this).threadPoolSize(3).build();

        TaskExecutor.getInstance().init(configuration);

        OkHttpManager.useHttps(false);
        OkHttpManager.httpsHostName("192.168.1.22");
        OkHttpManager.keystorePwd("222222");
        try {
            OkHttpManager.keystoreInput(getResources().getAssets().open(
                    "AndroidClientTrust.bks"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        OkHttpManager.useApplicationInterceptors(true);
        OkHttpManager.init(getApplicationContext());

        initRes();

    }

    private void initRes() {
        TaskExecutor.getInstance().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    InputStream is = getAssets().open("README.md");
                    BufferedOutputStream bo = new BufferedOutputStream(
                            new FileOutputStream(
                                    new File(
                                            FileUtils
                                                    .getExternalFileDirs(getApplicationContext()),
                                            "README.md")));
                    byte[] buffer = new byte[1024 * 8];
                    int len = 0;
                    while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                        bo.write(buffer, 0, len);
                    }

                    bo.flush();
                    bo.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
