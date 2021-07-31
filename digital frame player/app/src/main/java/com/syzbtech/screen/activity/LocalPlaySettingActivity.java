package com.syzbtech.screen.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syzbtech.screen.Control;
import com.syzbtech.screen.R;
import com.syzbtech.screen.entities.Setting;
import com.syzbtech.screen.fragment.LocalFileFragment;
import com.syzbtech.screen.utils.SettingUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_local_play_setting)
public class LocalPlaySettingActivity extends BaseActivity {

    private static final String TAG = LocalPlaySettingActivity.class.getSimpleName();

    @ViewInject(R.id.item1)
    private RelativeLayout item1;
    @ViewInject(R.id.item2)
    private RelativeLayout item2;
    @ViewInject(R.id.item3)
    private RelativeLayout item3;
    @ViewInject(R.id.item4)
    private RelativeLayout item4;
    @ViewInject(R.id.item5)
    private RelativeLayout item5;
    @ViewInject(R.id.item6)
    private RelativeLayout item6;
    @ViewInject(R.id.item7)
    private RelativeLayout item7;
    @ViewInject(R.id.item8)
    private RelativeLayout item8;

    @ViewInject(R.id.back)
    private LinearLayout back;

    @ViewInject(R.id.right_arrow_play_select)
    private ImageView rightArrowPlaySelect;
    @ViewInject(R.id.left_arrow_play_select)
    private ImageView leftArrowPlaySelect;
    @ViewInject(R.id.property_play_select)
    private TextView propertyPlaySelect;

    @ViewInject(R.id.right_arrow_bk_music)
    private ImageView rightArrowBkMusic;
    @ViewInject(R.id.left_arrow_bk_music)
    private ImageView leftArrowBkMusic;
    @ViewInject(R.id.property_bk_music)
    private TextView propertyBkMusic;

    @ViewInject(R.id.right_arrow_play_time)
    private ImageView rightArrowPlayTime;
    @ViewInject(R.id.left_arrow_play_time)
    private ImageView leftArrowPlayTime;
    @ViewInject(R.id.property_play_time)
    private TextView propertyPlayTime;

    @ViewInject(R.id.right_arrow_play_anim)
    private ImageView rightArrowPlayAnim;
    @ViewInject(R.id.left_arrow_play_anim)
    private ImageView leftArrowPlayAnim;
    @ViewInject(R.id.property_play_anim)
    private TextView propertyPlayAnim;

    @ViewInject(R.id.right_arrow_screen_rate)
    private ImageView rightArrowScreenRate;
    @ViewInject(R.id.left_arrow_screen_rate)
    private ImageView leftArrowScreenRate;
    @ViewInject(R.id.property_screen_rate)
    private TextView propertyScreenRate;

    @ViewInject(R.id.right_arrow_image_mode)
    private ImageView rightArrowImageMode;
    @ViewInject(R.id.left_arrow_image_mode)
    private ImageView leftArrowImageMode;
    @ViewInject(R.id.property_image_mode)
    private TextView propertyImageMode;

    @ViewInject(R.id.right_arrow_video_mode)
    private ImageView rightArrowVideoMode;
    @ViewInject(R.id.left_arrow_video_mode)
    private ImageView leftArrowVideoMode;
    @ViewInject(R.id.property_video_mode)
    private TextView propertyVideoMode;

    @ViewInject(R.id.right_arrow_music_mode)
    private ImageView rightArrowMusicMode;
    @ViewInject(R.id.left_arrow_music_mode)
    private ImageView leftArrowMusicMode;
    @ViewInject(R.id.property_music_mode)
    private TextView propertyMusicMode;

    private Map<Integer, Integer> idIndexMap = new HashMap<>();

    private List<View> rightArrowList = new ArrayList<>();
    private List<View> leftArrowList = new ArrayList<>();
    private List<TextView> propertyViewList = new ArrayList<>();

    private Integer currentId;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Integer index = idIndexMap.get(currentId);

        if(index==null || index == -1) {
            return super.onKeyDown(keyCode, event);
        }

        TextView textView = propertyViewList.get(index);

        if(keyCode==Control.LEFT) {
            setProperty(-1, index, textView);
            return true;
        } else if(keyCode==Control.RIGHT) {
            setProperty(1, index, textView);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void collectIdIndexMap() {
        idIndexMap.put(back.getId(), -1);
        idIndexMap.put(item1.getId(), 0);
        idIndexMap.put(item2.getId(), 1);
        idIndexMap.put(item3.getId(), 2);
        idIndexMap.put(item4.getId(), 3);
        idIndexMap.put(item5.getId(), 4);
        idIndexMap.put(item6.getId(), 5);
        idIndexMap.put(item7.getId(), 6);
        idIndexMap.put(item8.getId(), 7);

        rightArrowList.add(rightArrowPlaySelect);
        rightArrowList.add(rightArrowBkMusic);
        rightArrowList.add(rightArrowPlayTime);
        rightArrowList.add(rightArrowPlayAnim);
        rightArrowList.add(rightArrowScreenRate);
        rightArrowList.add(rightArrowImageMode);
        rightArrowList.add(rightArrowVideoMode);
        rightArrowList.add(rightArrowMusicMode);

        leftArrowList.add(leftArrowPlaySelect);
        leftArrowList.add(leftArrowBkMusic);
        leftArrowList.add(leftArrowPlayTime);
        leftArrowList.add(leftArrowPlayAnim);
        leftArrowList.add(leftArrowScreenRate);
        leftArrowList.add(leftArrowImageMode);
        leftArrowList.add(leftArrowVideoMode);
        leftArrowList.add(leftArrowMusicMode);

        propertyViewList.add(propertyPlaySelect);
        propertyViewList.add(propertyBkMusic);
        propertyViewList.add(propertyPlayTime);
        propertyViewList.add(propertyPlayAnim);
        propertyViewList.add(propertyScreenRate);
        propertyViewList.add(propertyImageMode);
        propertyViewList.add(propertyVideoMode);
        propertyViewList.add(propertyMusicMode);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertyPlaySelect.setText(SettingUtil.localPlaySetting.getProperty(0));
        propertyBkMusic.setText(SettingUtil.localPlaySetting.getProperty(1));
        propertyPlayTime.setText(SettingUtil.localPlaySetting.getProperty(2));
        propertyPlayAnim.setText(SettingUtil.localPlaySetting.getProperty(3));
        propertyScreenRate.setText(SettingUtil.localPlaySetting.getProperty(4));
        propertyImageMode.setText(SettingUtil.localPlaySetting.getProperty(5));
        propertyVideoMode.setText(SettingUtil.localPlaySetting.getProperty(6));
        propertyMusicMode.setText(SettingUtil.localPlaySetting.getProperty(7));


        item1.setSelected(true);
        item1.requestFocus();

        currentId = item1.getId();

        addFocusView(back,item1,item2,item3,item4,item5,item6,item7,item8).setFocusViewListener();
        collectIdIndexMap();

    }

    private void setProperty(int arrow, int index, TextView textView) {
        String str = SettingUtil.localPlaySetting.nextSetting(arrow, SettingUtil.LocalPlayProperty.KEYS[index], SettingUtil.LocalPlayProperty.PROP_ATTR[index]);
        textView.setText(str);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        currentId = v.getId();
    }

    @Event({R.id.back, R.id.right_arrow_play_select, R.id.left_arrow_play_select,
            R.id.right_arrow_bk_music, R.id.left_arrow_bk_music,
            R.id.right_arrow_play_time, R.id.left_arrow_play_time,
            R.id.right_arrow_play_anim, R.id.left_arrow_play_anim,
            R.id.right_arrow_screen_rate, R.id.left_arrow_screen_rate,
            R.id.right_arrow_image_mode, R.id.left_arrow_image_mode,
            R.id.right_arrow_video_mode, R.id.left_arrow_video_mode})
    private void click(View view) {
        int id =view.getId();
        switch (id) {
            case R.id.back:
                finish();
            break;
            case R.id.right_arrow_play_select:
                setProperty(-1, 0, propertyPlaySelect);
                break;
            case R.id.left_arrow_play_select:
                setProperty(1, 0, propertyPlaySelect);
                break;
            case R.id.right_arrow_bk_music:
                setProperty(-1, 1, propertyBkMusic);
                break;
            case R.id.left_arrow_bk_music:
                setProperty(1, 1, propertyBkMusic);
                break;
            case R.id.right_arrow_play_time:
                setProperty(-1, 2, propertyPlayTime);
                break;
            case R.id.left_arrow_play_time:
                setProperty(1, 2, propertyPlayTime);
                break;
            case R.id.right_arrow_play_anim:
                setProperty(-1, 3, propertyPlayAnim);
                break;
            case R.id.left_arrow_play_anim:
                setProperty(1, 3, propertyPlayAnim);
                break;
            case R.id.right_arrow_screen_rate:
                setProperty(-1, 4, propertyScreenRate);
                break;
            case R.id.left_arrow_screen_rate:
                setProperty(1, 4, propertyScreenRate);
                break;
            case R.id.right_arrow_image_mode:
                setProperty(-1, 5, propertyImageMode);
                break;
            case R.id.left_arrow_image_mode:
                setProperty(1, 5, propertyImageMode);
                break;
            case R.id.right_arrow_video_mode:
                setProperty(-1, 6, propertyVideoMode);
                break;
            case R.id.left_arrow_video_mode:
                setProperty(1, 6, propertyVideoMode);
                break;
            case R.id.right_arrow_music_mode:
                setProperty(-1, 7, propertyMusicMode);
                break;
            case R.id.left_arrow_music_mode:
                setProperty(1, 7, propertyMusicMode);
                break;
        }
    }

}