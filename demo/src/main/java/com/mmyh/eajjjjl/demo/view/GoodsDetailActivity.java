package com.mmyh.eajjjjl.demo.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mmyh.eajjjjl.annotation.EAView;
import com.mmyh.eajjjjl.demo.BaseActivity;
import com.mmyh.eajjjjl.demo.databinding.ActGoodsDetailBinding;
import com.mmyh.eajjjjl.demo.viewmodel.GoodsDetailViewModel;
import com.mmyh.eajjjjl.mvvm.EAMvvm;


@EAView(superClass = BaseActivity.class,
        bindings = ActGoodsDetailBinding.class,
        viewModels = GoodsDetailViewModel.class)
public class GoodsDetailActivity extends GoodsDetailActivityParent {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EAMvvm.work(this);
        setContentView(actGoodsDetailBinding.getRoot());
        getGoodsDetailViewModel().getDetail();
    }
}
