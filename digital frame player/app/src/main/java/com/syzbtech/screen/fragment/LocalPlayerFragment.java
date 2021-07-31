package com.syzbtech.screen.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.syzbtech.screen.R;
import com.syzbtech.screen.activity.LocalPlayerActivity;
import com.syzbtech.screen.adapter.VideoCallBackAdapter;
import com.syzbtech.screen.entities.LocalFile;
import com.syzbtech.screen.utils.SettingUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@ContentView(R.layout.fragment_local_player)
public class LocalPlayerFragment extends Fragment {

    private String TAG = LocalPlayerFragment.class.getName();

    @ViewInject(R.id.imageView)
    private ImageView imageView;

    @ViewInject(R.id.videoPlayer)
    private StandardGSYVideoPlayer videoPlayer;

    private LocalFile localFile;

    private float screenWidth;
    private float screenHeight;

    private float size;
    private Timer timer;

    private int playTime = 10;

    private LocalPlayerActivity activity;

    private int videoMode=1;
    private int imageMode=1;

    public LocalPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocalPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalPlayerFragment newInstance(LocalFile localFile) {
        LocalPlayerFragment fragment = new LocalPlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable("local_file", localFile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            localFile = (LocalFile) args.getSerializable("local_file");
        }
        activity = (LocalPlayerActivity) getActivity();
        //播放时间，图片模式生效。
        playTime = SettingUtil.localPlaySetting.getPlayTimeMinus();

        videoMode = SettingUtil.localPlaySetting.getVideoMode();
        imageMode = SettingUtil.localPlaySetting.getImageMode();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = x.view().inject(this, inflater, container);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        size = Math.min(screenWidth, screenHeight);
        return view;
    }
    public void init() {
        if(localFile!=null) {
            int type = localFile.getType();
            if(type==1) {
                imageView.setVisibility(View.VISIBLE);
                videoPlayer.setVisibility(View.GONE);

                activity.startBkMusic();

                Glide.with(getActivity()).load(localFile.getPath())
                        .override((int)size, (int)size)
                        .into(new GlideDrawableImageViewTarget(imageView){
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                Log.d(TAG, " >> 播放完成 mode >> " + imageMode);
                                if(imageMode==1) {
                                    if (timer == null) {
                                        timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                activity.runOnUiThread(activity::nextItem);
                                            }
                                        }, playTime * 1000);
                                    }
                                }
                            }
                        });
            } else if(type==2) {
                imageView.setVisibility(View.GONE);
                videoPlayer.setVisibility(View.VISIBLE);

                activity.stopBkMusic();

                videoPlayer.setUp(localFile.getPath(), true, "");
                videoPlayer.startPlayLogic();

                videoPlayer.getTitleTextView().setVisibility(View.GONE);
                videoPlayer.setVideoAllCallBack(new VideoCallBackAdapter() {
                    @Override
                    public void onAutoComplete(String url, Object... objects) {

                        Log.d(TAG, url+" >> 播放完成 mode >> " + videoMode);
                        if(videoMode==1) {
                            activity.nextItem();
                        } else  {
                            GSYVideoManager.releaseAllVideos();
                            videoPlayer.setUp(localFile.getPath(), true, "");
                            videoPlayer.startPlayLogic();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(localFile.getType()==2) {
            videoPlayer.setVideoAllCallBack(null);
            GSYVideoManager.releaseAllVideos();
        } else if(localFile.getType()==1) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}