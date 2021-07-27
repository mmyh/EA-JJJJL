package com.mmyh.eajjjjl.widget.toolbarwindow;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import com.mmyh.eajjjjl.widget.R;

import java.util.List;

public class EAPopWin {

    PopupWindow mPop;

    NavController mController;

    OnEAPopWinItemClickListener mOnEAPopWinItemClickListener;

    private EAPopWin(Builder builder) {
        mPop = createPop(builder);
        initNavController(builder);
    }

    public PopupWindow get() {
        return mPop;
    }

    private PopupWindow createPop(Builder builder) {
        View view = LayoutInflater.from(builder.activity).inflate(R.layout.ea_view_pop, null);
        LinearLayout itemContainer = view.findViewById(R.id.vgItemContainer);
        if (builder.popBack != 0) {
            itemContainer.setBackground(ResourcesCompat.getDrawable(
                    builder.activity.getResources(), builder.popBack, null));
        }
        int i = 0;
        for (final EAPopWinItem item : builder.items) {
            item.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPop.dismiss();
                    if (builder.onEAPopWinItemClickListener != null) {
                        mController.navigate(item.navDestinationId, builder.onEAPopWinItemClickListener.getBundle());
                    } else {
                        mController.navigate(item.navDestinationId);
                    }
                }
            });
            itemContainer.addView(item.view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (i != builder.items.size() - 1) {
                View line = new View(builder.activity);
                if (builder.dividerBack != 0) {
                    line.setBackground(ResourcesCompat.getDrawable(
                            builder.activity.getResources(), builder.dividerBack, null));
                }
                LinearLayout.LayoutParams llpLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                itemContainer.addView(line, llpLine);
            }
            i++;

        }
        itemContainer.measure(0, 0);
        PopupWindow dialog = new PopupWindow(view);
        if (builder.animStyle != 0) {
            dialog.setAnimationStyle(builder.animStyle);
        }
        dialog.setWidth(itemContainer.getMeasuredWidth());
        dialog.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setBackgroundDrawable(new BitmapDrawable());
        dialog.setOutsideTouchable(true);
        dialog.setFocusable(false);
        return dialog;
    }

    private void initNavController(Builder builder) {
        mController = Navigation.findNavController(builder.activity, builder.navHostFragmentId);
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(mController.getNavigatorProvider()));
        FragmentNavigator fragmentNavigator = mController.getNavigatorProvider().getNavigator(FragmentNavigator.class);
        int defaultStartDestination = 0;
        for (EAPopWinItem item : builder.items) {
            if (item.navDestinationId != 0
                    && !TextUtils.isEmpty(item.navDestinationClassName)) {
                defaultStartDestination = item.navDestinationId;
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setId(item.navDestinationId);
                destination.setClassName(item.navDestinationClassName);
                navGraph.addDestination(destination);
                if (item.isStartDestination) {
                    navGraph.setStartDestination(destination.getId());
                }
            }
        }
        if (navGraph.getStartDestination() == 0 && defaultStartDestination != 0) {
            navGraph.setStartDestination(defaultStartDestination);
        }
        mController.setGraph(navGraph);
    }

    public NavController getNavController() {
        return mController;
    }

    public void reClickItem(Bundle bundle) {
        if (mController.getCurrentDestination() != null) {
            mController.navigate(mController.getCurrentDestination().getId(), bundle);
        }
    }

    public static final class Builder {

        private int animStyle;

        private int dividerBack;

        private int popBack;

        private Activity activity;

        private List<EAPopWinItem> items;

        private int navHostFragmentId;

        private OnEAPopWinItemClickListener onEAPopWinItemClickListener;

        public Builder(Activity activity, int navHostFragmentId, List<EAPopWinItem> items) {
            this.activity = activity;
            this.items = items;
            this.navHostFragmentId = navHostFragmentId;
        }

        public Builder animStyle(int animStyle) {
            this.animStyle = animStyle;
            return this;
        }

        public Builder dividerBack(@DrawableRes int dividerBack) {
            this.dividerBack = dividerBack;
            return this;
        }

        public Builder popBack(@DrawableRes int popBack) {
            this.popBack = popBack;
            return this;
        }

        public Builder setEAPopWinItemClickListener(OnEAPopWinItemClickListener listener) {
            this.onEAPopWinItemClickListener = listener;
            return this;
        }

        public EAPopWin build() {
            return new EAPopWin(this);
        }
    }

    public interface OnEAPopWinItemClickListener {

        public Bundle getBundle();

    }
}
