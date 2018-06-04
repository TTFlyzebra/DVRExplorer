package com.longhorn.dvrexplorer.http;

import java.util.Map;

/**
 * Author: FlyZebra
 * Created by FlyZebra on 2018/3/29-下午1:24.
 */
public interface IHttp {

    /**
     * 下载文件指定位置的内容，以byte数组方式返回
     * @param url http请求地址
     * @param start 起始位置
     * @param end 结束位置
     * @param tag 请求标记
     * @param result 请求回调
     */
    void getBytes(String url, long start, long end, final Object tag, final HttpResult result);
    /**
     * 下载文件指定位置的内容，以byte数组方式返回
     * NOTO:网络请求，阻塞调用，需在线程中调用该方法
     * @param url http请求地址
     * @param start 起始位置
     * @param end 结束位置
     * @param tag 请求标记
     * @return byte数组
     */
    byte[] getBytes(String url, long start, long end, final Object tag);

    /**
     * Get方式发送HTTP请求
     * @param url http请求地址
     * @param tag 请求标记
     * @param result 请求回调
     */
    void getString(String url, Object tag, HttpResult result);

    /**
     * Post方式发送请求参数
     * @param url http请求地址
     * @param map 请求参数
     * @param tag 请求标记
     * @param result 请求回调
     */
    void postString(String url, Map<String, String> map, Object tag, HttpResult result);

    /**
     * Post方式发送json字符串
     * @param url http请求地址
     * @param json 要发送的json字符串
     * @param tag 请求标记
     * @param result 请求回调
     */
    void postJson(String url, String json, Object tag, HttpResult result);

    /**
     * 读取本地缓存
     * @param url http请求地址
     * @return 缓存的http请求结果
     */
    String readDiskCache(String url);

    /**
     * 取消一个http请求
     * @param tag 请求标记
     */
    void cancelAll(Object tag);

    /**
     * 请求回调接口
     */
    interface HttpResult {
        void succeed(Object object);
        void failed(Object object);
    }

}
