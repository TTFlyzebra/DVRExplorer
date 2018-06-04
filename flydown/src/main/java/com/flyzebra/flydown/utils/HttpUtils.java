package com.flyzebra.flydown.utils;

import com.flyzebra.flydown.DownInfo;
import com.flyzebra.flydown.FlyDownError;
import com.flyzebra.flydown.request.IFileBlockReQuestListener;

import java.net.HttpURLConnection;
import java.net.URL;

/** 
* 功能说明：
* @author 作者：FlyZebra 
* @version 创建时间：2017年3月8日 下午5:07:03  
*/
public class HttpUtils {
	/**
	 * 检测downUrl是否支持断点续传，网络请求，尽量在线程中调用
	 * @param downUrl
	 * @return
	 */
	public static long getLength(String downUrl, IFileBlockReQuestListener iFileBlockReQuestListener){
		TLog.d("获取文件长度......%s",downUrl);
		URL url;
		HttpURLConnection con = null;
		long fileLength = 0;
		try {
			url = new URL(downUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setChunkedStreamingMode(0);
			con.setRequestProperty("RANGE", "bytes=0-");

			fileLength = con.getContentLength();
			if (con.getResponseCode() != 206) {
				fileLength = -1;
			}
		} catch (Exception e) {
			TLog.d(e.toString());
			e.printStackTrace();
			if(iFileBlockReQuestListener!=null){
				iFileBlockReQuestListener.onFailed(null, new FlyDownError(DownInfo.NETWORK_ERROR,"网络下载失败",e));
			}
		} finally {
			if(con!=null){
				con.disconnect();
			}
		}
		TLog.d("获取文件长度完成，文件长度为%d",fileLength);
		return fileLength;
	}
}
