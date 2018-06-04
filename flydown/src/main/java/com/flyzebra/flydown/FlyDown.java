package com.flyzebra.flydown;

import com.flyzebra.flydown.request.IFileReQuest;
import com.flyzebra.flydown.request.SimpleFileReQuest;

import java.io.File;
import java.util.Hashtable;

/**
 * 功能说明：用户操作接口封装
 * 需求:
 * 1.进度提示
 * 2.检测存储空间是否足够
 *
 * @author 作者：FlyZebra
 * @version 创建时间：2017年2月27日 上午11:57:27
 */
public class FlyDown {
    private static String mCacheDir = "/sdcard/flyzebra";
    private static Hashtable<String, IFileReQuest> downQuests = new Hashtable<>();

    public static String getCacheDir() {
        return mCacheDir;
    }

    public static void setCacheDir(String path) {
        mCacheDir = path;
        File file = new File(mCacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static IFileReQuest load(String url) {
        setCacheDir(mCacheDir);
        IFileReQuest fileQequest;
        if (downQuests.get(url) != null) {
            fileQequest = new SimpleFileReQuest("exist");
        } else {
            fileQequest = new SimpleFileReQuest(url);
            downQuests.put(url, fileQequest);
        }
        return fileQequest;
    }

    public static void pause(String url) {
        IFileReQuest iFileReQuest = downQuests.get(url);
        if (iFileReQuest != null) {
            iFileReQuest.pause();
        }
        downQuests.remove(url);
    }

    public static void pauseAll() {
        for (Hashtable.Entry<String, IFileReQuest> table : downQuests.entrySet()) {
            table.getValue().pause();
        }
        downQuests.clear();
    }

    public static String getSavePath(String downUrl) {
        return getCacheDir() + File.separator + downUrl.substring(downUrl.lastIndexOf('/') + 1, downUrl.length());
    }

    public static String getTempSavePath(String downUrl) {
        return getCacheDir() + File.separator + "." + downUrl.substring(downUrl.lastIndexOf('/') + 1, downUrl.length()) + ".log";
    }

    public static void del(String url) {
        IFileReQuest iFileReQuest = downQuests.get(url);
        if (iFileReQuest != null) {
            iFileReQuest.delFile();
        }
        downQuests.remove(url);
    }
}
