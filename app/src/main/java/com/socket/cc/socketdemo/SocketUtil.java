package com.socket.cc.socketdemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by CC on 2018/2/24.
 */

public class SocketUtil {
    Socket socket = null;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    public SocketUtil(Socket socket) {
        super();
        this.socket = socket;
    }

    //断开连接
    public void closeConnect(){
        try {
            if(mOutputStream!=null){
                mOutputStream.close();
                Log.d("xxx","mOutputStream--CLOSE");
            }
            if(mInputStream!=null){
                mInputStream.close();
                Log.d("xxx","mInputStream--CLOSE");
            }
            if (socket != null) {
                socket.close();
                Log.d("xxx","socket--CLOSE");
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    //检测是否连接 如果断开就重连
    public boolean isConnect(){
        if(socket.isClosed()){//检测是否关闭状态
            //TODO 这个地方检测数 是断开，在这写重连的方法。

            return false;
        }
        return true;
    }
    //发送数据
    public void sendData(String data) throws IOException {
        mOutputStream = socket.getOutputStream();
        mOutputStream.write(data.getBytes());
    }

    //接收数据
    public String receiveData() throws IOException {
        mInputStream = socket.getInputStream();
        //        DataInputStream data=new DataInputStream(inputStream);
        byte[] buf = new byte[1024];
        int len = mInputStream.read(buf);
        String text = new String(buf, 0, len);
        return text;
    }
}
