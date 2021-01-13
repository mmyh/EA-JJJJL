package com.mmyh.eajjjjl.mvvm;

import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class BaseImageLoader {

    public void load(FragmentActivity activity, ImageView imageView, String url) {
        //Glide.with(activity).load(url).into(imageView);
    }

    public void load(Fragment fragment, ImageView imageView, String url) {
        //Glide.with(fragment).load(url).into(imageView);
    }

    public void load(ImageView imageView, String url) {
        //Glide.with(imageView.getContext()).load(url).into(imageView);
    }
}
