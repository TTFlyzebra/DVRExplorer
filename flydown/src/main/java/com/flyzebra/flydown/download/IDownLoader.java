package com.flyzebra.flydown.download;

import android.content.Context;

/**
 *下载类实现接口
 * Created by FlyZebra on 2016/9/13.
 */
public interface IDownLoader {
    /**
     * 初始化下载模块
     * @param context
     */
    void initFileDownloader(Context context);

    void initFileDownloader(Context context, String path);

    void initFileDownloader(Context context, String path,IDiskFileCache iDiskFileCache);

    /**
     * 释放下载模块资源
     * @param context
     */
    void releaseFileDownloader(Context context);

//    /**
//     * 启动下载状态监听服务
//     * @param context
//     */
//    void startListenerService(Context context);
//
//    /**
//     * 停止下载状态监听服务
//     * @param context
//     */
//    void stopListenerService(Context context);

//    /**
//     * 开始所有下载任务
//     */
//    void continueAll();

    /**
     * 执行指定的下载任务
     * @param url 下载链接
     */
    void startDownFile(String url);

    /**
     * 删除指定的下载任务
     * @param url 下载链接
     */
    void delDownFile(String url);

    /**
     * 暂停指定的下载任务
     * @param url 下载链接
     */
    void pauseDownFile(String url);

    /**
     * 暂停所有下在执行下载的任务
     */
    void pauseAll();

    void registerDownfileStatusListener(IDownfileStatuListener iDownfileStatuListener);
}
