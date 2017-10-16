package com.sunfusheng.base.widget.CollectionView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunfusheng.base.R;
import com.sunfusheng.base.R2;
import com.sunfusheng.infostream.anotations.FooterStatus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadMoreFooterView extends LinearLayout {

    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_tip)
    TextView tvTip;

    private FooterClickListener footerListener;

    @FooterStatus
    private int currentStatus;

    private String fullText;

    public LoadMoreFooterView(Context context) {
        super(context);
        initView(context);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_load_more_footer_view, this, true);
        ButterKnife.bind(this, view);

        tvTip.setOnClickListener(v -> loading());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setStatus(FooterStatus.IDLE);
    }

    public void loading() {
        if (footerListener == null) {
            setStatus(FooterStatus.GONE);
            return;
        }
        if (currentStatus == FooterStatus.FULL) {
            if (footerListener.onFullRefresh()) {
                setStatus(FooterStatus.LOADING);
            }
        } else if (currentStatus == FooterStatus.IDLE) {
            if (footerListener.onLoadMore()) {
                setStatus(FooterStatus.LOADING);
            }
        } else if (currentStatus == FooterStatus.ERROR) {
            if (footerListener.onErrorClick()) {
                setStatus(FooterStatus.LOADING);
            }
        }
    }

    public void setFooterListener(FooterClickListener footerListener) {
        this.footerListener = footerListener;
    }

    public void setStatus(@FooterStatus int status) {
        if (status == currentStatus) {
            return;
        }
        currentStatus = status;
        switch (status) {
            case FooterStatus.IDLE:
                progressBar.setVisibility(GONE);
                tvTip.setVisibility(VISIBLE);
                tvTip.setText(R.string.common_load_more);
                break;
            case FooterStatus.LOADING:
                progressBar.setVisibility(VISIBLE);
                tvTip.setVisibility(VISIBLE);
                tvTip.setText(R.string.common_loading);
                break;
            case FooterStatus.FULL:
                progressBar.setVisibility(GONE);
                tvTip.setVisibility(VISIBLE);
                tvTip.setText(TextUtils.isEmpty(fullText) ? getContext().getString(R.string.full_data) : fullText);
                break;
            case FooterStatus.ERROR:
                progressBar.setVisibility(GONE);
                tvTip.setVisibility(VISIBLE);
                tvTip.setText(R.string.common_load_error);
                break;
            case FooterStatus.GONE:
                progressBar.setVisibility(GONE);
                tvTip.setVisibility(GONE);
                break;
        }
    }

    @FooterStatus
    public int getStatus() {
        return currentStatus;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public interface FooterClickListener {
        boolean onFullRefresh();

        boolean onLoadMore();

        boolean onErrorClick();
    }
}
