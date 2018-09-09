package com.longhorn.dvrexplorer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.hsae.autosdk.dvr.Dvr;
import com.longhorn.dvrexplorer.module.SoundPlay;
import com.longhorn.dvrexplorer.module.wifi.NioSocketTools;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.ProgressDialog.STYLE_HORIZONTAL;

/**
 * Created by FlyZebra on 2018/5/23.
 * Descrip:
 */

public class DVRActivity extends Activity implements Dvr.DvrTyListener, View.OnClickListener, SocketResult {
    private Button bt_home, bt_record, bt_file, bt_set;
    private Dvr mDvr;
    private SoundPlay mSoundPlay;
    private ProgressDialog progressDialog;
    private AtomicBoolean isNeedReset = new AtomicBoolean(false);
    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private AtomicBoolean isRtspOK = new AtomicBoolean(false);
    private NioSocketTools mNioSocketTools = NioSocketTools.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dvr);

        //心跳包
        isRunning.set(true);

        //声音播放
        mSoundPlay = new SoundPlay(this);
        mSoundPlay.initSoundPool();

        //升级固件对话框
        progressDialog = new ProgressDialog(this, R.style.updataProgressDialog);
        progressDialog.setProgressStyle(STYLE_HORIZONTAL);
//        progressDialog.setMessage("");
        progressDialog.setMax(100);

        //航盛DA服务
        try {
            mDvr = new Dvr();
            mDvr.registerListener(this);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        } catch (NoSuchMethodError e) {
            FlyLog.e("version error!" + e.toString());
        }

        //初始化控件
        bt_home = findViewById(R.id.ac_dvr_bt_home);
        bt_record = findViewById(R.id.ac_dvr_bt_record);
        bt_set = findViewById(R.id.ac_dvr_bt_set);
        bt_file = findViewById(R.id.ac_dvr_bt_file);
        bt_home.setOnClickListener(this);
        bt_record.setOnClickListener(this);
        bt_file.setOnClickListener(this);
        bt_set.setOnClickListener(this);

        bt_record.setEnabled(false);

        replaceFragment("RtspFragment");

        mNioSocketTools.init();
        mNioSocketTools.registerSocketResult(this);

        /**
         * 测试用，正式版删除
         */
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testUPdata();
            }
        });
    }

    private boolean replaceFragment(String fName) {
        if (!isRtspOK.get()) return false;
        try {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Class<?> cls = Class.forName("com.longhorn.dvrexplorer.fragment." + fName);
            Constructor<?> cons = cls.getConstructor();
            Fragment fragment = (Fragment) cons.newInstance(); //
            ft.replace(R.id.ac_dvr_fm01, fragment).commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addFragment(Fragment fragment1, Fragment fragment2) {
        try {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(fragment1);
            ft.add(R.id.ac_dvr_fm01, fragment2);
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        progressDialog.dismiss();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mNioSocketTools.unregisterSocketResult(this);
        mNioSocketTools.close();
        isNeedReset.set(false);
        isRunning.set(false);
        mSoundPlay.stopSound();
        mHandler.removeCallbacksAndMessages(null);
        try {
            if (mDvr != null) {
                mDvr.unRegisterListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        super.onDestroy();
    }

    @Override
    public void result(ResultData msg) {
        if (!isRtspOK.get()) {
            isRtspOK.set(true);
            replaceFragment("RtspFragment");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_dvr_bt_home:
                finish();
                break;
            case R.id.ac_dvr_bt_file:
                if (replaceFragment("FileFragment")) {
                    bt_record.setEnabled(true);
                    bt_file.setEnabled(false);
                    bt_set.setEnabled(true);
                }
                break;
            case R.id.ac_dvr_bt_set:
                if (replaceFragment("SetFragment")) {
                    bt_record.setEnabled(true);
                    bt_file.setEnabled(true);
                    bt_set.setEnabled(false);
                }
                break;
            case R.id.ac_dvr_bt_record:
                if (replaceFragment("RtspFragment")) {
                    bt_record.setEnabled(false);
                    bt_file.setEnabled(true);
                    bt_set.setEnabled(true);
                }
                break;
        }
    }

    /**
     * state
     * 0x00 = initial status
     * 0x01 = general recording mode
     * 0x02 = stop recording mode
     * 0x03 = manually start  emergency video mode
     * 0x04 = auto start emergency video mode
     * 0x05 = power off status
     * 0x06 = system failure
     * 0x07 = invalid
     **/
    @Override
    public void notityWorkStatus(int i) {
        FlyLog.d("notityWorkStatus state=%d",i);
    }

    /**
     * state
     * 0x00: initial status
     * 0x01: In connection
     * 0x02: Connection Fail
     * 0x03: STA Mode, Connection Successful
     * 0x04: MAC Data error
     * 0x05: Password Data error
     * 0x06~0x07: invalid
     **/
    //如果连续10秒都是0x06，通知DA重连wifi
    private long systemTime = 0;
    private final long waitTime = 10000;

    @Override
    public void notityLinkStatus(int i) {
        FlyLog.d("notityLinkStatus state=%d",i);
        if (i == 0x06) {
            if (systemTime == 0) {
                systemTime = System.currentTimeMillis();
            } else {
                if ((systemTime - System.currentTimeMillis()) > waitTime) {
                    try {
                        if (mDvr != null) {
                            mDvr.notihsConnectWifi();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FlyLog.e(e.toString());
                    }
                }
                systemTime = 0;
            }
        } else {
            systemTime = 0;
        }

        //升级成功后收到wifi连接成功的消息将重启应用
        if (i == 0x03) {
            if (isNeedReset.compareAndSet(true, false)) {
                recreate();
            }
        }
    }

    /**
     * state
     * 0x00 = initial status
     * 0x01 = Need to update DSP software
     * 0x02 = Need to update MCU software
     * 0x03 = Need to update DSP and MCU
     **/
    private String strType = "";

    @Override
    public void notityUpdateNotify(int i) {
        FlyLog.d("notityUpdateNotify state=%d",i);
        strType = i == 0x01 ? "DSP" : i == 0x02 ? "MCU" : i == 0x03 ? "DSP && MCU" : "";
    }

    /**
     * state
     * 0b = Not Respond
     * 1b = Photo taken
     **/
    public void notityTakePhotoRespond(int i) {
        FlyLog.d("notityTakePhotoRespond state=%d",i);
        if (i == 1) {
            mSoundPlay.playSound(1, 0);
        }
    }

    /**
     * state
     * 0000000b~1100100b = 0%~100%
     * 1100101b~1111100b = reserved
     * 1111101b = Upgrade Failed
     * 1111110b = Update Successful
     * 1111111b = initial status
     **/
    //显示升级进度
    @Override
    public void notityUpdateSchedule(final int i) {
        FlyLog.d("notityUpdateSchedule state=%d",i);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //0作为无效数据处理
                if (i == 0) return;
                if (i <= 100) {
                    progressDialog.setMessage("正在升级" + strType + "......");
                    progressDialog.setProgress(i);
                    progressDialog.show();
                } else if (i == 0x7D) { //Upgrade Failed
                    progressDialog.setMessage("升级失败!");
                } else if (i == 0x7E) { //Update Successful
                    progressDialog.setMessage("升级成功，正在等待设备重新连接......");
                    isNeedReset.set(true);
                } else if (i == 0x7F) {//initial status
//                    progressDialog.setMessage("initial status!");
                } else {
//                    progressDialog.setMessage("reserved!");
                }
            }
        });
    }

    /**
     * state
     * 0x00 = SD insert
     * 0x01 = SD pull out
     * 0x02 = SD failure
     * 0x03 = SD full
     * 0x04 = emergency video file full
     * 0x05 = photo file full
     * 0x06 = emergency video file and photo file full
     * 0x07 = write protection
     **/
    @Override
    public void notitySDCardStatus(int i) {
        FlyLog.d("notitySDCardStatus state=%d",i);
    }

    //发出拍照音
    @Override
    public void notityPlayCameraSound() {
        FlyLog.d("notityPlayCameraSound");
//        mSoundPlay.playSound(1, 0);
    }

    public interface IonBackPressedListener {
        void onBackPressed();
    }

    private List<IonBackPressedListener> ionBackPressedListeners = new ArrayList<>();

    public void addIonBackPressedListener(IonBackPressedListener ionBackPressedListener) {
        ionBackPressedListeners.add(ionBackPressedListener);
    }

    public void removeIonBackPressedListener(IonBackPressedListener ionBackPressedListener) {
        ionBackPressedListeners.remove(ionBackPressedListener);
    }

    @Override
    public void onBackPressed() {
        for (IonBackPressedListener ionBackPressedListener : ionBackPressedListeners) {
            ionBackPressedListener.onBackPressed();
        }
        super.onBackPressed();
    }

    /**
     * 以下为测试用，正式版本可删除
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(event.toString());
        return super.dispatchKeyEvent(event);
    }

    private void testUPdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 0x7F; i++) {
                    final int progress = i;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            notityUpdateSchedule(progress);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
