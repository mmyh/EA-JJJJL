package com.mmyh.eajjjjl.demo.viewmodel.ex;

import androidx.lifecycle.ViewModel;

import com.mmyh.eajjjjl.demo.model.Goods;

import java.util.List;

public class SingleGoodsListViewModelEx extends ViewModel {
    public static final String _goodsList = "com.mmyh.eajjjjl.demo.viewmodel.SingleGoodsListViewModel,goodsList";

    public static final class TestData {
        public List<Goods> response;
        public String p1;
        public Integer p2;
        public Throwable err;
    }
}