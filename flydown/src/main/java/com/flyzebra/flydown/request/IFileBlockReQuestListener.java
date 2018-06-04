package com.flyzebra.flydown.request;

import com.flyzebra.flydown.FlyDownError;
import com.flyzebra.flydown.file.FileBlock;

/**
 * 功能说明：
 * 
 * @author 作者：FlyZebra
 * @version 创建时间：2017年3月21日 下午5:40:46
 */
public interface IFileBlockReQuestListener {

	void onFailed(FileBlock fileBlock, FlyDownError ErrorCode);

	void onCompleted(FileBlock fileBlock);
	
	void onDownning(FileBlock fileBlock, int downSize);
	
	void pauseTask(FileBlock fileBlock);
}
