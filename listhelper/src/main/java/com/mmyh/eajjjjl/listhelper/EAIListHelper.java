package com.mmyh.eajjjjl.listhelper;


import com.mmyh.eajjjjl.widget.refreshlistview.EARefreshListView;

import java.util.List;

public interface EAIListHelper {

    public EARefreshListView getRefreshListView();

    public EAIListRequest getListRequest();

    public void query(EAListQueryType queryListType);

    public void updateListData(List list, boolean loadmore, boolean showFootView);


}
