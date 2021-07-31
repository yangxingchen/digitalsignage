package com.syzbtech.screen.service;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.syzbtech.screen.RunInfo;
import com.syzbtech.screen.adapter.AbstractProgressCallback;
import com.syzbtech.screen.entities.Version;
import com.syzbtech.screen.http.AbstractCommonCallback;
import com.syzbtech.screen.http.Api;
import com.syzbtech.screen.http.NetParams;
import com.syzbtech.screen.http.Result;
import com.syzbtech.screen.utils.Util;

import org.xutils.x;

import java.io.File;

public class UpgradeService {
    private UpgradeService(){}
    private static UpgradeService mInstance = new UpgradeService();
    public static UpgradeService instance() {
        return mInstance;
    }

    public interface Callback {
        void call(Version version);
    }

    public void getUpgradeInfo(Callback callback) {
        NetParams netParams = new NetParams(Api.GET_UPGRADE_INFO, false , false);
        netParams.addBodyParameter("target", "tv");
        x.http().post(netParams, new AbstractCommonCallback<Result<Version>>() {
            @Override
            public void onSuccess(Result<Version> result) {
                RunInfo.version = result.getData();
                if(callback!=null) {
                    callback.call(result.getData());
                }
            }
        });
    }

    public void downloadUpgrade(Context context, String apkPath, Handler handler) {
        if(TextUtils.isEmpty(apkPath)) {
            return;
        }

        //File apkSavePath = new File(Environment.getExternalStorageDirectory(), "apk");
        //File apkSavePath = new File(context.getFilesDir(), "apk");
        File apkSavePath =context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if(!apkSavePath.exists()) {
            boolean b = apkSavePath.mkdirs();
        }
        String pathname = Util.getFilename(apkPath);
        File apkFile = new File(apkSavePath, "tv_" + pathname);
        NetParams netParams = new NetParams(apkPath, false, false);
        String filename = apkFile.getAbsolutePath();
        netParams.setSaveFilePath(filename);
        x.http().get(netParams, new AbstractProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                Log.d("upgrade", "apk >>> "+  result.getAbsolutePath());
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = result.getAbsolutePath();
                handler.sendMessage(msg);
            }
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //Log.d("upgrade", ">>> current >>> " + current);
                Message msg = Message.obtain();
                int progress = (int) (( current / (double)total) * 100);
                msg.what = 0;
                msg.obj = progress;
                handler.sendMessage(msg);
            }
        });
    }
}
