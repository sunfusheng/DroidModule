package com.sunfusheng.base.widget.RefreshView;

import android.content.Context;
import android.util.AttributeSet;

import com.sunfusheng.base.R;
import com.sunfusheng.utils.DisplayUtil;

public class SwipeRefreshWrapper extends MaterialRefreshLayout {

    public SwipeRefreshWrapper(Context context) {
        super(context);
        initUI(context);
    }

    public SwipeRefreshWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    @Override
    protected MaterialHeadView createHeadView(Context context) {
        MaterialHeadView view = super.createHeadView(context);
        view.showHintText(true); // 是否显示提示文字
        return view;
    }

    private void initUI(Context context) {
        setIsOverLay(false); // 是否在列表上显示
        setWaveShow(true); // 是否显示波浪
        setWaveColor(getResources().getColor(R.color.transparent05));
        setHeadBgColor(getResources().getColor(R.color.transparent));
    }

    public void setRefreshBarOffset(int dp) {
        getChildAt(0).setY(DisplayUtil.dip2px(dp));
    }

}
