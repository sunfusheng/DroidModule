package com.sunfusheng.base.widget.RefreshView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.List;

public class ProgressImage extends AppCompatImageView implements Animatable, MaterialHeadListener {

    private static final int ANIMATION_DURATION = 1000;
    private Integer[] animRes;
    private List<Drawable> drawableList = new ArrayList<>();
    private Animation mAnimation;

    public void initWithRes(Integer[] res) {
        animRes = res;
        init();
    }

    public ProgressImage(Context context) {
        super(context);
    }

    public ProgressImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        drawableList.clear();
        for (int i = 1; i < animRes.length; i++) {
            try {
                drawableList.add(getResources().getDrawable(animRes[i]));
            } catch (Resources.NotFoundException e) {
                break;
            }
        }
        setupAnimators();

        //show the initial img
        if (drawableList.size() > 0) {
            setImageDrawable(drawableList.get(0));
        }
    }

    public void updateProgress(float fraction) {
        if (drawableList.size() == 0) return;
        try {
            setImageDrawable(drawableList.get((int) (drawableList.size() * fraction)));
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public boolean isRunning() {
        return !this.mAnimation.hasEnded();
    }

    @Override
    public void start() {
        mAnimation.reset();
        mAnimation.setDuration(ANIMATION_DURATION);
        startAnimation(mAnimation);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        stop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    public void stop() {
        clearAnimation();
        //reset the initial img
        if (drawableList.size() > 0) {
            setImageDrawable(drawableList.get(0));
        }
    }

    private void setupAnimators() {
        Animation animation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
            }
        };
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearOutSlowInInterpolator() {
            @Override
            public float getInterpolation(float input) {
                float val = super.getInterpolation(input);
                try {
                    setImageDrawable(drawableList.get((int) (drawableList.size() * val)));
                } catch (IndexOutOfBoundsException ignored) {
                }
                return val;
            }
        });
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAnimation = animation;
    }

    @Override
    public void onComplete(MaterialRefreshLayout materialRefreshLayout) {
        stop();
    }

    @Override
    public void onBegin(MaterialRefreshLayout materialRefreshLayout) {
    }

    @Override
    public void onPull(MaterialRefreshLayout materialRefreshLayout, float fraction) {
    }

    @Override
    public void onRelease(MaterialRefreshLayout materialRefreshLayout, float fraction) {
    }

    @Override
    public void onRefreshing(MaterialRefreshLayout materialRefreshLayout) {
        start();
    }

    @Override
    public void onCanRelease(MaterialRefreshLayout materialRefreshLayout, boolean can) {
    }
}
