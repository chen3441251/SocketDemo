package com.socket.cc.socketdemo.util;


import com.socket.cc.socketdemo.bean.EventBusBean;

import org.greenrobot.eventbus.EventBus;

/**
 * @ Creator     :     chenchao
 * @ CreateDate  :     2017/12/14 0014 15:29
 * @ Description :     eventbus工具类
 */

public class EventBusUtil {
    // 注册
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    //反注册
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    //发送事件
    public static void post(EventBusBean eventBusBean) {
        EventBus.getDefault().post(eventBusBean);
    }

    //发送粘性事件
    public static void postSticky(EventBusBean eventBusBean) {
        EventBus.getDefault().postSticky(eventBusBean);
    }
}
