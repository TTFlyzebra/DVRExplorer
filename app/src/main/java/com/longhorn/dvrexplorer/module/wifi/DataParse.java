package com.longhorn.dvrexplorer.module.wifi;

import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.util.List;

/**
 * Created by FlyZebra on 2018/5/25.
 * Descrip:
 */

public class DataParse {
    private static byte[] buf = new byte[1024 * 4];

    /**
     * 解析数据获取文件列表
     *
     * @param data
     * @param start
     * @return
     */
    public static DvrFile getDvrFile(byte[] data, int[] start) {
        DvrFile dvrFile = new DvrFile();
        dvrFile.index = ByteTools.bytes2Int(data, start[0]);
        start[0] += 4;
        dvrFile.path = data[start[0]];
        start[0]++;
        dvrFile.type = data[start[0]];
        start[0]++;
        dvrFile.suffix = data[start[0]];
        start[0]++;
        dvrFile.reserved = data[start[0]];
        start[0]++;
        dvrFile.size = ByteTools.bytes2Int(data, start[0]);
        start[0] += 4;
        dvrFile.date = ByteTools.bytes2Int(data, start[0]);
        start[0] += 4;
        dvrFile.offset = ByteTools.bytes2Int(data, start[0]);
        start[0] += 4;
        FlyLog.d(dvrFile.toString());
        return dvrFile;
    }

    /**
     * 获取删除文件的命令字符串
     *
     * @param mList
     * @param len
     * @return
     */
    public static byte[] getDelCommandBytes(List<DvrFile> mList, int len[]) {
        len[0] = 6;
        for (DvrFile dvrFile : mList) {
            if (dvrFile.isSelect) {
                FlyLog.d("del index = %s", ByteTools.bytes2HexString(ByteTools.intToBytes(dvrFile.index)));
                ByteTools.intToBytes(dvrFile.index, buf, len[0]);
                len[0] += 4;
            }
        }
        ByteTools.intToBytes(len[0] - 4, buf, 0);
        System.arraycopy(CommandType.REQ_FILE_DELETE, 4, buf, 4, 2);
        return buf;
    }

    public static byte[] getDelCommandBytes(DvrFile dvrFile, int len[]) {
        len[0] = 6;
        FlyLog.d("del index = %s", ByteTools.bytes2HexString(ByteTools.intToBytes(dvrFile.index)));
        ByteTools.intToBytes(dvrFile.index, buf, len[0]);
        len[0] += 4;
        ByteTools.intToBytes(len[0] - 4, buf, 0);
        System.arraycopy(CommandType.REQ_FILE_DELETE, 4, buf, 4, 2);
        return buf;
    }
}
