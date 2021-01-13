package com.mmyh.eajjjjl.retrofitextension;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class EACallback<T> implements Callback<T> {

    boolean isCanceled = false;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!call.isCanceled()
                && !isCanceled
                && !onInterrupted(response.body())) {
            onFinish(response.body(), null);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!call.isCanceled()
                && !isCanceled) {
            onInterrupted(null);
            onFinish(null, t);
        }
    }

    protected void onFinish(T response, Throwable t) {

    }

    /**
     * @param response 接口返回response对象
     * @return true, 该方法已拦截,不用再继续执行
     */
    protected boolean onInterrupted(T response) {
        return false;
    }

    void cancel() {
        isCanceled = true;
    }
}
