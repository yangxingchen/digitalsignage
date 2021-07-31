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
import com.syzbtech.screen.activity.CloudPlayerActivity;
import com.syzbtech.screen.adapter.VideoCallBackAdapter;
import com.syzbtech.screen.dao.MediaCacheDao;
import com.syzbtech.screen.entities.Media;
import com.syzbtech.screen.entities.MediaCache;
import com.syzbtech.screen.entities.PlaySetting;
import com.syzbtech.screen.http.Api;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CloudPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CloudPlayerFragment extends Fragment {

    private String TAG = CloudPlayerFragment.class.getName();

    private int mediaType;
    private String path;
    private Media media;
    private PlaySetting playSetting;
    private int playTime;

    private float screenWidth;
    private float screenHeight;
    private float size;

    private CloudPlayerActivity activity;

    private StandardGSYVideoPlayer videoPlayer;
    private ImageView imageView;

    private Timer timer;

    public CloudPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CloudPlayerFragment newInstance(Media media, PlaySetting playSetting) {
        CloudPlayerFragment fragment = new CloudPlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable("media", media);
        args.putSerializable("playSetting", playSetting);
        //args.putInt("mediaType", mediaType);
        //args.putString("path", path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            media = (Media)args.getSerializable("media");
            playSetting = (PlaySetting) args.getSerializable("playSetting");
            mediaType = media.getType();
            path = media.getPath();
            playTime = playSetting.getPlayTime();
        }

        activity = (CloudPlayerActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_player, container, false);
        videoPlayer = view.findViewById(R.id.videoPlayer);
        imageView = view.findViewById(R.id.imageView);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        size = Math.min(screenWidth, screenHeight);
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        if(mediaType==2) {
            videoPlayer.setVideoAllCallBack(null);
            GSYVideoManager.releaseAllVideos();
        } else if(mediaType==1) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaCache mediaCache = MediaCacheDao.instance().getByMd5(media.getMd5());
        if(mediaType==2) {
            videoPlayer.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            activity.pauseBkMusic();

            String url = Api.host + path;
            if(mediaCache!=null) {
                url = "file://"+ mediaCache.getPath();
            }
            Log.d(TAG, url);
            videoPlayer.setUp(url, true, "");
            videoPlayer.getTitleTextView().setVisibility(View.GONE);
            videoPlayer.startPlayLogic();
            videoPlayer.setVideoAllCallBack(new VideoCallBackAdapter() {
                @Override
                public void onAutoComplete(String url, Object... objects) {
                    Log.d(TAG, url+" >> 播放完成");
                    activity.nextItem();
                }
            });
        } else if(mediaType==1) {
            videoPlayer.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            activity.startBkMusic();

            String url = Api.host+path;
            if(mediaCache!=null) {
                url = "file://" + mediaCache.getPath();
            }

            Glide.with(getActivity()).load(url)
                    .override((int)size, (int)size)
                    .into(new GlideDrawableImageViewTarget(imageView){
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                    super.onResourceReady(resource, animation);
                    if(timer==null) {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                activity.runOnUiThread(activity::nextItem);
                            }
                        }, playTime * 1000);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}