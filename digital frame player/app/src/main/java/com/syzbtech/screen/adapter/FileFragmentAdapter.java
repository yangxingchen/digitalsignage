package com.syzbtech.screen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.syzbtech.screen.fragment.LocalFileFragment;

public class FileFragmentAdapter extends FragmentStatePagerAdapter {

    private LocalFileFragment[] fragments;

    public FileFragmentAdapter(@NonNull FragmentManager fm) {
        this(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public FileFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragments = new LocalFileFragment[] {
                LocalFileFragment.newInstance(1),
                LocalFileFragment.newInstance(2),
                LocalFileFragment.newInstance(3)
        };
    }
    @NonNull
    @Override
    public LocalFileFragment getItem(int position) {
        return fragments[position];
    }
    @Override
    public int getCount() {
        return fragments.length;
    }
}
