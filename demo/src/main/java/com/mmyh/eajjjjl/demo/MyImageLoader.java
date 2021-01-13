package com.mmyh.eajjjjl.demo;

import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.mmyh.eajjjjl.mvvm.BaseImageLoader;


public class MyImageLoader extends BaseImageLoader {

    @Override
    public void load(FragmentActivity activity, ImageView imageView, String url) {
        //Glide.with(activity).load(url).error(R.drawable.my_error).into(imageView);
    }
}
