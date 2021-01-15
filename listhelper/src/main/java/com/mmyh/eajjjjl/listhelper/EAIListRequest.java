package com.mmyh.eajjjjl.listhelper;

public interface EAIListRequest {

    /**
     * 分页查询后,page+1
     */
    public void nextPage();

    /**
     * 下拉刷新重置page
     */
    public void resetPage();

}
