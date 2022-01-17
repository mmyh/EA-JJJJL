package com.mmyh.eajjjjl.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.mmyh.eajjjjl.annotation.EAView;
import com.mmyh.eajjjjl.demo.BaseActivity;
import com.mmyh.eajjjjl.demo.databinding.ActSingleGoodsListBinding;
import com.mmyh.eajjjjl.demo.databinding.ActSingleGoodsListFootBinding;
import com.mmyh.eajjjjl.demo.databinding.ActSingleGoodsListHeadBinding;
import com.mmyh.eajjjjl.demo.databinding.ActSingleGoodsListItemBinding;
import com.mmyh.eajjjjl.demo.model.Goods;
import com.mmyh.eajjjjl.demo.model.ListHead;
import com.mmyh.eajjjjl.demo.viewmodel.SingleGoodsListViewModel;
import com.mmyh.eajjjjl.mvvm.EAMvvm;

import java.util.List;

@EAView(viewModels = SingleGoodsListViewModel.class,
        bindings = ActSingleGoodsListBinding.class,
        listBindings = ActSingleGoodsListItemBinding.class,
        headViewBinding = ActSingleGoodsListHeadBinding.class,
        headViewModel = ListHead.class,
        footViewBinding = ActSingleGoodsListFootBinding.class,
        footViewModel = String.class,
        listModel = Goods.class,
        superClass = BaseActivity.class)
public class SingleGoodsListActivity extends SingleGoodsListActivityParent {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EAMvvm.work(this);
        setContentView(actSingleGoodsListBinding.getRoot());
        //recyclerview.setLayoutManager(new LinearLayoutManager(this));
        listview.setAdapter(mAdapter);
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

    @Override
    protected void renderListItem(ActSingleGoodsListItemBinding binding, Goods data, int pos) {
        super.renderListItem(binding, data, pos);
        binding.tvTitle.setText(data.name);
        binding.tvPrice.setText(data.price);
    }

    @Override
    protected void renderHead(ListHead value) {
        super.renderHead(value);
        mAdapter.setHeadViewData(value);
    }

    @Override
    protected void renderListHeadView(ActSingleGoodsListHeadBinding binding, ListHead value) {
        super.renderListHeadView(binding, value);
        binding.tvHead.setText(value.head);
    }

    @Override
    protected void renderFoot(String value) {
        super.renderFoot(value);
        mAdapter.setFootViewData(value);
    }

    @Override
    protected void renderListFootView(ActSingleGoodsListFootBinding binding, String value) {
        super.renderListFootView(binding, value);
        binding.tvFoot.setText(value);
    }
}
