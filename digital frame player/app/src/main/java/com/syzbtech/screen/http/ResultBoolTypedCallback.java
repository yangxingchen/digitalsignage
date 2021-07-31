package com.syzbtech.screen.http;

import com.alibaba.fastjson.TypeReference;

import org.xutils.common.Callback;

import java.lang.reflect.Type;

public class ResultBoolTypedCallback implements Callback.TypedCallback<Result<Boolean>> {

    @Override
    public Type getLoadType() {
        return new TypeReference<Result<Boolean>>(){}.getType();
    }

    @Override
    public void onSuccess(Result<Boolean> result) {

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
