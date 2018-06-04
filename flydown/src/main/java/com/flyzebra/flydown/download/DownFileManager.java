package com.flyzebra.flydown.download;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.flyzebra.flydown.FlyDownError;
import com.flyzebra.flydown.utils.TLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 下载文件管理类
 * Created by FlyZebra on 2016/9/11.
 */
public class DownFileManager implements IDownfileStatuListener, IDownStatus {
    private Context context;
    private DownFileDB sqlDB;
    private IDownLoader iDownLoader;
    private IDiskFileCache iDiskFileCache;
    private AtomicBoolean isDownning = new AtomicBoolean(false);
    private ExecutorService executor = Executors.newCachedThreadPool();
    private String mCurrentDBUrl;
    private List<DownFileBean> mDownFileList;
    private DownFileBean mCurrentDownFileBean;
    private String mCurrentDownUrl;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private String savePath;

    private DownFileManager() {
    }

    /**
     * 初始化下载模块
     *
     * @param context
     */
    public static void install(Context context) {
        TLog.d("DownFileManager start install!");
        getInstance().init(context);
        TLog.d("DownFileManager onCompleted install!");
    }

    private static class DownFileManagerHolder {
        public static final DownFileManager sInstance = new DownFileManager();
    }

    public static DownFileManager getInstance() {
        return DownFileManagerHolder.sInstance;
    }

    /**
     * 初始化下载管理模块
     *
     * @param context
     */
    private void init(Context context) {
        sqlDB = new DownFileDB(context.getApplicationContext());
        this.init(context, new DownLoader(), new DiskFileCache(context,sqlDB));
    }

    private void init(Context context, IDownLoader iDownLoader, IDiskFileCache iDiskFileCache) {
        this.context = context;
        this.iDownLoader = iDownLoader;
        this.iDiskFileCache = iDiskFileCache;

        savePath = "/sdcard/flyzebra";
        this.iDownLoader.initFileDownloader(context, savePath, iDiskFileCache);
        iDownLoader.registerDownfileStatusListener(this);
        isDownning.set(false);
        startDown();

//        checkAndDeleteNonCache();
    }

    /**
     * 开始下载任务
     */
    public void startDown() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<DownFileBean> list = checkAndgetDownFileBeanList();
                if (mDownFileList == null) {
                    mDownFileList = new ArrayList<DownFileBean>();
                } else {
                    mDownFileList.clear();
                }
                mDownFileList.addAll(list);
                //检测并删除多余的文件
                upDownListData(mDownFileList);
                startDownFileWithDB();
            }
        });
    }

    /**
     * 暂停所有下载任务
     */
    public void noWifiPasueAll() {
        isDownning.set(false);
        iDownLoader.pauseAll();
        for (int i = 0; i < mDownFileList.size(); i++) {
            switch (mDownFileList.get(i).getStatus()) {
                case WAITING:
                    mDownFileList.get(i).setStatus(NON_WIFI_PAUSED);
                    break;
                case DOWNLOADING:
                    mDownFileList.get(i).setStatus(NON_WIFI_PAUSED);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 读取数据库中记载的等待下载的记录并开始下载任务
     */
    private synchronized void startDownFileWithDB() {
        if (isDownning.get()) return;
        if (mDownFileList == null || mDownFileList.isEmpty()) return;
        TLog.d("check down task and start downning!");
        //下载文件顺序为，上次记录的正在下载文件未完成优先下载，等待下载的文件按添加先后顺序下载
        boolean hasDownFile = false;
        mCurrentDownFileBean = null;
        for (int i = 0; i < mDownFileList.size(); i++) {
            if (mDownFileList.get(i).getStatus() == DOWNLOADING) {
                mCurrentDownFileBean = mDownFileList.get(i);
                hasDownFile = true;
                break;
            }
        }

        if (!hasDownFile) {
            for (int i = 0; i < mDownFileList.size(); i++) {
                if (mDownFileList.get(i).getStatus() == WAITING) {
                    mCurrentDownFileBean = mDownFileList.get(i);
                    break;
                }
            }
        }

        if (mCurrentDownFileBean != null) {
            mCurrentDBUrl = mCurrentDownUrl = mCurrentDownFileBean.getUrl();
            isDownning.set(true);
            iDownLoader.startDownFile(mCurrentDBUrl);
            mCurrentDownFileBean.setStatus(DOWNLOADING);
            ContentValues cv = new ContentValues();
            cv.put(DownFileDB.COLUMN_STATU, DOWNLOADING);
            sqlDB.update(cv, "url=?", new String[]{mCurrentDBUrl});
            TLog.d("downning url=%s", mCurrentDBUrl);
        } else {
            TLog.d("all down task onCompleted!");
        }

        upDownListData(mDownFileList);
    }

    /**
     * 释放下载模块资源
     *
     * @param context
     */
    private void releaseAll(Context context) {
        mHandler.removeCallbacksAndMessages(null);
        iDownLoader.releaseFileDownloader(context);
//        iDownLoader.stopListenerService(context);
    }

    public static void release(Context context) {
        //检测删除无效的下载文件
        getInstance().checkAndDeleteNonCache();
        //释放资源
        getInstance().releaseAll(context);
    }

    /**
     * 获取已提交缓存申请的缓存列表
     *
     * @return
     */
    public synchronized List<DownFileBean> checkAndgetDownFileBeanList() {
        TLog.d("get down file list start!");
        List<DownFileBean> downList = sqlDB.queryAll();
        for (int i = 0; i < downList.size(); i++) {
            DownFileBean downFileBean = iDiskFileCache.getDownFileInfo(downList.get(i).getUrl());
            if (downFileBean == null) {
                downFileBean = new DownFileBean();
                downFileBean.setPercent(0);
            }
            downList.get(i).setPercent(downFileBean.getPercent());
            downList.get(i).setLocalPath(downFileBean.getLocalPath());
            downList.get(i).setFilePath(downFileBean.getFilePath());
        }
        TLog.d("get down file list onCompleted!");
        return downList;
    }

    public List<DownFileBean> getDownList() {
        return mDownFileList;
    }

    public synchronized void pauseDownUrl(final String url) {
        TLog.d("pause down task, url = " + url);
        isDownning.set(false);
        for (int i = 0; i < mDownFileList.size(); i++) {
            if (mDownFileList.get(i).getUrl().equals(url)) {
                mDownFileList.get(i).setStatus(PAUSED);
                break;
            }
        }

        upDownListData(mDownFileList);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                cv.put(DownFileDB.COLUMN_STATU, PAUSED);
                sqlDB.update(cv, "url=?", new String[]{url});
                iDownLoader.pauseAll();
                startDownFileWithDB();
            }
        });
    }

    public void startDownUrl(final String url) {
        TLog.d("start down task, url = " + url);
        for (int i = 0; i < mDownFileList.size(); i++) {
            if (mDownFileList.get(i).getUrl().equals(url)) {
                mDownFileList.get(i).setStatus(WAITING);
                break;
            }
        }

        upDownListData(mDownFileList);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                cv.put(DownFileDB.COLUMN_STATU, WAITING);
                sqlDB.update(cv, "url=?", new String[]{url});
                startDownFileWithDB();
            }
        });

    }

    public void addDownUrl(final String url, final String name, final String imgurl) {
        TLog.d("add new task , url = " + url);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (!sqlDB.findByUrl(url)) {
                    ContentValues value = new ContentValues();
                    value.put(DownFileDB.COLUMN_URL, url);
                    value.put(DownFileDB.COLUMN_NAME, name);
                    value.put(DownFileDB.COLUMN_TIME, System.currentTimeMillis() / 1000);
                    value.put(DownFileDB.COLUMN_IMGURL, imgurl);
                    value.put(DownFileDB.COLUMN_STATU, WAITING);
                    sqlDB.insert(value);
                    DownFileBean downFileBean = new DownFileBean();
                    downFileBean.setUrl(url);
                    downFileBean.setName(name);
                    downFileBean.setImgUrl(imgurl);
                    downFileBean.setStatus(WAITING);
                    downFileBean.setTsNum(0);
                    mDownFileList.add(downFileBean);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            Tips.showShortToast("添加缓存队列成功！");
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            Tips.showShortToast("该视频已在缓存队列！");
                        }
                    });
                }

                TLog.d("url = %s waiting down!", url);
                startDownFileWithDB();
            }
        });
    }

    public synchronized void delDownUrl(final String url) {
        TLog.d("del down task , url = " + url);
        for (int i = 0; i < mDownFileList.size(); i++) {
            if (mDownFileList.get(i).getUrl().equals(url)) {
                mDownFileList.remove(mDownFileList.get(i));
                upDownListData(mDownFileList);
                break;
            }
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                iDownLoader.delDownFile(url);
                sqlDB.delete(url);
                if (url.equals(mCurrentDBUrl)) {
                    isDownning.set(false);
                    startDownFileWithDB();
                }
            }
        });
    }


    private List<IDownfileStatuListener> downLoadListeners;

    public void addDownfileStatusListener(IDownfileStatuListener iDownfileStatuListener) {
        if (downLoadListeners == null) {
            downLoadListeners = new ArrayList<>();
        }
        downLoadListeners.add(iDownfileStatuListener);
    }

    public void removeDownfileStatusListener(IDownfileStatuListener iDownfileStatuListener) {
        if (downLoadListeners != null) {
            downLoadListeners.remove(iDownfileStatuListener);
        }
    }


    private List<IDownListener> downListeners;

    public void addDownListener(final IDownListener iDownListener) {
        if (downListeners == null) {
            downListeners = new ArrayList<>();
        }
        downListeners.add(iDownListener);
        if (mDownFileList != null) {
            iDownListener.upListData(mDownFileList);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    List<DownFileBean> list = checkAndgetDownFileBeanList();
                    if (mDownFileList == null) {
                        mDownFileList = new ArrayList<DownFileBean>();
                    } else {
                        mDownFileList.clear();
                    }
                    mDownFileList.addAll(list);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            iDownListener.upListData(mDownFileList);
                        }
                    });
                }
            });
        }
    }

    public void removeDownListener(IDownListener iDownListener) {
        if (downListeners != null) {
            downListeners.remove(iDownListener);
        }
    }

    /**
     * 校验并清除无用的本地缓存
     * 最好在线程中调用
     */
    public void checkAndDeleteNonCacheOnThread() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                checkAndDeleteNonCache();
            }
        });

    }

    /**
     * 校验并清除无用的本地缓存
     * 在线程中调用
     */
    public void checkAndDeleteNonCache() {
        try {
            List<DownFileBean> list = checkAndgetDownFileBeanList();
            HashSet<String> filename = new HashSet<String>();

            for (int i = 0; i < list.size(); i++) {
                DownFileBean downFileBean = list.get(i);
                String localPath = downFileBean.getLocalPath();
                String savePath = downFileBean.getFilePath();
                if (localPath.equals(savePath)) {
                    filename.add(new File(localPath).getName());
                } else {
                    filename.add(new File(savePath).getName());
                    filename.add(new File(localPath).getParentFile().getName());
                }
            }
            File file = new File(savePath);
            File listFile[] = file.listFiles();
            for (File f : listFile) {
                String name = f.getName();
                name = name.replace(".temp", "");
                if (!filename.contains(name)) {
                    TLog.d("删除无效的缓存文件:" + f.getName());
                    deleteFile(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TLog.e("删除无效的缓存文件发生致命错误！");
        }
    }

    private void upDownListData(final List list) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (downListeners != null) {
                    for (int i = 0; i < downListeners.size(); i++) {
                        downListeners.get(i).upListData(list);
                    }
                }
            }
        });
    }

    private void upDownListData(final List<DownFileBean> mDownFileList, final int speed) {
        // 发送通知或者广播
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (downListeners != null) {
                    for (int i = 0; i < downListeners.size(); i++) {
                        downListeners.get(i).downInfo(mDownFileList, speed);
                    }
                }
            }
        });
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    @Override
    public void onPrepared(DownFileBean downFileBean) {
        TLog.d("downFileBean" + downFileBean);
        if (downLoadListeners != null) {
            for (int i = 0; i < downLoadListeners.size(); i++) {
                downLoadListeners.get(i).onPrepared(downFileBean);
            }
        }
    }

    @Override
    public void onDowning(DownFileBean downFileBean, float downloadSpeed, int downPercent) {
        if (downFileBean == null) {
            TLog.d("下载统计进度时出现未知错误！");
            return;
        }
        String url = downFileBean.getUrl();
        if (url == null) {
            TLog.d("下载统计进度时出现未知错误！");
            return;
        }

        mCurrentDownFileBean.setPercent(downPercent);
        for (int i = 0; i < mDownFileList.size(); i++) {
            if (mDownFileList.get(i).getUrl().equals(url)) {
                mDownFileList.get(i).setPercent(downPercent);
                mDownFileList.get(i).setDownSpeed((int) downloadSpeed);
                break;
            }
        }


        upDownListData(mDownFileList, (int) downloadSpeed * 1000);

        if (downLoadListeners != null) {
            for (int i = 0; i < downLoadListeners.size(); i++) {
                downLoadListeners.get(i).onDowning(downFileBean, downloadSpeed, downPercent);
            }
        }
    }

    @Override
    public void onPasue(DownFileBean downFileBean) {
        TLog.d("downFileBean=" + downFileBean);
        upDownListData(mDownFileList);

        if (downLoadListeners != null) {
            for (int i = 0; i < downLoadListeners.size(); i++) {
                downLoadListeners.get(i).onPasue(downFileBean);
            }
        }

    }

    @Override
    public void onCompleted(DownFileBean downFileBean) {

        if (downFileBean == null) {
            TLog.d("下载完成时处理下载信息出现错误！");
            return;
        }
        String url = downFileBean.getUrl();
        if (url == null) {
            TLog.d("下载完成时处理下载信息出现错误！");
            return;
        }
        TLog.d("downloadFileInfo=%s", url);

        ContentValues cv = new ContentValues();
        cv.put(DownFileDB.COLUMN_STATU, COMPLETED);
        sqlDB.update(cv, "url=?", new String[]{mCurrentDBUrl});
        mCurrentDownFileBean.setStatus(COMPLETED);
        mCurrentDownFileBean.setPercent(100);
        mCurrentDownFileBean.setLocalPath(downFileBean.getFilePath());

        mCurrentDBUrl = null;
        isDownning.set(false);
        startDownFileWithDB();

        //自定义下载监听

        upDownListData(mDownFileList);

        //框架下载监听
        if (downLoadListeners != null) {
            for (int i = 0; i < downLoadListeners.size(); i++) {
                downLoadListeners.get(i).onCompleted(downFileBean);
            }
        }
    }

    @Override
    public void onFailed(String url, DownFileBean downFileBean, FlyDownError error) {
        TLog.d("onFailed,  error=" + error.toString());
        //下载失败延时10秒尝试重新下载
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isDownning.set(false);
                startDownFileWithDB();
                TLog.d(" retry down url=%s", mCurrentDownUrl);
            }
        });

        if (downLoadListeners != null) {
            for (int i = 0; i < downLoadListeners.size(); i++) {
                downLoadListeners.get(i).onFailed(url, downFileBean, error);
            }
        }
    }


}
