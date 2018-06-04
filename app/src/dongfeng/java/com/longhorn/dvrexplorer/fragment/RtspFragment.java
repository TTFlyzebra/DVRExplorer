package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flyzebra.live555.rtsp.RtspVideoView;
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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
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
        rtspVideoView.setRtspUrl(Global.getDvrRtsp());
    }

    @Override
    public void onStop() {
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void result(ResultData msg) {

    }
}
