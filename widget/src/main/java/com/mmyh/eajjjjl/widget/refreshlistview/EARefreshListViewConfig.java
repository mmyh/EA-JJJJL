package com.mmyh.eajjjjl.widget.refreshlistview;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

public class EARefreshListViewConfig {

    public EAIRefreshView getRefreshView(Context context) {
        return new EADefaultRefreshView(context);
    }

    public EAILoadMoreView getLoadMoreView(Context context) {
        return new EADefaultLoadMoreView(context);
    }

    public int getRefreshDistance(Context context) {
        return (int) (context.getApplicationContext().getResources().getDisplayMetrics().density * 65 + 0.5f);
    }

    public boolean usesSwipeRefreshLayout() {
        return false;
    }

    public @ColorRes int getProgressBarColorForApi21() {
        return 0;
    }

    public @DrawableRes int getProgressBarColorForBelowApi21() {
        return 0;
    }
}
