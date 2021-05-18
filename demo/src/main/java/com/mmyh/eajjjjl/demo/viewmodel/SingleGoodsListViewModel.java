package com.mmyh.eajjjjl.demo.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.mmyh.eajjjjl.annotation.EAApi;
import com.mmyh.eajjjjl.annotation.EAViewModelEx;
import com.mmyh.eajjjjl.demo.TestCallback;
import com.mmyh.eajjjjl.demo.model.Goods;
import com.mmyh.eajjjjl.demo.model.Shop;
import com.mmyh.eajjjjl.demo.viewmodel.ex.SingleGoodsListViewModelEx;

import java.util.ArrayList;
import java.util.List;

@EAViewModelEx(superClass = ViewModel.class)
public class SingleGoodsListViewModel extends SingleGoodsListViewModelEx {


    private Handler handler = new Handler(Looper.getMainLooper());

    public MutableLiveData<List<Goods>> goodsList = new MutableLiveData<>();

    public void getGoodsList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Goods> list = new ArrayList<>();
                Goods g1 = new Goods();
                g1.name = "aaaa";
                g1.price = "87.98";
                Shop shop1 = new Shop();
                shop1.shopName = "鞋子旗舰店";
                shop1.bossName = "鞋老板";
                g1.shop = shop1;
                list.add(g1);

                Goods g2 = new Goods();
                g2.name = "bbbb";
                Shop shop2 = new Shop();
                shop2.shopName = "衣服旗舰店";
                shop2.bossName = "衣老板";
                g2.shop = shop2;
                list.add(g2);

                Goods g3 = new Goods();
                g3.name = "cccc";
                g3.price = "847.98";
                list.add(g3);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goodsList.setValue(list);
                    }
                }, 1000);
            }
        }).start();


    }
}
