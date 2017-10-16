package com.sunfusheng.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

@SuppressWarnings("unused")
public class DisplayUtil {

    public static final int STANDARD_SCREEN_WIDTH = 720;
    public static final int STANDARD_SCREEN_HEIGHT = 1280;
    public static final int STANDARD_SCREEN_DENSITY = 320;

    private static WindowManager windowManager;

    private static WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) ApplicationContextHolder.getContext().getSystemService(Context.WINDOW_SERVICE);
        }

        return windowManager;
    }

    public static int getScreenWidth() {
        try {
            Display defaultDisplay = getWindowManager().getDefaultDisplay();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            if (displayMetrics.widthPixels > 0) {
                return displayMetrics.widthPixels;
            }

            Point screenSize = new Point();
            defaultDisplay.getSize(screenSize);
            if (screenSize.x > 0) {
                return screenSize.x;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return STANDARD_SCREEN_WIDTH;
    }

    public static int getScreenHeight() {
        try {
            Display defaultDisplay = getWindowManager().getDefaultDisplay();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            if (displayMetrics.heightPixels > 0) {
                return displayMetrics.heightPixels;
            }

            Point screenSize = new Point();
            defaultDisplay.getSize(screenSize);
            if (screenSize.y > 0) {
                return screenSize.y;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return STANDARD_SCREEN_HEIGHT;
    }

    public static float getScreenDensity() {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.density;
        } catch (Throwable e) {
            e.printStackTrace();
            return 1.0f;
        }
    }

    public static int getDensityDpi() {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.densityDpi;
        } catch (Throwable e) {
            e.printStackTrace();
            return STANDARD_SCREEN_DENSITY;
        }
    }

    public static float dip2pxFloat(float dp) {
        try {
            DisplayMetrics metrics = ApplicationContextHolder.getContext().getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public static int dip2px(float dp) {
        return (int) (dip2pxFloat(dp) + 0.5f);
    }

    public static float sp2pxFloat(float sp) {
        try {
            DisplayMetrics metrics = ApplicationContextHolder.getContext().getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public static int sp2px(float sp) {
        return (int) (sp2pxFloat(sp) + 0.5f);
    }

    private static int px2dipWithStand(int px) {
        return (px * 160) / STANDARD_SCREEN_DENSITY;
    }

    public static int pxStand2Local(int px) {
        return dip2px(px2dipWithStand(px));
    }
}
