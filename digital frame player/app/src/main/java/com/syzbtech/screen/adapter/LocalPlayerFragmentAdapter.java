package com.syzbtech.screen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.syzbtech.screen.fragment.CloudPlayerFragment;
import com.syzbtech.screen.fragment.LocalPlayerFragment;

import java.util.ArrayList;
import java.util.List;

public class LocalPlayerFragmentAdapter extends FragmentStatePagerAdapter {

    private List<LocalPlayerFragment> fragmentList = new ArrayList<>();

    public LocalPlayerFragmentAdapter(@NonNull FragmentManager fm, List<LocalPlayerFragment> fragmentList) {
        this(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentList = fragmentList;
    }

    public LocalPlayerFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
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
