package com.frank.servapp_0508;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MyAidlServiceActivity extends AppCompatActivity implements View.OnClickListener, Handler.Callback {
    Button mButtonBind = null;
    Button mButtonUnbind = null;
    Button mButtonSet = null;
    EditText mEditVal1 = null;
    EditText mEditVal2 = null;
    ProgressBar mProgressBarVal1 = null;
    ProgressBar mProgressBarVal2 = null;

    private static final String TAG = "MyService001";
    private IMyAidlServiceInterface mService = null;
    Handler mHandler = new Handler(Looper.getMainLooper(), this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_aidl_service);
        mButtonBind = (Button) findViewById(R.id.button_bind);
        mButtonUnbind = (Button)findViewById(R.id.button_unbind);
        mProgressBarVal1 = (ProgressBar) findViewById(R.id.progressBar_val1);
        mProgressBarVal2 = (ProgressBar) findViewById(R.id.progressBar_val2);
        mButtonSet = (Button)findViewById(R.id.button_setValue);
        mEditVal1 = (EditText)findViewById(R.id.editText_val1);
        mEditVal2 = (EditText)findViewById(R.id.editText_val2);
        mButtonBind.setOnClickListener(this);
        mButtonUnbind.setOnClickListener(this);
        mButtonSet.setOnClickListener(this);
        mProgressBarVal1.setProgress(0);
        mProgressBarVal2.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_bind:
                bind();
                break;
            case R.id.button_unbind:
                unbind();
                break;
            case R.id.button_setValue:
                int val1 = Integer.valueOf(mEditVal1.getText().toString());
                int val2 = Integer.valueOf(mEditVal2.getText().toString());
                if (mService != null) {
                    try {
                        mService.setValue(val1, val2);
                    } catch (RemoteException e) {
                        Log.e(TAG, "onClick exception: e" + e);
                    }
                    Log.d(TAG, "setValue val1=" + val1 + " val2=" + val2);
                }
                break;
        }
    }

    IMyAidlClientCallback myAidlClientCallback = new IMyAidlClientCallback.Stub(){

        @Override
        public int onValueChanaged(int val1, int val2) throws RemoteException {
            Log.d(TAG, "MyAidlServiceActivity.onValueChanaged val1=" + val1 + " val2=" + val2);
            Message msg = Message.obtain();
            msg.what = 1;
            msg.arg1 = val1;
            msg.arg2 = val2;
            mHandler.sendMessage(msg);
            return 0;
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMyAidlServiceInterface.Stub.asInterface(service);
            try {
                mService.registerCallback(myAidlClientCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "onServiceConnected exception: e" + e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mService.unRegisterCallback(myAidlClientCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "onServiceDisconnected exception: e" + e);
            }
            mService = null;
        }
    };

    void bind() {
        if (mService == null) {
            Intent intent = new Intent("android.intent.action.MyAidlService");
            intent.setPackage("com.frank.servapp_0508");
            boolean ret = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "MyAidlServiceActivity.bind ret=" + ret);
        }
    }
    void unbind() {
        if (mService != null) {
            try {
                mService.unRegisterCallback(myAidlClientCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "unbind exception: e" + e);
                e.printStackTrace();
            }
            unbindService(mConnection);
            mService = null;
            Log.d(TAG, "MyAidlServiceActivity.unbind");
        }
    }

    void setProgress(int val1, int val2) {
        mProgressBarVal1.setProgress(val1);
        mProgressBarVal2.setProgress(val2);
        Log.d(TAG, "MyAidlServiceActivity.setProgress val1=" + val1 + " val2=" + val2);
    }

    @Override
    public boolean handleMessage(Message msg) {
       // Log.d(TAG, "handleMessage msg.what=" + msg.what);
        switch (msg.what) {
            case 1:
                setProgress(msg.arg1, msg.arg2);
                break;
            default:
                break;
        }
        return false;
    }
}
