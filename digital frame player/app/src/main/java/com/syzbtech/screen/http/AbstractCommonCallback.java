package com.syzbtech.screen.http;

import android.util.Log;

import org.xutils.common.Callback;

public abstract class AbstractCommonCallback<T> implements Callback.CommonCallback<T> {

    @Override
    public abstract void onSuccess(T result);

    @Override
    public void onError(Throwable ex, boolean isOnCallback){
        Log.e("AbstractCommonCallback", ">>> ", ex);
    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
