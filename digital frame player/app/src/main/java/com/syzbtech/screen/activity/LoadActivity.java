package com.syzbtech.screen.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syzbtech.screen.R;
import com.syzbtech.screen.RunInfo;
import com.syzbtech.screen.entities.Device;
import com.syzbtech.screen.http.AbstractCommonCallback;
import com.syzbtech.screen.http.Api;
import com.syzbtech.screen.http.NetParams;
import com.syzbtech.screen.http.Result;
import com.syzbtech.screen.service.CopyFileService;
import com.syzbtech.screen.service.WebSocketService;
import com.syzbtech.screen.utils.ActivityUtil;
import com.syzbtech.screen.utils.QRCodeUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


@ContentView(R.layout.activity_load)
public class LoadActivity extends BaseActivity {

    private String TAG = LoadActivity.class.getName();

    @ViewInject(R.id.img_device)
    private ImageView ivDevice;

    @ViewInject(R.id.img_download)
    private ImageView ivDownload;

    @ViewInject(R.id.tvCode)
    private TextView tvCode;

    @ViewInject(R.id.btn_local_play)
    private LinearLayout llLocalPlay;

    @ViewInject(R.id.btn_cloud_play)
    private LinearLayout llCloudPlay;

    private float codeWidth = 0;
    private float codeHeight = 0;
    private Bitmap logo;

    private WebSocketServiceConnection webServiceConnection;
    private CopyFileServiceConnection copyFileServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFocusView(llLocalPlay, llCloudPlay).setFocusViewListener();

        llLocalPlay.setSelected(true);
        codeWidth = getResources().getDimension(R.dimen.dp110);
        codeHeight = getResources().getDimension(R.dimen.dp110);
        logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        bindService();
        setCodeInfo();
        setDownloadInfo();
    }

    private void bindService() {
        Intent webService = new Intent();
        webService.setClass(getApplication(), WebSocketService.class);
        webServiceConnection = new WebSocketServiceConnection();
        bindService(webService, webServiceConnection, BIND_AUTO_CREATE);

        Intent copyFileService = new Intent();
        copyFileService.setClass(getApplication(), CopyFileService.class);
        copyFileServiceConnection = new CopyFileServiceConnection();
        bindService(copyFileService, copyFileServiceConnection, BIND_AUTO_CREATE);
    }

    private void setCodeInfo() {
        String codeLabel = getString(R.string.label_code);
        tvCode.setText(codeLabel + RunInfo.deviceCode);
        Bitmap code = QRCodeUtil.createQRImage(RunInfo.deviceCode, (int)codeWidth, (int)codeHeight, logo);
        ivDevice.setImageBitmap(code);
    }

    private void setDownloadInfo() {
        String url = Api.host+"index.html";
        Bitmap downloadImage = QRCodeUtil.createQRImage(url, (int)codeWidth, (int)codeHeight, logo);
        ivDownload.setImageBitmap(downloadImage);
    }

    @Override
    protected void onDestroy() {
        unbindService(webServiceConnection);
        unbindService(copyFileServiceConnection);
        super.onDestroy();
    }

    private class WebSocketServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.LocalBinder binder = (WebSocketService.LocalBinder) service;
            RunInfo.webSocketService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            RunInfo.webSocketService = null;
        }
    }

    //Upload service binder.
    private class CopyFileServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CopyFileService.LocalBinder binder = (CopyFileService.LocalBinder) service;
            RunInfo.copyFileService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            RunInfo.copyFileService = null;
        }
    }


    @Event({R.id.btn_local_play,R.id.btn_cloud_play})
    private void playClick(View view) {
        int id=view.getId();
        switch (id) {
            case R.id.btn_local_play:
                ActivityUtil.navigateTo(this, LocalPlayActivity.class);
                break;
            case R.id.btn_cloud_play:
                ActivityUtil.navigateTo(this, CloudPlaySettingActivity.class);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDevice();
    }

    private void getDevice() {

        NetParams netParams = new NetParams(Api.GET_DEVICE, false, false);
        netParams.addQueryStringParameter("code", RunInfo.deviceCode);
        x.http().get(netParams, new AbstractCommonCallback<Result<Device>>(){
            @Override
            public void onSuccess(Result<Device> result) {
                Log.d(TAG, ">>>> "+ result.getData());
                RunInfo.device = result.getData();
            }
        });
    }

}