package com.mmyh.eajjjjl.demo;

import retrofit2.Call;

public interface Api {

    public Call<String> test(String s1, String s2);

}
