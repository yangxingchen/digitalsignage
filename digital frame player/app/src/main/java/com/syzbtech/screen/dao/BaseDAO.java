package com.syzbtech.screen.dao;

import com.syzbtech.screen.MyApplication;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

public abstract class BaseDAO<T> {

    protected DbManager db;

    public BaseDAO() {
        db = getDb();
    }
   protected DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("cstv.db")
            // 不设置dbDir时, 默认存储在app的私有目录.
            .setDbDir(MyApplication.getInstance().getFilesDir()) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
            .setDbVersion(1)
            .setDbOpenListener(db -> {
                // 开启WAL, 对写入加速提升巨大
                db.getDatabase().enableWriteAheadLogging();
            })
            .setDbUpgradeListener((db, oldVersion, newVersion) -> {
                // TODO: ...
                // db.addColumn(...);
                // db.dropTable(...);
                // ...
                // or
                // db.dropDb();
            });

   protected DbManager getDb() {
       try {
           return x.getDb(daoConfig);
       } catch (DbException e) {
           e.printStackTrace();
       }
       return null;
   }

   public boolean save(T t) {
       try {
           db.save(t);
           return true;
       } catch (DbException e) {
           e.printStackTrace();
       }
       return false;
   }

   public boolean deleteById(Class<?> clazz, Object id) {
       try {
           db.deleteById(clazz, id);
           return true;
       } catch (DbException e) {
           e.printStackTrace();
       }
       return false;
   }
}
