package com.syzbtech.screen.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.syzbtech.screen.R;
import com.syzbtech.screen.adapter.FileFragmentAdapter;
import com.syzbtech.screen.fragment.LocalFileFragment;
import com.syzbtech.screen.utils.Util;
import com.syzbtech.screen.view.CommonDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;


@ContentView(R.layout.activity_file)
public class FileActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = FileActivity.class.getName();
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;

    @ViewInject(R.id.rg_tab_bar)
    private RadioGroup rbTabbar;

    @ViewInject(R.id.rbImage)
    private RadioButton rbImage;
    @ViewInject(R.id.rbVideo)
    private RadioButton rbVideo;
    @ViewInject(R.id.rbMusic)
    private RadioButton rbMusic;

    private RadioButton [] rbs ;

    private int currentItem = 0;

    private FileFragmentAdapter adapter;

    @ViewInject(R.id.back)
    private LinearLayout back;

    @ViewInject(R.id.ll_top)
    private LinearLayout top;
    @ViewInject(R.id.ll_bottom)
    private LinearLayout bottom;
    @ViewInject(R.id.ll_down)
    private LinearLayout down;
    @ViewInject(R.id.ll_up)
    private LinearLayout up;
    @ViewInject(R.id.ll_delete)
    private LinearLayout delete;

    @Event({R.id.back, R.id.ll_top, R.id.ll_bottom, R.id.ll_up, R.id.ll_down, R.id.ll_delete})
    private void click(View view) {
        int id =view.getId();
        LocalFileFragment fragment = adapter.getItem(currentItem);
        view.setSelected(true);
        switch (id) {
            case R.id.back:
                finish();
                break;
            case R.id.ll_top:
                fragment.doTop();
                break;
            case R.id.ll_bottom:
                fragment.doBottom();
                break;
            case R.id.ll_down:
                fragment.doDown();
                break;
            case R.id.ll_up:
                fragment.doUp();
                break;
            case R.id.ll_delete:
                if(!fragment.isSelect()){
                    Toast.makeText(this,R.string.hint_delete_content, Toast.LENGTH_LONG).show();
                    return;
                }
                fragment.doDelete();
                break;
        }

        fragment.unSelectAll();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFocusView(back,rbImage,rbVideo,rbMusic, top, bottom, up, down, delete).setFocusViewListener();

        rbs = new RadioButton[] {
                rbImage, rbVideo, rbMusic
        };

        adapter = new FileFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        rbTabbar.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG, ">>>> " + checkedId);
        switch (checkedId) {
            case R.id.rbImage:
                currentItem = 0;
                break;
            case R.id.rbVideo:
                currentItem = 1;
                break;
            case R.id.rbMusic:
                currentItem = 2;
                break;
        }
        viewPager.setCurrentItem(currentItem);
    }


}