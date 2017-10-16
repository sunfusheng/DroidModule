package com.sunfusheng.base.widget.CustomViewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PagerDotIndex extends HorizontalScrollView {

    private final DisplayMetrics dm;
    private LinearLayout tabsContainer;
    private int tabCount;
    private int defaultDotResId = 0;
    private int selectedDotResId = 0;
    private int selectedPosition = 0;
    private int dotPadding = 2;

    public PagerDotIndex(Context context) {
        this(context, null);
    }

    public PagerDotIndex(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerDotIndex(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dm = getResources().getDisplayMetrics();
        dotPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dotPadding, dm);
        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(tabsContainer);
    }

    public void setViewPager(CustomViewPager pager, int count) {
        this.tabCount = count;
        selectedPosition = 0;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.removeOnPageChangeListener(pageListener);
        pager.addOnPageChangeListener(pageListener);
        notifyDataSetChanged();
    }

    private CustomViewPager.OnPageChangeListener pageListener = new CustomViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position % tabCount;
            updateTabStyles();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            if (v instanceof ImageView) {
                ImageView tab = (ImageView) v;
                tab.setImageResource(defaultDotResId);
                if (i == selectedPosition) {
                    tab.setImageResource(selectedDotResId);
                }
            }
        }
    }

    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();
        for (int i = 0; i < tabCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setPadding(dotPadding, 0, dotPadding, 0);
            tabsContainer.addView(imageView, i);
        }
        updateTabStyles();
    }

    public void setSelectedDotResId(int selectedDotResId) {
        this.selectedDotResId = selectedDotResId;
        updateTabStyles();
    }

    public void setDefaultDotResId(int defaultDotResId) {
        this.defaultDotResId = defaultDotResId;
        updateTabStyles();
    }

    public void setDotPadding(int padding) {
        this.dotPadding = padding;
        dotPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dotPadding, dm);
        updateTabStyles();
    }

}
