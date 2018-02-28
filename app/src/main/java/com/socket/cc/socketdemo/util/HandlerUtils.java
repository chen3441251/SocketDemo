package com.socket.cc.socketdemo.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @ Creator     :     chenchao
 * @ CreateDate  :     2017/4/24 0024 10:25
 * @ Description :     DDCashThin
 */

public class HandlerUtils extends Handler {


    public static Handler getInstance(OnHandleMessageListener listener) {

        return new MyHandler(listener);
    }

    public static class MyHandler extends Handler {

        private WeakReference<OnHandleMessageListener> mWeakReference;

        public MyHandler(OnHandleMessageListener listener) {
            mWeakReference = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference != null && mWeakReference.get() != null) {
                mWeakReference.get().handleMessage(msg);
            }
        }
    }

    public interface OnHandleMessageListener {
        void handleMessage(Message msg);
    }
}
