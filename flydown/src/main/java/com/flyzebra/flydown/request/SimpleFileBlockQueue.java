package com.flyzebra.flydown.request;

import com.flyzebra.flydown.FlyDownError;
import com.flyzebra.flydown.file.FileBlock;
import com.flyzebra.flydown.file.IFileIO;
import com.flyzebra.flydown.network.IDownTask;
import com.flyzebra.flydown.network.TaskFactory;
import com.flyzebra.flydown.utils.TLog;
import com.flyzebra.flydown.utils.HttpUtils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能说明：
 * 
 * @author 作者：FlyZebra
 * @version 创建时间：2017年3月22日 下午3:11:27
 */
public class SimpleFileBlockQueue implements IFileBlockQueue, IFileBlockReQuestListener {
	private BlockingQueue<FileBlock> fileBlockQueue = new LinkedBlockingQueue<>();
	private String downUrl;
	private IFileIO saveFileIO;
	private IFileIO tempFileIO;
	private IFileBlockReQuestListener iFileBlockReQuestListener;
	private AtomicInteger blockSum = new AtomicInteger(0);
	private Hashtable<Integer, IDownTask> tasks = new Hashtable<>();
	private long fileLength = -1;
	private long hasDownSize = 0;

	@Override
	public boolean isEmpty() {
		return fileBlockQueue.isEmpty();
	}

	@Override
	public void doNextQueue() {
		TLog.d("下载下一个文件块......");
		if (!fileBlockQueue.isEmpty()) {
			IDownTask iHandleTask = TaskFactory.creat(downUrl);
			FileBlock fileBlock = fileBlockQueue.poll();
			tasks.put(fileBlock.getOrder(), iHandleTask);
			iHandleTask.setUrl(downUrl).setSaveFileIO(saveFileIO).setTempFileIO(tempFileIO)
					.setFileBlockReQuestListener(this).setFileBlock(fileBlock).handle();
		}else{
			TLog.d("下载队列为空.....");
		}
	}

	@Override
	public SimpleFileBlockQueue setUrl(String downUrl) {
		this.downUrl = downUrl;
		return this;
	}

	@Override
	public SimpleFileBlockQueue setSaveFileIO(IFileIO saveFileIO) {
		this.saveFileIO = saveFileIO;
		return this;
	}

	@Override
	public SimpleFileBlockQueue setTempFileIO(IFileIO tempFileIO) {
		this.tempFileIO = tempFileIO;
		return this;
	}

	@Override
	public SimpleFileBlockQueue listener(IFileBlockReQuestListener iFileBlockReQuestListener) {
		this.iFileBlockReQuestListener = iFileBlockReQuestListener;
		return this;
	}

	@Override
	public void createQueue() {
		TLog.d("开始创建下载队列......");
		fileLength = HttpUtils.getLength(downUrl,iFileBlockReQuestListener);
		String str = tempFileIO.readAll();
		if (str != null && str.length() != 0) {
			long needDownSize = 0;
			for (int i = 0; i < str.length(); i = i + 48) {
				int end = i + 48;
				FileBlock fileBlockData = FileBlock.create(str.substring(i, end));
				if (fileBlockData.getState() != 0xff) {
					fileBlockQueue.add(fileBlockData);
					blockSum.incrementAndGet();
					needDownSize = needDownSize + (fileBlockData.getEndPos()-fileBlockData.getStaPos());
					TLog.d("add fileBlock %s", fileBlockData.toString());
				}
			}
			hasDownSize = fileLength - needDownSize;
		} else {
			hasDownSize = 0;
			if (fileLength == -1) {
				// 不支持断点续传
				TLog.d("不支持断点续传！\n");
				FileBlock fileBlockData = new FileBlock();
				fileBlockData.setStaPos(0);
				fileBlockData.setEndPos(Long.MAX_VALUE);
				fileBlockData.setOrder(0);
				try {
					tempFileIO.save(fileBlockData);
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileBlockQueue.add(fileBlockData);
				blockSum.incrementAndGet();
				TLog.d("add fileBlock %s", fileBlockData.toString());
			} else {
				// 简单划分成64块
				for (int i = 0; i < 1; i++) {
					FileBlock fileBlockData = new FileBlock();
					fileBlockData.setStaPos(fileLength * i / 1);
					fileBlockData.setEndPos(fileLength * (i + 1) / 1);
					fileBlockData.setOrder(i);
					byte b[] = fileBlockData.toString().getBytes();
					try {
						tempFileIO.save(b, i * b.length, b.length);
					} catch (IOException e) {
						e.printStackTrace();
					}
					fileBlockQueue.add(fileBlockData);
					blockSum.incrementAndGet();
				}
			}
		}

		TLog.d("创建下载队列完成....");

	}

	@Override
	public int getBlockSum() {
		return blockSum.get();
	}

	@Override
	public long getFileLength() {
		return fileLength;
	}

	@Override
	public long getHasDownSize() {
		return hasDownSize;
	}

	@Override
	public void onFailed(FileBlock fileBlock, FlyDownError ErrorCode) {
		if (iFileBlockReQuestListener != null) {
			iFileBlockReQuestListener.onFailed(fileBlock, ErrorCode);
		}
	}

	@Override
	public void onCompleted(FileBlock fileBlock) {
		tasks.remove(fileBlock.getOrder());
		blockSum.decrementAndGet();
		if (iFileBlockReQuestListener != null) {
			iFileBlockReQuestListener.onCompleted(fileBlock);
		}
	}

	@Override
	public void onDownning(FileBlock fileBlock, int downSize) {
		if (iFileBlockReQuestListener != null) {
			iFileBlockReQuestListener.onDownning(fileBlock,downSize);
		}
	}

	@Override
	public void pause() {
		Set<Integer> keys = tasks.keySet();
		for (Integer key : keys) {
			tasks.get(key).cancle();
		}
	}

	@Override
	public void pauseTask(FileBlock fileBlock) {
		TLog.d("pauseTask\n");
		tasks.remove(fileBlock.getOrder());
//		blockSum.decrementAndGet();
		if (iFileBlockReQuestListener != null) {
			iFileBlockReQuestListener.pauseTask(fileBlock);
		}

	}

}
