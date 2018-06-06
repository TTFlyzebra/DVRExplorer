package com.longhorn.dvrexplorer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.hsae.autosdk.dvr.IDvrStateNotify;
import com.hsae.autosdk.dvr.IDvrTyListener;
import com.longhorn.dvrexplorer.data.Global;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by FlyZebra on 2018/5/23.
 * Descrip:
 */

public class DVRActivity extends Activity {
    private Button bt_home, bt_record, bt_file, bt_set;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final Executor executor = Executors.newFixedThreadPool(1);

    private IDvrStateNotify iDvrStateNotify;

    private IDvrTyListener iDvrTyListener = new IDvrTyListener.Stub() {
        @Override
        public void notityWorkStatus(int state) throws RemoteException {

        }

        @Override
        public void notityLinkStatus(int state) throws RemoteException {

        }

        @Override
        public void notityUpdateNotify(int state) throws RemoteException {

        }

        @Override
        public void notityTakePhotoRespond(int state) throws RemoteException {

        }

        @Override
        public void notityUpdateSchedule(int state) throws RemoteException {

        }

        @Override
        public void notitySDCardStatus(int state) throws RemoteException {

        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iDvrStateNotify = IDvrStateNotify.Stub.asInterface(service);
            try {
                iDvrStateNotify.registerDvrTyListener(iDvrTyListener);
                iDvrStateNotify.notityLinkStatus(1);
                iDvrStateNotify.notitySDCardStatus(2);
                iDvrStateNotify.notityTakePhotoRespond(3);
                iDvrStateNotify.notityLinkStatus(4);
                iDvrStateNotify.notityUpdateSchedule(5);
                iDvrStateNotify.notityWorkStatus(6);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //TODO:服务断开处理
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dvr);

        initService();

        bt_home = findViewById(R.id.ac_dvr_bt_home);
        bt_record = findViewById(R.id.ac_dvr_bt_record);
        bt_set = findViewById(R.id.ac_dvr_bt_set);
        bt_file = findViewById(R.id.ac_dvr_bt_file);

        bt_record.setEnabled(false);
        bt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFragment("RtspFragment")) {
                    bt_record.setEnabled(false);
                    bt_file.setEnabled(true);
                    bt_set.setEnabled(true);
                }
            }
        });

        bt_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFragment("FileFragment")) {
                    bt_record.setEnabled(true);
                    bt_file.setEnabled(false);
                    bt_set.setEnabled(true);
                }
            }
        });

        bt_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFragment("SetFragment")) {
                    bt_record.setEnabled(true);
                    bt_file.setEnabled(true);
                    bt_set.setEnabled(false);
                }
            }
        });

//        addFragment("RtspFragment");

        setDvrRtspIPTask();
    }

    private void initService() {
//        ComponentName componentName = new ComponentName("com.longhorn.dvrstatenotify","com.longhorn.dvrstatenotify.service.DvrStateNotifyService");
        Intent intent = new Intent();
//        intent.setComponent(componentName);
        intent.setAction("com.hsae.auto.DVR_SERVICE");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean addFragment(String fName) {
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

    public boolean addFragment(Fragment fragment1, Fragment fragment2) {
        try {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(fragment1);
            ft.add(R.id.ac_dvr_fm01, fragment2);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unbindService(serviceConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void setDvrRtspIPTask() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                InputStream ins = null;
                ServerSocket serverSocket = null;
                try {
                    FlyLog.d("create socket server prot 50003");
                    serverSocket = new ServerSocket(50003);
                    FlyLog.d("socket accept");
                    socket = serverSocket.accept();
                    socket.setSoTimeout(5000);
                    FlyLog.d("socket read");
                    ins = socket.getInputStream();
                    byte buf[] = new byte[1024];
                    int len = ins.read(buf);
                    for (int i = 0; i < len; i++) {
                        if (buf[i] == 0x00) {
                            len = i;
                            break;
                        }
                    }
                    byte recv[] = new byte[len];
                    System.arraycopy(buf, 0, recv, 0, len);
                    Global.DVR_IP = new String(recv, "utf-8");
                    FlyLog.d("recv string:%s", Global.DVR_IP);
                    StringBuilder sb = new StringBuilder("");
                    for (byte aByte : recv) {
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
                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                    FlyLog.e(e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    FlyLog.e(e.toString());
                } finally {
                    try {
                        if (ins != null) ins.close();
                        if (socket != null) socket.close();
                        if (serverSocket != null) serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        addFragment("RtspFragment");
                    }
                });
            }
        });
    }
}
