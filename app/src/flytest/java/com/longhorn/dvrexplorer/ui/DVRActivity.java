package com.longhorn.dvrexplorer.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.module.wifi.CommandType;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.module.wifi.SocketTools;
import com.longhorn.dvrexplorer.fragment.PhotoFragment;
import com.longhorn.dvrexplorer.fragment.RtspViewFragment;
import com.longhorn.dvrexplorer.fragment.VideoNorFragment;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;



public class DVRActivity extends Activity implements CommandType{
    private Button bt01, bt02, bt03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dvr);
        bt01 = findViewById(R.id.ac_dvr_bt01);
        bt02 = findViewById(R.id.ac_dvr_bt02);
        bt03 = findViewById(R.id.ac_dvr_bt03);
        onRtspView(bt01);
    }

    public void onExit(View view) {
        finish();
    }

    public void onTest(final View view) {
        view.setEnabled(false);
        SocketTools.getInstance().sendCommand(GET_FILE_NOR, new SocketResult() {
            @Override
            public void result(ResultData msg) {
                if (msg.getMark() > 0) {
                    byte[] data = msg.getBytes();
                    FlyLog.d("sendCommand recv data is: %s", ByteTools.bytes2HexString(data));
                    int sum =  ByteTools.bytes2Int(data,8);
                    FlyLog.d("file sum = %d",sum);
                    view.setEnabled(true);
                }
            }
        });
    }

    public void onPhoto(final View view) {
        view.setEnabled(false);
        SocketTools.getInstance().sendCommand(FAST_PHOTOGRAPHY, new SocketResult() {
            @Override
            public void result(ResultData msg) {
                if (msg.getMark() > 0) {
                    byte[] data = msg.getBytes();
                    FlyLog.d("sendCommand recv data is: %s", ByteTools.bytes2HexString(data));
                    Toast.makeText(DVRActivity.this,"拍照成功",Toast.LENGTH_LONG).show();
                    view.setEnabled(true);
                }
            }
        });
    }

    public void onRtspView(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        RtspViewFragment rtspViewFragment = new RtspViewFragment();
        ft.replace(R.id.dvr_fm_fl01, rtspViewFragment);
        ft.commit();
    }

    public void onBrowseVideo(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        VideoNorFragment videoNorFragment = new VideoNorFragment();
        ft.replace(R.id.dvr_fm_fl01, videoNorFragment);
        ft.commit();
    }


    public void onBrowsePhoto(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PhotoFragment photoFragment = new PhotoFragment();
        ft.replace(R.id.dvr_fm_fl01, photoFragment);
        ft.commit();
    }

}
