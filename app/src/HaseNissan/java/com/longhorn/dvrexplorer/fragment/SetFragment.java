package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.module.wifi.CommandType;
import com.longhorn.dvrexplorer.module.wifi.NioSocketTools;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;
import com.longhorn.dvrexplorer.view.FlyDialog;

import java.nio.ByteBuffer;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class SetFragment extends Fragment implements CommandType, SocketResult, View.OnClickListener {
    private Switch set_sw_record, set_sw_sound, set_sw_tcjk, set_sw_lmd;
    private RadioGroup set_rg_lmd, set_rg_time, set_rg_pix;
    private CheckBox set_info_time, set_info_car;
    private RadioButton set_lmd_low, set_lmd_medium, set_lmd_high, set_time_1min, set_time_3min, set_time_5min,set_pix_1080,set_pix_720;
    private Button set_factory, set_format;
    private TextView set_tv_version;
    private ProgressDialog progressDialog;
    private NioSocketTools mNioSocketTools = NioSocketTools.getInstance();
    private boolean isStop = false;

    private boolean isUpView = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TextView set_video_set,set_system_set;
    private RelativeLayout set_rl_video,set_rl_system;

    public SetFragment() {
    }

    public static SetFragment newInstance() {
        Bundle args = new Bundle();
        SetFragment fragment = new SetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        set_sw_record = view.findViewById(R.id.set_sw_record);
        set_sw_sound = view.findViewById(R.id.set_sw_sound);
        set_sw_tcjk = view.findViewById(R.id.set_sw_tcjk);
        set_sw_lmd = view.findViewById(R.id.set_sw_lmd);

        set_rg_pix = view.findViewById(R.id.set_rg_pix);
        set_rg_time = view.findViewById(R.id.set_rg_time);
        set_rg_lmd = view.findViewById(R.id.set_rg_lmd);

        set_info_time = view.findViewById(R.id.set_info_time);
        set_info_car = view.findViewById(R.id.set_info_car);

        set_lmd_low = view.findViewById(R.id.set_lmd_low);
        set_lmd_medium = view.findViewById(R.id.set_lmd_medium);
        set_lmd_high = view.findViewById(R.id.set_lmd_high);
        set_time_1min = view.findViewById(R.id.set_time_1min);
        set_time_3min = view.findViewById(R.id.set_time_3min);
        set_time_5min = view.findViewById(R.id.set_time_5min);
        set_pix_720 = view.findViewById(R.id.set_pix_720);
        set_pix_1080 = view.findViewById(R.id.set_pix_1080);
        set_tv_version = view.findViewById(R.id.set_tv_version);

        set_factory = view.findViewById(R.id.set_factory);
        set_format = view.findViewById(R.id.set_format);

        set_video_set = view.findViewById(R.id.set_video_set);
        set_system_set = view.findViewById(R.id.set_system_set);
        set_rl_video = view.findViewById(R.id.set_rl_video);
        set_rl_system = view.findViewById(R.id.set_rl_system);

        set_factory.setOnClickListener(this);
        set_format.setOnClickListener(this);

        set_sw_record.setOnClickListener(this);
        set_sw_lmd.setOnClickListener(this);
        set_sw_sound.setOnClickListener(this);
        set_sw_tcjk.setOnClickListener(this);

        set_time_1min.setOnClickListener(this);
        set_time_3min.setOnClickListener(this);
        set_time_5min.setOnClickListener(this);
        set_pix_720.setOnClickListener(this);
        set_pix_1080.setOnClickListener(this);
        set_lmd_low.setOnClickListener(this);
        set_lmd_medium.setOnClickListener(this);
        set_lmd_high.setOnClickListener(this);

        set_video_set.setOnClickListener(this);
        set_system_set.setOnClickListener(this);


        set_sw_record.setEnabled(false);
        set_sw_lmd.setEnabled(false);
        set_sw_sound.setEnabled(false);
        set_sw_tcjk.setEnabled(false);
        set_time_1min.setEnabled(false);
        set_time_3min.setEnabled(false);
        set_time_5min.setEnabled(false);
        set_pix_720.setEnabled(false);
        set_pix_1080.setEnabled(false);
        set_lmd_low.setEnabled(false);
        set_lmd_medium.setEnabled(false);
        set_lmd_high.setEnabled(false);
        String sb = "软件版本       V0.0";
        set_tv_version.setText(sb);

        progressDialog = new ProgressDialog(getActivity(), R.style.setProgressDialog);

        set_video_set.setEnabled(false);
        set_system_set.setEnabled(true);
        set_rl_video.setVisibility(View.VISIBLE);
        set_rl_system.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        isStop = false;
        mNioSocketTools.registerSocketResult(this);
        upRefreshSettings();
    }

    @Override
    public void onStop() {
        isStop = true;
        mNioSocketTools.unregisterSocketResult(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    private void upRefreshSettings() {
        mNioSocketTools.sendCommand(GET_RECORD_CFG);
        mNioSocketTools.sendCommand(GET_G_SENSOR_CFG);
        mNioSocketTools.sendCommand(GET_PARKING_MODE_CFG);
        mNioSocketTools.sendCommand(GET_VERSION);

    }

    @Override
    public void onClick(View v) {
        isUpView = false;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isUpView = true;
            }
        },1000);
        switch (v.getId()) {
            case R.id.set_sw_record:
                mNioSocketTools.sendCommand(set_sw_record.isChecked()?FAST_CYCLE_RECORD_START:FAST_CYCLE_RECORD_STOP);
                break;
            case R.id.set_sw_sound:
                byte[] soundCmd = new byte[7];
                System.arraycopy(SET_AUDIO_RECORD, 0, soundCmd, 0, SET_AUDIO_RECORD.length);
                soundCmd[6] = set_sw_sound.isChecked() ? (byte) 0x01 : (byte) 0x00;
                mNioSocketTools.sendCommand(soundCmd);
                break;
            case R.id.set_sw_lmd:
                set_lmd_high.setEnabled(set_sw_lmd.isChecked());
                set_lmd_low.setEnabled(set_sw_lmd.isChecked());
                set_lmd_medium.setEnabled(set_sw_lmd.isChecked());
                byte[] collideCmd = new byte[7];
                System.arraycopy(SET_G_SENSOR_CFG, 0, collideCmd, 0, SET_G_SENSOR_CFG.length);
                collideCmd[6] = set_sw_lmd.isChecked() ? (byte) 0x01 : (byte) 0x00;
                mNioSocketTools.sendCommand(collideCmd);
            case R.id.set_pix_1080:
            case R.id.set_pix_720:
                byte[] pixCmd = new byte[7];
                System.arraycopy(SET_RESOLUTION, 0, pixCmd, 0, SET_RESOLUTION.length);
                pixCmd[6] = v.getId() == R.id.set_pix_1080 ? (byte) 0x00 : (byte) 0x01;
                mNioSocketTools.sendCommand(pixCmd);
                break;
            case R.id.set_time_1min:
            case R.id.set_time_3min:
            case R.id.set_time_5min:
                byte[] timeCmd = new byte[7];
                System.arraycopy(SET_DURATION, 0, timeCmd, 0, SET_DURATION.length);
                timeCmd[6] = v.getId() == R.id.set_time_1min ? (byte) 0x01 : (v.getId() == R.id.set_time_3min ? (byte) 0x03 : (byte) 0x05);
                mNioSocketTools.sendCommand(timeCmd);
                break;
            case R.id.set_lmd_low:
            case R.id.set_lmd_medium:
            case R.id.set_lmd_high:
                byte[] sensorCmd = new byte[7];
                System.arraycopy(SET_G_SENSOR_CFG, 0, sensorCmd, 0, SET_G_SENSOR_CFG.length);
                sensorCmd[6] = v.getId() == R.id.set_lmd_low ? (byte) 0x01 : (v.getId() == R.id.set_lmd_medium ? (byte) 0x02 : (byte) 0x03);
                mNioSocketTools.sendCommand(sensorCmd);
            case R.id.set_sw_tcjk:
                byte[] parkingCmd = new byte[7];
                System.arraycopy(SET_PARKING_MODE_CFG, 0, parkingCmd, 0, SET_PARKING_MODE_CFG.length);
                parkingCmd[6] = set_sw_tcjk.isChecked() ? (byte) 0x01 : (byte) 0x00;
                mNioSocketTools.sendCommand(parkingCmd);
                break;
            case R.id.set_factory:
                FlyDialog.Builder factoryDialog = new FlyDialog.Builder(getActivity());
                factoryDialog.setTitle(getString(R.string.alert1))//设置对话框的标题
                        .setMessage(getString(R.string.factoy_alert))//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNioSocketTools.sendCommand(FACTORY_RESET);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.set_format:
                FlyDialog.Builder formatDialog = new FlyDialog.Builder(getActivity());
                formatDialog.setTitle(getString(R.string.alert1))//设置对话框的标题
                        .setMessage(getString(R.string.format_alert))//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNioSocketTools.sendCommand(SDCARD_FORMATTING);
                                progressDialog.setMessage(getString(R.string.formatting));
                                progressDialog.show();
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.set_video_set:
                set_video_set.setEnabled(false);
                set_system_set.setEnabled(true);
                set_rl_video.setVisibility(View.VISIBLE);
                set_rl_system.setVisibility(View.GONE);
                break;
            case R.id.set_system_set:
                set_video_set.setEnabled(true);
                set_system_set.setEnabled(false);
                set_rl_video.setVisibility(View.GONE);
                set_rl_system.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void result(ResultData msg) {
        if (isStop) return;
        try {
            byte[] recv = msg.getBytes();
            int cmd = ByteTools.bytes2ShortInt2(recv, 0);
            switch (cmd) {
//                HEARTBEAT
                case 0x0010:
                    if (!isUpView) break;
                    set_sw_record.setEnabled(true);
                    set_sw_sound.setEnabled(true);
                    set_rg_pix.setEnabled(true);
                    set_rg_time.setEnabled(true);
                    set_pix_1080.setEnabled(true);
                    set_pix_720.setEnabled(true);
                    set_time_1min.setEnabled(true);
                    set_time_3min.setEnabled(true);
                    set_time_5min.setEnabled(true);
                    set_sw_record.setChecked(recv[3]==0x02||recv[3]==0x01);
                    set_sw_sound.setChecked(recv[5]==0x02||recv[5]==0x01);
                    set_rg_pix.check(recv[7] == 0x00 ? R.id.set_pix_1080 : R.id.set_pix_720);
                    set_rg_time.check(recv[8] == 0x01 ? R.id.set_time_1min : (recv[8] == 0x03 ? R.id.set_time_3min : R.id.set_time_5min));
                    //TODO:录像信息叠加
                    set_info_time.setChecked(true);
                    set_info_car.setChecked(true);
                    break;
//                GET_RECORD_CFG
                case 0x1100:
                    set_rg_pix.setEnabled(true);
                    set_rg_time.setEnabled(true);
                    set_sw_sound.setEnabled(true);
                    set_pix_1080.setEnabled(true);
                    set_pix_720.setEnabled(true);
                    set_time_1min.setEnabled(true);
                    set_time_3min.setEnabled(true);
                    set_time_5min.setEnabled(true);
                    set_rg_pix.check(recv[2] == 0x00 ? R.id.set_pix_1080 : R.id.set_pix_720);
                    set_rg_time.check(recv[3] == 0x01 ? R.id.set_time_1min : (recv[3] == 0x03 ? R.id.set_time_3min : R.id.set_time_5min));
                    set_sw_sound.setChecked(recv[4] == 0x01);
                    break;
//                GET_G_SENSOR_CFG
                case 0x1123:
                    set_sw_lmd.setEnabled(true);
                    set_rg_lmd.setEnabled(true);
                    set_sw_lmd.setChecked(recv[2] != 0x00);
                    set_lmd_low.setEnabled(recv[2] != 0x00);
                    set_lmd_medium.setEnabled(recv[2] != 0x00);
                    set_lmd_high.setEnabled(recv[2] != 0x00);
                    set_rg_lmd.check(recv[2] == 0x01 ? R.id.set_lmd_low : (recv[2] == 0x02 ? R.id.set_lmd_medium : R.id.set_lmd_high));
                    break;
//                GET_PARKING_MODE_CFG
                case 0x1124:
                    set_sw_tcjk.setEnabled(true);
                    set_sw_tcjk.setChecked(recv[2] == 0x01);
                    break;
//                SDCARD_FORMATTING
                case 0x1220:
                    progressDialog.dismiss();
                    break;
//                FACTORY_RESET
                case 0x1221:
                    upRefreshSettings();
                    break;
//                GET_VERSION
                case 0x1120:
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.put(recv);
                    buffer.flip();
                    buffer.getShort();
                    String sb = "软件版本       V" +
                            String.valueOf(buffer.getFloat());
                    set_tv_version.setText(sb);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

}
