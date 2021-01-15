package com.mmyh.eajjjjl.listhelper;

import android.app.Activity;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import com.mmyh.eajjjjl.widget.refreshlistview.EARefreshListView;
import com.mmyh.eajjjjl.widget.refreshlistview.OnLoadMoreListener;
import com.mmyh.eajjjjl.widget.refreshlistview.OnRefreshListener;


public class EAListHelper {

    EARefreshListView refreshListView;

    EAIListHelper iListHelper;

    Activity activity;

    EAIListRequest request;

    private EAListQueryType lastListQueryType;

    private View netOffView;

    private View netErrorView;

    private boolean canQuery = true;

    private int dataCount;

    private static int netOffViewId;

    private static int netOffViewRetryViewId;

    private static int netErrorViewId;

    private static int netErrorViewRetryViewId;

    public static EAListHelper create(Activity activity) {
        EAListHelper listHelper = new EAListHelper();
        listHelper.activity = activity;
        if (activity instanceof EAIListHelper) {
            listHelper.iListHelper = (EAIListHelper) activity;
            listHelper.refreshListView = listHelper.iListHelper.getRefreshListView();
            listHelper.request = listHelper.iListHelper.getListRequest();
        } else {
            throw new RuntimeException(activity.getClass().getName() + " must implements IQueryList");
        }
        listHelper.init();
        return listHelper;
    }

    public static EAListHelper create(Fragment fragment) {
        EAListHelper listHelper = new EAListHelper();
        listHelper.activity = fragment.getActivity();
        if (fragment instanceof EAIListHelper) {
            listHelper.iListHelper = (EAIListHelper) fragment;
            listHelper.refreshListView = listHelper.iListHelper.getRefreshListView();
            listHelper.request = listHelper.iListHelper.getListRequest();
        } else {
            throw new RuntimeException(fragment.getClass().getName() + " must implements IQueryList");
        }
        listHelper.init();
        return listHelper;
    }

    private EAListHelper() {
    }

    public static void setNetOffView(@LayoutRes int viewId, @IdRes int retryViewId) {
        netOffViewId = viewId;
        netOffViewRetryViewId = retryViewId;
    }

    public static void setNetErrorView(@LayoutRes int viewId, @IdRes int retryViewId) {
        netErrorViewId = viewId;
        netErrorViewRetryViewId = retryViewId;
    }

    public void start() {
        if (netOffView != null) {
            refreshListView.setFullScreenView(netOffView);
        }
        if (netErrorView != null) {
            refreshListView.setFullScreenView(netErrorView);
        }
        initQuery();
    }

    public void initQuery() {
        query(EAListQueryType.Init);
    }

    public boolean showNetOffView() {
        if (netOffView != null) {
            netOffView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    public void dismissNetOffView() {
        if (netOffView != null) {
            netOffView.setVisibility(View.GONE);
        }
    }

    public boolean showNetErrorView() {
        if (netErrorView != null) {
            netErrorView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    public void dismissNetErrorView() {
        if (netErrorView != null) {
            netErrorView.setVisibility(View.GONE);
        }
    }

    public boolean handlerError(EAListErrorRule rule) {
        if (rule.isNetOff(activity)) {
            if (showNetOffView()) {
                refreshListView.setEnableRefresh(false);
                refreshListView.setEnableLoadMore(false);
            }
            return true;
        } else if (rule.isNetError()) {
            if (showNetErrorView()) {
                refreshListView.setEnableRefresh(false);
                refreshListView.setEnableLoadMore(false);
            }
            return true;
        }
        if (refreshListView.getOnRefreshListener() != null) {
            refreshListView.setEnableRefresh(true);
        }
        return false;
    }

    private void query(final EAListQueryType queryListType) {
        synchronized (EAListHelper.class) {
            if (!canQuery) {
                return;
            }
            canQuery = false;
        }
        lastListQueryType = queryListType;
        if (EAListQueryType.Init.equals(queryListType) || EAListQueryType.Refresh.equals(queryListType)) {
            if (request != null) {
                request.resetPage();
            }
        } else if (EAListQueryType.LoadMore.equals(queryListType)) {
            if (request != null) {
                request.nextPage();
            }
        }
        if (iListHelper != null) {
            iListHelper.query(queryListType);
        }
    }

    public void finishQuery(EAIListResponse response, EAListErrorRule rule) {
        canQuery = true;
        refreshListView.finishRefresh();
        refreshListView.finishLoadMore();
        if (!handlerError(rule)) {
            dismissNetErrorView();
            dismissNetOffView();
            if (response != null && response.isSuccess()) {
                if (EAListQueryType.Init.equals(lastListQueryType) || EAListQueryType.Refresh.equals(lastListQueryType)) {
                    if (response.getDataList() != null) {
                        dataCount = response.getDataList().size();
                    }
                    if (refreshListView.getOnLoadMoreListener() != null) {
                        refreshListView.setEnableLoadMore(true);
                    }
                } else {
                    if (response.getDataList() != null) {
                        dataCount += response.getDataList().size();
                    }
                }
                boolean showFootview = false;
                if (!response.hasNextPage()) {
                    refreshListView.setEnableLoadMore(false);
                    if (dataCount > 0) {
                        showFootview = true;
                    }
                }
                iListHelper.updateListData(response.getDataList(), EAListQueryType.LoadMore.equals(lastListQueryType), showFootview);
            }
            if (dataCount == 0) {
                refreshListView.showEmptyView();
            } else {
                refreshListView.hideEmptyView();
            }
        }
    }

    private void init() {
        if (netOffViewId != 0) {
            netOffView = activity.getLayoutInflater().inflate(netOffViewId, null);
            netOffView.findViewById(netOffViewRetryViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query(lastListQueryType);
                }
            });
            netOffView.setVisibility(View.GONE);
        }
        if (netErrorViewId != 0) {
            netErrorView = activity.getLayoutInflater().inflate(netErrorViewId, null);
            netErrorView.findViewById(netErrorViewRetryViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query(lastListQueryType);
                }
            });
            netErrorView.setVisibility(View.GONE);
        }

        refreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                query(EAListQueryType.Refresh);
            }
        });
        refreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                query(EAListQueryType.LoadMore);
            }
        });
    }


}
