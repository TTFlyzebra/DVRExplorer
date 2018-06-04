package com.flyzebra.flydown.download;

import java.util.List;

/**
 * 下载监听封装实现
 * Created by FlyZebra on 2016/9/20.
 */
public interface IDownListener {
    void upListData(List<DownFileBean> list);

    void downInfo(List<DownFileBean> list, int speed);
}
