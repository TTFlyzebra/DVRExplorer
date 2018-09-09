package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flyzebra.live555.rtsp.RtspVideoView;
import com.longhorn.dvrexplorer.DVRActivity;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.data.Global;
import com.longhorn.dvrexplorer.module.wifi.CommandType;
import com.longhorn.dvrexplorer.module.wifi.DataParse;
import com.longhorn.dvrexplorer.module.wifi.NioSocketTools;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;
import com.longhorn.dvrexplorer.view.FlyDialog;

import java.util.ArrayList;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class RtspFragment extends Fragment implements SocketResult {
    private RtspVideoView rtspVideoView;
    private Button rtsp_evt, rtsp_pho;
    private TextView rtsp_tvinfo;
    private ProgressBar rtsp_loading;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isStop = false;
    private NioSocketTools mNioSocketTools = NioSocketTools.getInstance();
    private int tackPhotoIndex;
    private static boolean isEvtRecord = false;

    public RtspFragment() {
    }

    public static RtspFragment newInstance() {
        Bundle args = new Bundle();
        RtspFragment fragment = new RtspFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rtsp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rtspVideoView = view.findViewById(R.id.fm_rtspview_01);
        rtsp_evt = view.findViewById(R.id.rtsp_evt);
        rtsp_pho = view.findViewById(R.id.rtsp_pho);
        rtsp_loading = view.findViewById(R.id.rtsp_loading);
        rtsp_tvinfo = view.findViewById(R.id.rtsp_tvinfo);
        rtsp_pho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                tackPhoto();
            }
        });

        rtsp_evt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mNioSocketTools.sendCommand(CommandType.FAST_EMERGE);
            }
        });
    }


    private void tackPhoto() {
        rtsp_pho.setEnabled(false);
        mNioSocketTools.sendCommand(CommandType.FAST_PHOTOGRAPHY);
    }

    @Override
    public void onStart() {
        super.onStart();
        rtsp_tvinfo.setText("");
        upEvtButton();
        mNioSocketTools.registerSocketResult(this);
        isStop = false;
//        setDvrRtspIPTask();
        showRtspView();
    }

    @Override
    public void onStop() {
        mNioSocketTools.unregisterSocketResult(this);
        isStop = true;
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    private void showRtspView() {
        rtsp_loading.setVisibility(View.GONE);
        rtspVideoView.setVisibility(View.GONE);
        rtspVideoView.setRtspUrl(Global.getDvrRtsp());
        rtspVideoView.setVisibility(View.VISIBLE);
    }

    public void showLastPhoto() {
        //TODO:拍照成功后服务器不能立即正确返回图片地址
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNioSocketTools.sendCommand(CommandType.GET_FILE_PHO);
            }
        }, 200);
    }

    private void showTackFaileDialog() {
        FlyDialog.Builder dialog = new FlyDialog.Builder(getActivity());
        dialog.setTitle(getString(R.string.alert1))//设置对话框的标题
                .setMessage(getString(R.string.show_pho_fail_dialog))//设置对话框的内容
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLastPhoto();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void result(ResultData msg) {
        if (isStop) return;
        try {
            byte recv[] = msg.getBytes();
            int command = ByteTools.bytes2ShortInt2(recv, 0);
            int[] pos = {0};
            switch (command) {
                //HEARTBEAT
                case 0x0010:
                    isEvtRecord = (recv[4] == 0x01 || recv[4] == 0x02);
                    upEvtButton();
                    String recod = (recv[3] == 0x02 || recv[3] == 0x01) ? "视频录制：ON\n" : "视频录制：OFF\n";
                    String sound = (recv[5] == 0x02 || recv[5] == 0x01) ? "声音录制：ON\n" : "声音录制：OFF\n";
//                    String pix = (recv[7] == 0x00) ? "分辨率：1080P\n" : "分辨率：720P\n";
                    String evt = (recv[4] == 0x02 || recv[4] == 0x01) ? "正在紧急录像.....\n" : "";
                    rtsp_tvinfo.setText(Html.fromHtml("<font color=green>" + recod +"<br>"
                            + sound +"<br></font><font color=red>"
                            + evt + "</font>"));
                    break;
                //拍照FAST_PHOTOGRAPHY
                case 0x0211:
                    FlyLog.d("length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    rtsp_pho.setEnabled(true);
                    tackPhotoIndex = ByteTools.bytes2Int(recv, 6);
                    showLastPhoto();
                    break;
                //获取文件列表GET_FILE_PHO
                case 0x1002:
                    if (tackPhotoIndex == 0) return;
                    FlyLog.d("length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    ArrayList<DvrFile> list = new ArrayList<>();
                    pos[0] += 2;
                    int sumItem = ByteTools.bytes2Int(recv, pos[0]);
                    pos[0] += 4;
                    for (int i = 0; i < sumItem; i++) {
                        DvrFile dvrFile = DataParse.getDvrFile(recv, pos);
                        if (dvrFile.index == tackPhotoIndex) {
                            list.add(dvrFile);
                            tackPhotoIndex = 0;
                            break;
                        }
                    }
                    if (list.size() > 0) {
                        FileFragment_VIEW fragment = FileFragment_VIEW.newInstance(list, list.size() - 1);
                        ((DVRActivity) getActivity()).addFragment(RtspFragment.this, fragment);
                    } else {
                        showTackFaileDialog();
                    }
                    break;
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    private void upEvtButton() {
        rtsp_evt.setEnabled(!isEvtRecord);
        rtsp_evt.setText(isEvtRecord ? getString(R.string.evt_button2) : getString(R.string.evt_button1));
    }
}
