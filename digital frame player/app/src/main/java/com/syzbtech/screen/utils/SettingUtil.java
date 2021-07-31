package com.syzbtech.screen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.syzbtech.screen.R;
import com.syzbtech.screen.entities.Setting;

import java.util.ArrayList;
import java.util.List;

public class SettingUtil {

    public static final int PLAY_SELECTOR_VIDEO_MUSIC = 1;
    public static final int PLAY_SELECTOR_VIDEO = 2;
    public static final int PLAY_SELECTOR_MUSIC = 3;

    private Context context;
    private String localPlaySettingFileName;

    private SettingUtil(){}
    public static SettingUtil init() {
        SettingUtil util = new SettingUtil();
        return util;
    }

    public SettingUtil setContext(Context context) {
        this.context = context;
        LocalPlayProperty.init(context);
        return this;
    }

    public SettingUtil setLocalPlaySettingFileName(String name) {
        this.localPlaySettingFileName = name;
        return this;
    }

    public void build() {
        localPlaySetting();
    }

    private void localPlaySetting() {
        localPlaySetting = new LocalPlaySetting(context, localPlaySettingFileName, Context.MODE_PRIVATE);
    }

    public static class LocalPlayProperty{
        public static String [] PLAY_SELECTOR;
        public static String [] BK_MUSIC;
        public static String [] PLAY_TIME_STR;

        public static String [] PLAY_ANIM;
        public static String [] SCREEN_RATE;
        public static String [] IMAGE_MODE;
        public static String [] ViDEO_MODE;
        public static String [] MUSIC_MODE;
        public static String [] AUTO_UPDATE_FILE;

        public static final int [] PLAY_TIME = new int[] {0, 3, 6, 10, 20, 30, 60};
        public static final String [] KEYS = new String[] {"PLAY_SELECTOR", "BK_MUSIC", "PLAY_TIME_STR","PLAY_ANIM","SCREEN_RATE","IMAGE_MODE","VIDEO_MODE","MUSIC_MODE"};
        public static String [][] PROP_ATTR;

        public static void init(Context context) {
            Resources res = context.getResources();
            PLAY_SELECTOR = new String[]{"", res.getString(R.string.video_and_image), res.getString(R.string.video), res.getString(R.string.image)};
            BK_MUSIC = new String[]{"", res.getString(R.string.open), res.getString(R.string.close)};
            PLAY_TIME_STR = new String[] {"",res.getString(R.string.s3),res.getString(R.string.s6),res.getString(R.string.s10), res.getString(R.string.s20), res.getString(R.string.s30), res.getString(R.string.s60)};
            PLAY_ANIM = new String[] {"",res.getString(R.string.has_not), res.getString(R.string.has)};
            SCREEN_RATE = new String []{"",res.getString(R.string.full_screen), res.getString(R.string.origin_size)};
            IMAGE_MODE = new String[] {"", res.getString(R.string.all_loop),res.getString(R.string.single_loop)};
            ViDEO_MODE = new String[] {"", res.getString(R.string.all_loop),res.getString(R.string.single_loop)};
            MUSIC_MODE = new String[] {"", res.getString(R.string.all_loop),res.getString(R.string.single_muisc_loop)};
            AUTO_UPDATE_FILE = new String[]{"",res.getString(R.string.open), res.getString(R.string.close)};
            PROP_ATTR = new String[][] {PLAY_SELECTOR, BK_MUSIC,PLAY_TIME_STR,PLAY_ANIM,SCREEN_RATE,IMAGE_MODE,ViDEO_MODE,MUSIC_MODE};
        }
    }


    public static LocalPlaySetting localPlaySetting;

    public static class LocalPlaySetting {

        private SharedPreferences sharedPreferences;

        private LocalPlaySetting(Context context, String name, int model) {
            sharedPreferences = context.getSharedPreferences(name, model);
            if(isFirstOpen()) {
                setDefault();
                setFirstOpenFalse();
            }
        }

        //默认设置
        public void setDefault() {
            setPlaySelector(3);
            setBkMusic(1);
            setPlayTime(3);
            setPlayAnim(1);
            setScreenRate(1);
            setImageMode(1);
            setVideoMode(1);
            setMusicMode(1);
            setAutoUpdateFile(1);
        }

        public List<Setting> listLocalPlaySetting() {
            List<Setting> dataList = new ArrayList<>();
            dataList.add(new Setting(0, R.mipmap.ic_play_setting, R.string.play_select, getPlaySelectorStr(),true));
            dataList.add(new Setting(1, R.mipmap.ic_bk_music,R.string.bk_music,getBkMusicStr()));
            dataList.add(new Setting(2,R.mipmap.ic_time,R.string.play_time, getPlayTimeStr()));
            dataList.add(new Setting(3, R.mipmap.ic_animation,R.string.paly_anim, getPlayAnimStr()));
            dataList.add(new Setting(4, R.mipmap.ic_screen_rate, R.string.screen_rate, getScreenRateStr()));
            dataList.add(new Setting(5, R.mipmap.ic_photo,R.string.image_mode, getImageModeStr()));
            dataList.add(new Setting(6, R.mipmap.ic_video,R.string.video_mode, getVideoModeStr()));
            dataList.add(new Setting(7, R.mipmap.ic_music,R.string.music_mode, getMusicModeStr()));

            return dataList;
        }

        public String nextAutoUpdateFile(int arrow) {
            return nextSetting(arrow, "auto_update_file", SettingUtil.LocalPlayProperty.AUTO_UPDATE_FILE);
        }

        public String getAutoUpdateFileStr() {
            int index = getAutoUpdateFile();
            return LocalPlayProperty.AUTO_UPDATE_FILE[index];
        }

        public int getAutoUpdateFile() {
            return sharedPreferences.getInt("auto_update_file", 1);
        }

        public void setAutoUpdateFile(int flag) {
            sharedPreferences.edit().putInt("auto_update_file", flag).apply();
        }
        public boolean isFirstOpen() {
            return sharedPreferences.getBoolean("local_play_setting_first", true);
        }
        public void setFirstOpenFalse() {
            sharedPreferences.edit().putBoolean("local_play_setting_first", true).apply();
        }

        public void setPlaySelector(int selector) {
            sharedPreferences.edit().putInt("PLAY_SELECTOR", selector).apply();
        }
        public int getPlaySelector() {
            return sharedPreferences.getInt("PLAY_SELECTOR", 0);
        }

        public String getPlaySelectorStr() {
            int selector = getPlaySelector();
            return LocalPlayProperty.PLAY_SELECTOR[selector];
        }

        public void setBkMusic(int bkMusic) {
            sharedPreferences.edit().putInt("BK_MUSIC", bkMusic).apply();
        }
        public int getBkMusic() {
            return sharedPreferences.getInt("BK_MUSIC", 0);
        }

        public String getBkMusicStr(){
            int bkMuisc=getBkMusic();
            return LocalPlayProperty.BK_MUSIC[bkMuisc];
        }

        public void setPlayTime(int index) {
            sharedPreferences.edit().putInt("PLAY_TIME_STR", index).apply();
        }

        public int getPlayTime() {
            return sharedPreferences.getInt("PLAY_TIME_STR", 0);
        }

        public String getPlayTimeStr() {
            int index = getPlayTime();
            return LocalPlayProperty.PLAY_TIME_STR[index];
        }

        public int getPlayTimeMinus() {
            int index=getPlayTime();
            return LocalPlayProperty.PLAY_TIME[index];
        }

        public void setPlayAnim(int anim) {
            sharedPreferences.edit().putInt("PLAY_ANIM", anim).apply();
        }
        public int getPlayAnim() {
            return sharedPreferences.getInt("PLAY_ANIM", 0);
        }
        public String getPlayAnimStr() {
            int anim = getPlayAnim();
            return LocalPlayProperty.PLAY_ANIM[anim];
        }

        public String getProperty(int index) {
            String key = SettingUtil.LocalPlayProperty.KEYS[index];
            int idx = sharedPreferences.getInt(key, 0);
            return SettingUtil.LocalPlayProperty.PROP_ATTR[index][idx];
        }

        public void setScreenRate(int rate) {
            sharedPreferences.edit().putInt("SCREEN_RATE", rate).apply();
        }
        public int getScreenRate() {
            return sharedPreferences.getInt("SCREEN_RATE", 0);
        }
        public String getScreenRateStr() {
            int rate = getScreenRate();
            return LocalPlayProperty.SCREEN_RATE[rate];
        }

        public void setImageMode(int mode) {
            sharedPreferences.edit().putInt("IMAGE_MODE", mode).apply();
        }

        public int getImageMode() {
            return sharedPreferences.getInt("IMAGE_MODE",0);
        }

        public String getImageModeStr() {
            int mode = getImageMode();
            return LocalPlayProperty.IMAGE_MODE[mode];
        }

        public void setVideoMode(int mode) {
            sharedPreferences.edit().putInt("VIDEO_MODE", mode).apply();
        }

        public int getVideoMode() {
            return sharedPreferences.getInt("VIDEO_MODE",0);
        }

        public String getVideoModeStr() {
            int mode = getVideoMode();
            return LocalPlayProperty.ViDEO_MODE[mode];
        }

        public void setMusicMode(int mode) {
            sharedPreferences.edit().putInt("MUSIC_MODE", mode).apply();
        }

        public int getMusicMode() {
            return sharedPreferences.getInt("MUSIC_MODE",0);
        }

        public String getMusicModeStr() {
            int mode = getMusicMode();
            return LocalPlayProperty.MUSIC_MODE[mode];
        }

        public String nextPlaySelector(int arrow) {
            return nextSetting(arrow, "PLAY_SELECTOR", LocalPlayProperty.PLAY_SELECTOR);
        }

        public String nextBkMusic(int arrow){
            return nextSetting(arrow, "BK_MUSIC", LocalPlayProperty.BK_MUSIC);
        }

        public String nextPlayTime(int arrow) {
            return nextSetting(arrow, "PLAY_TIME_STR", LocalPlayProperty.PLAY_TIME_STR);
        }

        public String nextPlayAnim(int arrow) {
            return nextSetting(arrow, "PLAY_ANIM", LocalPlayProperty.PLAY_ANIM);
        }

        public String nexScreenRate(int arrow) {
            return nextSetting(arrow, "SCREEN_RATE", LocalPlayProperty.SCREEN_RATE);
        }

        public String nextImageMode(int arrow) {
            return nextSetting(arrow, "IMAGE_MODE", LocalPlayProperty.IMAGE_MODE);
        }
        public String nextVideoMode(int arrow) {
            return nextSetting(arrow, "ViDEO_MODE", LocalPlayProperty.ViDEO_MODE);
        }

        public String nextMusicMode(int arrow) {
            return nextSetting(arrow, "MUSIC_MODE", LocalPlayProperty.MUSIC_MODE);
        }

        public String nextSetting(int arrow, String key, String [] properties) {
            int property = sharedPreferences.getInt(key,0);
            if(arrow>0) {
                property++;
                if(property>=properties.length) {
                    property = properties.length-1;
                }
            } else if(arrow<0) {
                property--;
                if(property<1) {
                    property = 1;
                }
            }
            sharedPreferences.edit().putInt(key, property).apply();
            return properties[property];
        }
    }
}
