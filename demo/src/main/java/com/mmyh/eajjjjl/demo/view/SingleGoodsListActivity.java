package com.mmyh.eajjjjl.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.mmyh.eajjjjl.annotation.EAView;
import com.mmyh.eajjjjl.demo.BaseActivity;
import com.mmyh.eajjjjl.demo.databinding.ActSingleGoodsListBinding;
import com.mmyh.eajjjjl.demo.databinding.ActSingleGoodsListItemBinding;
import com.mmyh.eajjjjl.demo.model.Goods;
import com.mmyh.eajjjjl.demo.viewmodel.SingleGoodsListViewModel;
import com.mmyh.eajjjjl.library.EAjjjjl;

import java.util.List;

@EAView(viewModels = SingleGoodsListViewModel.class,
        bindings = ActSingleGoodsListBinding.class,
        listBindings = ActSingleGoodsListItemBinding.class,
        listModel = Goods.class,
        superClass = BaseActivity.class)
public class SingleGoodsListActivity extends SingleGoodsListActivityParent {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EAjjjjl.work(this);
        setContentView(actSingleGoodsListBinding.getRoot());
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mAdapter);
        getSingleGoodsListViewModel().getGoodsList();
    }

    @Override
    protected void renderGoodsList(List<Goods> value) {
        super.renderGoodsList(value);
        mAdapter.updateData(value, false);
    }

    @Override
    protected void onListItemClick(View view, Goods value, int pos) {
        super.onListItemClick(view, value, pos);
        startActivity(new Intent(this, GoodsDetailActivity.class));
    }
}
