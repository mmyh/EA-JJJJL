package com.mmyh.eajjjjl.demo.fragmentandviewmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.mmyh.eajjjjl.annotation.EAView;
import com.mmyh.eajjjjl.demo.databinding.FmtView1Binding;
import com.mmyh.eajjjjl.library.EAjjjjl;


@EAView(viewModels = TestViewModel.class,
        bindings = FmtView1Binding.class,
        superClass = Fragment.class)
public class F1Fragment extends F1FragmentParent {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EAjjjjl.work(this, container);
        return fmtView1Binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTestViewModel().getCount();
    }

    @Override
    protected void renderCount(String value) {
        super.renderCount(value);
        fmtView1Binding.tv.setText(value);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println(this);
    }
}
