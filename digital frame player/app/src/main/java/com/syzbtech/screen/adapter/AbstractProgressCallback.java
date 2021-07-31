package com.syzbtech.screen.adapter;

import android.util.Log;

import org.xutils.common.Callback;

public abstract class AbstractProgressCallback<T> implements Callback.ProgressCallback<T> {

    private String TAG = "AbstractProgressCallback";

    @Override
    public void onWaiting() {
        Log.d(TAG, "progress waiting");
    }

    @Override
    public void onStarted() {
        Log.d(TAG, "progress start");
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {

    }

    @Override
    public void onSuccess(T result) {
        Log.d(TAG, "progress success");
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        Log.e(TAG, "progress error", ex);
    }

    @Override
    public void onCancelled(CancelledException cex) {
        Log.e(TAG, "progress cancelled", cex);
    }

    @Override
    public void onFinished() {
        Log.d(TAG, "progress finished");
    }
}
