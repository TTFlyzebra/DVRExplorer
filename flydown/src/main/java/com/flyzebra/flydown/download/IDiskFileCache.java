package com.flyzebra.flydown.download;

import java.util.List;

/**
 *
 * Created by FlyZebra on 2016/9/13.
 */
public interface IDiskFileCache {
    /**
     * 获取所有下载列表信息，包括已完成的和未完成的
     * @return
     */
    List<DownFileBean> geAllDownFileList();

    /**
     * 获取指定下载文件信息
     * 已下载文件大小，下载进度
     * @param url
     * @return
     */
    DownFileBean getDownFileInfo(String url);

    /**
     * 获取本地保存的文件保存路径（本地播放路径）
     * @param url
     * @return
     */
    String getLocaSavePath(String url);

    /**
     * 獲取下載文件的狀態
     * @param url
     * @return
     */
    int getDownFileStatus(String url);

    /**
     * 刪除下載任務
     * @param url
     */
    void delete(String url);
}
