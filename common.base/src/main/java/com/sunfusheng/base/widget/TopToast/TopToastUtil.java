package com.sunfusheng.base.widget.TopToast;

import android.view.ViewGroup;
import android.widget.TextView;

import com.sunfusheng.base.R;

public class TopToastUtil {

    public static synchronized TopToast showTopToast(ViewGroup container, String content) {
        return showTopToast(container, content, 0xff007AFF, 0x20007AFF);
    }

    public static synchronized TopToast showTopToast(ViewGroup container, String content, int textColor, int backgroundColor) {
        TopToast toast = TopToast.make(container, content, TopToast.LENGTH_SHORT);
        ((TextView) toast.getView().findViewById(R.id.toast_text)).setTextColor(textColor);
        toast.getView().setBackgroundColor(backgroundColor);
        toast.show();
        return toast;
    }
}
