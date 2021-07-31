package com.syzbtech.screen.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.syzbtech.screen.MediaCacheService;
import com.syzbtech.screen.R;
import com.syzbtech.screen.RunInfo;
import com.syzbtech.screen.adapter.CloudPlayerFragmentAdapter;
import com.syzbtech.screen.animation.ZoomOutSlideTransformer;
import com.syzbtech.screen.dao.MediaCacheDao;
import com.syzbtech.screen.entities.DeviceMedia;
import com.syzbtech.screen.entities.DeviceMediaVo;
import com.syzbtech.screen.entities.MediaCache;
import com.syzbtech.screen.entities.PlayBkMusic;
import com.syzbtech.screen.entities.PlaySetting;
import com.syzbtech.screen.fragment.CloudPlayerFragment;
import com.syzbtech.screen.http.AbstractCommonCallback;
import com.syzbtech.screen.http.Api;
import com.syzbtech.screen.http.NetParams;
import com.syzbtech.screen.utils.SettingUtil;
import com.syzbtech.screen.utils.Util;
import com.syzbtech.screen.view.CommonDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_cloud_player)
public class CloudPlayerActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private String TAG = CloudPlayerActivity.class.getName();

    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;

    @ViewInject(R.id.time)
    private TextView tvTime;

    private List<CloudPlayerFragment> fragmentList = new ArrayList<>();

    private CloudPlayerFragmentAdapter adapter;

    //背景音乐
    private List<PlayBkMusic> bkMusicList = new ArrayList<>();
    private MediaPlayer bkMusicPlayer;
    private int currentBkMusic = 0;
    private boolean isBkMusicPause = true;

    private PlaySetting playSetting;

    private int current = 0;

    public void nextItem() {

        current++;
        if(current>=fragmentList.size()) {
            if(playSetting.getCycleType()==1) {
                current = fragmentList.size()-1;
            } else {
                current = 0;
            }
        }
        viewPager.setCurrentItem(current);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CloudPlayerFragmentAdapter(getSupportFragmentManager(), fragmentList);

        MediaCacheService.instance().loadAndCacheToStorage();

        Util.setViewPagerScroller(this, viewPager);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        //镜像翻转
        //viewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        listDeviceMedia();
    }


    private void listDeviceMedia() {
        NetParams netParams = new NetParams(Api.LIST_DEVICE_MEDIA, false, false);
        netParams.addBodyParameter("deviceId", RunInfo.device.getId());
        netParams.addBodyParameter("userId", RunInfo.device.getUserId());
        x.http().post(netParams, new AbstractCommonCallback<DeviceMediaVo>() {
            @Override
            public void onSuccess(DeviceMediaVo result) {
                runOnUiThread(()->{
                    if(result!=null && result.getDeviceMediaList() != null && result.getDeviceMediaList().size() != 0) {

                        playSetting = result.getPlaySetting();
                        for(DeviceMedia deviceMedia : result.getDeviceMediaList()) {
                            fragmentList.add(CloudPlayerFragment.newInstance(deviceMedia.getMedia(), playSetting));
                            adapter.notifyDataSetChanged();
                        }

                        if(playSetting.getShowTime()==2) {
                            tvTime.setVisibility(View.VISIBLE);
                            tvTime.setText(Util.formatNowDateTime());
                        }

                        List<PlayBkMusic> musicList = result.getMusicList();
                        if(musicList!=null) {
                            bkMusicList.addAll(musicList);
                        }

                    } else {

                        CommonDialog commonDialog = new CommonDialog(CloudPlayerActivity.this);
                        commonDialog.setMessage(getString(R.string.msg_not_media_to_play)).setTitle(getString(R.string.hint))
                                .setSingle(true)
                                .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        CloudPlayerActivity.this.finish();
                                        commonDialog.dismiss();
                                    }
                                    @Override
                                    public void onNegtiveClick() {
                                        commonDialog.dismiss();
                                    }
                                });
                        commonDialog.show();


                    }
                });
            }
        });
    }

    public void startBkMusic() {
        try {
            if (bkMusicList.size() != 0) {
                if (bkMusicPlayer == null) {
                    bkMusicPlayer = new MediaPlayer();
                    PlayBkMusic music = bkMusicList.get(currentBkMusic);
                    MediaCache mediaCache = MediaCacheDao.instance().getByMd5(music.getMedia().getMd5());
                    String path = Api.host + music.getMedia().getPath();
                    if(mediaCache!=null) {
                        path = mediaCache.getPath();
                    }
                    Log.d(TAG, "music path >> " + path);
                    bkMusicPlayer.setDataSource(path);
                    bkMusicPlayer.prepare();
                    bkMusicPlayer.start();
                    isBkMusicPause = false;
                    bkMusicPlayer.setOnCompletionListener(mp -> {
                        nextBkMusic();
                    });
                } else {
                    if(!bkMusicPlayer.isPlaying() && isBkMusicPause) {
                        bkMusicPlayer.start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseBkMusic() {
        if(bkMusicPlayer!=null && bkMusicPlayer.isPlaying()) {
            bkMusicPlayer.pause();
            isBkMusicPause=true;
        }
    }

    private void nextBkMusic() {
        try {
            if (bkMusicList.size() != 0 && bkMusicPlayer != null) {
                currentBkMusic++;
                if (currentBkMusic >= bkMusicList.size()) {
                    currentBkMusic = 0;
                }
                PlayBkMusic music = bkMusicList.get(currentBkMusic);
                MediaCache mediaCache = MediaCacheDao.instance().getByMd5(music.getMedia().getMd5());
                String path = Api.host + music.getMedia().getPath();
                if(mediaCache!=null) {
                    path = mediaCache.getPath();
                }
                Log.d(TAG, "music path >> " + path);
                bkMusicPlayer.setDataSource(path);
                bkMusicPlayer.reset();
                bkMusicPlayer.setDataSource(path);
                bkMusicPlayer.prepare();
                bkMusicPlayer.start();
                isBkMusicPause = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseBkMusicPlayer() {
        if(bkMusicPlayer!=null) {
            bkMusicPlayer.stop();
            bkMusicPlayer.release();
            bkMusicPlayer = null;
            isBkMusicPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        releaseBkMusicPlayer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
