package com.syzbtech.screen.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.syzbtech.screen.R;
import com.syzbtech.screen.adapter.LocalPlayerFragmentAdapter;
import com.syzbtech.screen.animation.ZoomOutSlideTransformer;
import com.syzbtech.screen.dao.LocalFileDao;
import com.syzbtech.screen.entities.LocalFile;
import com.syzbtech.screen.fragment.LocalPlayerFragment;
import com.syzbtech.screen.utils.SettingUtil;
import com.syzbtech.screen.utils.Util;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.syzbtech.screen.utils.SettingUtil.PLAY_SELECTOR_MUSIC;
import static com.syzbtech.screen.utils.SettingUtil.PLAY_SELECTOR_VIDEO;
import static com.syzbtech.screen.utils.SettingUtil.PLAY_SELECTOR_VIDEO_MUSIC;

@ContentView(R.layout.activity_local_player)
public class LocalPlayerActivity extends BaseActivity  implements ViewPager.OnPageChangeListener{

    private String TAG = LocalPlayerActivity.class.getName();

    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;

    @ViewInject(R.id.empty)
    private RelativeLayout rlEmpty;

    private List<LocalPlayerFragment> fragmentList = new ArrayList<>();
    private LocalPlayerFragmentAdapter adapter;

    private List<LocalFile> bkMusicList = new ArrayList<>();

    private MediaPlayer mediaPlayer;
    private boolean isBkMusicPause = true;

    private int currentBkMusic = 0;

    private int current = 0;

    public void nextItem() {

        current++;
        if(current>=fragmentList.size()) {
           current=0;
        }
        viewPager.setCurrentItem(current);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        initData();
    }

    private void setupView() {
        Util.setViewPagerScroller(this, viewPager);
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        adapter = new LocalPlayerFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void initData() {
        //获取播放选择
        loadLocalFile();
        loadBkMusic();
    }

    private void loadBkMusic() {
        int bkMusic = SettingUtil.localPlaySetting.getBkMusic();
        if(bkMusic==1) {
            bkMusicList = LocalFileDao.instance().listByType(3);
        }
    }

    public void startBkMusic() {
        if(bkMusicList.size()==0 ) {
           return;
        }
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                String path = bkMusicList.get(currentBkMusic).getPath();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isBkMusicPause = false;
                mediaPlayer.setOnCompletionListener(mp -> {
                    nextBackgroundMusic();
                });
            } else {
                if(!mediaPlayer.isPlaying() && isBkMusicPause) {
                    mediaPlayer.start();
                    isBkMusicPause = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBkMusic() {
        if(mediaPlayer!=null) {
            mediaPlayer.pause();
            isBkMusicPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public void nextBackgroundMusic(){

        int musicMode = SettingUtil.localPlaySetting.getMusicMode();
        Log.d(TAG, "music mode >>> "+ musicMode);
        if(musicMode==1) { //全部循环
            currentBkMusic++;
            if (currentBkMusic >= bkMusicList.size()) {
                currentBkMusic = 0;
            }
        }
        try {
            mediaPlayer.reset();
            String path = bkMusicList.get(currentBkMusic).getPath();
            Log.d(TAG,"music path >>> " + path);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadLocalFile() {
        int playSelector = SettingUtil.localPlaySetting.getPlaySelector();
        List<Integer> typeList = null;
        switch (playSelector) {
            case PLAY_SELECTOR_VIDEO_MUSIC:
                typeList = Arrays.asList(1, 2);
                break;
            case PLAY_SELECTOR_VIDEO:
                typeList = Collections.singletonList(2);
                break;
            case PLAY_SELECTOR_MUSIC:
                typeList = Collections.singletonList(1);
                break;
        }

        List<LocalFile> localFileList = LocalFileDao.instance().listByType(typeList);
        if(localFileList.size()==0) {
            rlEmpty.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        } else {
            rlEmpty.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            for (LocalFile localFile : localFileList) {
                LocalPlayerFragment fragment = LocalPlayerFragment.newInstance(localFile);
                fragmentList.add(fragment);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        current = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}