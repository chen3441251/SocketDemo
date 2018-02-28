package com.socket.cc.socketdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.socket.cc.socketdemo.bean.EventBusBean;
import com.socket.cc.socketdemo.util.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketService extends Service {

    private static final int TIMEOUTLIMIT = 3000;
    private static final int HEARTBEATLIMIT = 2000;
    private ExecutorService mThreadPool;
    private Socket          mSocket;
    private Thread          connectThread;
    private Timer timer = new Timer();
    private OutputStream outputStream;
    private TimerTask    task;
    private Handler handler     = new Handler(Looper.getMainLooper());
    /*默认重连*/
    private boolean isReConnect = true;
    private String  mAddress    = "192.168.123.1";
    private int     mPort       = 60000;
    private InputStream    mInputStream;


    public SocketService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThreadPool = Executors.newCachedThreadPool();
        initConnSocket();
        EventBusUtil.register(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Bundle socketInfo = intent.getBundleExtra("socketInfo");
        mAddress = socketInfo.getString("address");
        mPort = socketInfo.getInt("port");
        return new SocketBinder();
    }

    private void initConnSocket() {
        if (mSocket == null ) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mSocket = new Socket();
                        //设置超时时间
                        mSocket.connect(new InetSocketAddress(mAddress, mPort), TIMEOUTLIMIT);
                        if (mSocket.isConnected()) {
                            //连接成功发送心跳包
                            sendWeakData();
                            receiveMsg();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            toastMsg("连接超时，正在重连");
                            releaseSocket();
                        } else if (e instanceof NoRouteToHostException) {
                            toastMsg("该地址不存在，请检查");
                            stopSelf();

                        } else if (e instanceof ConnectException) {
                            toastMsg("连接异常或被拒绝，请检查");
                            stopSelf();

                        }
                    }
                }
            });
        }

    }


    /*因为Toast是要运行在主线程的   所以需要到主线程哪里去显示toast*/
    private void toastMsg(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendWeakData() {
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        outputStream = mSocket.getOutputStream();
                                                /*这里的编码方式根据你的需求去改*/
                        outputStream.write(("beat").getBytes());
                        outputStream.flush();
//                        mSocket.sendUrgentData(0xF);
//                        Log.d("xxx","发送心跳包");
                    } catch (Exception e) {
                        /*发送失败说明socket断开了或者出现了其他错误*/
                        try {
                            mSocket.shutdownOutput();
                            mSocket.shutdownInput();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        /*重连*/
                        releaseSocket();
                        e.printStackTrace();


                    }
                }
            };
        }

        timer.schedule(task, 0, HEARTBEATLIMIT);
    }

    public void sendSocketData() {
        Toast.makeText(getApplicationContext(), "sendSocketData", Toast.LENGTH_LONG).show();
    }


    private class SocketBinder extends Binder implements ISockerService {

        @Override
        public void sendData(String data) {
            sendOrder(data);
        }
    }

    /*释放资源*/
    private void releaseSocket() {

        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }

        if (outputStream != null) {
            try {
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            mInputStream = null;
        }
        if (mSocket != null) {
            try {
                mSocket.close();

            } catch (IOException e) {
            }
            mSocket = null;
        }

        if (connectThread != null) {
            connectThread = null;
        }

          /*重新初始化socket*/
        if (isReConnect) {
            initConnSocket();
        }

    }

    /*发送数据*/
    public void sendOrder(final String order) {
        if (mSocket != null && mSocket.isConnected()) {
            /*发送指令*/
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream = mSocket.getOutputStream();
                        if (outputStream != null) {
                            outputStream.write((order).getBytes());
                            outputStream.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

        } else {
            toastMsg("socket连接错误,请重试");
        }
    }

    /**
     * 接收数据
     */
    private void receiveMsg() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSocket != null) {
                        while (!mSocket.isInputShutdown() && !mSocket.isClosed()) {
                            mInputStream = mSocket.getInputStream();
                            byte[] buf = new byte[1024];
                            int len = mInputStream.read(buf);
                            String text = new String(buf, 0, len);
                            EventBusUtil.post(new EventBusBean("TYPE_RECEIVE",text));
                        }

                    }
                } catch (Exception e) {
                    Log.d("xxx", "e===" + e);
                }

            }
        });
//        mReceiveThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReConnect = false;
        releaseSocket();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMessage(EventBusBean eventBusBean){
        switch (eventBusBean.getType()) {
            case "TYPE_NETWORK_AVAILABLE":
                releaseSocket();
                break;
            default:
                break;
        }
    }
}
