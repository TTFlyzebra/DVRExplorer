package com.flyzebra.flydown.download;

import com.flyzebra.flydown.FlyDownError;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-8-上午9:08.
 */

public interface IDownfileStatuListener {

    void onPrepared(DownFileBean downFileBean);

    void onDowning(DownFileBean downFileBean, final float downloadSpeed, int downPercent);

    void onPasue(DownFileBean downFileBean);

    void onCompleted(DownFileBean downFileBean);

    void onFailed(String url,DownFileBean downFileBean,FlyDownError Error);
}
