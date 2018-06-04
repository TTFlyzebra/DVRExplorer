package com.flyzebra.flydown.download;

/**
 *下载文件的状态
 * Created by FlyZebra on 2016/9/20.
 */
public interface IDownStatus {
    /**
     * 暂停下载状态
     */
    int PAUSED = 6;
    /**
     * 等待下载状态
     */
    int WAITING = 1;
    /**
     * 正在下载状态
     */
    int DOWNLOADING = 4;
    /**
     * 下载已完成状态
     */
    int COMPLETED = 5;

    /**
     * 非wifi下设置成暂停
     */
    int NON_WIFI_PAUSED = 106;

    /**
     * 未知狀態，出錯
     */
    int FAILED = -2;

    /**
     * 未知狀態，出錯
     */
    int ERROR = -1;
}
