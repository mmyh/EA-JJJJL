package com.mmyh.eajjjjl.demo.model;


import com.mmyh.eajjjjl.annotation.EAModelEx;
import com.mmyh.eajjjjl.demo.model.ex.GoodsEx;


@EAModelEx
public class Goods extends GoodsEx {

    public String name;

    public String price;

    public String picUrl;

    public Shop shop;

}
