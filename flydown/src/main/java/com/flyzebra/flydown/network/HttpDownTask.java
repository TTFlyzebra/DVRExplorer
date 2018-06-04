package com.flyzebra.flydown.network;

import android.os.Handler;
import android.os.Looper;

import com.flyzebra.flydown.DownInfo;
import com.flyzebra.flydown.FlyDownError;
import com.flyzebra.flydown.file.FileBlock;
import com.flyzebra.flydown.file.IFileIO;
import com.flyzebra.flydown.request.IFileBlockReQuestListener;
import com.flyzebra.flydown.utils.CloseableUtil;
import com.flyzebra.flydown.utils.TLog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能说明：
 *
 * @author 作者：FlyZebra
 * @version 创建时间：2017年3月1日 上午9:53:41
 */
public class HttpDownTask implements IDownTask, Runnable {
    private String downUrl;
    private IFileIO saveFileIO;
    private IFileIO tempFileIO;
    private FileBlock fileBlock;
    private AtomicInteger downSize = new AtomicInteger(0);
    private IFileBlockReQuestListener iFileBlockReQuestListener;
    private AtomicBoolean isCancel = new AtomicBoolean(false);
    private ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    @Override
    public void handle() {
        executor.execute(this);
    }

    @Override
    public HttpDownTask setFileBlockReQuestListener(IFileBlockReQuestListener iFileBlockReQuestListener) {
        this.iFileBlockReQuestListener = iFileBlockReQuestListener;
        return this;
    }

    @Override
    public HttpDownTask setFileBlock(FileBlock fileBlock) {
        this.fileBlock = fileBlock;
        return this;
    }

    @Override
    public HttpDownTask setUrl(String downUrl) {
        this.downUrl = downUrl;
        return this;
    }

    @Override
    public HttpDownTask setSaveFileIO(IFileIO saveFileIO) {
        this.saveFileIO = saveFileIO;
        return this;
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 使用Handler機制計算下載速度
     */
    private Runnable countDownSpeedTask = new Runnable() {
        @Override
        public void run() {
            if (iFileBlockReQuestListener != null) {
                iFileBlockReQuestListener.onDownning(fileBlock, downSize.get());
                downSize.set(0);
            }
            mHandler.postDelayed(countDownSpeedTask, 1000);
        }
    };

    @Override
    public void run() {
        TLog.d("开始下载文件块,start=%d,end=%d", fileBlock.getStaPos(), fileBlock.getEndPos());
        HttpURLConnection con = null;
        InputStream ins = null;
        try {
            final URL url = new URL(downUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.setChunkedStreamingMode(0);
            if (fileBlock.getEndPos() != Long.MAX_VALUE) {//判断是否支持断点续传
                con.setRequestProperty("RANGE", "bytes=" + fileBlock.getStaPos() + "-" + fileBlock.getEndPos());
            }
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK || con.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                ins = con.getInputStream();
                byte[] b = new byte[1024 * 1024];
                int nRead = 0;
                mHandler.post(countDownSpeedTask);
                while (!isCancel.get() && fileBlock.getStaPos() <= fileBlock.getEndPos() && (nRead = ins.read(b, 0, 1024 * 1024)) > 0) {
                    if (!isCancel.get()) {
                        //保存數據到文件
                        saveFileIO.save(b, fileBlock.getStaPos(), nRead);
                    }
                    if(!isCancel.get()){
                        //用文件記錄下載點
                        fileBlock.setStaPos(fileBlock.getStaPos() + nRead);
                        if (fileBlock.getEndPos() != Long.MAX_VALUE) {
                            tempFileIO.save(fileBlock);
                        }
                        downSize.set(downSize.get() + nRead);
                    }
                }
                if (!isCancel.get() && fileBlock.getEndPos() != Long.MAX_VALUE) {
                    fileBlock.setState(0xff);
                    tempFileIO.save(fileBlock);
                }

                downSize.set(0);
                mHandler.removeCallbacksAndMessages(null);

                if (iFileBlockReQuestListener != null) {
                    if (isCancel.get()) {
                        iFileBlockReQuestListener.onDownning(fileBlock, 0);
                        iFileBlockReQuestListener.pauseTask(fileBlock);
                    } else {
                        iFileBlockReQuestListener.onDownning(fileBlock, 0);
                        iFileBlockReQuestListener.onCompleted(fileBlock);
                    }
                }
            } else {
                TLog.d("ResponseCode = \n" + con.getResponseCode());
            }
        } catch (IOException e){
            TLog.d("文件已關閉，下載任務已取消！");
        } catch (Exception e) {
            TLog.d("下载文件出错%s", e.toString());
            if (iFileBlockReQuestListener != null) {
                iFileBlockReQuestListener.onFailed(fileBlock, new FlyDownError(DownInfo.NETWORK_ERROR, "网络下载失败", e));
            }
        } finally {
            mHandler.removeCallbacksAndMessages(null);
            if (con != null) {
                con.disconnect();
            }
            CloseableUtil.Close(ins);
        }
        TLog.d("文件块下载线程结束 %s", fileBlock.toString());
    }

    @Override
    public IDownTask setTempFileIO(IFileIO tempFileIO) {
        this.tempFileIO = tempFileIO;
        return this;
    }

    @Override
    public void cancle() {
        isCancel.set(true);
    }

}
