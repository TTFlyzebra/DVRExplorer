package com.flyzebra.flydown.download;

import android.content.Context;

import com.flyzebra.flydown.FlyDown;

import java.util.List;

/**
 * 第三方开源下载数据存储封装实现
 * Created by FlyZebra on 2016/9/13.
 */
public class DiskFileCache implements IDiskFileCache {
    private Context context;
    private DownFileDB sqlDB;

    public DiskFileCache(Context context,DownFileDB sqlDB) {
        this.context = context;
        this.sqlDB = sqlDB;
    }

    @Override
    public List<DownFileBean> geAllDownFileList() {
        return sqlDB.queryAll();
    }

    @Override
    public DownFileBean getDownFileInfo(String url) {
        return sqlDB.queryOneByUrl(url);
    }

    @Override
    public String getLocaSavePath(String url) {
        return FlyDown.getSavePath(url);
    }


    @Override
    public int getDownFileStatus(String url) {
        DownFileBean downFileBean = sqlDB.queryOneByUrl(url);
        if(downFileBean==null){
            return IDownStatus.ERROR;
        }
        return downFileBean.getStatus();
    }

    @Override
    public void delete(String url) {
        sqlDB.delete(url);
    }
}
