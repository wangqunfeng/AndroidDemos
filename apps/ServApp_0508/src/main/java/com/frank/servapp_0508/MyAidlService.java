package com.frank.servapp_0508;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class MyAidlService extends Service {
    private static final String TAG = "MyService001";
    RemoteCallbackList<IMyAidlClientCallback> mCallbackList = new RemoteCallbackList<>();
    int mVal1 = 0, mVal2 = 0;
    Thread mUpdateThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mVal1++;
                mVal2--;
                if (mVal1 > 100)
                    mVal1= 0;
                if (mVal2 < 0)
                    mVal2 = 100;
                //Log.d(TAG, "update val1=" + mVal1 + " val2=" + mVal2);
                callBack(mVal1, mVal2);
            }
        }
    });

    public MyAidlService() {
        mUpdateThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "MyAidlService.onBind");
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }


    void callBack(int val1, int val2) {
        int N = mCallbackList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbackList.getBroadcastItem(i).onValueChanaged(val1, val2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbackList.finishBroadcast();
    }

    IMyAidlServiceInterface.Stub mBinder = new IMyAidlServiceInterface.Stub() {
        @Override
        public int registerCallback(IMyAidlClientCallback callback) throws RemoteException {
            if (callback != null) {
                mCallbackList.register(callback);
                Log.d(TAG, "MyAidlService.registerCallback");
            }
            return 0;
        }


        @Override
        public int unRegisterCallback(IMyAidlClientCallback callback) throws RemoteException {
            if (callback != null) {
                mCallbackList.unregister(callback);
                Log.d(TAG, "MyAidlService.unRegisterCallback");
            }
            return 0;
        }

        @Override
        public int setValue(int val1, int val2) throws RemoteException {
            Log.d(TAG, "MyAidlService.setValue val1=" + val1 + " val2=" + val2);
            mVal1 = val1;
            mVal2 = val2;
            return val1 + val2;
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "MyAidlService.onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MyAidlService.onDestroy");
        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "MyAidlService.onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "MyAidlService.onUnbind");
        return super.onUnbind(intent);
    }
}
