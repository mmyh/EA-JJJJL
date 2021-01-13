package com.mmyh.eajjjjl.retrofitextension;

import android.app.Application;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class EARetrofitService {

    public enum Converter {
        Gson, FastJson
    }

    private volatile static EARetrofitService mWebService;

    private Retrofit mRetrofit;

    private boolean mIsDebug = false;

    private String mBaseUrl;

    private EAOkHttpClientHelper mHelper;

    private OkHttpClient mClient;

    private Application mApplication;

    private EARetrofitService() {
    }

    public static EARetrofitService getInstance() {
        if (mWebService == null) {
            synchronized (EARetrofitService.class) {
                if (mWebService == null) {
                    mWebService = new EARetrofitService();
                }
            }
        }
        return mWebService;
    }

    public static <T> T getApi(Class<T> c) {
        return getInstance().mRetrofit.create(c);
    }

    public void config(Application application, String baseUrl, boolean isDebug, EAOkHttpClientHelper helper, Converter converter) {
        mApplication = application;
        mBaseUrl = baseUrl;
        mIsDebug = isDebug;
        mHelper = helper;
        if (mHelper == null) {
            mHelper = new EAOkHttpClientHelper();
        }
        if (isDebug) {
            mClient = mHelper.getDebugHttpClient();
        } else {
            mClient = mHelper.getReleaseHttpClient();
        }
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(mBaseUrl);
        if (converter.equals(Converter.Gson)) {
            builder.addConverterFactory(GsonConverterFactory.create());
        } else if (converter.equals(Converter.FastJson)) {
            builder.addConverterFactory(FastJsonConverterFactory.create());
        }
        builder.client(mClient);
        mRetrofit = builder.build();
    }

    public OkHttpClient getHttpClient() {
        return mClient;
    }

    public Application getApplication() {
        return mApplication;
    }

}
