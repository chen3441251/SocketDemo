package com.socket.cc.socketdemo.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * @ Creator     :     chenchao
 * @ CreateDate  :     2018/2/27 0027 17:27
 * @ Description :     SocketDemo
 */

public class HttpUtil {
    /**
     * 检测网络状态（true、可用 false、不可用）
     */
    public static boolean checkNetState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isAvailable();
            }
        }

        return false;
    }
    /**
     * 判断应用是否在后台
     * */
    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;

                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
