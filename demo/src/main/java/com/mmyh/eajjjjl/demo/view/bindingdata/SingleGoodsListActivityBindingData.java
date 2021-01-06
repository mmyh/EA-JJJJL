package com.mmyh.eajjjjl.demo.view.bindingdata;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mmyh.eajjjjl.annotation.EAText;
import com.mmyh.eajjjjl.demo.model.Goods;
import com.mmyh.eajjjjl.demo.viewmodel.SingleGoodsListViewModel;


public class SingleGoodsListActivityBindingData {


    public static class ActSingleGoodsListBinding {
        public RecyclerView recyclerview;
    }

    public static class ActSingleGoodsListItemBinding {
        public ImageView ivPic;
        @EAText(vm = SingleGoodsListViewModel._goodsList, m = Goods._name)
        public TextView tvTitle;
        @EAText(vm = SingleGoodsListViewModel._goodsList, m = Goods._price)
        public TextView tvPrice;
        @EAText(vm = SingleGoodsListViewModel._goodsList, m = Goods._shop._shopName)
        public TextView tvShop;
    }
}