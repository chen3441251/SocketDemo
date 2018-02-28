package com.socket.cc.socketdemo.bean;

/**
 * @ Creator     :     chenchao
 * @ CreateDate  :     2017/12/14 0014 15:13
 * @ Description :     eventbus类
 */

public class EventBusBean<T> {
    private String type;
    private T      data;

    public EventBusBean(String type) {
        this.type = type;
    }

    public EventBusBean(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
