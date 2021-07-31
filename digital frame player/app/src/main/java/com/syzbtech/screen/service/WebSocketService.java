package com.syzbtech.screen.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.syzbtech.screen.RunInfo;
import com.syzbtech.screen.entities.Device;
import com.syzbtech.screen.http.Api;
import com.syzbtech.screen.websocket.Command;
import com.syzbtech.screen.websocket.JWebSocketClient;

import java.net.URI;

public class WebSocketService extends Service {
    private String TAG = WebSocketService.class.getName();
    private final IBinder mBinder = new LocalBinder();
    private JWebSocketClient client;
    private Thread workThread;
    private volatile boolean running = true;

    private class WebSocketRunnable implements Runnable {

        @Override
        public void run() {
            while(running) {
                try {
                    Log.d("WebSocketService", "check web socket >>> ");
                    if(client==null) {
                        startWebSocket();
                    } else if(client.isClosed()){
                        client.reconnectBlocking();
                        Command<String> command = new Command<>();
                        command.setCommand(0x002);
                        command.setData(RunInfo.deviceCode);
                        client.send(JSON.toJSONString(command));
                    }

                    Thread.sleep(10 * 1000);
                } catch (Exception e) {
                    Log.d(WebSocketService.class.getName(), ">>>> ", e);
                }
            }

            if(client!=null && !client.isClosed()) {
                client.close();
            }
        }
    }

    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy >>>");
        running = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        workThread = new Thread(new WebSocketRunnable());
        workThread.start();
    }

    private void startWebSocket() throws InterruptedException {
        Log.d(TAG, ">>> start web socket to : " + (Api.ws + RunInfo.deviceCode));
        URI uri = URI.create(Api.ws + "1/"+ RunInfo.deviceCode);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                Log.e("JWebSClientService", message);

                JSONObject obj = JSON.parseObject(message);
                if(obj.getInteger("command")==3) {
                    return;
                }

                Command<Device> ret = JSON.parseObject(message,new TypeReference<Command<Device>>(){}.getType());
                if(ret.getCommand()==1) {
                    //绑定了。
                    RunInfo.device = ret.getData();
                } else if(ret.getCommand()==2) {
                    //解除绑定
                    if(RunInfo.device!=null) {
                        //设置设备为未绑定状态
                        RunInfo.device.setUserId(null);
                        RunInfo.device.setUser(null);
                    }
                }

                Intent intent = new Intent();
                intent.setAction("action.device.bind.changed");
                sendBroadcast(intent);
            }
        };
        client.connectBlocking();

        Command<String> command = new Command<>();
        command.setCommand(0x002);
        command.setData(RunInfo.deviceCode);
        client.send(JSON.toJSONString(command));
    }
}
