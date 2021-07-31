package com.syzbtech.screen.http;

import android.content.SharedPreferences;

import com.syzbtech.screen.MyApplication;

import org.xutils.http.RequestParams;

/**
 * 请求参数封装。
 * uri - 请求地址，不带域名
 * hasCookie - 需要登录的前提下的请求必须为 true
 * jsonParam - 是否是json 参数 true-是，false-不是
 */
public class NetParams extends RequestParams {

    public NetParams(String uri, boolean hasCookie, boolean jsonParam, int timeout) {
        super(Api.host + uri);
        String cookie = "";
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences("loginToken", 0);
        cookie = sp.getString("cookie", null);
        setConnectTimeout(timeout==0?30*1000:timeout);
        if(hasCookie) {
            addHeader("Cookie", cookie);
            setUseCookie(false);
        }
        if(jsonParam) {
            addHeader("Content-Type", "application/json;charset=UTF-8");
        }
    }

    public NetParams(String uri, boolean hasCookie, boolean jsonParam) {
        this(uri, hasCookie, jsonParam,0);
    }

    public NetParams(String uri) {
        this(uri, false, false,0);
    }
}
