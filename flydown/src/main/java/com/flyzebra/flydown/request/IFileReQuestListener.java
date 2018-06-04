package com.flyzebra.flydown.request;

import com.flyzebra.flydown.FlyDownError;

/**
 * 功能说明：
 *
 * @author 作者：FlyZebra
 * @version 创建时间：2017年2月27日 下午1:55:43
 */
public interface IFileReQuestListener {

    void onFailed(String url, FlyDownError flyDownError);

    void onCompleted(String url);

    void onDownning(String url, long downBytes, long sumBytes, int downSpeed);

}