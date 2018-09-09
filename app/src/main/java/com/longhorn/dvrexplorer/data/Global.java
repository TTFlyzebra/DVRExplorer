package com.longhorn.dvrexplorer.data;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-2-下午4:54.
 * 存储全局变量
 */

public class Global {
    //连接322设为true,连接311设为false
    public static final int DVR_TYPE = DvrType.DVR_311;

    public static String DVR_IP = "192.168.42.1";
    public static int WEB_PORT = 8080;
    public static int CMD_PORT = 7878;
    public static String PHO_322 = "/DCIM/PHO/PHO_";
    public static String NOR_322 = "/DCIM/NOR/NOR_";
    public static String EVT_322 = "/DCIM/EVT/EVT_";
    public static String DOWN_MP4_322 = "A.MP4";
    public static String PLAY_MP4_322 = "B.MP4";
    public static String PHO_311 = "/DCIM/PHO/";
    public static String NOR_311 = "/DCIM/NOR/";
    public static String EVT_311 = "/DCIM/EVT/";
    public static String DOWN_MP4_311 = ".MP4";
    public static String PLAY_MP4_311 = ".MP4";

    public static String getDvrRtsp(){
        return "rtsp://"+DVR_IP+"/live";
    }

    public static String getDvrWeb(){
        return "http://"+DVR_IP+":"+WEB_PORT;
    }

    public static String getPathPho(){
        return getDvrWeb()+(DVR_TYPE == DvrType.DVR_322 ?PHO_322:PHO_311);
    }

    public static String getPathEvt(){
        return getDvrWeb()+(DVR_TYPE == DvrType.DVR_322  ?EVT_322:EVT_311);
    }

    public static String getPathNor(){
        return getDvrWeb()+(DVR_TYPE == DvrType.DVR_322  ?NOR_322:NOR_311);
    }
}
