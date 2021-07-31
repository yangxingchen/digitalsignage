package com.syzbtech.screen.dao;

import com.syzbtech.screen.entities.MediaCache;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaCacheDao extends BaseDAO<MediaCache> {

    private static MediaCacheDao mInstance = new MediaCacheDao();
    private MediaCacheDao(){}

    public static MediaCacheDao instance(){
        return mInstance;
    }

    public boolean deleteByUserAndDevice(Long userId, Long deviceId) {
        try {
            db.delete(MediaCache.class, WhereBuilder.b("userid","=",userId)
                .and("deviceid","=", deviceId));
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MediaCache> listByUserAndDevice(Long userId, Long deviceId) {
        try {
            List<MediaCache> cacheList = db.selector(MediaCache.class)
                    .where("userid","=", userId)
                    .and("deviceid", "=", deviceId).findAll();
            if(cacheList!=null) {
                return cacheList;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public MediaCache getByMd5(String md5) {
        try {
            return db.selector(MediaCache.class)
                    .where("md5","=", md5).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean exist(String md5) {
        try {
            return db.selector(MediaCache.class)
                    .where("md5", "=", md5).count() > 0;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cacheFileExist(String md5) {
        try {
            MediaCache cache = db.selector(MediaCache.class).where("md5", "=", md5).findFirst();
            if(cache!=null) {
                return new File(cache.getPath()).exists();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveIfNotExist(MediaCache mediaCache) {
        try {
            WhereBuilder builder = WhereBuilder.b("md5", "=", mediaCache.getMd5());
            if (db.selector(MediaCache.class).where(builder).count() == 0) {
                db.save(mediaCache);
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
}
