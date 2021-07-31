package com.syzbtech.screen;

import android.annotation.SuppressLint;
import android.util.Log;

import com.syzbtech.screen.dao.MediaCacheDao;
import com.syzbtech.screen.entities.DeviceMedia;
import com.syzbtech.screen.entities.DeviceMediaVo;
import com.syzbtech.screen.entities.Media;
import com.syzbtech.screen.entities.MediaCache;
import com.syzbtech.screen.entities.PlayBkMusic;
import com.syzbtech.screen.http.AbstractCommonCallback;
import com.syzbtech.screen.http.Api;
import com.syzbtech.screen.http.NetParams;

import org.xutils.x;

import java.io.File;
import java.util.List;

public class MediaCacheService {

    private static MediaCacheService mInstance = new MediaCacheService();
    private MediaCacheService(){}
    public static MediaCacheService instance() {
        return mInstance;
    }

    public void loadAndCacheToStorage() {
        if(RunInfo.device!=null && RunInfo.device.getUserId()!=null) {
            NetParams netParams = new NetParams(Api.LIST_DEVICE_MEDIA, false, false);
            netParams.addBodyParameter("deviceId", RunInfo.device.getId());
            netParams.addBodyParameter("userId", RunInfo.device.getUserId());
            x.http().post(netParams, new AbstractCommonCallback<DeviceMediaVo>() {
                @Override
                public void onSuccess(DeviceMediaVo result) {
                    List<DeviceMedia> mediaList = result.getDeviceMediaList();
                    if(mediaList!=null) {
                        for (DeviceMedia media : mediaList) {
                            cacheMedia(media.getMedia());
                        }
                    }
                    //缓存背景音乐
                    List<PlayBkMusic> musicList = result.getMusicList();
                    if(musicList!=null) {
                        for(PlayBkMusic music : musicList) {
                            cacheMedia(music.getMedia());
                        }
                    }
                }
            });
        }
    }

    private String getCachePath(String path) {
        File fileDir = MyApplication.getInstance().getApplicationContext().getFilesDir();
        File cache = new File(fileDir, "cache");
        if(!cache.exists()) {
            cache.mkdirs();
        }

        int start = path.lastIndexOf("/") + 1;
        String pathName = path.substring(start);
        File filepath = new File(cache, pathName);
        return filepath.getAbsolutePath();
    }

    private void cacheMedia(Media media) {
        MediaCacheDao cacheDao = MediaCacheDao.instance();
        String md5 = media.getMd5();
        if(cacheDao.cacheFileExist(md5)){
            Log.d("MediaCacheService","media cache exist >> "+ media.getMd5());
            return;
        }
        NetParams netParams = new NetParams(media.getPath(), false, false);
        //netParams.setAutoRename(true);
        String filepath = getCachePath(media.getPath());
        Log.d("MediaCacheService","cache save path >> "+ filepath);
        netParams.setSaveFilePath(filepath);
        x.http().get(netParams, new AbstractCommonCallback<File>() {
            @Override
            public void onSuccess(File result) {

                MediaCache cache = new MediaCache();
                cache.setDeviceid(RunInfo.device.getId());
                cache.setUserid(RunInfo.device.getUserId());
                cache.setMd5(media.getMd5());
                cache.setUrl(media.getPath());
                cache.setSize(media.getSize());
                cache.setPath(filepath);
                MediaCacheDao.instance().saveIfNotExist(cache);

                Log.d("MediaCacheService", "cache >> " + result.getAbsolutePath());
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public String countCacheTotalSize(String defaultStr) {
        if(RunInfo.device!=null && RunInfo.device.getUserId()!=null) {
            List<MediaCache> mediaCacheList = MediaCacheDao.instance().listByUserAndDevice(RunInfo.device.getUserId(), RunInfo.device.getId());
            int total = 0;
            for(MediaCache cache : mediaCacheList) {
                total+=cache.getSize();
            }
            if(total>0) {
                int gb = 1024 * 1024 * 1024;
                int mb = 1024 * 1024;
                int kb = 1024;
                String unit = "KB";
                String totalStr = "";
                if(total/gb>0) { //可以整除GB
                    unit = "G";
                    totalStr = String.format("%.2f %s", (double) total / gb, unit);
                } else if(total/mb>0) { //可以整除MB
                    unit = "MB";
                    totalStr = String.format("%.2f %s", (double) total/ mb, unit);
                } else {//否则按kb显示
                    totalStr = String.format("%.2f %s", (double) total/ kb, unit);
                }
                return totalStr;
            }
        }
        return defaultStr;
    }

    public void clearCache() {
        if(RunInfo.device!=null && RunInfo.device.getUserId()!=null) {
            List<MediaCache> mediaCacheList = MediaCacheDao.instance().listByUserAndDevice(RunInfo.device.getUserId(), RunInfo.device.getId());
            for(MediaCache mediaCache : mediaCacheList) {
                boolean b = new File(mediaCache.getPath()).delete();
                if(b) {
                    MediaCacheDao.instance().deleteById(MediaCache.class, mediaCache.getId());
                    Log.d("MediaCache", "cache clear >> " + mediaCache.getPath());
                }
            }
        }
    }
}
