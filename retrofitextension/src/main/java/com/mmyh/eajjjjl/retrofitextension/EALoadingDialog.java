package com.mmyh.eajjjjl.retrofitextension;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;


public class EALoadingDialog {

    public static EALoadingDialogConfig mConfig;

    public static void show(final LifecycleOwner owner, final FrameLayout rootView) {
        if (rootView != null) {
            rootView.post(new Runnable() {
                @Override
                public void run() {
                    if (owner != null) {
                        View view = null;
                        if (rootView.getTag(R.id.re_loading_view) != null && rootView.findViewById((Integer) rootView.getTag(R.id.re_loading_view)) != null) {
                            view = rootView.findViewById((Integer) rootView.getTag(R.id.re_loading_view));
                        }
                        if (view == null) {
                            view = createLV(rootView.getContext());
                            rootView.setTag(R.id.re_loading_view, view.getId());
                            rootView.setTag(R.id.re_loading_count, 0);
                        }
                        Activity activity = null;
                        if (owner instanceof Activity) {
                            activity = (Activity) owner;
                        } else if (owner instanceof Fragment) {
                            activity = ((Fragment) owner).getActivity();
                        }
                        int count = (int) rootView.getTag(R.id.re_loading_count);
                        rootView.setTag(R.id.re_loading_count, count + 1);
                        if (activity != null && !activity.isFinishing()) {
                            if (view.getParent() == null) {
                                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(dip2px(rootView.getContext(), 150), dip2px(rootView.getContext(), 150));
                                flp.gravity = Gravity.CENTER;
                                rootView.addView(view, flp);
                            }
                            if (view.getVisibility() != View.VISIBLE || count == 0) {
                                try {
                                    view.setVisibility(View.VISIBLE);
                                    view.getHandler().removeCallbacksAndMessages(view);
                                    rootView.setTag(R.id.re_loading_time, System.currentTimeMillis());
                                    view.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.ea_pop_in));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public static void dismiss(final LifecycleOwner owner, final FrameLayout rootView) {
        if (rootView != null) {
            rootView.post(new Runnable() {
                @Override
                public void run() {
                    View view = null;
                    if (rootView.getTag(R.id.re_loading_view) != null && rootView.findViewById((Integer) rootView.getTag(R.id.re_loading_view)) != null) {
                        view = rootView.findViewById((Integer) rootView.getTag(R.id.re_loading_view));
                    }
                    Activity activity = null;
                    if (owner instanceof Activity) {
                        activity = (Activity) owner;
                    } else if (owner instanceof Fragment) {
                        activity = ((Fragment) owner).getActivity();
                    }
                    if (activity != null && !activity.isFinishing()) {
                        int count = (int) rootView.getTag(R.id.re_loading_count);
                        count--;
                        rootView.setTag(R.id.re_loading_count, count);
                        if (view != null && count == 0 && view.getVisibility() != View.GONE) {
                            long tmp = 500 - (System.currentTimeMillis() - (long) rootView.getTag(R.id.re_loading_time));
                            if (tmp < 0) {
                                tmp = 0;
                            }
                            final View finalView = view;
                            try {
                                Message message = Message.obtain(view.getHandler(), new Runnable() {
                                    @Override
                                    public void run() {
                                        finalView.setVisibility(View.GONE);
                                        finalView.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.ea_pop_out));
                                    }
                                });
                                message.obj = view;
                                if (view.getHandler() != null) {
                                    view.getHandler().sendMessageDelayed(message, tmp);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    @SuppressLint("InflateParams")
    private static View createLV(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ea_view_loading_dialog, null);
        view.setId(ViewCompat.generateViewId());
        view.setVisibility(View.INVISIBLE);
        ProgressBar progressBar = view.findViewById(R.id.re_pb);
        if (mConfig != null && mConfig.progressBarBg != null) {
            progressBar.setIndeterminateDrawable(mConfig.progressBarBg);
        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        TextView textView = view.findViewById(R.id.re_text);
        if (mConfig != null && !TextUtils.isEmpty(mConfig.text)) {
            textView.setText(mConfig.text);
        }
//        PopupWindow dialog = new PopupWindow(view, dip2px(context, 150), dip2px(context, 150));
//        dialog.setAnimationStyle(R.style.re_pop_animation);
//        dialog.setBackgroundDrawable(new BitmapDrawable());
//        dialog.setOutsideTouchable(false);
//        dialog.setFocusable(false);
        return view;
    }

    private static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
