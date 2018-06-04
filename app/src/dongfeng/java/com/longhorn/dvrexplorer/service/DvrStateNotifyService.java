package com.longhorn.dvrexplorer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.hsae.autosdk.dvr.IDvrStateNotify;
import com.longhorn.dvrexplorer.utils.FlyLog;


/**
 * Created by FlyZebra on 2018/5/15.
 * Descrip:
 */

public class DvrStateNotifyService extends Service {

    private IBinder mBinder = new IDvrStateNotify.Stub() {
        @Override
        public void notityWorkStatus(int state) throws RemoteException {
            FlyLog.d("notityWorkStatus state=%d",state);
        }

        @Override
        public void notityLinkStatus(int state) throws RemoteException {
            FlyLog.d("notityLinkStatus state=%d",state);
        }

        @Override
        public void notityUpdateNotify(int state) throws RemoteException {
            FlyLog.d("notityUpdateNotify state=%d",state);
        }

        @Override
        public void notityTakePhotoRespond(int state) throws RemoteException {
            FlyLog.d("notityTakePhotoRespond state=%d",state);
        }

        @Override
        public void notityUpdateSchedule(int state) throws RemoteException {
            FlyLog.d("notityUpdateSchedule state=%d",state);
        }

        @Override
        public void notitySDCardStatus(int state) throws RemoteException {
            FlyLog.d("notitySDCardStatus state=%d",state);
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
