package com.longhorn.dvrexplorer;

import android.support.multidex.MultiDexApplication;

import com.longhorn.dvrexplorer.http.FlyOkHttp;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        FlyOkHttp.getInstance().init(getApplicationContext());
//        DownFileManager.install(getApplicationContext());
    }
}
