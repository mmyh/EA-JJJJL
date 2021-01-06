package com.mmyh.eajjjjl.demo;

import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.mmyh.eajjjjl.library.BaseImageLoader;


public class CommonImageLoader extends BaseImageLoader {

    @Override
    public void load(FragmentActivity activity, ImageView imageView, String url) {
        //Glide.with(activity).load(url).error(R.drawable.common_error).into(imageView);
    }
}
