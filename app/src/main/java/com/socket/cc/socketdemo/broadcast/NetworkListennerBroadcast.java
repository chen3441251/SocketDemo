package com.socket.cc.socketdemo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.widget.Toast;

import com.socket.cc.socketdemo.bean.EventBusBean;
import com.socket.cc.socketdemo.util.EventBusUtil;
import com.socket.cc.socketdemo.util.HandlerUtils;
import com.socket.cc.socketdemo.util.HttpUtil;


/**
 * @ Creator     :     chenchao
 * @ CreateDate  :     2017/12/13 0013 9:29
 * @ Description :     DDCash
 */

public class NetworkListennerBroadcast extends BroadcastReceiver implements HandlerUtils.OnHandleMessageListener {
    private Context context;
    private boolean flag;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context.getApplicationContext();
        switch (intent.getAction()) {
            case ConnectivityManager.CONNECTIVITY_ACTION://网络状态监听
                HandlerUtils.getInstance(this).postDelayed(mNetRunnable, 500);
                break;

            default:
                break;
        }

    }


    Runnable mNetRunnable = new Runnable() {
        @Override
        public void run() {
            if (HttpUtil.checkNetState(context)) {
                if (!flag) {//首次进入不提示网络状态
                    flag = true;
                } else {
                    networkState();
                }
                EventBusUtil.post(new EventBusBean("TYPE_NETWORK_AVAILABLE"));
            } else {
                if (HttpUtil.isAppForeground(context)) {
                    Toast.makeText(context, "请检查网络设置", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    /**
     * 网络已经连接，然后去判断是wifi连接还是mobile连接
     */
    private void networkState() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
                NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                if (mobile == NetworkInfo.State.CONNECTED) {
                    if (HttpUtil.isAppForeground(context)) {
                        Toast.makeText(context, "当前网络已切换为数据流量", Toast.LENGTH_LONG).show();
                    }

                }
            }
            if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) {
                NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
                if (wifi == NetworkInfo.State.CONNECTED) {
                    if (HttpUtil.isAppForeground(context)) {
                        Toast.makeText(context, "当前网络已切换为WIFI", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
