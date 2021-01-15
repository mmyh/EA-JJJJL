package com.mmyh.eajjjjl.widget.refreshlistview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class EARefreshListView extends ConstraintLayout implements NestedScrollingChild2, NestedScrollingParent2 {

    enum ActionMode {
        Non, Refresh, LoadMore
    }

    private EAIRefreshView mRefreshView;

    private EAILoadMoreView mLoadMoreView;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View mEmptyView;

    private NestedScrollingChildHelper mChildHelper;

    private NestedScrollingParentHelper mParentHelper;

    private OnRefreshListener mRefreshListener;

    private OnLoadMoreListener mLoadMoreListener;

    private boolean enableRefresh = true;

    private boolean enableLoadMore = true;

    private float mTotalUnconsumed;

    private final int[] mParentOffsetInWindow = new int[2];

    private final int[] mParentScrollConsumed = new int[2];

    private ActionMode mActionMode = ActionMode.Non;

    private int mLoadMoreHeight;

    private int mRefreshViewHeight;

    private int mRefreshDistance;

    public static EARefreshListViewConfig Config;

    public EARefreshListView(Context context) {
        this(context, null);
    }

    public EARefreshListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mChildHelper = new NestedScrollingChildHelper(this);
        mParentHelper = new NestedScrollingParentHelper(this);
        setNestedScrollingEnabled(true);
        init();
    }

    static EARefreshListViewConfig getConfig() {
        if (Config == null) {
            Config = new EARefreshListViewConfig();
        }
        return Config;
    }

    public void init() {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mRecyclerView.getId() == View.NO_ID) {
            mRecyclerView.setId(ViewCompat.generateViewId());
        }
        EARefreshListViewConfig config = getConfig();
        mRefreshDistance = config.getRefreshDistance(getContext());

        if (!config.usesSwipeRefreshLayout()) {
            mRefreshView = config.getRefreshView(getContext());
            final View refreshView = (View) mRefreshView;
            if (refreshView.getId() == View.NO_ID) {
                refreshView.setId(ViewCompat.generateViewId());
            }
            ConstraintLayout.LayoutParams rvlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            rvlp.topToTop = LayoutParams.PARENT_ID;
            addView(refreshView, rvlp);
        }

        mLoadMoreView = config.getLoadMoreView(getContext());
        final View loadMoreView = (View) mLoadMoreView;
        if (loadMoreView.getId() == View.NO_ID) {
            loadMoreView.setId(ViewCompat.generateViewId());
        }
        ConstraintLayout.LayoutParams lvlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lvlp.bottomToBottom = LayoutParams.PARENT_ID;
        addView(loadMoreView, lvlp);

        ConstraintLayout.LayoutParams clp = new Constraints.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        if (mRefreshView != null) {
            clp.topToBottom = ((View) mRefreshView).getId();
        } else {
            clp.topToTop = LayoutParams.PARENT_ID;
        }
        clp.bottomToTop = loadMoreView.getId();
        addView(mRecyclerView, clp);

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
                if (mRefreshView != null) {
                    mRefreshViewHeight = ((View) mRefreshView).getHeight();
                }
                mLoadMoreHeight = loadMoreView.getHeight();
                setPadding(0, -mRefreshViewHeight, 0, -mLoadMoreHeight);
                loadMoreView.setVisibility(INVISIBLE);
            }
        });
    }

    public void setAdapter(@Nullable RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
            }
        });
        if (mSwipeRefreshLayout.getId() == View.NO_ID) {
            mSwipeRefreshLayout.setId(ViewCompat.generateViewId());
        }
    }

    public void setEmptyView(View emptyView) {
        setEmptyView(emptyView, null);
    }

    public void setEmptyView(View emptyView, final View headView) {
        if (mEmptyView != null && mEmptyView.getParent() != null) {
            removeView(mEmptyView);
        }
        if (emptyView == null) {
            return;
        }
        mEmptyView = emptyView;
        mEmptyView.setVisibility(GONE);
        if (headView != null) {
            headView.post(new Runnable() {
                @Override
                public void run() {
                    addEmptyView(headView.getHeight());
                }
            });
        } else {
            addEmptyView(0);
        }
    }

    private void addEmptyView(int topMargin) {
        ConstraintLayout.LayoutParams flp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0);
        flp.topMargin = topMargin;
        flp.topToTop = mRecyclerView.getId();
        flp.bottomToBottom = mRecyclerView.getId();
        addView(mEmptyView, flp);
    }

    public void showEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(VISIBLE);
        }
    }

    public void hideEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(GONE);
        }
    }

    public void setFullScreenView(View view) {
        if (mRecyclerView == null) {
            throw new RuntimeException("call this method after setAdapter");
        }
        ConstraintLayout.LayoutParams flp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0);
        flp.topToTop = mRecyclerView.getId();
        flp.bottomToBottom = mRecyclerView.getId();
        addView(view, flp);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public OnLoadMoreListener getOnLoadMoreListener() {
        return mLoadMoreListener;
    }

    public OnRefreshListener getOnRefreshListener() {
        return mRefreshListener;
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
    }

    public boolean isEnableRefresh() {
        return enableRefresh;
    }

    public boolean isEnableLoadMore() {
        return enableLoadMore;
    }

    //parent

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return isNestedScrollingEnabled()
                && isEnabled()
                && (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                && (enableRefresh || enableLoadMore)
                && mActionMode.equals(ActionMode.Non);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(axes, type);
        if (mActionMode.equals(ActionMode.Refresh)) {
            mTotalUnconsumed = 0;
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target, type);
        stopNestedScroll(type);
        if (mActionMode.equals(ActionMode.Refresh)) {
            finishPull();
            mTotalUnconsumed = 0;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow, type);
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (!mActionMode.equals(ActionMode.LoadMore)
                && !target.canScrollVertically(-1)
                && dy < 0
                && enableRefresh
                && type == ViewCompat.TYPE_TOUCH) {
            mTotalUnconsumed += Math.abs(dy);
            startPull(mTotalUnconsumed);
        } else if (mActionMode.equals(ActionMode.Non)
                && !target.canScrollVertically(1)
                && dy > 0
                && enableLoadMore) {
            startLoadMore();
            //防止快速滑动惯性滚动
            mRecyclerView.stopNestedScroll(type);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy > 0 && mTotalUnconsumed > 0 && mActionMode.equals(ActionMode.Refresh)) {
            if (mRefreshView != null) {
                if (dy > mTotalUnconsumed) {
                    consumed[1] = dy - (int) mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                } else {
                    mTotalUnconsumed -= dy;
                    consumed[1] = dy;
                }
            }
            startPull(mTotalUnconsumed);
        }
        final int[] parentConsumed = mParentScrollConsumed;
        if (mChildHelper.dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    //child


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        try {
            return super.onNestedPreFling(target, velocityX, velocityY);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        try {
            return super.onNestedFling(target, velocityX, velocityY, consumed);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startPull(float dis) {
        mActionMode = ActionMode.Refresh;
        if (mRefreshView != null) {
            dis = (dis <= 0) ? 0 : dis / 2;
            mRefreshView.startPull(dis, mRefreshDistance);
            setRefreshTranslationY((int) dis);
        }
    }

    private void finishPull() {
        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
            mActionMode = ActionMode.Non;
        }
        if (mRefreshView != null) {
            View refreshView = (View) mRefreshView;
            if (refreshView.getTranslationY() >= mRefreshDistance) {
                refreshViewAnim((int) refreshView.getTranslationY(), mRefreshDistance, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRefreshView.startRefresh();
                        if (mRefreshListener != null) {
                            mRefreshListener.onRefresh();
                        }
                    }
                });
            } else {
                refreshViewAnim((int) refreshView.getTranslationY(), 0, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mActionMode = ActionMode.Non;
                        mRefreshView.finishPull();
                    }
                });
            }
        }
    }

    public void finishRefresh() {
        if (mSwipeRefreshLayout != null) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            mActionMode = ActionMode.Non;
        }
        if (mRefreshView != null) {
            View refreshView = (View) mRefreshView;
            if ((int) refreshView.getTranslationY() != 0) {
                refreshViewAnim((int) refreshView.getTranslationY(), 0, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mActionMode = ActionMode.Non;
                        mRefreshView.finishRefresh();
                    }
                });
            }
        }
    }

    private void refreshViewAnim(int start, int finish, AnimatorListenerAdapter adapter) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, finish);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setRefreshTranslationY(value);
            }
        });
        valueAnimator.addListener(adapter);
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    private void setRefreshTranslationY(int translationY) {
        if (mRefreshView == null) {
            return;
        }
        ((View) mRefreshView).setTranslationY(translationY);
        mRecyclerView.setTranslationY(translationY);
        if (mEmptyView != null) {
            mEmptyView.setTranslationY(translationY);
        }
    }

    private void startLoadMore() {
        if (mLoadMoreHeight == 0 || mLoadMoreView == null || ((View) mLoadMoreView).getVisibility() == VISIBLE) {
            return;
        }
        mActionMode = ActionMode.LoadMore;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(false);
        }
        final View loadMoreView = (View) mLoadMoreView;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mLoadMoreHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setLoadMoreTranslationY(-value);
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                loadMoreView.setVisibility(VISIBLE);
            }
        });
        valueAnimator.start();
    }

    public void finishLoadMore() {
        if (mLoadMoreHeight == 0 || mLoadMoreView == null || ((View) mLoadMoreView).getVisibility() == INVISIBLE) {
            return;
        }
        final View loadMoreView = (View) mLoadMoreView;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(mLoadMoreHeight, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setLoadMoreTranslationY(-value);
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                loadMoreView.setVisibility(INVISIBLE);
                mActionMode = ActionMode.Non;
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
            }

        });
        valueAnimator.start();
    }

    private void setLoadMoreTranslationY(int translationY) {
        ((View) mLoadMoreView).setTranslationY(translationY);
        mRecyclerView.setTranslationY(translationY);
        if (mEmptyView != null) {
            mEmptyView.setTranslationY(translationY);
        }
    }
}
