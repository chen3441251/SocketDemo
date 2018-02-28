package com.socket.cc.socketdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.socket.cc.socketdemo.bean.EventBusBean;
import com.socket.cc.socketdemo.broadcast.NetworkListennerBroadcast;
import com.socket.cc.socketdemo.service.ISockerService;
import com.socket.cc.socketdemo.service.SocketService;
import com.socket.cc.socketdemo.util.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {
    public  TextView                  receive;
    private ServiceConnection         mConnection;
    private NetworkListennerBroadcast mNetworkListennerBroadcast;
    private BufferedReader            mBufferedReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        EventBusUtil.register(this);
        initNetWorkBroadcast();
    }

    private void initView() {
        final EditText editText = (EditText) findViewById(R.id.edt);
        Button send = (Button) findViewById(R.id.btn_send);
        receive = (TextView) findViewById(R.id.tv);
        Button conn = (Button) findViewById(R.id.btn_conn);
        conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                connectSocket();
                //                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //                if (defaultUri != null) {
                //                    Ringtone ringtone = RingtoneManager.getRingtone(getApplication(), defaultUri);
                //                    ringtone.play();
                //                }

                bindSocketService();

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //            new Thread(new Runnable() {
                //                @Override
                //                public void run() {
                //                    SocketClient.getSingle().sendTCPMessage(editText.getText().toString().trim());
                //                    runOnUiThread(new Runnable() {
                //                        @Override
                //                        public void run() {
                //                            editText.setText("");
                //                        }
                //                    });
                //                }
                //            }).start();
                service1.sendData(editText.getText().toString().trim());
                editText.setText("");
                /*service1.receiveData(new IMsgCallBack() {
                    @Override
                    public void callBack(final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                receive.setText(s);
                            }
                        });
                    }
                });*/
            }
        });

    }

    public void connectSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketClient.getSingle().getTCPConnect("192.168.123.1", 60000,
                        new SocketClient.CallBackSocketTCP() {
                            @Override
                            public void Receive(final String info) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        receive.setText(info);
                                    }
                                });
                            }

                            @Override
                            public void isConnect(boolean state) {

                            }
                        });
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketClient.getSingle().closeSocket();
        unbindService(mConnection);
        Intent intent = new Intent(getApplication(), SocketService.class);
        stopService(intent);
        EventBusUtil.unregister(this);
        unregisterReceiver(mNetworkListennerBroadcast);
    }

    ISockerService service1;

    private void bindSocketService() {
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("port", 60000);
        bundle.putString("address", "192.168.123.1");
        intent.putExtra("socketInfo", bundle);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                service1 = (ISockerService) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, mConnection, BIND_AUTO_CREATE);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMessage(EventBusBean eventBusBean) {
        switch (eventBusBean.getType()) {
            case "TYPE_RECEIVE"://收到service消息
                String data = (String) eventBusBean.getData();
                receive.setText(data);
                break;
            default:
                break;
        }
    }

    private void initNetWorkBroadcast() {
        mNetworkListennerBroadcast = new NetworkListennerBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkListennerBroadcast, intentFilter);

    }


}
