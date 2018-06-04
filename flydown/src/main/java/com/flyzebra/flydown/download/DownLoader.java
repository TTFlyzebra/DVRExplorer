package com.flyzebra.flydown.download;

import android.content.Context;
import android.os.Environment;

import com.flyzebra.flydown.FlyDown;
import com.flyzebra.flydown.FlyDownError;
import com.flyzebra.flydown.request.IFileReQuestListener;
import com.flyzebra.flydown.utils.FileUtils;
import com.flyzebra.flydown.utils.TLog;

import java.io.File;

/**
 * 第三方下载库工具类
 * Created by FlyZebra on 2016/9/11.
 */
public class DownLoader implements IDownLoader {

    private String path;
    private IDiskFileCache iDiskFileCache;
    private IDownfileStatuListener iDownfileStatuListener;

    @Override
    public void initFileDownloader(Context context) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "flyzebra";
        this.initFileDownloader(context, path);
    }

    @Override
    public void initFileDownloader(Context context, String path) {
        IDiskFileCache iDiskFileCache = new DiskFileCache(context,new DownFileDB(context.getApplicationContext()));
        this.initFileDownloader(context, path, iDiskFileCache);

    }

    @Override
    public void initFileDownloader(Context context, String path, IDiskFileCache iDiskFileCache) {
        // 设置缓存的存放地址
        FlyDown.setCacheDir(path); // config the download path
        this.path = path;
        this.iDiskFileCache = iDiskFileCache;
    }

    // release FileDownloader
    public void releaseFileDownloader(Context context) {
    }


    @Override
    public void startDownFile(String url) {

        FlyDown.load(url).listener(new IFileReQuestListener() {
            @Override
            public void onFailed(String url, FlyDownError ErrorCode) {
                if(iDownfileStatuListener!=null){
                    iDownfileStatuListener.onFailed(url, iDiskFileCache.getDownFileInfo(url), ErrorCode);
                }
            }

            @Override
            public void onCompleted(String url) {
                if(iDownfileStatuListener!=null){
                    iDownfileStatuListener.onCompleted(iDiskFileCache.getDownFileInfo(url));
                }
            }

            @Override
            public void onDownning(String url, long downBytes, long sumBytes, int downSpeed) {
                if(iDownfileStatuListener!=null){
                    iDownfileStatuListener.onDowning(iDiskFileCache.getDownFileInfo(url), downSpeed, (int) (downBytes * 100 / sumBytes));
                }
            }

        }).goStart();

    }

    @Override
    public void delDownFile(String url) {
        if(iDiskFileCache.getDownFileStatus(url)== IDownStatus.COMPLETED){
            FileUtils.delFileInTread(FlyDown.getSavePath(url));
        }else{
            FlyDown.del(url);
        }
    }


    @Override
    public void pauseDownFile(String url) {
        DownFileBean downFileBean = iDiskFileCache.getDownFileInfo(url);
        if (downFileBean == null) return;
        FlyDown.pause(url);
    }

    @Override
    public void pauseAll() {
        TLog.d("pause All task!");
        FlyDown.pauseAll();
    }

    @Override
    public void registerDownfileStatusListener(IDownfileStatuListener iDownfileStatuListener) {
        this.iDownfileStatuListener = iDownfileStatuListener;
    }

}
