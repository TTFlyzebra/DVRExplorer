package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flyzebra.live555.rtsp.RtspVideoView;
import com.longhorn.dvrexplorer.DVRActivity;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.data.Global;
import com.longhorn.dvrexplorer.module.wifi.CommandType;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.module.wifi.SocketTools;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class RtspFragment extends Fragment implements SocketResult{
    private RtspVideoView rtspVideoView;
    private Button rtsp_evt,rtsp_pho;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final Executor executor = Executors.newFixedThreadPool(1);
    private boolean isStop = false;
    public RtspFragment(){
    }

    public static RtspFragment newInstance() {
        Bundle args = new Bundle();
        RtspFragment fragment = new RtspFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_rtsp,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rtspVideoView = view.findViewById(R.id.fm_rtspview_01);
        rtsp_evt = view.findViewById(R.id.rtsp_evt);
        rtsp_pho = view.findViewById(R.id.rtsp_pho);

        rtsp_pho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    ((DVRActivity)getActivity()).iDvrStateNotify.notityTakePhotoRespond(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                v.setEnabled(false);
                SocketTools.getInstance().sendCommand(CommandType.FAST_PHOTOGRAPHY, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {
                        try {
                            v.setEnabled(true);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        rtsp_evt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                SocketTools.getInstance().sendCommand(CommandType.FAST_EMERGE, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {
                        if(msg.getMark()>1){
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        v.setEnabled(true);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }, 35000);
                        }else{
                            try {
                                v.setEnabled(true);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        isStop = false;
        setDvrRtspIPTask();
    }

    @Override
    public void onStop() {
        isStop = true;
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void result(ResultData msg) {

    }

    public void setDvrRtspIPTask() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket(50003);  //定义服务，监视端口上面的发送端口，注意不是send本身端口
                    byte[] buf = new byte[1024];//接受内容的大小，注意不要溢出
                    DatagramPacket dp = new DatagramPacket(buf, 0, buf.length);//定义一个接收的包
                    ds.receive(dp);//将接受内容封装到包中
                    byte recv[] = dp.getData();
                    int len = recv.length;
                    for (int i = 0; i < recv.length; i++) {
                        if (recv[i] == 0x00) {
                            len = i;
                            break;
                        }
                    }
                    byte ipbytes[] = new byte[len];
                    System.arraycopy(recv, 0, ipbytes, 0, len);
                    Global.DVR_IP = new String(ipbytes, "utf-8");
                    FlyLog.d("recv string:%s", Global.DVR_IP);
                    StringBuilder sb = new StringBuilder("");
                    for (byte aByte : ipbytes) {
                        String hv = Integer.toHexString(aByte & 0xFF);
                        if (hv.length() < 2) {
                            sb.append(0);
                        }
                        sb.append(hv);
                        sb.append(":");
                    }
                    if (sb.length() > 1) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    FlyLog.d("recv bytes:%s", sb.toString());
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    FlyLog.e(e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    FlyLog.e(e.toString());
                } finally {
                    if (ds != null) ds.close();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isStop) {
                            rtspVideoView.setVisibility(View.GONE);
                            rtspVideoView.setRtspUrl(Global.getDvrRtsp());
                            rtspVideoView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}
