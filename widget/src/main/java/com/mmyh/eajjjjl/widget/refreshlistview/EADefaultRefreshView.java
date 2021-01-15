package com.mmyh.eajjjjl.widget.refreshlistview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mmyh.eajjjjl.widget.R;

public class EADefaultRefreshView extends LinearLayout implements EAIRefreshView {

    enum State {
        Pull, ReadyToRefresh, Refreshing
    }

    private TextView mTvRefresh;

    public EADefaultRefreshView(Context context) {
        this(context, null);
    }

    public EADefaultRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ea_view_default_refresh, this, true);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        EARefreshListViewConfig config = EARefreshListView.getConfig();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (config.getProgressBarColorForApi21() != 0) {
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(config.getProgressBarColorForApi21())));
                progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            if (config.getProgressBarColorForBelowApi21() != 0) {
                progressBar.setIndeterminateDrawable(getResources().getDrawable(config.getProgressBarColorForBelowApi21()));
            }
        }
        mTvRefresh = findViewById(R.id.tvRefreshText);
        reSetText(State.Pull);
    }

    @Override
    public void startPull(float dis, int refreshDistance) {
        reSetText(dis >= refreshDistance ? State.ReadyToRefresh : State.Pull);
    }

    @Override
    public void finishPull() {
    }

    @Override
    public void startRefresh() {
        reSetText(State.Refreshing);
    }

    @Override
    public void finishRefresh() {
        reSetText(State.Pull);
    }

    private void reSetText(State state) {
        switch (state) {
            case Pull:
                mTvRefresh.setText("下拉刷新...");
                break;
            case Refreshing:
                mTvRefresh.setText("正在刷新...");
                break;
            case ReadyToRefresh:
                mTvRefresh.setText("松开刷新...");
                break;
            default:
                break;
        }
    }
}
