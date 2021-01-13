package com.mmyh.eajjjjl.retrofitextension;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

public class EARetrofitException {

    public String UnknownHostExceptionMsg() {
        return "网络异常，请稍后再试";
    }

    public String SSLHandshakeExceptionMsg() {
        return "服务器证书异常";
    }

    public String SocketTimeoutExceptionMsg() {
        return "服务器请求超时，请稍后再试";
    }

    public String otherExceptionMsg() {
        return "服务器开小差了，请稍后再试";
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    private void toast(final String str, final int time) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(EARetrofitService.getInstance().getApplication().getApplicationContext(), str, time).show();
            }
        });
    }

    public void toastShort(String str) {
        toast(str, Toast.LENGTH_SHORT);
    }

    public Exception handle(boolean showErrorToast, Throwable t) {
        if (t != null) {
            t.printStackTrace();
            try {
                throw new Exception(t);
            } catch (Exception e) {
                if (e.getCause() instanceof UnknownHostException) {
                    if (showErrorToast) {
                        toastShort(UnknownHostExceptionMsg());
                    }
                } else if (e.getCause() instanceof SSLHandshakeException) {
                    if (showErrorToast) {
                        toastShort(SSLHandshakeExceptionMsg());
                    }
                } else if (e.getCause() instanceof SocketTimeoutException) {
                    if (showErrorToast) {
                        toastShort(SocketTimeoutExceptionMsg());
                    }
                } else {
                    if (showErrorToast) {
                        toastShort(otherExceptionMsg());
                    }
                }
                return e;
            }
        }
        return null;
    }

}
