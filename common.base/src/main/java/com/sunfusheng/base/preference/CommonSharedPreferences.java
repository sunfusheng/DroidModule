package com.sunfusheng.base.preference;

import com.sunfusheng.utils.ApplicationContextHolder;

/**
 * Created by sunfusheng on 2017/5/25.
 */
public class CommonSharedPreferences extends BaseSharedPreferences {

    private static final int VERSION = 1;
    private static final String PREF_COMMON_MODULE = "pref_common_module";
    private static CommonSharedPreferences mInstance = new CommonSharedPreferences();

    public static CommonSharedPreferences getInstance() {
        return mInstance;
    }

    private CommonSharedPreferences() {
        super(ApplicationContextHolder.getContext(), PREF_COMMON_MODULE, VERSION);
    }

}
