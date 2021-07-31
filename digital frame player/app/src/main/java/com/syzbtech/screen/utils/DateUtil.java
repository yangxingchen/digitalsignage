package com.syzbtech.screen.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String formatNow(String pattern) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }
}
