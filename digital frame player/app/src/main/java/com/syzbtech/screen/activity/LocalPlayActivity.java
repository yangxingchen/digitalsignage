package com.syzbtech.screen.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syzbtech.screen.Control;
import com.syzbtech.screen.R;
import com.syzbtech.screen.RunInfo;
import com.syzbtech.screen.entities.LocalFile;
import com.syzbtech.screen.utils.ActivityUtil;
import com.syzbtech.screen.utils.SettingUtil;
import com.syzbtech.screen.utils.Util;
import com.syzbtech.screen.view.CommonDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_local_play)
public class LocalPlayActivity extends BaseActivity {

    @ViewInject(R.id.back)
    LinearLayout back;

    @ViewInject(R.id.llFileUpdate)
    LinearLayout llFileUpdate;

    @ViewInject(R.id.rlPlaySetting)
    RelativeLayout rlPlaySetting;

    @ViewInject(R.id.rlFile)
    RelativeLayout rlFile;

    @ViewInject(R.id.rlDefaultSetting)
    RelativeLayout rlDefaultSetting;

    @ViewInject(R.id.rlImportImage)
    RelativeLayout rlImportImage;

    @ViewInject(R.id.rlImportVideo)
    RelativeLayout rlImportVideo;

    @ViewInject(R.id.rlImportMusic)
    RelativeLayout rlImportMusic;

    @ViewInject(R.id.tvFileUpdateFlag)
    TextView tvAutoUpdateFileFlag;

    @ViewInject(R.id.llFileImportSection)
    LinearLayout llFileImportSection;

    @ViewInject(R.id.right_arrow)
    ImageView rightArrow;
    @ViewInject(R.id.left_arrow)
    ImageView leftArrow;

    private int localFileTotal = 0;
    private int processLocalFile = 0;

    private ProgressDialog progressDialog;

    private List<View> viewList = new ArrayList<>();//可以被focus的视图列表

    private int currentIndex = 1;

    public void collectFocusedViewList(boolean fileUpdateOpen) {
        viewList.clear();
        viewList.add(back);
        viewList.add(llFileUpdate);
        if(fileUpdateOpen) {
            viewList.add(rlImportImage);
            viewList.add(rlImportVideo);
            viewList.add(rlImportMusic);
        }
        viewList.add(rlPlaySetting);
        viewList.add(rlFile);
        viewList.add(rlDefaultSetting);
        currentIndex = 1;
        llFileUpdate.setSelected(true);
        llFileUpdate.requestFocus();
    }

    private void setUnFocusView() {
        for (View view : viewList) {
            view.setSelected(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d("LocalPlayActivity", "key code = " + keyCode);

        if(keyCode == Control.OK || keyCode==Control.BACK) {
            return super.onKeyDown(keyCode, event);
        }

        setUnFocusView();

        if( keyCode == Control.DOWN) {
            currentIndex++;
            if(currentIndex>=viewList.size()) {
                currentIndex=0;
            }
        } else if(keyCode == Control.UP) {
            currentIndex--;
            if(currentIndex<0) {
                currentIndex=viewList.size()-1;
            }
        }

        View currentView = viewList.get(currentIndex);

        currentView.setSelected(true);
        currentView.requestFocus();

        if(currentIndex==1) {

            if(keyCode == Control.LEFT) {
                tvAutoUpdateFileFlag.setText(SettingUtil.localPlaySetting.nextAutoUpdateFile(-1));
            } else if(keyCode == Control.RIGHT) {
                tvAutoUpdateFileFlag.setText(SettingUtil.localPlaySetting.nextAutoUpdateFile(1));
            }

            importFileOpen();
        }

        //return super.onKeyDown(keyCode, event);
        return true;
    }

    private void importFileOpen() {
        int isUploadFile = SettingUtil.localPlaySetting.getAutoUpdateFile();
        if(isUploadFile==1) {
            llFileImportSection.setVisibility(View.VISIBLE);
            collectFocusedViewList(true);
        } else if(isUploadFile==2) {
            llFileImportSection.setVisibility(View.GONE);
            collectFocusedViewList(false);
        }
    }

    private BroadcastReceiver localFileComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocalFile localFile = (LocalFile) intent.getSerializableExtra("local_file");
            Log.d("LocalPlayActivity", "copy file complete >> "+ localFile);
            processLocalFile++;
            if(progressDialog!=null) {
                progressDialog.setProgress(processLocalFile);
                if(processLocalFile>=localFileTotal) {
                    processLocalFile =0;
                    localFileTotal = 0;
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        importFileOpen();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.file.copy.complete");
        registerReceiver(localFileComplete, intentFilter);

        //setChildItmeUnSelect();
        tvAutoUpdateFileFlag.setText(SettingUtil.localPlaySetting.getAutoUpdateFileStr());
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(localFileComplete);
        } catch (Exception e) {}

        super.onDestroy();
    }

    @Event({R.id.rlPlaySetting, R.id.rlFile, R.id.rlDefaultSetting, R.id.back, R.id.rlImportMusic, R.id.rlImportVideo, R.id.rlImportImage})
    private void itemClick(View view) {



        /*
        llFileUpdate.setSelected(false);
        rlPlaySetting.setSelected(false);
        rlFile.setSelected(false);
        rlDefaultSetting.setSelected(false);
        */

        //setUnFocusView();
        //view.setSelected(true);

        //setChildItmeUnSelect();

        int id = view.getId();
        switch (id) {
            case R.id.rlImportImage:
                importFile(1, getResources().getString(R.string.image), "jpg", "png");
                break;
            case R.id.rlImportVideo:
                importFile(2, getResources().getString(R.string.video), "mp4", "ogg","rm","rmvb","avi","3gp","wmv","asf","flv","f4v","vob","mkv");
                break;
            case R.id.rlImportMusic:
                importFile(3, getResources().getString(R.string.music), "mp3", "mgg");
                break;
            case R.id.back:
                ActivityUtil.navigateTo(this, LocalPlayerActivity.class);
                break;
            case R.id.rlPlaySetting:
                ActivityUtil.navigateTo(this, LocalPlaySettingActivity.class);
                break;
            case R.id.rlFile:
                ActivityUtil.navigateTo(this, FileActivity.class);
                break;
            case R.id.rlDefaultSetting:

                CommonDialog commonDialog = new CommonDialog(this);
                commonDialog.setMessage(getString(R.string.hint_set_default_sure)).setTitle(getString(R.string.hint))
                        .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick() {
                                SettingUtil.localPlaySetting.setDefault();
                                Toast.makeText(LocalPlayActivity.this, R.string.hint_set_default_ok, Toast.LENGTH_LONG).show();
                                commonDialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick() {
                                commonDialog.dismiss();
                            }
                        });
                commonDialog.show();
                break;
            //case R.id.left_arrow:
            //    tvAutoUpdateFileFlag.setText(SettingUtil.localPlaySetting.nextAutoUpdateFile(-1));
            //    break;
            //case R.id.right_arrow:
            //    tvAutoUpdateFileFlag.setText(SettingUtil.localPlaySetting.nextAutoUpdateFile(1));
            //    break;
        }

        /*
        int isUploadFile = SettingUtil.localPlaySetting.getAutoUpdateFile();
        if(isUploadFile==1) {
            llFileImportSection.setVisibility(View.VISIBLE);
        } else if(isUploadFile==2) {
            llFileImportSection.setVisibility(View.GONE);
        }*/
    }

    private void setChildItmeUnSelect() {
        rlImportMusic.setSelected(false);
        rlImportVideo.setSelected(false);
        rlImportImage.setSelected(false);
    }

    public interface ImportCallback {
        void doImport();
    }

    private void alertDialog(String title, ImportCallback callback) {

        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setMessage(title).setTitle(getString(R.string.hint))

                .setPositive(getString(R.string.btn_start_import))
                .setNegtive(getString(R.string.btn_cancel))
                .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick() {
                        callback.doImport();
                        commonDialog.dismiss();
                    }
                    @Override
                    public void onNegtiveClick() {
                        commonDialog.dismiss();
                    }
                });
        commonDialog.show();
    }


    private List<File> searchFile(Integer type, String... suffixs) {

        //File path = new File(Environment.getExternalStorageDirectory(), "usb");

        List<String> pathList = Util.getPathByMount();
        //Log.d("LocalPlayActivity",pathList.toString());
        if(pathList.size()==0) {
            return new ArrayList<>();
        }
        File path = new File(pathList.get(0));
        if(path.exists() && path.isDirectory()) {
            List<File> fileList = new ArrayList<>();
            Util.searchFile(path, fileList, suffixs);
            return fileList;
        }

        return new ArrayList<>();
    }

    private void importFile(Integer type, String typeStr, String... suffixs) {
        List<File> fileList = searchFile(type, suffixs);
        int size = fileList.size();
        if(size==0) {
            Toast.makeText(this, String.format(getResources().getString(R.string.hint_not_media_import), typeStr), Toast.LENGTH_LONG).show();
            return;
        }



        alertDialog(String.format(getResources().getString(R.string.hint_import_sure), size, typeStr),()->{
            if(RunInfo.copyFileService!=null) {
                localFileTotal = fileList.size();
                showProgressDialog();
                RunInfo.copyFileService.addFileBatch(fileList, type);
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog==null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.title_import_wait);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(localFileTotal);
            progressDialog.show();
        }
    }

}