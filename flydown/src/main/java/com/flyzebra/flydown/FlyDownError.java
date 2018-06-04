package com.flyzebra.flydown;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-8-上午11:09.
 */

public class FlyDownError {
    private int code;
    private String msg;
    private Object data;

    public FlyDownError(int code, String msg, Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FlyDownError{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
