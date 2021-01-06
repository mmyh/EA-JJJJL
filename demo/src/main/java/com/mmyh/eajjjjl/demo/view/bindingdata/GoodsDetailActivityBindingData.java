package com.mmyh.eajjjjl.demo.view.bindingdata;

import android.widget.TextView;

import com.mmyh.eajjjjl.annotation.EAText;
import com.mmyh.eajjjjl.demo.model.Goods;
import com.mmyh.eajjjjl.demo.viewmodel.GoodsDetailViewModel;

public class GoodsDetailActivityBindingData {

    public static class ActGoodsDetailBinding {
        @EAText(vm = GoodsDetailViewModel._detail, m = Goods._name)
        public TextView tvGoodsName;
        @EAText(vm = GoodsDetailViewModel._shopInfo)
        public TextView tvShopInfo;
    }
}