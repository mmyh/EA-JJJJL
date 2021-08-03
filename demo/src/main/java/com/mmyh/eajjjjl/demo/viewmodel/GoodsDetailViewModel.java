package com.mmyh.eajjjjl.demo.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.mmyh.eajjjjl.demo.model.Goods;
import com.mmyh.eajjjjl.demo.model.Shop;
import com.mmyh.eajjjjl.demo.viewmodel.ex.GoodsDetailViewModelEx;


public class GoodsDetailViewModel extends GoodsDetailViewModelEx {


    public Handler handler = new Handler(Looper.getMainLooper());

    public MutableLiveData<Goods> detail = new MutableLiveData<>();

    public MutableLiveData<String> shopInfo = new MutableLiveData<>();

    public void getDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Goods g1 = new Goods();
                g1.name = "aaaa";
                g1.price = "87.98";
                Shop shop1 = new Shop();
                shop1.shopName = "鞋子旗舰店";
                shop1.bossName = "鞋老板";
                g1.shop = shop1;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        detail.setValue(g1);
                        shopInfo.setValue(g1.shop.shopName + ":" + g1.shop.bossName);
                    }
                }, 1000);
            }
        }).start();
    }

}
