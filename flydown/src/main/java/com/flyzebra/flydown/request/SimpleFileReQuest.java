package com.flyzebra.flydown.request;

import com.flyzebra.flydown.FlyDown;
import com.flyzebra.flydown.FlyDownError;
import com.flyzebra.flydown.file.FileBlock;
import com.flyzebra.flydown.file.FileIO;
import com.flyzebra.flydown.file.IFileIO;
import com.flyzebra.flydown.utils.FileUtils;
import com.flyzebra.flydown.utils.TLog;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：单个文件下载请求
 *
 * @author 作者：FlyZebra
 * @version 创建时间：2017年3月22日 上午10:03:57
 */
public class SimpleFileReQuest implements Runnable, IFileReQuest, IFileBlockReQuestListener {

    private String downUrl;
    private String saveFile;
    private String tempFile;
    private int threadNum;
    private IFileReQuestListener iFileReQuestListener;
    private IFileBlockQueue iFileBlockQueue;

    private IFileIO saveFileIO;
    private IFileIO tempFileIO;

    private long downSize;
    private long sumSize;

    // 添加原子操作数判断下载是否完成

    public SimpleFileReQuest(String downUrl) {
        this.downUrl = downUrl;
    }

    private ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    @Override
    public SimpleFileReQuest setSaveFile(String saveFile) {
        this.saveFile = saveFile;
        return this;
    }

    @Override
    public SimpleFileReQuest setThread(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    @Override
    public SimpleFileReQuest listener(IFileReQuestListener iFileReQuestListener) {
        this.iFileReQuestListener = iFileReQuestListener;
        return this;
    }

    @Override
    public void goStart() {
        // 在线程中执行下载操作
        if (!downUrl.equals("exist")) {
            TLog.d("开始下载......");
            executor.execute(this);
        } else {
            TLog.d("任务已经存在!");
        }

    }

    @Override
    public void pause() {
        if (iFileBlockQueue != null) {
            iFileBlockQueue.pause();
        }
    }

    @Override
    public void delFile() {
        if (iFileBlockQueue != null) {
            iFileBlockQueue.pause();
            saveFileIO.close();
            tempFileIO.close();
            FileUtils.delFileInTread(saveFile);
            FileUtils.delFileInTread(tempFile);
        }
    }

    @Override
    public void run() {
        if (downUrl == null) {
            TLog.d("无效的下载地址!");
            return;
        }

        if (saveFile == null) {
            saveFile = FlyDown.getSavePath(downUrl);
        }

        if (tempFile == null) {
            tempFile = FlyDown.getTempSavePath(downUrl);
        }

        if (iFileBlockQueue == null) {
            iFileBlockQueue = new SimpleFileBlockQueue();
        }

        try {
            saveFileIO = new FileIO(saveFile);
            tempFileIO = new FileIO(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        iFileBlockQueue.setUrl(downUrl).setSaveFileIO(saveFileIO).setTempFileIO(tempFileIO).listener(this).createQueue();
        downSize = iFileBlockQueue.getHasDownSize();
        sumSize = iFileBlockQueue.getFileLength();

        threadNum = Math.max(threadNum, 1);

        for (int i = 0; i < threadNum; i++) {
            iFileBlockQueue.doNextQueue();
        }

        lastTime = System.currentTimeMillis();

    }

    @Override
    public void onFailed(FileBlock fileBlock, FlyDownError ErrorCode) {
        iFileReQuestListener.onFailed(downUrl, ErrorCode);
    }

    @Override
    public void onCompleted(FileBlock fileBlock) {
        if (iFileBlockQueue.getBlockSum() == 0) {
            iFileReQuestListener.onCompleted(downUrl);
            saveFileIO.close();
            tempFileIO.close();
            FlyDown.pause(downUrl);
            FileUtils.delFileInTread(tempFile);
        } else {
            iFileBlockQueue.doNextQueue();
        }
    }

    private long lastSize;
    private long lastTime;
    private int downSpeed;

    @Override
    public void onDownning(FileBlock fileBlock, int downSize) {
        this.downSize = this.downSize + downSize;
        long time = System.currentTimeMillis();
        if (time - lastTime > 950) {
            downSpeed = (int) ((this.downSize - lastSize) / ((time - lastTime)));
            lastSize = this.downSize;
            lastTime = time;
        }

        if (iFileReQuestListener != null) {
            iFileReQuestListener.onDownning(downUrl, this.downSize, this.sumSize, downSpeed);
            TLog.d("downSpeed=%dKB/S,downBytes=%d,sumBytes=%d,url=%s", downSpeed, this.downSize, this.sumSize, downUrl);
        }
    }

    @Override
    public void pauseTask(FileBlock fileBlock) {
        if (iFileBlockQueue.getBlockSum() == 0) {
            saveFileIO.close();
            tempFileIO.close();
        }
    }

}
