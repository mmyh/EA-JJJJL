package com.mmyh.eajjjjl.retrofitextension;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import retrofit2.Call;

public class EACall<T> {

    private static final String TAG = EACall.class.getName();

    Call<T> call;

    EACallback<T> callback;

    EAILoadingDialog dialog;

    private boolean showDialog = true;

    private EACall(LifecycleOwner owner) {
        if (owner instanceof EAILoadingDialog) {
            dialog = (EAILoadingDialog) owner;
        }
        owner.getLifecycle().addObserver(new EALifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (dialog != null) {
                    dialog.dismissLoadingDialog();
                }
                owner.getLifecycle().removeObserver(this);
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                    if (!call.isExecuted()) {
                        Log.d(TAG, "request \"" + call.request().url().toString() + "\" is canceled");
                    }
                }
                if (callback != null) {
                    callback.cancel();
                }
            }
        });
    }

    public static <T> EACall<T> bind(LifecycleOwner owner, Call<T> call) {
        EACall<T> reCall = new EACall<>(owner);
        reCall.call = call;
        return reCall;
    }

    public EACall<T> noDialog() {
        showDialog = false;
        return this;
    }

    public void enqueue(final EACallback<T> cb) {
        enqueue(dialog, cb);
    }

    public void enqueue(final EAILoadingDialog dialog, final EACallback<T> cb) {
        if (dialog != null && showDialog) {
            dialog.showLoadingDialog();
        }
        this.callback = new EACallback<T>() {

            @Override
            protected void onFinish(T response, Throwable t) {
                if (cb != null) {
                    cb.onFinish(response, t);
                }
            }

            @Override
            protected boolean onInterrupted(T response) {
                if (dialog != null && showDialog) {
                    dialog.dismissLoadingDialog();
                }
                if (cb != null) {
                    return cb.onInterrupted(response);
                }
                return super.onInterrupted(response);
            }

        };
        call.enqueue(this.callback);
    }
}
