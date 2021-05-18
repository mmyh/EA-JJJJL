package com.mmyh.eajjjjl.demo1.service;

import com.mmyh.eajjjjl.demo1.model.User;
import com.mmyh.eajjjjl.router.EAService;

import java.net.URLEncoder;

public class Demo1ServiceImpl extends EAService {

    private int a;

    public void goDemo1(String a, int b, Long c) {
        System.out.println("goDemo1 start");
        System.out.println("a=" + a);
        System.out.println("b=" + b);
        System.out.println("c=" + c);
        //return 0;
    }

    public boolean doDemo1() {
        return false;
    }

    public Integer doDemo2(long a) {
        return 99;
    }
}
