package com.longhorn.dvrexplorer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.hsae.autosdk.dvr.Dvr;
import com.hsae.autosdk.dvr.IDvrStateNotify;
import com.hsae.autosdk.dvr.IDvrTyListener;
import com.longhorn.dvrexplorer.module.SoundPlay;
import com.longhorn.dvrexplorer.utils.FlyLog;


/**
 * Created by FlyZebra on 2018/5/15.
 * Descrip:
 */

public class DvrStateNotifyService extends Service implements Dvr.DvrTyListener {

    private SoundPlay soundPlay = null;

    private RemoteCallbackList<IDvrTyListener> iDvrTyListeners = new RemoteCallbackList<>();
    private Dvr mDvr;


    @Override
    public void onCreate() {
        super.onCreate();
        soundPlay = new SoundPlay(this);
        soundPlay.initSoundPool();
        try {
            mDvr = new Dvr();
            mDvr.registerListener(this);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        } catch (NoSuchMethodError e) {
            FlyLog.e("version error!" + e.toString());
        }
    }

    @Override
    public void onDestroy() {
        soundPlay.stopSound();
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

    private IBinder mBinder = new IDvrStateNotify.Stub() {
        @Override
        public void notityWorkStatus(int state) throws RemoteException {
            notifyWorkStatus(state);
        }

        @Override
        public void notityLinkStatus(int state) throws RemoteException {
            notifyLinkStatus(state);
        }

        @Override
        public void notityUpdateNotify(int state) throws RemoteException {
            notifyUpdateNotify(state);
        }

        @Override
        public void notityTakePhotoRespond(int state) throws RemoteException {
            notifyTakePhotoRespond(state);
        }

        @Override
        public void notityUpdateSchedule(int state) throws RemoteException {
            notifyUpdateSchedule(state);
        }

        @Override
        public void notitySDCardStatus(int state) throws RemoteException {
            notifySDCardStatus(state);
        }

        @Override
        public void notityTakePhoto() throws RemoteException {
            notifyTackPhoto();
        }
    };

    private void notifyTackPhoto() {
        FlyLog.d("notitySDCardStatus");
        soundPlay.playSound(1, 0);
        final int N = iDvrTyListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDvrTyListener l = iDvrTyListeners.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.notityPlayCameraSound();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        iDvrTyListeners.finishBroadcast();
    }

    private void notifySDCardStatus(int state) {
        FlyLog.d("notitySDCardStatus state=%d", state);
        final int N = iDvrTyListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDvrTyListener l = iDvrTyListeners.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.notitySDCardStatus(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        iDvrTyListeners.finishBroadcast();
    }

    private void notifyUpdateSchedule(int state) {
        FlyLog.d("notityUpdateSchedule state=%d", state);
        final int N = iDvrTyListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDvrTyListener l = iDvrTyListeners.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.notityUpdateSchedule(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        iDvrTyListeners.finishBroadcast();
    }

    private void notifyTakePhotoRespond(int state) {
        FlyLog.d("notityTakePhotoRespond state=%d", state);
        if (state == 1) {
            soundPlay.playSound(1, 0);
        }
        final int N = iDvrTyListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDvrTyListener l = iDvrTyListeners.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.notityTakePhotoRespond(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        iDvrTyListeners.finishBroadcast();
    }

    private void notifyUpdateNotify(int state) {
        FlyLog.d("notityUpdateNotify state=%d", state);
        final int N = iDvrTyListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDvrTyListener l = iDvrTyListeners.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.notityUpdateNotify(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        iDvrTyListeners.finishBroadcast();
    }

    private void notifyLinkStatus(int state) {
        FlyLog.d("notityLinkStatus state=%d", state);
        final int N = iDvrTyListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDvrTyListener l = iDvrTyListeners.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.notityLinkStatus(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        iDvrTyListeners.finishBroadcast();
    }

    private void notifyWorkStatus(int state) {
        FlyLog.d("notityWorkStatus state=%d", state);
        final int N = iDvrTyListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDvrTyListener l = iDvrTyListeners.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.notityWorkStatus(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        iDvrTyListeners.finishBroadcast();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void notityWorkStatus(int i) {
        notifyWorkStatus(i);
    }

    @Override
    public void notityLinkStatus(int i) {
        notifyLinkStatus(i);
    }

    @Override
    public void notityUpdateNotify(int i) {
        notifyUpdateNotify(i);
    }

    @Override
    public void notityTakePhotoRespond(int i) {
        notifyTakePhotoRespond(i);
    }

    @Override
    public void notityUpdateSchedule(int i) {
        notifyUpdateSchedule(i);
    }

    @Override
    public void notitySDCardStatus(int i) {
        notifySDCardStatus(i);
    }

    @Override
    public void notityPlayCameraSound() {
        notifyTackPhoto();
    }
}
