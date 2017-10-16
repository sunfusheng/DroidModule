package com.sunfusheng.base.widget.CollectionView;


import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CollectionViewFactory {

    @IntDef({Type.RECYCLER_VIEW, Type.LIST_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int RECYCLER_VIEW = 0;
        int LIST_VIEW = 1;
    }

    public static ICollectionView getCollectionView(Context context, @Type int type) {
        if (type == Type.RECYCLER_VIEW) {
            return new RecyclerViewCollection(context);
        } else if (type == Type.LIST_VIEW) {
            return new ListViewCollection(context);
        } else {
            throw new IllegalArgumentException("Invalid collection view type!");
        }
    }
}
