package com.syzbtech.screen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NoSwipeViewPager extends ViewPager {
    private boolean canSwipe = false;
    public NoSwipeViewPager(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }
    public void setCanSwipe(boolean canSwipe)
    {
        this.canSwipe = canSwipe;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canSwipe && super.onTouchEvent(ev);
    }
 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSwipe && super.onInterceptTouchEvent(ev);
    }
}
