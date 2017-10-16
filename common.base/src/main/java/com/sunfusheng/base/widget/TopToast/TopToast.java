/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sunfusheng.base.widget.TopToast;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunfusheng.base.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * refrom from android.support.design.widget.TopToast
 */
public final class TopToast {

    static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    /**
     * Callback class for {@link TopToast} instances.
     *
     * @see TopToast#setCallback(Callback)
     */
    public static abstract class Callback {
        /**
         * Indicates that the TopToast was dismissed via a swipe.
         */
        public static final int DISMISS_EVENT_SWIPE = 0;
        /**
         * Indicates that the TopToast was dismissed via an action click.
         */
        public static final int DISMISS_EVENT_ACTION = 1;
        /**
         * Indicates that the TopToast was dismissed via a timeout.
         */
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        /**
         * Indicates that the TopToast was dismissed via a call to {@link #dismiss()}.
         */
        public static final int DISMISS_EVENT_MANUAL = 3;
        /**
         * Indicates that the TopToast was dismissed from a new TopToast being shown.
         */
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        /**
         * @hide
         */
        @IntDef({DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT,
                DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface DismissEvent {
        }

        /**
         * Called when the given {@link TopToast} has been dismissed, either through a time-out,
         * having been manually dismissed, or an action being clicked.
         *
         * @param TopToast The TopToast which has been dismissed.
         * @param event    The event which caused the dismissal. One of either:
         *                 {@link #DISMISS_EVENT_SWIPE}, {@link #DISMISS_EVENT_ACTION},
         *                 {@link #DISMISS_EVENT_TIMEOUT}, {@link #DISMISS_EVENT_MANUAL} or
         *                 {@link #DISMISS_EVENT_CONSECUTIVE}.
         * @see TopToast#dismiss()
         */
        public void onDismissed(TopToast TopToast, @DismissEvent int event) {
            // empty
        }

        /**
         * Called when the given {@link TopToast} is visible.
         *
         * @param TopToast The TopToast which is now visible.
         * @see TopToast#show()
         */
        public void onShown(TopToast TopToast) {
            // empty
        }
    }

    /**
     * @hide
     */
    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    /**
     * Show the TopToast indefinitely. This means that the TopToast will be displayed from the time
     * that is {@link #show() shown} until either it is dismissed, or another TopToast is shown.
     *
     * @see #setDuration
     */
    public static final int LENGTH_INDEFINITE = -2;

    /**
     * Show the TopToast for a short period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = -1;

    /**
     * Show the TopToast for a long period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_LONG = 0;

    private static final int ANIMATION_DURATION = 250;
    private static final int ANIMATION_FADE_DURATION = 180;

    private static final Handler sHandler;
    private static final int MSG_SHOW = 0;
    private static final int MSG_DISMISS = 1;

    static {
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW:
                        ((TopToast) message.obj).showView();
                        return true;
                    case MSG_DISMISS:
                        ((TopToast) message.obj).hideView(message.arg1);
                        return true;
                }
                return false;
            }
        });
    }

    private final ViewGroup mTargetParent;
    private final Context mContext;
    private final FrameLayout mView;
    private int mDuration;
    private Callback mCallback;

    private TopToast(ViewGroup parent) {
        mTargetParent = parent;
        mContext = parent.getContext();

        ThemeUtils.checkAppCompatTheme(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = (FrameLayout) inflater.inflate(
                R.layout.top_toast_layout, mTargetParent, false);
    }


    @NonNull
    public static TopToast make(@NonNull View view, @NonNull CharSequence text,
                                @Duration int duration) {
        TopToast TopToast = new TopToast(findSuitableParent(view));
        TopToast.setText(text);
        TopToast.setDuration(duration);
        return TopToast;
    }


    @NonNull
    public static TopToast make(@NonNull View view, @StringRes int resId, @Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                    break;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    /**
     * Update the text in this {@link TopToast}.
     *
     * @param message The new text for the Toast.
     */
    @NonNull
    public TopToast setText(@NonNull CharSequence message) {
        final TextView tv = ((TopToastLayout) mView.findViewById(R.id.top_toast)).getMessageView();
        tv.setText(message);
        return this;
    }

    /**
     * Update the text in this {@link TopToast}.
     *
     * @param resId The new text for the Toast.
     */
    @NonNull
    public TopToast setText(@StringRes int resId) {
        return setText(mContext.getText(resId));
    }

    /**
     * Set how long to show the view for.
     *
     * @param duration either be one of the predefined lengths:
     *                 {@link #LENGTH_SHORT}, {@link #LENGTH_LONG}, or a custom duration
     *                 in milliseconds.
     */
    @NonNull
    public TopToast setDuration(@Duration int duration) {
        mDuration = duration;
        return this;
    }

    /**
     * Return the duration.
     *
     * @see #setDuration
     */
    @Duration
    public int getDuration() {
        return mDuration;
    }

    /**
     * Returns the {@link TopToast}'s view.
     */
    @NonNull
    public View getView() {
        return mView;
    }

    /**
     * Show the {@link TopToast}.
     */
    public void show() {
        TopToastManager.getInstance().show(mDuration, mManagerCallback);
    }

    /**
     * Dismiss the {@link TopToast}.
     */
    public void dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL);
    }

    private void dispatchDismiss(@Callback.DismissEvent int event) {
        TopToastManager.getInstance().dismiss(mManagerCallback, event);
    }

    /**
     * Set a callback to be called when this the visibility of this {@link TopToast} changes.
     */
    @NonNull
    public TopToast setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    /**
     * Return whether this {@link TopToast} is currently being shown.
     */
    public boolean isShown() {
        return TopToastManager.getInstance().isCurrent(mManagerCallback);
    }

    /**
     * Returns whether this {@link TopToast} is currently being shown, or is queued to be
     * shown next.
     */
    public boolean isShownOrQueued() {
        return TopToastManager.getInstance().isCurrentOrNext(mManagerCallback);
    }

    private final TopToastManager.Callback mManagerCallback = new TopToastManager.Callback() {
        @Override
        public void show() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, TopToast.this));
        }

        @Override
        public void dismiss(int event) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, TopToast.this));
        }
    };

    final void showView() {
        if (mView.getParent() == null) {
            mTargetParent.addView(mView);
        }

        ((TopToastLayout) mView.findViewById(R.id.top_toast)).setOnAttachStateChangeListener(new TopToastLayout.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isShownOrQueued()) {
                    // If we haven't already been dismissed then this event is coming from a
                    // non-user initiated action. Hence we need to make sure that we callback
                    // and keep our state up to date. We need to post the call since removeView()
                    // will call through to onDetachedFromWindow and thus overflow.
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onViewHidden(Callback.DISMISS_EVENT_MANUAL);
                        }
                    });
                }
            }
        });

        if (ViewCompat.isLaidOut(mView)) {
            // If the view is already laid out, animate it now
            animateViewIn();
        } else {
            // Otherwise, add one of our layout change listeners and animate it in when laid out
            ((TopToastLayout) mView.findViewById(R.id.top_toast)).setOnLayoutChangeListener(new TopToastLayout.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom) {
                    animateViewIn();
                    ((TopToastLayout) mView.findViewById(R.id.top_toast)).setOnLayoutChangeListener(null);
                }
            });
        }
    }

    private void animateViewIn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.setTranslationY(mView, -mView.getHeight());
            ViewCompat.animate(mView)
                    .translationY(0f)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(ANIMATION_DURATION)
                    .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(View view) {
                            mTargetParent.getChildAt(0).setTranslationY(
                                    mView.getHeight() - mView.getTranslationY());
                        }
                    })
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(View view) {
                            ((TopToastLayout) mView.findViewById(R.id.top_toast)).animateChildrenIn(ANIMATION_DURATION - ANIMATION_FADE_DURATION,
                                    ANIMATION_FADE_DURATION);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            if (mCallback != null) {
                                mCallback.onShown(TopToast.this);
                            }
                            TopToastManager.getInstance().onShown(mManagerCallback);
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(mView.getContext(),
                    R.anim.design_snackbar_in);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCallback != null) {
                        mCallback.onShown(TopToast.this);
                    }
                    TopToastManager.getInstance().onShown(mManagerCallback);
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mView.startAnimation(anim);
        }
    }

    private void animateViewOut(final int event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(mView)
                    .translationY(-mView.getHeight())
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(ANIMATION_DURATION)
                    .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(View view) {
                            mTargetParent.getChildAt(0).setTranslationY(
                                    mView.getHeight() + mView.getTranslationY());
                        }
                    })
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(View view) {
                            ((TopToastLayout) mView.findViewById(R.id.top_toast)).animateChildrenOut(0, ANIMATION_FADE_DURATION);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            onViewHidden(event);
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(mView.getContext(), R.anim.design_snackbar_out);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    onViewHidden(event);
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mView.startAnimation(anim);
        }
    }

    final void hideView(int event) {
        if (mView.getVisibility() != View.VISIBLE || isBeingDragged()) {
            onViewHidden(event);
        } else {
            animateViewOut(event);
        }
    }

    private void onViewHidden(int event) {
        // First tell the TopToastManager that it has been dismissed
        TopToastManager.getInstance().onDismissed(mManagerCallback);
        // Now call the dismiss listener (if available)
        if (mCallback != null) {
            mCallback.onDismissed(this, event);
        }
        // Lastly, remove the view from the parent (if attached)
        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mView);
        }
    }

    /**
     * @return if the view is being being dragged or settled by {@link SwipeDismissBehavior}.
     */
    private boolean isBeingDragged() {
        final ViewGroup.LayoutParams lp = mView.getLayoutParams();

        if (lp instanceof CoordinatorLayout.LayoutParams) {
            final CoordinatorLayout.LayoutParams cllp = (CoordinatorLayout.LayoutParams) lp;
            final CoordinatorLayout.Behavior behavior = cllp.getBehavior();

            if (behavior instanceof SwipeDismissBehavior) {
                return ((SwipeDismissBehavior) behavior).getDragState()
                        != SwipeDismissBehavior.STATE_IDLE;
            }
        }
        return false;
    }

    /**
     * @hide
     */
    public static class TopToastLayout extends LinearLayout {
        private TextView mMessageView;

        private int mMaxWidth;

        interface OnLayoutChangeListener {
            void onLayoutChange(View view, int left, int top, int right, int bottom);
        }

        interface OnAttachStateChangeListener {
            void onViewAttachedToWindow(View v);

            void onViewDetachedFromWindow(View v);
        }

        private OnLayoutChangeListener mOnLayoutChangeListener;
        private OnAttachStateChangeListener mOnAttachStateChangeListener;

        public TopToastLayout(Context context) {
            this(context, null);
        }

        public TopToastLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
            mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
            a.recycle();

            setClickable(true);

            // Now inflate our content. We need to do this manually rather than using an <include>
            // in the layout since older versions of the Android do not inflate includes with
            // the correct Context.
            LayoutInflater.from(context).inflate(R.layout.top_toast_layout_include, this);

            ViewCompat.setAccessibilityLiveRegion(this,
                    ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
        }

        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
            mMessageView = (TextView) findViewById(R.id.toast_text);
        }

        TextView getMessageView() {
            return mMessageView;
        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            if (mMaxWidth > 0 && getMeasuredWidth() > mMaxWidth) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }

        void animateChildrenIn(int delay, int duration) {
            ViewCompat.setAlpha(mMessageView, 0f);
            ViewCompat.animate(mMessageView).alpha(1f).setDuration(duration)
                    .setStartDelay(delay).start();

        }

        void animateChildrenOut(int delay, int duration) {
            ViewCompat.setAlpha(mMessageView, 1f);
            ViewCompat.animate(mMessageView).alpha(0f).setDuration(duration)
                    .setStartDelay(delay).start();

        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (changed && mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener.onLayoutChange(this, l, t, r, b);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewAttachedToWindow(this);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
            mOnLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
            mOnAttachStateChangeListener = listener;
        }

    }

    static class ThemeUtils {

        private static final int[] APPCOMPAT_CHECK_ATTRS = {R.attr.colorPrimary};

        static void checkAppCompatTheme(Context context) {
            TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
            final boolean failed = !a.hasValue(0);
            if (a != null) {
                a.recycle();
            }
            if (failed) {
                throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                        + "(or descendant) with the design library.");
            }
        }
    }

}
