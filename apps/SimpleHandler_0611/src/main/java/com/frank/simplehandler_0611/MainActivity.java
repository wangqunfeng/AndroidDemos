package com.frank.simplehandler_0611;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SimpleHandler_0611";

    public static class MyLooperHandler extends Handler {
        private MainHandler mMainHandler = null;
        private static final int MSG_SET_PROGRESS = 0;

        public MyLooperHandler(Looper looper, MainHandler mainHandler) {
            super(looper);
            mMainHandler = mainHandler;
        }

        /****
         * handleMessage called in LooperThread.
         * @param msg message object.
         */
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SET_PROGRESS) {
                Log.d(TAG, "MyLooperHandler.handleMessage msg.what=" + msg.what + " arg1=" + msg.arg1);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mMainHandler.updateProgress(msg.arg1);
            }
        }

        /***
         * setProgress called in Main Thread.
         * @param arg0 new seek bar value.
         */
        public void setProgress(int arg0) {
            Log.d(TAG, "MyLooperHandler.setProgress arg0=" + arg0);
            Message msg = Message.obtain();
            msg.what = MSG_SET_PROGRESS;
            msg.arg1 = arg0;
            sendMessage(msg);
        }
    }

    public class MainHandler extends Handler {
        public static final int MSG_SET_PROGRESS_MAIN = 1;

        public MainHandler(Looper looper) {
            super(looper);
        }

        /***
         * handleMessage called in UI thread
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SET_PROGRESS_MAIN) {
                int prg = msg.arg1;
                String content = (String) msg.obj;
                Log.d(TAG, "MainHandler.handleMessage msg.what=" + msg.what + " progress=" + prg + " content=" + content);
                if (progressBar_01 != null) {
                    progressBar_01.setProgress(prg);
                }
                if (editText_log != null) {
                    editText_log.append(content);
                }
            }
        }

        /***
         * updateProgress called in MyLooperThread
         * This message is send to Main Thread for  UI update
         * @param arg
         */
        public void updateProgress(int arg) {
            Log.d(TAG, "MainHandler.updateProgress arg=" + arg);
            Message msg = Message.obtain();
            msg.what = MSG_SET_PROGRESS_MAIN;
            msg.arg1 = arg;
            msg.obj = String.format("set progress=%d", arg);
            sendMessage(msg);
        }
    }


    MyLooperHandler myLooperHandler = null;
    MainHandler mainHandler = null;

    EditText editText_log = null;
    SeekBar seekBar_01 = null;
    ProgressBar progressBar_01 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity.onCreate UI thread running...");
        // UI components initialize
        editText_log = (EditText)findViewById(R.id.editText_log);
        seekBar_01 = (SeekBar)findViewById(R.id.seekBar_01);
        progressBar_01 = (ProgressBar)findViewById(R.id.progressBar_01);
        findViewById(R.id.button_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBar_01 != null) {
                    int prg = seekBar_01.getProgress();
                    if (myLooperHandler != null) {
                        Log.d(TAG, "MainActivity.onCreate.button_action.onClick prg=" + prg);
                        myLooperHandler.setProgress(prg);
                    }
                }
            }
        });

        MyLooperThread myLooperThread = new MyLooperThread("Worker");
        myLooperThread.start();
        mainHandler = new MainHandler(getMainLooper());
        myLooperHandler = new MyLooperHandler(myLooperThread.getLooper(), mainHandler);
    }


}
