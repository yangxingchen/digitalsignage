package com.syzbtech.screen.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtil {

    public static void navigateTo(Activity context, Class<? extends Activity> activityClass ) {
        Intent intent = new Intent();
        intent.setClass(context.getApplication(), activityClass);
        context.startActivity(intent);
    }

}
