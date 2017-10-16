package com.sunfusheng.base.widget.RefreshView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.sunfusheng.base.R;
import com.sunfusheng.utils.DisplayUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MaterialRefreshLayout extends FrameLayout implements NestedScrollingParent, NestedScrollingChild {

    private int DEFAULT_WAVE_HEIGHT = 140;
    private int HIGHER_WAVE_HEIGHT = 180;
    private int DEFAULT_HEAD_HEIGHT = 70;
    private int hIGHER_HEAD_HEIGHT = 100;
    private int DEFAULT_PROGRESS_SIZE = 50;
    private int BIG_PROGRESS_SIZE = 60;
    public static final int ANIM_DURATION = 200;
    private int PROGRESS_STOKE_WIDTH = 3;

    protected MaterialHeadView materialHeadView;
    protected FrameLayout mHeadLayout;
    private View mChildView;

    private boolean isOverlay;
    private int waveType;
    private int waveColor;
    private int headBgColor;
    protected float mWaveHeight;
    protected float mHeadHeight;
    protected boolean isRefreshing;
    private float mTouchY;
    private float mCurrentY;
    private DecelerateInterpolator decelerateInterpolator;
    private float headHeight;
    private float waveHeight;
    private int[] colorSchemeColors;
    private int colorsId;
    private boolean showArrow;
    private int textType;
    private MaterialRefreshListener refreshListener;
    private boolean showProgressBg;
    private int progressBg;
    private boolean isShowWave;
    private int progressSizeType;
    private int progressSize = 0;

    private boolean mNestedScrollInProgress;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mReturningToStart;

    public MaterialRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public MaterialRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defstyleAttr) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("can only have one child widget");
        }

        decelerateInterpolator = new DecelerateInterpolator(10);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MaterialRefreshLayout, defstyleAttr, 0);
        isOverlay = t.getBoolean(R.styleable.MaterialRefreshLayout_overlay, false);
        /**attrs for materialWaveView*/
        waveType = t.getInt(R.styleable.MaterialRefreshLayout_wave_height_type, 0);
        if (waveType == 0) {
            headHeight = DEFAULT_HEAD_HEIGHT;
            waveHeight = DEFAULT_WAVE_HEIGHT;
            MaterialWaveView.defaultHeadHeight = DEFAULT_HEAD_HEIGHT;
            MaterialWaveView.defaultWaveHeight = DEFAULT_WAVE_HEIGHT;
        } else {
            headHeight = hIGHER_HEAD_HEIGHT;
            waveHeight = HIGHER_WAVE_HEIGHT;
            MaterialWaveView.defaultHeadHeight = hIGHER_HEAD_HEIGHT;
            MaterialWaveView.defaultWaveHeight = HIGHER_WAVE_HEIGHT;
        }
        waveColor = t.getColor(R.styleable.MaterialRefreshLayout_wave_color, Color.WHITE);
        isShowWave = t.getBoolean(R.styleable.MaterialRefreshLayout_wave_show, true);

        /**attrs for circleprogressbar*/
        colorsId = t.getResourceId(R.styleable.MaterialRefreshLayout_progress_colors, R.array.google_colors);
        colorSchemeColors = context.getResources().getIntArray(colorsId);
        showArrow = t.getBoolean(R.styleable.MaterialRefreshLayout_progress_show_arrow, true);
        textType = t.getInt(R.styleable.MaterialRefreshLayout_progress_text_visibility, 1);
        showProgressBg = t.getBoolean(R.styleable.MaterialRefreshLayout_progress_show_circle_backgroud, true);
        progressBg = t.getColor(R.styleable.MaterialRefreshLayout_progress_background_color, 0xFFFAFAFA);
        progressSizeType = t.getInt(R.styleable.MaterialRefreshLayout_progress_size_type, 0);
        if (progressSizeType == 0) {
            progressSize = DEFAULT_PROGRESS_SIZE;
        } else {
            progressSize = BIG_PROGRESS_SIZE;
        }

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        setNestedScrollingEnabled(true);
        t.recycle();
        initHeadView();
    }

    private void initHeadView() {
        Context context = getContext();

        FrameLayout headViewLayout = new FrameLayout(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.TOP;
        headViewLayout.setLayoutParams(layoutParams);
        mHeadLayout = headViewLayout;

        materialHeadView = createHeadView(context);
        mHeadLayout.addView(materialHeadView);

        setWaveHeight(DisplayUtil.dip2px(waveHeight));
        setHeaderHeight(DisplayUtil.dip2px(headHeight));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isRefreshing) {
            finishRefresh();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mChildView = getChildAt(0);
        if (mChildView == null) {
            return;
        }
        if (getChildCount() == 0) {
            this.addView(mHeadLayout);
        } else {
            boolean flag = false;
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) == mHeadLayout) {
                    flag = true;
                }
            }
            if (!flag) {
                this.addView(mHeadLayout);
            }
        }
        if (isRefreshing) { //补放materialHeadView为创建之前请求的refresh动画
            AndroidSchedulers.mainThread().createWorker().schedule(
                    this::autoRefresh, 50, TimeUnit.MILLISECONDS);
        }
    }


    protected MaterialHeadView createHeadView(Context context) {
        MaterialHeadView view = new MaterialHeadView(context);
        view.setWaveColor(isShowWave ? waveColor : Color.WHITE);
        view.setBackgroundColor(headBgColor);
        return view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || isRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float dy = currentY - mTouchY;
                if (dy > 0 && !canChildScrollUp()) {
                    if (materialHeadView != null) {
                        materialHeadView.onBegin(this);
                    }
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void moveDown(float dy) {
        if (!isEnabled()) return;
        dy = Math.min(mWaveHeight * 2, dy);
        dy = Math.max(0, dy);
        if (mChildView != null) {
            float offsetY = decelerateInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2;
            float fraction = offsetY / mHeadHeight;
            mHeadLayout.getLayoutParams().height = (int) offsetY;
            mHeadLayout.requestLayout();

            if (materialHeadView != null) {
                materialHeadView.onPull(this, fraction);
                if (mHeadLayout.getLayoutParams().height > mHeadHeight) {
                    materialHeadView.onCanRelease(this, true);
                } else {
                    materialHeadView.onCanRelease(this, false);
                }
            }

            if (!isOverlay)
                ViewCompat.setTranslationY(mChildView, offsetY);

        }
    }

    private void moveCancel() {
        if (mChildView != null) {
            if (isOverlay && mHeadLayout != null) {
                if (mHeadLayout.getLayoutParams().height > mHeadHeight) {

                    updateListener();

                    mHeadLayout.getLayoutParams().height = (int) mHeadHeight;
                    mHeadLayout.requestLayout();

                } else {
                    mHeadLayout.getLayoutParams().height = 0;
                    mHeadLayout.requestLayout();
                }

            } else {
                if (ViewCompat.getTranslationY(mChildView) >= mHeadHeight) {
                    createAnimatorTranslationY(mChildView, mHeadHeight, mHeadLayout, ANIM_DURATION);
                    updateListener();
                } else {
                    createAnimatorTranslationY(mChildView, 0, mHeadLayout, ANIM_DURATION);
                }
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!isEnabled() || mReturningToStart || canChildScrollUp() || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        if (isRefreshing) {
            return super.onTouchEvent(e);
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = mCurrentY - mTouchY;
                moveDown(dy);
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                moveCancel();
                return true;
        }
        return super.onTouchEvent(e);
    }


    /**
     * only show loading anim,
     * don't load data really
     */
    public void autoRefresh() {
//        updateListener();
        isRefreshing = true;
        if (materialHeadView != null) {
            materialHeadView.onRefreshing(MaterialRefreshLayout.this);
            if (isOverlay && mHeadLayout != null) {
                mHeadLayout.getLayoutParams().height = (int) mHeadHeight;
                mHeadLayout.requestLayout();
            } else if (mChildView != null) {
                createAnimatorTranslationY(mChildView, mHeadHeight, mHeadLayout, ANIM_DURATION);
            }
        }
    }

    public void updateListener() {
        isRefreshing = true;
        if (materialHeadView != null) {
            materialHeadView.onRefreshing(MaterialRefreshLayout.this);
        }

        if (refreshListener != null) {
            refreshListener.onRefresh(MaterialRefreshLayout.this);
        } else if (swipeRefreshListener != null) {
            swipeRefreshListener.onRefresh();
        }
    }

    public void setProgressColors(int[] colors) {
        this.colorSchemeColors = colors;
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
    }

    public void setShowProgressBg(boolean showProgressBg) {
        this.showProgressBg = showProgressBg;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
        if (materialHeadView != null)
            materialHeadView.setWaveColor(isShowWave ? waveColor : Color.WHITE);
    }

    public void setHeadBgColor(int color) {
        this.headBgColor = color;
        if (materialHeadView != null)
            materialHeadView.setBackgroundColor(headBgColor);
    }

    public void setWaveShow(boolean isShowWave) {
        this.isShowWave = isShowWave;
    }

    public void setIsOverLay(boolean isOverLay) {
        this.isOverlay = isOverLay;
    }

    public void createAnimatorTranslationY(final View v, final float h, final FrameLayout fl, long duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationY", ViewCompat.getTranslationY(v), h);
        animation.setDuration(duration);
        animation.addUpdateListener(animation1 -> {
            float height = ViewCompat.getTranslationY(v);
            fl.getLayoutParams().height = (int) height;
            fl.requestLayout();
        });
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束时强制状态设置
                fl.getLayoutParams().height = (int) h;
                fl.requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    public boolean canChildScrollDown() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                if (absListView.getChildCount() > 0) {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                } else {
                    return false;
                }

            } else {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, 1);
        }
    }

    public void setWaveHigher() {
        headHeight = hIGHER_HEAD_HEIGHT;
        waveHeight = HIGHER_WAVE_HEIGHT;
        MaterialWaveView.defaultHeadHeight = hIGHER_HEAD_HEIGHT;
        MaterialWaveView.defaultWaveHeight = HIGHER_WAVE_HEIGHT;
    }

    public void finishRefreshing() {
        ObjectAnimator animation;
        if (!isOverlay && mHeadLayout != null) {
            if (mChildView != null) {
                animation = ObjectAnimator.ofFloat(mChildView, "translationY", ViewCompat.getTranslationY(mChildView), 0);
                animation.addUpdateListener(animation1 -> {
                    float height = ViewCompat.getTranslationY(mChildView);
                    mHeadLayout.getLayoutParams().height = (int) height;
                    mHeadLayout.requestLayout();
                });
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //动画结束时强制状态设置
                        mHeadLayout.setTranslationY(0);
                        mHeadLayout.getLayoutParams().height = 0;
                        mHeadLayout.requestLayout();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            } else {
                return;
            }
        } else {
            animation = ObjectAnimator.ofFloat(mHeadLayout, "translationY", 0, -mHeadLayout.getLayoutParams().height);
            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //动画结束时强制状态设置
                    mHeadLayout.setTranslationY(0);
                    mHeadLayout.getLayoutParams().height = 0;
                    mHeadLayout.requestLayout();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRefreshing = false;
                if (refreshListener != null) {
                    refreshListener.onFinish();
                }
                if (materialHeadView != null) {
                    materialHeadView.onComplete(MaterialRefreshLayout.this);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.setDuration(ANIM_DURATION).start();

    }

    public void finishRefresh() {
        this.post(() -> finishRefreshing());
    }

    public void setWaveHeight(float waveHeight) {
        this.mWaveHeight = waveHeight;
    }

    public void setHeaderHeight(float headHeight) {
        this.mHeadHeight = headHeight;
    }

    public void setMaterialRefreshListener(MaterialRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((Build.VERSION.SDK_INT < 21 && mChildView instanceof AbsListView)
                || (mChildView != null && !ViewCompat.isNestedScrollingEnabled(mChildView))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    ///////////////////////////////// NestedScrollingParent ////////////////////////

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean rtn = !mReturningToStart && !isRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
//        Log.d("---> ", "MaterialRefreshLayout onStartNestedScroll:" + rtn);
        return rtn;
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;

        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            moveCancel();
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed;

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);
        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0) {
            mTotalUnconsumed += Math.abs(dy);
            moveDown(mTotalUnconsumed);
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
//        Log.d("---> ", "MaterialRefreshLayout onNestedPreScroll dy:" + dy);
//        Log.d("---> ", "MaterialRefreshLayout onNestedPreScroll mTotalUnconsumed:" + mTotalUnconsumed);
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;

            }
            moveDown(mTotalUnconsumed);
        }

//        // If a client layout is using a custom start position for the circle
//        // view, they mean to hide it again before scrolling the child view
//        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
//        // the circle so it isn't exposed if its blocking content is moved
//        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
//                && Math.abs(dy - consumed[1]) > 0) {
//            mCircleView.setVisibility(View.GONE);
//        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    ////////////////////////////// NestedScrollingChild ////////////////////////

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }


    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        swipeRefreshListener = listener;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (isRefreshing == refreshing)
            return;
        if (refreshing) {
            autoRefresh();
        } else {
            finishRefresh();
        }
    }

}
