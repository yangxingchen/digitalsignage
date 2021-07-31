package com.syzbtech.screen.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetWorkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetWorkStateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            if("WIFI".equals(name)) {

            }
            Toast.makeText(context, "您的网络处于连接状态 >>> " + name, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "您的网络连接已中断", Toast.LENGTH_SHORT).show();
        }
    }
}

