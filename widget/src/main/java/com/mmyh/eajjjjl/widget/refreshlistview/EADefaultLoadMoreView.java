package com.mmyh.eajjjjl.widget.refreshlistview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.mmyh.eajjjjl.widget.R;

public class EADefaultLoadMoreView extends LinearLayout implements EAILoadMoreView {

    public EADefaultLoadMoreView(Context context) {
        this(context, null);
    }

    public EADefaultLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ea_view_default_loadmore, this);
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
    }

}
