package com.syzbtech.screen.http;

import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.syzbtech.screen.MyApplication;

import org.xutils.common.util.ParameterizedTypeUtil;
import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonResponseParser implements ResponseParser<String> {

    @Override
    public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {
        Log.d("JsonResponseParser", "result >> "+ result );
        Log.d("JsonResponseParser", "result type >> "+ resultType );
        try {
            Class<?> clazz = (Class<?>) ParameterizedTypeUtil.getParameterizedType(resultType, Result.class, 0);
            Log.d("JsonResponseParser", "result clazz >> " + clazz);
        } catch (Exception e) {

        }

        try{
            return JSON.parseObject(result, resultType);
        } catch(Exception e){
            Log.d("JsonResponseParser", "json parse error >> " + e.getMessage());
        }




        return JSON.parseObject(result, Result.class);
    }

    @Override
    public void beforeRequest(UriRequest request) throws Throwable {

    }

    @Override
    public void afterRequest(UriRequest request) throws Throwable {
        String uri = request.getRequestUri();
        if(uri.contains("user/login")) {
            //Log.d("JsonResponseParser", "uri >> " + uri);
            Map<String, List<String>> headers = request.getResponseHeaders();
            //Log.d("JsonResponseParser", "cookie >> " + headers);
            List<String> cookies = headers.get("Set-Cookie");
            String cookie = cookies.get(0).split(";")[0];
            if(!"rememberMe=deleteMe".equals(cookie)) {
                SharedPreferences sp = MyApplication.getInstance().getSharedPreferences("loginToken", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("cookie",cookies.get(0).split(";")[0]);
              //  editor.putString("status",status);
               // editor.putString("mobile",userInfo.getString("mobile"));
                editor.commit();
            }
        }

    }
}
