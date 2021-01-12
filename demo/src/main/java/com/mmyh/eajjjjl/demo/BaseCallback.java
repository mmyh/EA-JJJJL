package com.mmyh.eajjjjl.demo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCallback<T> implements Callback<T> {

    protected boolean showErrorToast;

    protected RetrofitError.ErrorType mErrorType;

    boolean isCanceled = false;

    public BaseCallback() {
        this(true);
    }

    public BaseCallback(boolean showErrorToast) {
        this.showErrorToast = showErrorToast;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (call != null && !call.isCanceled()) {
            if (response != null && response.isSuccessful() && !isCanceled) {
                if (!onBeforeFinish(response.body())) {
                    onFinish(response.body());
                    onFinish(response.body(), null);
                }
            } else {
                onFailure(call, null);
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (call != null && !call.isCanceled() && !isCanceled) {
            mErrorType = RetrofitError.handle(showErrorToast, t);
            onBeforeFinish(null);
            onFinish(null);
            onFinish(null, mErrorType);
            onError(mErrorType);
        }
    }

    protected void onFinish(T response) {

    }

    protected void onFinish(T response, RetrofitError.ErrorType errorType) {

    }

    /**
     * @param response 接口返回response对象
     * @return true, 该方法已拦截,不用再继续执行
     */
    protected boolean onBeforeFinish(T response) {
        return false;
    }

    protected void onError(RetrofitError.ErrorType errorType) {

    }

    void cancel() {
        isCanceled = true;
    }
}