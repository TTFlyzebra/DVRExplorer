package com.longhorn.dvrexplorer.module.wifi;

import java.util.Arrays;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-4-上午11:15.
 */

public class ResultData {
    int mark = -1;
    byte[] bytes;
    String msg;

    public ResultData(int type, byte[] bytes, String msg) {
        this.mark = type;
        this.bytes = bytes;
        this.msg = msg;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "mark=" + mark +
                ", bytes=" + Arrays.toString(bytes) +
                ", msg='" + msg + '\'' +
                '}';
    }
}
