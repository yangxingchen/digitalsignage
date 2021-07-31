package com.syzbtech.screen.dao;

import android.util.Log;

import com.syzbtech.screen.entities.LocalFile;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class LocalFileDao extends BaseDAO<LocalFile> {
    private String TAG = LocalFileDao.class.getName();

    private static LocalFileDao mInstance = new LocalFileDao();
    private LocalFileDao(){}

    public static LocalFileDao instance(){
        return mInstance;
    }

    public boolean exist(String md5) {
        try {
            return db.selector(LocalFile.class).where("md5","=", md5).count()>0;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveIfNotExist(LocalFile localFile) {
        try {
            if (!exist(localFile.getMd5())) {
                db.save(localFile);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<LocalFile> listByType(List<Integer> types) {
        try {
            List<LocalFile> fileList = db.selector(LocalFile.class).where("type", "in", types)
                    .orderBy("type", true).findAll();
            if(fileList!=null) {
                return fileList;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<LocalFile> listByType(Integer type) {
        try {
            Log.d(TAG,"type >>> " + type);
            List<LocalFile> localFileList = db.selector(LocalFile.class)
                    .where("type","=", type)
                    .orderBy("sort")
                    .findAll();


            if(localFileList!=null) {
                return localFileList;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateBatch(List<LocalFile> localFileList, String... columns) {
        try {
            for (LocalFile localFile : localFileList) {
                db.update(localFile, columns);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void removeBatch(List<Long> ids) {
        try {
            db.delete(LocalFile.class, WhereBuilder.b("id", "in", ids));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
