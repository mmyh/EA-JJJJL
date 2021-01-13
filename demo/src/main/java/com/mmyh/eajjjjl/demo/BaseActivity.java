package com.mmyh.eajjjjl.demo;

import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.mmyh.eajjjjl.mvvm.EAIImageRender;


public class BaseActivity extends FragmentActivity implements EAIImageRender {
    @Override
    public void render(ImageView imageView, String url) {
        //Glide.with(this).load(url).error(R.drawable.common_error).into(imageView);
    }
}
