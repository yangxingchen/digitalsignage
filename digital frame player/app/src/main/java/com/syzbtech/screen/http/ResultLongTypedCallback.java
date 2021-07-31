package com.syzbtech.screen.http;

import com.alibaba.fastjson.TypeReference;

import org.xutils.common.Callback;

import java.lang.reflect.Type;

public class ResultLongTypedCallback implements Callback.TypedCallback<Result<Long>> {
    @Override
    public Type getLoadType() {
        return new TypeReference<Result<Long>>(){}.getType();
    }

    @Override
    public void onSuccess(Result<Long> result) {

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
