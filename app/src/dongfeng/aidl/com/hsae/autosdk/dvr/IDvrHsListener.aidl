package com.hsae.autosdk.dvr;

/**
 * Created by FlyZebra on 2018/6/6.
 * Descrip:
 */

interface IDvrHsListener {
    /**
        * 请求DA发送MAC地址和Wifi热点密码给DVR设备，执行连接wifi的操作
        **/
        void notihsConnectWifi();

        /**
        *DVR APP点击确认升级后通知DA，DA向DVR设备发送执行升级操作的命令
        *   0x01 = update DSP software
        *   0x02 = update MCU software
        *   0x03 = update DSP and MCU
        **/
        void notihsUpdateEvent(int type);

        /**
        *将获取的DVRIP地址转发给DA
        **/
        void notihsDvrIPAddress(String ipStr);
}
