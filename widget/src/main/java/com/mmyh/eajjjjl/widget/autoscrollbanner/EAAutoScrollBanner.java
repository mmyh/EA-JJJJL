package com.mmyh.eajjjjl.widget.autoscrollbanner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.mmyh.eajjjjl.widget.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */

public class EAAutoScrollBanner extends RelativeLayout {

    private static final int INTERVALTIME = 3000;

    ViewPager mViewPager;

    LinearLayout mVgDot;

    BannerAdapter mAdapter;

    private SafeHandler mHandler;

    private int mIntervalTime = INTERVALTIME;

    private boolean mIsAutoPlay = false;

    private boolean mIsPaused = false;

    OnBannerClickListener mListener;

    EADotConfig mDotConfig;

    int mLastSelectIndex = 0;

    public EAAutoScrollBanner(Context context) {
        this(context, null);
    }

    public EAAutoScrollBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EAAutoScrollBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.ea_view_autoscrollbanner, this);
        mViewPager = findViewById(R.id.mmyh_viewpager);
        mVgDot = findViewById(R.id.mmyh_vg_dot);
        mAdapter = new BannerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    changeDot(mAdapter.getRealSize() - 1);
                } else if (position == mAdapter.getCount() - 1) {
                    changeDot(0);
                } else {
                    changeDot(position - 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (mViewPager.getCurrentItem() == 0) {
                        mViewPager.setCurrentItem(mAdapter.getRealSize(), false);
                    } else if (mViewPager.getCurrentItem() == mAdapter.getCount() - 1) {
                        mViewPager.setCurrentItem(1, false);
                    }
                    if (mIsAutoPlay) {
                        sendChangePageMessage();
                    }
                } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    mHandler.removeMessages(0);
                }
            }
        });
        mHandler = new SafeHandler(this);
    }

    public void setBannerData(List<? extends EAIBanner> data, ViewPager.OnPageChangeListener listener) {
        setBannerData(data, listener, null);
    }

    public void setBannerData(List<? extends EAIBanner> data, ViewPager.OnPageChangeListener pageChangeListener, OnBannerClickListener listener) {
        mListener = listener;
        if (pageChangeListener != null) {
            mViewPager.addOnPageChangeListener(pageChangeListener);
        }
        mAdapter.setDatas(data);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getRealSize() > 1) {
            mViewPager.setCurrentItem(1, false);
        }
        mLastSelectIndex = 0;
        createDot();
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void autoPlay(LifecycleOwner lifecycleOwner) {
        autoPlay(INTERVALTIME, lifecycleOwner);
    }

    public void autoPlay(int intervalTime, final LifecycleOwner lifecycleOwner) {
        if (mIsAutoPlay) {
            return;
        }
        mIsAutoPlay = true;
        mIntervalTime = intervalTime;
        lifecycleOwner.getLifecycle().addObserver(new EALifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                mIsPaused = false;
                if (mIsAutoPlay) {
                    sendChangePageMessage();
                }
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                mIsPaused = true;
                mHandler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                owner.getLifecycle().removeObserver(this);
            }
        });
        sendChangePageMessage();
    }

    public void initDot(boolean enable, EADotConfig dotConfig) {
        if (dotConfig != null) {
            mDotConfig = dotConfig;
        }
        createDot();
        mVgDot.setVisibility(enable ? VISIBLE : GONE);
    }

    private void createDot() {
        mVgDot.removeAllViews();
        if (mDotConfig == null) {
            mDotConfig = new EADotConfig(getContext());
        }
        if (mAdapter.getRealSize() < 2) {
            return;
        }
        for (int i = 0; i < mAdapter.getRealSize(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(mDotConfig.getUnSelectedDotResId());
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(mDotConfig.getDotWidth(), mDotConfig.getDotHeight());
            if (i == 0) {
                imageView.setImageResource(mDotConfig.getSelectedDotResId());
                llp.rightMargin = mDotConfig.getDotRlMargin();
            } else if (i == mAdapter.getRealSize() - 1) {
                llp.leftMargin = mDotConfig.getDotRlMargin();
            } else {
                llp.rightMargin = mDotConfig.getDotRlMargin();
                llp.leftMargin = mDotConfig.getDotRlMargin();
            }
            mVgDot.addView(imageView, llp);
        }
        mVgDot.setPadding(0, 0, 0, mDotConfig.getDotPaddingBottom());
    }

    private void changeDot(int pos) {
        if (mVgDot.getChildCount() == 0) {
            return;
        }
        if (mDotConfig == null) {
            mDotConfig = new EADotConfig(getContext());
        }
        ((ImageView) mVgDot.getChildAt(mLastSelectIndex)).setImageResource(mDotConfig.getUnSelectedDotResId());
        ((ImageView) mVgDot.getChildAt(pos)).setImageResource(mDotConfig.getSelectedDotResId());
        mLastSelectIndex = pos;
    }

    private void doHandler() {
        if (!mIsPaused) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            sendChangePageMessage();
        }
    }

    private void sendChangePageMessage() {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, mIntervalTime);
    }

    final class BannerAdapter extends PagerAdapter {

        List<EAIBanner> mDatas = new ArrayList<>();

        SparseArray<View> mViews = new SparseArray<>();

        void setDatas(List<? extends EAIBanner> data) {
            if (data != null) {
                mDatas.clear();
                mDatas.addAll(data);
            }
        }

        int getRealSize() {
            return mDatas.size();
        }

        @Override
        public int getCount() {
            if (mDatas.size() < 2) {
                return mDatas.size();
            }
            return mDatas.size() + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            EAIBanner iBanner;
            if (position == 0) {
                iBanner = mDatas.get(mDatas.size() - 1);
            } else if (position == mDatas.size() + 1) {
                iBanner = mDatas.get(0);
            } else {
                iBanner = mDatas.get(position - 1);
            }
            View view = mViews.get(position);
            if (view == null || view.getParent() != null) {
                view = iBanner.createHandleView(getContext());
                view.setTag(R.id.tag_auto_scroll_banner, iBanner);
                mViews.put(position, view);
            }
            container.addView(view);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        EAIBanner banner = (EAIBanner) view.getTag(R.id.tag_auto_scroll_banner);
                        mListener.onBannerClick(mDatas.indexOf(banner), banner);
                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private static class SafeHandler extends Handler {

        private final WeakReference<EAAutoScrollBanner> mAutoScrollBanner;

        SafeHandler(EAAutoScrollBanner autoScrollBanner) {
            mAutoScrollBanner = new WeakReference<>(autoScrollBanner);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mAutoScrollBanner.get() != null) {
                mAutoScrollBanner.get().doHandler();
            }
        }
    }
}
