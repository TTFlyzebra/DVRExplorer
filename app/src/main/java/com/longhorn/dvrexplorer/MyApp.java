package com.longhorn.dvrexplorer;

import android.content.Context;
import android.support.multidex.MultiDex;
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
