package com.frank.simplehandler_0611;

/**
 * Created by qunfeng on 2017/6/11.
 */

import android.os.Looper;
import android.system.Os;
import android.util.Log;


public class MyLooperThread extends Thread {
    private static final String TAG = "SimpleHandler_0611";

    int mTid = -1;
    Looper mLooper = null;
    String mThreadName = null;
    public MyLooperThread(String name) {
        super(name);
        mThreadName = name;
    }

    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }
        synchronized (this) {
            if (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return mLooper;
    }

    @Override
    public void run() {
        Looper.prepare();
        mTid = Os.gettid();
        Log.d(TAG, "Thread " + mThreadName + " " + mTid + " begin running...");
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Looper.loop();
        mLooper = null;
        mTid = -1;
    }
}
