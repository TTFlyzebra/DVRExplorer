package com.longhorn.dvrexplorer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.hsae.autosdk.dvr.IDvrHsListener;
import com.hsae.autosdk.dvr.IDvrStateNotify;
import com.hsae.autosdk.dvr.IDvrTyListener;
import com.longhorn.dvrexplorer.utils.FlyLog;


/**
 * Created by FlyZebra on 2018/5/15.
 * Descrip:
 */

public class DvrStateNotifyService extends Service {

    private RemoteCallbackList<IDvrTyListener> iDvrTyListeners = new RemoteCallbackList<>();
    private RemoteCallbackList<IDvrHsListener> iDvrHsListeners = new RemoteCallbackList<>();

    private IBinder mBinder = new IDvrStateNotify.Stub() {
        @Override
        public void notityWorkStatus(int state) throws RemoteException {
            FlyLog.d("notityWorkStatus state=%d",state);
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

        @Override
        public void notityLinkStatus(int state) throws RemoteException {
            FlyLog.d("notityLinkStatus state=%d",state);
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

        @Override
        public void notityUpdateNotify(int state) throws RemoteException {
            FlyLog.d("notityUpdateNotify state=%d",state);
        }

        @Override
        public void notityTakePhotoRespond(int state) throws RemoteException {
            FlyLog.d("notityTakePhotoRespond state=%d",state);
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

        @Override
        public void notityUpdateSchedule(int state) throws RemoteException {
            FlyLog.d("notityUpdateSchedule state=%d",state);
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

        @Override
        public void notitySDCardStatus(int state) throws RemoteException {
            FlyLog.d("notitySDCardStatus state=%d",state);
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

        @Override
        public void notihsConnectWifi() throws RemoteException {
            final int N = iDvrHsListeners.beginBroadcast();
            for (int i = 0; i < N; i++) {
                IDvrHsListener l = iDvrHsListeners.getBroadcastItem(i);
                if (l != null) {
                    try {
                        l.notihsConnectWifi();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            iDvrTyListeners.finishBroadcast();
        }

        @Override
        public void notihsUpdateEvent(int type) throws RemoteException {
            final int N = iDvrHsListeners.beginBroadcast();
            for (int i = 0; i < N; i++) {
                IDvrHsListener l = iDvrHsListeners.getBroadcastItem(i);
                if (l != null) {
                    try {
                        l.notihsUpdateEvent(type);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            iDvrHsListeners.finishBroadcast();
        }

        @Override
        public void notihsDvrIPAddress(String ipStr) throws RemoteException {
            final int N = iDvrHsListeners.beginBroadcast();
            for (int i = 0; i < N; i++) {
                IDvrHsListener l = iDvrHsListeners.getBroadcastItem(i);
                if (l != null) {
                    try {
                        l.notihsDvrIPAddress(ipStr);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            iDvrHsListeners.finishBroadcast();
        }

        @Override
        public void registerDvrTyListener(IDvrTyListener listener) throws RemoteException {
            iDvrTyListeners.register(listener);
        }

        @Override
        public void unregisterDvrTyListener(IDvrTyListener listener) throws RemoteException {
            iDvrTyListeners.unregister(listener);
        }

        @Override
        public void registerDvrHsListener(IDvrHsListener listener) throws RemoteException {
            iDvrHsListeners.register(listener);
        }

        @Override
        public void unregisterDvrHsListener(IDvrHsListener listener) throws RemoteException {
            iDvrHsListeners.unregister(listener);
        }

    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
