package com.mmyh.eajjjjl.widget.autoscrollbanner;

import android.content.Context;

import com.mmyh.eajjjjl.widget.R;


/**
 * Created by Administrator on 2018/8/14.
 */

public class EADotConfig {

    Context mContext;

    public EADotConfig(Context context) {
        mContext = context;
    }

    public int getDotWidth() {
        return mContext.getResources().getDimensionPixelOffset(R.dimen.banner_dot_size);
    }

    public int getDotHeight() {
        return mContext.getResources().getDimensionPixelOffset(R.dimen.banner_dot_size);
    }

    public int getDotPaddingBottom() {
        return mContext.getResources().getDimensionPixelOffset(R.dimen.banner_dot_padding_bottom);
    }

    public int getSelectedDotResId() {
        return R.drawable.ea_x_banner_dot_on;
    }

    public int getUnSelectedDotResId() {
        return R.drawable.ea_x_banner_dot_off;
    }

    public int getDotRlMargin() {
        return mContext.getResources().getDimensionPixelOffset(R.dimen.banner_dot_rl_margin);
    }
}
