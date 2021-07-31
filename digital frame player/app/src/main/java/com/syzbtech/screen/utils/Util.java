package com.syzbtech.screen.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.syzbtech.screen.R;
import com.syzbtech.screen.entities.LocalFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;
import static android.media.ThumbnailUtils.extractThumbnail;

public class Util {

    public static void install(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 将此段代码移到此，可正常安装
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apkUri = FileProvider.getUriForFile(context, "com.syzbtech.screen.FileProvider", apkFile);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName=null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    public static String formatNowDateTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static String getDisplay(String filename) {
        if(!TextUtils.isEmpty(filename)) {
            int pos = filename.lastIndexOf(".");
            return filename.substring(0, pos);
        }
        return "";
    }

    public static String getSuffix(String filename) {
        if(!TextUtils.isEmpty(filename)) {
            int pos = filename.lastIndexOf(".");
            return filename.substring(pos+1);
        }
        return "";
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    private static Bitmap createImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null) return null;

        if (kind == 1) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == 2) {
            bitmap = extractThumbnail(bitmap,
                    200,
                    200,
                    OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public static String getThumbnail(File savePath, String sourceFile, String display, Integer type) {
        try {
            Bitmap bitmap = null;
            if (type == 1) {
                bitmap = createImageThumbnail(sourceFile, 200, 200);
            } else if (type == 2) {
                bitmap = createVideoThumbnail(sourceFile, 2);
            }
            if (bitmap != null) {
                File outPath = new File(savePath, "thumb");
                if (!outPath.exists()) {
                    outPath.mkdirs();
                }
                File outfile = new File(outPath, display + ".jpg");
                FileOutputStream out = new FileOutputStream(outfile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                return outfile.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void searchFile(File path, List<File> fileList, String... suffixs) {

        File [] files = path.listFiles();
        for(File file: files) {
            if(file.isDirectory()) {
                searchFile(file, fileList, suffixs);
            } else {
                boolean add = false;
                String filename = file.getName().toLowerCase();
                for(String suffix: suffixs) {
                    if(filename.endsWith(suffix)) {
                        add = true;
                        break;
                    }
                }
                if(add) {
                    fileList.add(file);
                }
            }
        }
    }

    public static List<LocalFile> collectSelectedLocalFile(List<LocalFile> sourceList) {
        List<LocalFile> resultList = new ArrayList<>();
        for(LocalFile localFile : sourceList) {
            if(localFile.isSelected()) {
                resultList.add(localFile);
            }
        }
        return resultList;
    }

    public static void sortLocalFile(List<LocalFile> sourceList) {
        Collections.sort(sourceList, (o1, o2) -> {
            if(o1.getSort()>o2.getSort()) {
                return 1;
            } else if(o1.getSort()<o2.getSort()) {
                return -1;
            }
            return 0;
        });
    }

    public static void resetSortLocalFile(List<LocalFile> sourceList) {
        long sort = 0;
        for(LocalFile localFile:sourceList) {
            localFile.setSort(sort++);
        }
    }

    public static List<LocalFile> deleteLocalFile(List<LocalFile> sourceList) {
        List<LocalFile> removeList = new ArrayList<>();
        List<LocalFile> localFileList = new ArrayList<>();
        for(LocalFile localFile:sourceList) {
            if(localFile.isSelected()) {
                removeList.add(localFile);
            } else {
                localFileList.add(localFile);
            }
        }
        sourceList.clear();
        sourceList.addAll(localFileList);
        return removeList;
    }

    public static void setViewPagerScroller(Activity activity,  ViewPager viewPager) {

        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            Scroller scroller = new Scroller(activity, (Interpolator) interpolator.get(null)) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy, duration * 7);    // 这里是关键，将duration变长或变短
                }
            };
            scrollerField.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
            // Do nothing.
        } catch (IllegalAccessException e) {
            // Do nothing.
        }
    }

    public static String getFilename(String apkPath) {
        int pos = apkPath.lastIndexOf("/");
        return apkPath.substring(pos+1);
    }

    public static void unSelectAll(List<LocalFile> localFiles) {
        for(LocalFile localFile : localFiles) {
            localFile.setSelected(false);
        }
    }

    private String getCorrectPath(String path) {
        if (!TextUtils.isEmpty(path)){
            int lastSeparator = path.lastIndexOf(File.separator);
            String endStr = path.substring(lastSeparator + 1, path.length());
            if (!TextUtils.isEmpty(endStr) && (endStr.contains("USB_DISK") || endStr.contains("usb_disk"))){//不区分大小写
                File file = new File(path);
                if (file.exists() && file.listFiles().length == 1 && file.listFiles()[0].isDirectory()){
                    path = file.listFiles()[0].getAbsolutePath();
                }
            }
        }
        return path;
    }


    /**
     * 根据StorageManager获取Usb插入的U盘路径
     * 可以获取内部存储、sd卡以及所有usb路径
     * 获取到的路径可能是不完整的，需要判断追加
     */
    private List<String> getPathListByStorageManager(Context context) {
        List<String> pathList = new ArrayList<>();
        try {
            StorageManager storageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
            Method method_volumeList = StorageManager.class.getMethod("getVolumeList");
            method_volumeList.setAccessible(true);
            Object[] volumeList = (Object[]) method_volumeList.invoke(storageManager);
            if (volumeList != null) {
                for (int i = 0; i < volumeList.length; i++) {
                    try {
                        String path = (String) volumeList[i].getClass().getMethod("getPath").invoke(volumeList[i]);
                        boolean isRemovable = (boolean) volumeList[i].getClass().getMethod("isRemovable").invoke(volumeList[i]);
                        String state = (String) volumeList[i].getClass().getMethod("getState").invoke(volumeList[i]);
//                        Util.logE("isRemovable:"+isRemovable+" / state:"+state+" / path:"+path);
                        if (isRemovable && "mounted".equalsIgnoreCase(state) && path.contains("usb_storage")){
                            pathList.add(getCorrectPath(path));//将正确的路径添加到集合中
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pathList;
    }


    /**
     * 使用mount命令获取usb插入的U盘路径
     * 可以获取内部存储、外部存储、tf卡、otg、系统分区等路径，获取到的U盘路径是完整的正确的
     * 限制条件是机子必须得解开root
     */
    //smallstar: 在3288主板上调试可用，不同主板需要重新调试
    public static List<String> getPathByMount() {
        List<String> usbMemoryList = new ArrayList<>();
        try {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {

                // 将常见的linux分区过滤掉
                if (line.contains("proc") || line.contains("tmpfs") || line.contains("self") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue;
                }
                if (!TextUtils.isEmpty(line) && line.contains("usb")){//根据情况过来需要的字段
                    Log.e("smallstar", line);
                    String items[] = line.split(" ");
                    if (null != items && items.length > 1) {
                        String path = items[1];
                        Log.e("smallstar1", "------->path:"+path);
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        //if (path != null && !usbMemoryList.contains(path) && path.contains("sd"))usbMemoryList.add(items[1]);
                        if (path != null && !usbMemoryList.contains(path))usbMemoryList.add(items[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usbMemoryList;
    }

    public static void modifyAlertDialogButton(Context context, AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        modifyButtonParams(positiveButton,
                (int)context.getResources().getDimension(R.dimen.dp60),
                ContextCompat.getDrawable(context, R.drawable.bk_btn_blue),
                ContextCompat.getColor(context, android.R.color.black), 10);

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        modifyButtonParams(negativeButton,
                (int)context.getResources().getDimension(R.dimen.dp60),
                ContextCompat.getDrawable(context, R.drawable.bk_btn_red),
                ContextCompat.getColor(context, android.R.color.black), 10);
    }

    private static void modifyButtonParams(Button button, int height, Drawable background, int colorId, int margin) {
        LinearLayout.LayoutParams buttonParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        buttonParams.setMargins(margin,margin,margin,margin);
        //设置按钮的大小
        buttonParams.height = height;
        button.setPadding(15,0,15,0);
        button.setGravity(Gravity.CENTER);
        button.setLayoutParams(buttonParams);
        button.setBackground(background);
        button.setTextColor(colorId);
        button.setTextSize(13);
    }
}
