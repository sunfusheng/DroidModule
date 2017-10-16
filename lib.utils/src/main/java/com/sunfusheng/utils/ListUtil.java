package com.sunfusheng.utils;

import java.util.List;

/**
 * Created by sunfusheng on 2017/5/26.
 */
public class ListUtil {

    public static <E> boolean isEmpty(List<E> list) {
        return list == null || list.size() == 0;
    }
}
