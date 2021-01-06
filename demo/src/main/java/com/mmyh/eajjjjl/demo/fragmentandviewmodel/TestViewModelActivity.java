package com.mmyh.eajjjjl.demo.fragmentandviewmodel;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mmyh.eajjjjl.demo.R;


public class TestViewModelActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test_viewmodel);
        final F1Fragment f1Fragment = new F1Fragment();
        final F2Fragment f2Fragment = new F2Fragment();
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(f1Fragment);
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(f2Fragment);
            }
        });
        addFragment(f1Fragment);
    }

    private void addFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
