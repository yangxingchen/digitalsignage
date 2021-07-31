package com.syzbtech.screen.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import qiu.niorgai.StatusBarCompat;

public class BaseActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    protected Map<Integer, View> focusView = new HashMap<>();

    protected BaseActivity addFocusView(View ...views) {
        for(View view: views) {
            focusView.put(view.getId(), view);
        }
        return this;
    }

    protected void setFocusViewListener() {
        for(Map.Entry<Integer, View> e : focusView.entrySet()) {
            e.getValue().setOnFocusChangeListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.translucentStatusBar(this);
        StatusBarCompat.translucentStatusBar(this, true);
        x.view().inject(this);
        showPermissions();
    }

    private static final int PERMISSION_REQ_CODE = 100;

    //请求权限
    public void showPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE,
            }, PERMISSION_REQ_CODE);
        } else {
            // PERMISSION_GRANTED
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISSION_GRANTED
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        for(Map.Entry<Integer, View> e : focusView.entrySet()) {
            View view = e.getValue();
            view.setSelected(false);
            if(view instanceof RadioButton) {
                RadioButton button = (RadioButton) view;
                button.setChecked(false);
            }
        }
        if(hasFocus) {
            v.setSelected(true);
            if(v instanceof RadioButton) {
                RadioButton button = (RadioButton) v;
                button.setChecked(true);
            }
        }
    }
}