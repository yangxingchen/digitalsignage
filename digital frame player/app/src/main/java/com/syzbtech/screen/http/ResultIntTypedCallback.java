package com.syzbtech.screen.http;

import com.alibaba.fastjson.TypeReference;

import org.xutils.common.Callback;

import java.lang.reflect.Type;

public class ResultIntTypedCallback implements Callback.TypedCallback<Result<Integer>> {
    @Override
    public Type getLoadType() {
        return new TypeReference<Result<Integer>>(){}.getType();
    }

    @Override
    public void onSuccess(Result<Integer> result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
