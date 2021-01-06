package com.mmyh.eajjjjl.demo.testfragment;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.mmyh.eajjjjl.demo.R;

public class TestFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.root, new TestFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();
    }

}
