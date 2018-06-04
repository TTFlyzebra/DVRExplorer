package com.longhorn.dvrexplorer.data;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-2-下午4:54.
 * 存储全局变量
 */

public class Global {
    public static String DVR_IP = "192.168.173.101";
    public static int WEB_PORT = 8080;
    public static int CMD_PORT = 7878;
//    public static String DVR_RTSP = "rtsp://"+DVR_IP+"/live";
//    public static String DVR_WEB = "http://"+DVR_IP+":"+WEB_PORT;
    public static String PHO = "/DCIM/PHO/PHO_";
    public static String NOR = "/DCIM/NOR/NOR_";
    public static String EVT = "/DCIM/EVT/EVT_";
    public static String DOWN_MP4 = "A.MP4";
    public static String PLAY_MP4 = "B.MP4";
//    public static String PHO = "/DCIM/PHO/";
//    public static String NOR = "/DCIM/NOR/";
//    public static String EVT = "/DCIM/EVT/";
//    public static String DOWN_MP4 = ".MP4";
//    public static String PLAY_MP4 = ".MP4";

    public static String getDvrRtsp(){
        return "rtsp://"+DVR_IP+"/live";
    }

    public static String getDvrWeb(){
        return "http://"+DVR_IP+":"+WEB_PORT;
    }

    public static String getPathPho(){
        return getDvrWeb()+PHO;
    }

    public static String getPathEvt(){
        return getDvrWeb()+EVT;
    }

    public static String getPathNor(){
        return getDvrWeb()+NOR;
    }
}
