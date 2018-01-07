package com.sunfusheng.utils;

import java.io.Closeable;

/**
 * @author sunfusheng on 2018/1/7.
 */
public class IoUtil {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
