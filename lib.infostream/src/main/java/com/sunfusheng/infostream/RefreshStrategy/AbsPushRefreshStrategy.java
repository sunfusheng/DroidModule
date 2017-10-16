package com.sunfusheng.infostream.RefreshStrategy;

import android.support.annotation.IntDef;

import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsPushRefreshStrategy extends AbsRefreshStrategy {

    @IntDef({NewItemInsertPosition.InsertAtTop, NewItemInsertPosition.InsertAtBottom, NewItemInsertPosition.CustomPosition})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NewItemInsertPosition {
        int InsertAtTop = 1;
        int InsertAtBottom = 2;
        int CustomPosition = 3;
    }

    // 如果positionOfNewItem为CustomPosition，将会调用onNewItemArrived来决定接受到的数据的插入位置
    public List<ViewObject> onNewItemArrived(List<ViewObject> currentItems, List<ViewObject> newItems) {
        return new ArrayList<>();
    }

    public
    @NewItemInsertPosition
    int positionOfNewItem() {
        return NewItemInsertPosition.InsertAtTop;
    }
}
