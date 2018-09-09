package com.longhorn.dvrexplorer.module.wifi;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-4-上午11:16.
 */

public interface CommandType {

    /**
     * 握手信號，可用於測試通信
     * 0..3	uint8[4] TpVersion 传输协议版本号（用于通信测试）
     */
    String HANDSHAKE_CMD = "06000000000087654321";
    byte[] HANDSHAKE = {
            (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
            (byte) 0x87, (byte) 0x65, (byte) 0x43, (byte) 0x21
    };

    /**
     * 心跳包
     * 0	uint8	UIRequest	0-preview,1-files,2-config,3-playback
     * 1..3	uint8[3]	reserved
     * 4..5	uint16	Year	e.g.2016
     * 6..7	uint16	month	1~12
     * 8..9	uint16	day	1~31
     * 10..11	uint16	hour	0~23
     * 12..13	uint16	minute	0~59
     * 14..15	uint16	second	0~59
     */
    String HEARTBEAT_CMD = "020000000001";
    byte[] HEARTBEAT = {
            (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xDF, (byte) 0x07,//2015
            (byte) 0x06, (byte) 0x00,//6
            (byte) 0x01, (byte) 0x00,//1
            (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
    };


    String UI_REQ_PREVIEW_CMD = "020000000100";
    byte[] UI_REQ_PREVIEW = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00,
    };

    String UI_REQ_FILES_CMD = "020000000101";
    byte[] UI_REQ_FILES = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x01,
    };

    String UI_REQ_CONFIG_CMD = "020000000102";
    byte[] UI_REQ_CONFIG = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x02,
    };

    String UI_REQ_PLAYBACK_CMD = "020000000103";
    byte[] UI_REQ_PLAYBACK = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x03,
    };

    /**
     * 临时关闭循环录制
     */
    String FAST_CYCLE_RECORD_STOP_CMD = "03000000020000";
    byte[] FAST_CYCLE_RECORD_STOP = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x00,
            (byte) 0x00
    };

    /**
     * 临时开启循环录制
     */
    String FAST_CYCLE_RECORD_START_CMD = "03000000020001";
    byte[] FAST_CYCLE_RECORD_START = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x00,
            (byte) 0x01
    };

    /**
     * 紧急录像
     */
    String FAST_EMERGE_CMD = "020000000210";
    byte[] FAST_EMERGE = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x10
    };
    /**
     * 拍照
     */
    String FAST_PHOTOGRAPHY_CMD = "020000000211";
    byte[] FAST_PHOTOGRAPHY = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x11
    };

    /**
     *
     */
    String GET_FILE_NOR_CMD = "06000000100200000000";
    byte[] GET_FILE_NOR = {
            (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x10, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };

    /**
     *
     */
    String GET_FILE_EVT_CMD = "06000000100201010000";
    byte[] GET_FILE_EVT = {
            (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x10, (byte) 0x02,
            (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00
    };

    /**
     *
     */
    String GET_FILE_PHO_CMD = "06000000100202020000";
    byte[] GET_FILE_PHO = {
            (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x10, (byte) 0x02,
            (byte) 0x02, (byte) 0x02, (byte) 0x00, (byte) 0x00
    };


    /**
     *
     */
    String GET_FILE_LAST_PHO_CMD = "06000000100202020000";
    byte[] GET_FILE_LAST_PHO = {
            (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x10, (byte) 0x02,
            (byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x00
    };

    /**
     * 移动文件
     * 长度和后续字节需要动态生成
     */
    String REQ_FILE_MOVE_CMD = "060000001021";
    byte[] REQ_FILE_MOVE = {
            (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x10, (byte) 0x20,
    };

    /**
     * 删除文件
     * 长度和后续字节需要动态生成
     */
    String REQ_FILE_DELETE_CMD = "060000001021";
    byte[] REQ_FILE_DELETE = {
            (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x10, (byte) 0x21,
    };

    /**
     * 获取DVR设置状态，返回信息如下
     * 0	uint8	Resolution	0-1080P, 1-720P
     * 1	uint8	Duration	Cycle recording duration, uint minutes.
     * 2	uint8	AudioRecordEnable	0-disable, 1-enable.
     * 3	uint8	HdrEnable	0-disable, 1-enable.
     * 4	uint8	OsdEnable	0-disable, 1-enable.
     */
    String GET_RECORD_CFG_CMD = "020000001100";
    byte[] GET_RECORD_CFG = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x11, (byte) 0x00
    };

    /**
     * 获取WIFI设置信息，返回信息如下
     * 0..49	char[50]	SSID	name of this device
     * 50..97	char[50]	PSWD	password of wifi-connecting
     * 98	uint8	channel	2.4G: 0,1,6,11; 5G: 149, 157, 165
     * 99	uint8	mode	0: ap, 1: sta, 2: p2p
     */
    String GET_WIFI_CFG_CMD = "020000001101";
    byte[] GET_WIFI_CFG = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x11, (byte) 0x20
    };

    /**
     * 获取版本信息，返回信息如下
     * 0..9	char[10]	SocCVersion
     * 10..19	char[10]	McuVersion
     * 20..219	char[10]	HardwareVersion
     */
    String GET_VERSION_CMD = "020000001120";
    byte[] GET_VERSION = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x11, (byte) 0x20
    };


    /**
     * 获取重力感应设置信息
     * 0	uint8	Sensitive	3-High,2-medium,1-low,0-Gsensor Off
     */
    String GET_G_SENSOR_CFG_CMD = "020000001123";
    byte[] GET_G_SENSOR_CFG = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x11, (byte) 0x23
    };

    /**
     * 0	uint8	Switch	1-On,0-Off
     */
    String GET_PARKING_MODE_CFG_CMD = "020000001124";
    byte[] GET_PARKING_MODE_CFG = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x11, (byte) 0x24
    };

    /**
     * 设置分辨率
     * 后面加上分辨率参数
     * 0	uint8	Resolution	0-1080P, 1-720P
     */
    String SET_RESOLUTION_CMD = "030000001200";
    byte[] SET_RESOLUTION = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x00
    };

    /**
     * 设置录像时长
     * 后面加上参数
     * 0	uint8	Duration	Cycle recording duration, uint minutes.
     */
    String SET_DURATION_CMD = "030000001201";
    byte[] SET_DURATION = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x01
    };

    /**
     * 设置声音开关
     * 后面加上参数
     * 0	uint8	AudioRecordEnable	0-disable, 1-enable.
     */
    String SET_AUDIO_RECORD_CMD = "030000001202";
    byte[] SET_AUDIO_RECORD = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x02
    };

    /**
     * 设置wifi
     * 后面加上参数
     * 0..49	char[50]	SSID	name of this device 		0..3	uint32	[ERROR_CODE]	0-success,others-error
     * 50..97	char[50]	PSWD	password of wifi-connecting
     * 98	uint8	channel	2.4G: 0,1,6,11; 5G: 149, 157, 165
     * 99	uint8	mode	0: ap, 1: sta, 2: p2p
     */
    String SET_WIFI_CFG_CMD = "030000001205";
    byte[] SET_WIFI_CFG = {
            (byte) 0x65, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x05
    };

    /**
     * 执行SDCARD格式化
     * 0..3	uint32	[ERROR_CODE]	0-success,others-error(阻塞型报文，执行格式化后响应)
     */
    String SDCARD_FORMATTING_CMD = "020000001220";
    byte[] SDCARD_FORMATTING = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x20
    };

    /**
     * 恢复出厂设置
     * 0..3	uint32	[ERROR_CODE]	0-success,others-error
     */
    String FACTORY_RESET_CMD = "020000001221";
    byte[] FACTORY_RESET = {
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x21
    };


    /**
     * 设置重力感应
     * 0	uint8	Sensitive	3-High,2-medium,1-low,0-Gsensor Off
     */
    String SET_G_SENSOR_CFG_CMD = "020000001221";
    byte[] SET_G_SENSOR_CFG = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x23
    };


    /**
     * 设置......
     * 0	uint8	Switch	1-On,0-Off
     */
    String SET_PARKING_MODE_CFG_CMD = "020000001221";
    byte[] SET_PARKING_MODE_CFG = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x12, (byte) 0x24
    };

}
