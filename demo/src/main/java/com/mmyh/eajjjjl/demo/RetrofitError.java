package com.mmyh.eajjjjl.demo;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

public class RetrofitError {

    public enum ErrorType {
        No_Network, SSLError, WebService_Down
    }

    private static final String TAG = RetrofitError.class.getName();

    private static int ERROR_NET_LAYOUT_ID;

    private static int NO_NET_LAYOUT_ID;

    private static int RETRYVIEWID;

    public static void Config(@LayoutRes int error_net_layout_id, @LayoutRes int no_net_layout_id, @IdRes int retryViewId) {
        ERROR_NET_LAYOUT_ID = error_net_layout_id;
        NO_NET_LAYOUT_ID = no_net_layout_id;
        RETRYVIEWID = retryViewId;
    }



    public static ErrorType handle(boolean showErrorToast, Throwable t) {
        if (t != null) {
            t.printStackTrace();
            try {
                throw new Exception(t);
            } catch (Exception e) {
                if (e.getCause() instanceof UnknownHostException) {
                    if (showErrorToast) {
                    }
                    return ErrorType.No_Network;
                } else if (e.getCause() instanceof SSLHandshakeException) {
                    if (showErrorToast) {
                    }
                    return ErrorType.SSLError;
                } else if (e.getCause() instanceof SocketTimeoutException) {
                    if (showErrorToast) {
                    }
                    return ErrorType.WebService_Down;
                } else {
                    if (showErrorToast) {
                    }
                    return ErrorType.WebService_Down;
                }
            }
        } else {
            if (showErrorToast) {
            }
            return ErrorType.WebService_Down;
        }
    }

    public static ErrorType handle(boolean showErrorToast) {
        return handle(showErrorToast, null);
    }
}
