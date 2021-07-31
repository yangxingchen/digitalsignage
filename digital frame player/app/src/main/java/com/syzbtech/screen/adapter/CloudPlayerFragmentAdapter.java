package com.syzbtech.screen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.syzbtech.screen.fragment.CloudPlayerFragment;

import java.util.ArrayList;
import java.util.List;

public class CloudPlayerFragmentAdapter extends FragmentStatePagerAdapter {

    private List<CloudPlayerFragment> fragmentList = new ArrayList<>();

    public CloudPlayerFragmentAdapter(@NonNull FragmentManager fm, List<CloudPlayerFragment> fragmentList) {
        this(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentList = fragmentList;
    }

    public CloudPlayerFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
