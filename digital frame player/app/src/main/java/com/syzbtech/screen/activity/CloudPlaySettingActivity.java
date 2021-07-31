package com.syzbtech.screen.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.syzbtech.screen.MediaCacheService;
import com.syzbtech.screen.R;
import com.syzbtech.screen.RunInfo;
import com.syzbtech.screen.http.Api;
import com.syzbtech.screen.service.UpgradeService;
import com.syzbtech.screen.utils.NetWorkUtil;
import com.syzbtech.screen.utils.QRCodeUtil;
import com.syzbtech.screen.utils.Util;
import com.syzbtech.screen.view.CommonDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_cloud_play)
public class CloudPlaySettingActivity extends BaseActivity {

    @ViewInject(R.id.tvAccountState)
    private TextView tvAccountState;

    @ViewInject(R.id.tvNetworkState)
    private TextView tvNetworkState;

    @ViewInject(R.id.icNetwork)
    private ImageView ivWifi;

    @ViewInject(R.id.barcode)
    private ImageView barcode;

    @ViewInject(R.id.tvDeviceCode)
    private TextView tvCode;

    @ViewInject(R.id.tvVersion)
    private TextView tvVersion;

    @ViewInject(R.id.serverIp)
    private TextView tvServerIp;

    @ViewInject(R.id.tvClearState)
    private TextView tvClearState;

    @ViewInject(R.id.hasNew)
    private TextView tvHasNew;

    private float codeWidth =0;
    private float codeHeight = 0;

    private UpgradeHandler upgradeHandler;
    private ProgressDialog upgradeProcessDlg;

    @ViewInject(R.id.rlNetworkSet)
    private RelativeLayout rlNetworkSet;
    @ViewInject(R.id.rlAccount)
    private RelativeLayout rlAccount;
    @ViewInject(R.id.rlClear)
    private RelativeLayout rlClear;
    @ViewInject(R.id.rlUpgrade)
    private RelativeLayout rlUpgrade;

    @ViewInject(R.id.rlBackPlay)
    private LinearLayout rlBackPlay;

    @SuppressLint("HandlerLeak")
    private class UpgradeHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {

            if(msg.what==0) {
                if(upgradeProcessDlg!=null) {
                    upgradeProcessDlg.setProgress((int) msg.obj);
                }
            } else if(msg.what==1) {
                if(upgradeProcessDlg!=null) {
                    upgradeProcessDlg.dismiss();
                    upgradeProcessDlg = null;
                }

                Util.install(getApplicationContext(), (String) msg.obj);
            }

            super.handleMessage(msg);
        }
    }



    private class DeviceBindReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("action.device.bind.changed".equals(action)) {
                Log.d("JWebSClientService", "cloud >>> " + RunInfo.device);
                if(RunInfo.device!=null && RunInfo.device.getUser()!=null) {
                    tvAccountState.setText(RunInfo.device.getUser().getMobile());
                } else{
                    tvAccountState.setText(R.string.txt_account_status);
                }
            }
        }
    }



    private DeviceBindReceiver deviceBindReceiver = new DeviceBindReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFocusView(rlNetworkSet, rlAccount, rlClear, rlUpgrade, rlBackPlay).setFocusViewListener();

        deviceBingChangedReceiver();

        codeWidth = getResources().getDimension(R.dimen.dp110);
        codeHeight = getResources().getDimension(R.dimen.dp110);
        tvNetworkState.setFocusable(true);
        tvNetworkState.setFocusableInTouchMode(true);

        upgradeHandler = new UpgradeHandler();

        UpgradeService.instance().getUpgradeInfo(version -> {
           if(version!=null) {
               runOnUiThread(()->{
                   String currentVersion = Util.getAppVersionName(getApplicationContext());
                   if(!currentVersion.equals(version.getVersionName())) {
                       tvHasNew.setVisibility(View.VISIBLE);
                   }
               });
           }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        setCodeInfo();
        tvVersion.setText(Util.getAppVersionName(this));

        new Thread(){
            @Override
            public void run() {
                String ip = NetWorkUtil.getInetAddress(Api.serverIp);
                runOnUiThread(()->{
                    tvServerIp.setText(ip);
                });
            }
        }.start();

        if(RunInfo.device!=null && RunInfo.device.getUser()!=null) {
            tvAccountState.setText(RunInfo.device.getUser().getMobile());
        } else{
            tvAccountState.setText(R.string.txt_account_status);
        }

        boolean network = NetWorkUtil.isNetWorkEnable(this);
        if(network) {
            tvNetworkState.setText(R.string.network_enable);
            int level = NetWorkUtil.getNetworkWifiLevel(this);
            ivWifi.setImageLevel(level);
        } else {
            tvNetworkState.setText(R.string.network_not_set);
        }
        setCacheState();
    }

    private void setCacheState() {
        String cacheTotal = MediaCacheService.instance().countCacheTotalSize(getResources().getString(R.string.txt_already_clear));
        tvClearState.setText(cacheTotal);
    }


    private void deviceBingChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.device.bind.changed");
        registerReceiver(deviceBindReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(deviceBindReceiver);
        } catch (Exception e){}
        super.onDestroy();
    }

    private void setCodeInfo() {
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        String codeLabel = getString(R.string.label_code);
        tvCode.setText(codeLabel + RunInfo.deviceCode);
        Bitmap code = QRCodeUtil.createQRImage(RunInfo.deviceCode, (int)codeWidth, (int)codeHeight, logo);
        barcode.setImageBitmap(code);
    }

    @Event({R.id.rlNetworkSet, R.id.rlBackPlay, R.id.rlClear, R.id.rlUpgrade})
    private void viewClick(View v) {
        switch (v.getId()) {
            case R.id.rlNetworkSet:
                openNetworkSetting();
                break;
            case R.id.rlBackPlay:
                openPlayer();
                break;
            case R.id.rlClear:
                clearCache();
                break;
            case R.id.rlUpgrade:
                upgrade();
                break;
        }
    }


    private void ShowUpgradeProcessDlg() {
        if(upgradeProcessDlg==null) {
            upgradeProcessDlg = new ProgressDialog(this);
            upgradeProcessDlg.setCanceledOnTouchOutside(false);
            upgradeProcessDlg.setMax(100);
            upgradeProcessDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            upgradeProcessDlg.setTitle(R.string.title_wait_upgrade);
            upgradeProcessDlg.show();
        }
    }

    private void upgrade() {

        if(RunInfo.version==null) {
            Toast.makeText(this, "已是最新版本,无需更新", Toast.LENGTH_LONG).show();
            return;
        }

        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setTitle(getString(R.string.hint));
        commonDialog.setMessage(getResources().getString(R.string.msg_hint_upgrade_version) + RunInfo.version.getVersionName());
        commonDialog.setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
                commonDialog.dismiss();
                ShowUpgradeProcessDlg();
                Log.d("upgrade", ">>>> "+ RunInfo.version.getPath());
                UpgradeService.instance().downloadUpgrade(getApplicationContext(), RunInfo.version.getPath(), upgradeHandler);
            }

            @Override
            public void onNegtiveClick() {
                commonDialog.dismiss();
            }
        });
        commonDialog.show();
    }

    //清空缓存
    private void clearCache() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.txt_clear_cache);
        dialog.show();
        MediaCacheService.instance().clearCache();
        tvClearState.setText(R.string.txt_already_clear);
        dialog.dismiss();
    }

    private void openPlayer() {

        if(RunInfo.device!=null && RunInfo.device.getUser()!=null) {
            startActivity(new Intent(this, CloudPlayerActivity.class));
        } else {

            CommonDialog commonDialog = new CommonDialog(this);
            commonDialog.setMessage(getString(R.string.empty_cloud_media)).setTitle(getString(R.string.hint));
            commonDialog.setPositive(getString(R.string.back_home));
            commonDialog.setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick() {
                    CloudPlaySettingActivity.this.finish();
                }

                @Override
                public void onNegtiveClick() {
                    commonDialog.dismiss();
                }
            });
            commonDialog.show();
        }
    }

    private void openNetworkSetting() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
    }
}