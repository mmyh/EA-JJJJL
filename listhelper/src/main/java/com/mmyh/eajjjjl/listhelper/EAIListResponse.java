package com.mmyh.eajjjjl.listhelper;

import java.util.List;

public interface EAIListResponse {

    /**
     * 分页查询是否还有下一页
     *
     * @return true 还有下一页
     */
    public boolean hasNextPage();

    /**
     * 获取数据列表
     *
     * @return 数据列表
     */
    public List<?> getDataList();

    /**
     * 判断请求返回结果是否正确,正确才会更新数据
     *
     * @return true, 包括服务器正常响应和业务返回正确
     */
    public boolean isSuccess();
}
