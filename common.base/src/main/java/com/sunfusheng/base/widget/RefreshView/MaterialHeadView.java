package com.sunfusheng.base.widget.RefreshView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sunfusheng.base.R;

public class MaterialHeadView extends FrameLayout implements MaterialHeadListener {

    ProgressImage progressImage;
    TextView progressText;
    MaterialWaveView materialWaveView;
    private int waveColor;

    private Integer[] animRes = {
            R.drawable.head_image_0,
            R.drawable.head_image_1,
            R.drawable.head_image_2,
            R.drawable.head_image_3,
            R.drawable.head_image_4,
            R.drawable.head_image_5,
            R.drawable.head_image_6,
            R.drawable.head_image_7,
            R.drawable.head_image_8,
            R.drawable.head_image_9,
            R.drawable.head_image_10,
            R.drawable.head_image_11,
            R.drawable.head_image_12,
            R.drawable.head_image_13,
            R.drawable.head_image_14,
            R.drawable.head_image_15,
            R.drawable.head_image_16,
            R.drawable.head_image_17,
            R.drawable.head_image_18,
            R.drawable.head_image_19,
            R.drawable.head_image_20,
            R.drawable.head_image_21,
            R.drawable.head_image_22,
            R.drawable.head_image_23,
            R.drawable.head_image_24,
            R.drawable.head_image_25,
            R.drawable.head_image_26,
            R.drawable.head_image_27,
            R.drawable.head_image_28,
            R.drawable.head_image_29,
            R.drawable.head_image_30,
            R.drawable.head_image_31,
            R.drawable.head_image_32,
            R.drawable.head_image_33,
            R.drawable.head_image_34,
            R.drawable.head_image_35,
            R.drawable.head_image_36,
            R.drawable.head_image_37,
            R.drawable.head_image_38,
            R.drawable.head_image_39,
            R.drawable.head_image_40,
            R.drawable.head_image_41,
            R.drawable.head_image_42,
            R.drawable.head_image_43,
            R.drawable.head_image_44,
            R.drawable.head_image_45,
            R.drawable.head_image_46,
            R.drawable.head_image_47,
            R.drawable.head_image_48,
            R.drawable.head_image_49,
            R.drawable.head_image_50,
            R.drawable.head_image_51,
            R.drawable.head_image_52,
            R.drawable.head_image_53,
            R.drawable.head_image_54,
            R.drawable.head_image_55,
    };

    private boolean isShowText = true;

    public MaterialHeadView(Context context) {
        this(context, null);
    }

    public MaterialHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialHeadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setProgressImage(Integer[] source) {
        animRes = source;
    }

    public void showHintText(boolean showText) {
        isShowText = showText;
    }

    protected void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode()) return;
        setClipToPadding(false);
        setWillNotDraw(false);
    }

    public int getWaveColor() {
        return waveColor;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
        if (null != materialWaveView) {
            materialWaveView.setColor(this.waveColor);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inflate(getContext(), R.layout.pull_refresh_layout, this);
        progressImage = (ProgressImage) findViewById(R.id.progress_img);
        progressText = (TextView) findViewById(R.id.progress_text);
        materialWaveView = (MaterialWaveView) findViewById(R.id.wave_view);
        progressImage.initWithRes(animRes);
        materialWaveView.setColor(waveColor);
        progressText.setVisibility(isShowText ? VISIBLE : GONE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onComplete(MaterialRefreshLayout materialRefreshLayout) {
        if (materialWaveView != null) {
            materialWaveView.onComplete(materialRefreshLayout);
        }
        if (progressImage != null) {
            progressImage.onComplete(materialRefreshLayout);
        }

    }

    @Override
    public void onBegin(MaterialRefreshLayout materialRefreshLayout) {
        if (materialWaveView != null) {
            materialWaveView.onBegin(materialRefreshLayout);
        }
        if (progressImage != null) {
            progressImage.onBegin(materialRefreshLayout);
        }
        if (progressText != null) {
            progressText.setText("下拉加载");
        }
    }

    @Override
    public void onPull(MaterialRefreshLayout materialRefreshLayout, float fraction) {
        if (materialWaveView != null) {
            materialWaveView.onPull(materialRefreshLayout, fraction);
        }
        if (fraction < 1) {
            progressImage.updateProgress(fraction / 1);
        }
    }

    @Override
    public void onRelease(MaterialRefreshLayout materialRefreshLayout, float fraction) {

    }

    @Override
    public void onRefreshing(MaterialRefreshLayout materialRefreshLayout) {
        if (materialWaveView != null) {
            materialWaveView.onRefreshing(materialRefreshLayout);
        }
        if (progressImage != null) {
            progressImage.onRefreshing(materialRefreshLayout);
        }
        if (progressText != null) {
            progressText.setText("正在加载中...");
        }
    }

    @Override
    public void onCanRelease(MaterialRefreshLayout materialRefreshLayout, boolean can) {
        if (can) {
            progressText.setText("松开加载");
        } else {
            progressText.setText("下拉加载");
        }
    }

}
