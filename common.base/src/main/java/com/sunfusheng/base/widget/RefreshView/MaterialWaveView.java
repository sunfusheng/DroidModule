package com.sunfusheng.base.widget.RefreshView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.sunfusheng.utils.DisplayUtil;

public class MaterialWaveView extends View implements MaterialHeadListener {

    private int waveHeight;
    private int headHeight;
    public static int defaultWaveHeight;
    public static int defaultHeadHeight;
    private Path path;
    private Paint paint;
    private int color;

    public MaterialWaveView(Context context) {
        this(context, null, 0);
    }

    public MaterialWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    public int getHeadHeight() {
        return headHeight;
    }

    public void setHeadHeight(int headHeight) {
        this.headHeight = headHeight;
    }

    public int getWaveHeight() {
        return waveHeight;
    }

    public void setWaveHeight(int waveHeight) {
        this.waveHeight = waveHeight;
    }

    public static int getDefaultWaveHeight() {
        return defaultWaveHeight;
    }

    public static void setDefaultWaveHeight(int defaultWaveHeight) {
        MaterialWaveView.defaultWaveHeight = defaultWaveHeight;
    }

    public static int getDefaultHeadHeight() {
        return defaultHeadHeight;
    }

    public static void setDefaultHeadHeight(int defaultHeadHeight) {
        MaterialWaveView.defaultHeadHeight = defaultHeadHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        path.lineTo(0, headHeight);
        path.quadTo(getMeasuredWidth() / 2, headHeight + waveHeight, getMeasuredWidth(), headHeight);
        path.lineTo(getMeasuredWidth(), 0);
        canvas.drawPath(path, paint);
    }

    @Override
    public void onComplete(MaterialRefreshLayout br) {
        waveHeight = 0;
        ValueAnimator animator = ValueAnimator.ofInt(headHeight, 0);
        animator.setDuration(MaterialRefreshLayout.ANIM_DURATION);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
        animator.addUpdateListener(animation -> {
            headHeight = (int) animation.getAnimatedValue();
            invalidate();
        });
    }

    @Override
    public void onBegin(MaterialRefreshLayout br) {

    }

    @Override
    public void onPull(MaterialRefreshLayout br, float fraction) {
        setHeadHeight((int) (DisplayUtil.dip2px(defaultHeadHeight) * limitValue(1, fraction)));
        setWaveHeight((int) (DisplayUtil.dip2px(defaultWaveHeight) * Math.max(0, fraction - 1)));
        invalidate();
    }

    @Override
    public void onRelease(MaterialRefreshLayout br, float fraction) {

    }

    @Override
    public void onRefreshing(MaterialRefreshLayout br) {
        setHeadHeight(DisplayUtil.dip2px(defaultHeadHeight));
        ValueAnimator animator = ValueAnimator.ofInt(getWaveHeight(), 0);
        animator.addUpdateListener(animation -> {
            setWaveHeight((int) animation.getAnimatedValue());
            invalidate();
        });
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(MaterialRefreshLayout.ANIM_DURATION);
        animator.start();
    }

    @Override
    public void onCanRelease(MaterialRefreshLayout materialRefreshLayout, boolean can) {

    }

    public static float limitValue(float a, float b) {
        float valve = 0;
        final float min = Math.min(a, b);
        final float max = Math.max(a, b);
        valve = valve > min ? valve : min;
        valve = valve < max ? valve : max;
        return valve;
    }
}
