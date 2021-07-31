package com.syzbtech.screen.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.syzbtech.screen.dao.LocalFileDao;
import com.syzbtech.screen.entities.LocalFile;
import com.syzbtech.screen.utils.Util;

import org.xutils.common.util.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CopyFileService extends Service {

    private ExecutorService executor;

    public void addFileBatch(List<File> fileList, Integer type) {
        for(File file: fileList) {
            addFile(file, type);
        }
    }

    public class LocalBinder extends Binder {
        public CopyFileService getService() {
            return CopyFileService.this;
        }
    }

    public class CopyWork implements Runnable {
        private File file;
        private Integer type;

        public CopyWork(File file, Integer type) {
            this.file = file;
            this.type = type;
        }

        @Override
        public void run() {
            FileOutputStream out = null;
            FileInputStream in = null;
            try {
                String md5 = MD5.md5(file);
                byte [] buff = new byte[8192];
                int len = 0;
                in = new FileInputStream(file);
                String filename=file.getName();
                File savePath = new File(getFilesDir(), "local");
                if(!savePath.exists()) {
                    boolean b = savePath.mkdirs();
                }

                File saveFile = new File(savePath, filename);
                Log.d("CopyFileService", ">>> " + saveFile.getAbsolutePath());
                out = new FileOutputStream(saveFile);
                while((len=in.read(buff))!=-1) {
                    out.write(buff, 0, len);
                }
                out.flush();

                LocalFile localFile = new LocalFile();
                localFile.setSize(saveFile.length());
                localFile.setType(type);
                localFile.setMd5(md5);
                localFile.setPath(saveFile.getAbsolutePath());
                localFile.setName(filename);
                localFile.setDisplay(Util.getDisplay(filename));
                localFile.setSuffix(Util.getSuffix(filename));
                localFile.setSort(Long.MAX_VALUE);

                if(type!=3) {
                    String thumbPath = Util.getThumbnail(savePath, localFile.getPath(), localFile.getDisplay(), type);
                    localFile.setThumb(thumbPath);
                }
                LocalFileDao.instance().saveIfNotExist(localFile);

                Intent intent = new Intent();
                intent.setAction("action.file.copy.complete");
                intent.putExtra("local_file", localFile);
                sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    assert in != null;
                    in.close();
                    assert out != null;
                    out.close();
                } catch (Exception ignored) {

                }
            }
        }
    }

    public void addFile(File file, Integer type) {
        executor.submit(new CopyWork(file, type));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newFixedThreadPool(5);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }
}
