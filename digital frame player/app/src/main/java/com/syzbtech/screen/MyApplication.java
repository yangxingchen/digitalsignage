package com.syzbtech.screen;

import android.app.Application;
import android.util.Log;

import com.syzbtech.screen.utils.MacUtil;
import com.syzbtech.screen.utils.NetWorkUtil;
import com.syzbtech.screen.utils.SettingUtil;

import org.xutils.x;

public class MyApplication extends Application {
    private static Application instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        x.Ext.init(this);
        x.Ext.setDebug(true);
        SettingUtil.init().setContext(this)
                .setLocalPlaySettingFileName("local_play_setting")
                .build();
        String macCode = MacUtil.getMac(this);
        if(!"".equals(macCode)) {
            RunInfo.deviceCode = macCode;
        }
        Log.d("MyApplication", "device code >>>> "+ macCode);
    }


    public static Application getInstance() {
        return instance;
    }
}
