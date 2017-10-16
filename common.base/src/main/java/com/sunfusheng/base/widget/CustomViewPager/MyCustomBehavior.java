package com.sunfusheng.base.widget.CustomViewPager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.sunfusheng.base.R;

/**
 * Created by sunfusheng on 2017/6/2.
 */
public class MyCustomBehavior extends CoordinatorLayout.Behavior<View>{

    private int id;
    public MyCustomBehavior(Context context, AttributeSet attrs) {
        super(context,attrs);
        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.MyCustomStyle);
        id = typedArray.getResourceId(R.styleable.MyCustomStyle_anchor_id, -1);
        typedArray.recycle();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

//        return dependency instanceof AppBarLayout;
        return dependency.getId() == id;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        child.setTranslationY(-dependency.getTop());
        return true;
    }
}
