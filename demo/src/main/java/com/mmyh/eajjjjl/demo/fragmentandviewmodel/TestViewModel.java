package com.mmyh.eajjjjl.demo.fragmentandviewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class TestViewModel extends ViewModel {

    private int tmp = 0;

    private Handler handler = new Handler(Looper.getMainLooper());

    public MutableLiveData<String> count = new MutableLiveData<>();

    public void getCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tmp++;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        count.setValue(String.valueOf(tmp));
                    }
                });
            }
        }).start();
    }
}
