package com.longhorn.dvrexplorer.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.module.wifi.CommandType;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.module.wifi.SocketTools;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class SetFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, CommandType, SocketResult, View.OnClickListener {
    private Switch set_sw_record, set_sw_sound, set_sw_tcjk, set_sw_collide;
    private RadioGroup set_rg_sensi, set_rg_time, set_rg_pix;
    private CheckBox set_info_time, set_info_car;
    private RadioButton set_lm_low, set_lm_medium, set_lm_high, set_time_1min, set_time_3min, set_time_5min;
    private Button set_factory, set_format;
    private ProgressDialog progressDialog;

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
        set_sw_collide = view.findViewById(R.id.set_sw_collide);

        set_rg_pix = view.findViewById(R.id.set_rg_pix);
        set_rg_time = view.findViewById(R.id.set_rg_time);
        set_rg_sensi = view.findViewById(R.id.set_rg_sensi);

        set_info_time = view.findViewById(R.id.set_info_time);
        set_info_car = view.findViewById(R.id.set_info_car);

        set_lm_low = view.findViewById(R.id.set_lm_low);
        set_lm_medium = view.findViewById(R.id.set_lm_medium);
        set_lm_high = view.findViewById(R.id.set_lm_high);
        set_time_1min = view.findViewById(R.id.set_time_1min);
        set_time_3min = view.findViewById(R.id.set_time_3min);
        set_time_5min = view.findViewById(R.id.set_time_5min);

        set_factory = view.findViewById(R.id.set_factory);
        set_format = view.findViewById(R.id.set_format);

        set_factory.setOnClickListener(this);
        set_format.setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        upRefreshSettings();
    }

    private void upRefreshSettings() {
        SocketTools.getInstance().sendCommand(CommandType.GET_RECORD_CFG, this);
        SocketTools.getInstance().sendCommand(CommandType.GET_G_SENSOR_CFG, this);
        SocketTools.getInstance().sendCommand(CommandType.GET_PARKING_MODE_CFG, this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.set_sw_collide:
                set_lm_high.setEnabled(isChecked);
                set_lm_low.setEnabled(isChecked);
                set_lm_medium.setEnabled(isChecked);
                byte[] collideCmd = new byte[7];
                System.arraycopy(SET_G_SENSOR_CFG, 0, collideCmd, 0, SET_G_SENSOR_CFG.length);
                collideCmd[6] = isChecked ? (byte) 0x01 : (byte) 0x00;
                SocketTools.getInstance().sendCommand(collideCmd, this);
                break;
            case R.id.set_sw_record:
                break;
            case R.id.set_sw_sound:
                byte[] soundCmd = new byte[7];
                System.arraycopy(SET_AUDIO_RECORD, 0, soundCmd, 0, SET_AUDIO_RECORD.length);
                soundCmd[6] = isChecked ? (byte) 0x01 : (byte) 0x00;
                SocketTools.getInstance().sendCommand(soundCmd, this);
                break;
            case R.id.set_sw_tcjk:
                byte[] parkingCmd = new byte[7];
                System.arraycopy(SET_PARKING_MODE_CFG, 0, parkingCmd, 0, SET_PARKING_MODE_CFG.length);
                parkingCmd[6] = isChecked ? (byte) 0x01 : (byte) 0x00;
                SocketTools.getInstance().sendCommand(parkingCmd, this);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.set_pix_1080:
            case R.id.set_pix_720:
                byte[] pixCmd = new byte[7];
                System.arraycopy(SET_RESOLUTION, 0, pixCmd, 0, SET_RESOLUTION.length);
                pixCmd[6] = checkedId == R.id.set_pix_1080 ? (byte) 0x00 : (byte) 0x01;
                SocketTools.getInstance().sendCommand(pixCmd, this);
                break;
            case R.id.set_time_1min:
            case R.id.set_time_3min:
            case R.id.set_time_5min:
                byte[] timeCmd = new byte[7];
                System.arraycopy(SET_DURATION, 0, timeCmd, 0, SET_DURATION.length);
                timeCmd[6] = checkedId == R.id.set_time_1min ? (byte) 0x01 : (checkedId == R.id.set_time_3min ? (byte) 0x03 : (byte) 0x05);
                SocketTools.getInstance().sendCommand(timeCmd, this);
                break;
            case R.id.set_lm_low:
            case R.id.set_lm_medium:
            case R.id.set_lm_high:
                byte[] soundCmd = new byte[7];
                System.arraycopy(SET_G_SENSOR_CFG, 0, soundCmd, 0, SET_G_SENSOR_CFG.length);
                soundCmd[6] = checkedId == R.id.set_lm_low ? (byte) 0x01 : (checkedId == R.id.set_lm_medium ? (byte) 0x02 : (byte) 0x03);
                SocketTools.getInstance().sendCommand(soundCmd, this);
                break;
        }
    }

    @Override
    public void result(ResultData msg) {
        try {
            if (msg.getMark() <= 8) {
                Toast.makeText(getActivity(), msg.getMsg(), Toast.LENGTH_LONG).show();
                return;
            }
            byte[] ret = msg.getBytes();
            int cmd = ByteTools.bytes2ShortInt2(ret, 6);
            switch (cmd) {
                //GET_RECORD_CFG
                case 0x1100:
                    FlyLog.d("GET_RECORD_CFG length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
//                send->ee:aa:02:00:00:00:11:00:af:d3:23:b8
//                recv->ee:aa:05:00:00:00:11:00:13:03:00:c4:53:a1:39
                    set_rg_pix.check(ret[8] == 0x00 ? R.id.set_pix_1080 : R.id.set_pix_720);
                    set_rg_pix.setOnCheckedChangeListener(this);
                    set_rg_time.check(ret[9] == 0x01 ? R.id.set_time_1min : (ret[9] == 0x03 ? R.id.set_time_3min : R.id.set_time_5min));
                    set_rg_time.setOnCheckedChangeListener(this);
                    set_sw_sound.setChecked(ret[10] == 0x01);
                    set_sw_sound.setOnCheckedChangeListener(SetFragment.this);
                    break;
                case 0x1123:
                    FlyLog.d("GET_G_SENSOR_CFG length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
//                recv->ee:aa:02:00:00:00:11:23:0d:b4:52:ca
//                send->ee:aa:03:00:00:00:11:23:13:8b:77:3a:1b
                    set_sw_collide.setChecked(ret[8] != 0x00 && ret[8] != 0x13);
                    set_lm_low.setEnabled(ret[8] != 0x00);
                    set_lm_medium.setEnabled(ret[8] != 0x00);
                    set_lm_high.setEnabled(ret[8] != 0x00);
                    set_sw_collide.setOnCheckedChangeListener(this);
                    set_rg_sensi.check(ret[8] == 0x01 ? R.id.set_lm_low : (ret[8] == 0x02 ? R.id.set_lm_medium : R.id.set_lm_high));
                    set_rg_sensi.setOnCheckedChangeListener(this);
                    break;
                case 0x1124:
                    FlyLog.d("GET_PARKING_MODE_CFG length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
//                send->ee:aa:02:00:00:00:11:24:93:d0:c7:69
//                recv->ee:aa:03:00:00:00:11:24:13:c4:36:ac:dc
                    set_sw_tcjk.setChecked(ret[8] == 0x01);
                    set_sw_tcjk.setOnCheckedChangeListener(SetFragment.this);
                    break;
                case 0x1200:
                    FlyLog.d("SET_RESOLUTION length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    break;
                case 0x1201:
                    FlyLog.d("SET_DURATION length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    break;
                case 0x1202:
                    FlyLog.d("SET_AUDIO_RECORD length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    break;
                case 0x1223:
                    FlyLog.d("SET_G_SENSOR_CFG length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    break;
                case 0x1224:
                    FlyLog.d("SET_PARKING_MODE_CFG length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    break;
                case 0x1220:
                    FlyLog.d("SDCARD_FORMATTING length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    progressDialog.dismiss();
                    break;
                case 0x1221:
                    FlyLog.d("FACTORY_RESET length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                    upRefreshSettings();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_factory:
                AlertDialog.Builder factoryDialog = new AlertDialog.Builder(getActivity());
                factoryDialog.setTitle("警告提示!")//设置对话框的标题
                        .setMessage("恢复出厂设置后所有内容将清空，确定要继续吗？")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SocketTools.getInstance().sendCommand(FACTORY_RESET,SetFragment.this);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.set_format:
                AlertDialog.Builder formatDialog = new AlertDialog.Builder(getActivity());
                formatDialog.setTitle("警告提示!")//设置对话框的标题
                        .setMessage("格式化后所有内容将清空，确定要格式化吗？")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SocketTools.getInstance().sendCommand(SDCARD_FORMATTING,SetFragment.this);
                                progressDialog.setMessage("正在格式化SDCARD......");
                                progressDialog.show();
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }
}
