// IDvrStateNotify.aidl
package com.hsae.autosdk.dvr;

import com.hsae.autosdk.dvr.IDvrTyListener;
import com.hsae.autosdk.dvr.IDvrHsListener;

interface IDvrStateNotify {
    /** state
         0x00 = initial status
         0x01 = general recording mode
         0x02 = stop recording mode
         0x03 = manually start  emergency video mode
         0x04 = auto start emergency video mode
         0x05 = power off status
         0x06 = system failure
         0x07 = invalid
    **/
    void notityWorkStatus(int state);

    /** state
         0x00: initial status
         0x01: In connection
         0x02: Connection Fail
         0x03: STA Mode, Connection Successful
         0x04: MAC Data error
         0x05: Password Data error
         0x06~0x07: invalid
        **/
    void notityLinkStatus(int state);

    /** state
        0x00 = initial status
        0x01 = Need to update DSP software
        0x02 = Need to update MCU software
        0x03 = Need to update DSP and MCU
    **/
    void notityUpdateNotify(int state);

    /**state
        0b = Not Respond
        1b = Photo taken
    **/
    void notityTakePhotoRespond(int state);

    /**state
      0000000b~1100100b = 0%~100%
      1100101b~1111100b = reserved
      1111101b = Upgrade Failed
      1111110b = Update Successful
      1111111b = initial status
    **/
    void notityUpdateSchedule(int state);

    /**state
        0x00 = SD insert
        0x01 = SD pull out
        0x02 = SD failure
        0x03 = SD full
        0x04 = emergency video file full
        0x05 = photo file full
        0x06 = emergency video file and photo file full
        0x07 = write protection
    **/
    void notitySDCardStatus(int state);


    /**
     * 请求DA发送MAC地址和Wifi热点密码给DVR设备，执行连接wifi的操作
    **/
    void notihsConnectWifi();

    /**
     * DVR APP点击确认升级后通知DA，DA向DVR设备发送执行升级操作的命令
     *   0x01 = update DSP software
     *   0x02 = update MCU software
     *   0x03 = update DSP and MCU
    **/
    void notihsUpdateEvent(int type);

    /**
     * 将获取的DVRIP地址转发给DA
    **/
    void notihsDvrIPAddress(String ipStr);

    /**
    * Dvr注册监听信息，接收自己所需的信息
    **/
    void registerDvrTyListener(IDvrTyListener listener);

    /**
    * Dvr注销监听信息
    **/
    void unregisterDvrTyListener(IDvrTyListener listener);

    /**
    * DA注册监听信息，接收自己所需的信息
    **/
    void registerDvrHsListener(IDvrHsListener listener);

    /**
    * DA注销监听信息
    **/
    void unregisterDvrHsListener(IDvrHsListener listener);

}
