package com.mmyh.eajjjjl.widget.autoscrollbanner;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2018/8/13.
 */

public interface EAIBanner {

    public enum BannerType {
        PIC, VIDEO
    }

    public String getBannerUrl();

    public BannerType getBannerType();

    public View createHandleView(Context context);

}
