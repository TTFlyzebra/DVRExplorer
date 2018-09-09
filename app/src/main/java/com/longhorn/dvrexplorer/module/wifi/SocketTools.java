package com.longhorn.dvrexplorer.module.wifi;


import android.os.Handler;
import android.os.Looper;

import com.longhorn.dvrexplorer.data.Global;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.zip.CRC32;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-2-上午8:25.
 */

public class SocketTools {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private byte[] recvBfu = new byte[1024 * 1024];
    private static final int TIME_OUT = 10000;

    private static class SocketToolsHolder {
        public static final SocketTools sInstance = new SocketTools();
    }

    public static SocketTools getInstance() {
        return SocketToolsHolder.sInstance;
    }

    public void sendCommand(final byte[] command, final SocketResult socketResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                DataOutputStream dos = null;
                DataInputStream dis = null;
                try {
                    SocketAddress socAddress = new InetSocketAddress(Global.DVR_IP, Global.CMD_PORT);
                    FlyLog.d("socket ip=%s,port=%d",Global.DVR_IP,Global.CMD_PORT);
                    //设置10秒之后即认为是超时
                    socket = new Socket();
                    socket.connect(socAddress, TIME_OUT);
                    socket.setSoTimeout(TIME_OUT);
                    dos = new DataOutputStream(socket.getOutputStream());

                    CRC32 crc32 = new CRC32();
                    crc32.update(command);
                    long lcrc32 = crc32.getValue();

                    byte[] wr = new byte[command.length + 6];
                    wr[0] = (byte) 0xEE;
                    wr[1] = (byte) 0xAA;
                    for (int i = 2; i < wr.length; i++) {
                        if (i >= (command.length + 2)) {
                            wr[i] = (byte) ((lcrc32 >> ((3 - (i - command.length - 2)) * 8)) & 0xFF);
                        } else {
                            wr[i] = command[i - 2];
                        }
                    }

                    dos.write(wr, 0, wr.length);
                    FlyLog.d("sendCommand send data is: %s", ByteTools.bytes2HexString(wr));
                    dis = new DataInputStream(socket.getInputStream());
                    int recvLen = dis.read(recvBfu);
                    int size = ByteTools.bytes2Int(recvBfu, 2) + 10;
                    byte[] data = new byte[size];
                    //TODO:验证字符串合法性
                    System.arraycopy(recvBfu, 0, data, 0, recvLen);
                    while (recvLen < size) {
                        int len = dis.read(recvBfu);
                        System.arraycopy(recvBfu, 0, data, recvLen, len);
                        recvLen += len;
                        FlyLog.d("recv size=%d",len);
                    }
                    final ResultData resultData = new ResultData(recvLen, data, "success");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            socketResult.result(resultData);
                        }
                    });
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            socketResult.result(new ResultData(-1, null, e.toString()));
                        }
                    });
                    FlyLog.e(e.toString());
                } finally {
                    try {
                        if (dis != null) {
                            dis.close();
                        }
                        if (dos != null) {
                            dos.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
