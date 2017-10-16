package com.sunfusheng.base.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.ItemNotFoundException;
import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener;
import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.TrayStorage;

import java.util.Collection;

import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("unused")
public class BaseSharedPreferences implements OnTrayPreferenceChangeListener {

    private static final int MAX_LRU_CACHE_SIZE = 1024 * 1024;

    private TrayPreferences trayPreferences;
    private LruCache<String, String> preferencesCache = new LruCache<String, String>(MAX_LRU_CACHE_SIZE) {
        @Override
        protected int sizeOf(String key, String value) {
            int size = 0;

            if (!TextUtils.isEmpty(key)) {
                size += key.getBytes().length;
            }

            if (!TextUtils.isEmpty(value)) {
                size += value.getBytes().length;
            }

            return size;
        }
    };

    private OnTrayPreferenceChangeListener preferenceChangeListener;

    public BaseSharedPreferences(@NonNull Context context, @NonNull String module, int version, TrayStorage.Type type) {
        try {
            trayPreferences = new TrayPreferences(context, module, version, type);
            trayPreferences.registerOnTrayPreferenceChangeListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public BaseSharedPreferences(@NonNull Context context, @NonNull String module, int version) {
        try {
            trayPreferences = new TrayPreferences(context, module, version);
            trayPreferences.registerOnTrayPreferenceChangeListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void wipe() {
        try {
            trayPreferences.wipe();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        preferencesCache.evictAll();
    }

    public void clear() {
        try {
            trayPreferences.clear();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        preferencesCache.evictAll();
    }

    public void put(@NonNull String key, String value) {
        Schedulers.io().createWorker().schedule(() -> {
            try {
                trayPreferences.put(key, value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        preferencesCache.put(key, value);
    }

    public void put(@NonNull String key, int value) {
        Schedulers.io().createWorker().schedule(() -> {
            try {
                trayPreferences.put(key, value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        preferencesCache.put(key, String.valueOf(value));
    }

    public void put(@NonNull String key, float value) {
        Schedulers.io().createWorker().schedule(() -> {
            try {
                trayPreferences.put(key, value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        preferencesCache.put(key, String.valueOf(value));
    }

    public void put(@NonNull String key, long value) {
        Schedulers.io().createWorker().schedule(() -> {
            try {
                trayPreferences.put(key, value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        preferencesCache.put(key, String.valueOf(value));
    }

    public void put(@NonNull String key, boolean value) {
        Schedulers.io().createWorker().schedule(() -> {
            try {
                trayPreferences.put(key, value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        preferencesCache.put(key, String.valueOf(value));
    }

    public void remove(@NonNull String key) {
        Schedulers.io().createWorker().schedule(() -> {
            try {
                trayPreferences.remove(key);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        preferencesCache.remove(key);
    }

    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Boolean.parseBoolean(cachedVal);
        }

        Boolean value;
        try {
            value = trayPreferences.getBoolean(key, defaultValue);
        } catch (Throwable e) {
            e.printStackTrace();
            value = defaultValue;
        }
        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public boolean getBoolean(@NonNull String key) throws ItemNotFoundException {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Boolean.parseBoolean(cachedVal);
        }

        Boolean value;
        try {
            value = trayPreferences.getBoolean(key);
        } catch (Throwable e) {
            throw new ItemNotFoundException();
        }

        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public float getFloat(@NonNull String key, float defaultValue) {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Float.parseFloat(cachedVal);
        }

        Float value;
        try {
            value = trayPreferences.getFloat(key, defaultValue);
        } catch (Throwable e) {
            e.printStackTrace();
            value = defaultValue;
        }

        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public float getFloat(@NonNull String key) throws ItemNotFoundException {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Float.parseFloat(cachedVal);
        }

        Float value;
        try {
            value = trayPreferences.getFloat(key);
        } catch (Throwable e) {
            throw new ItemNotFoundException();
        }

        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public int getInt(@NonNull String key, int defaultValue) {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Integer.parseInt(cachedVal);
        }

        Integer value;
        try {
            value = trayPreferences.getInt(key, defaultValue);
        } catch (Throwable e) {
            e.printStackTrace();
            value = defaultValue;
        }

        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public int getInt(@NonNull String key) throws ItemNotFoundException {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Integer.parseInt(cachedVal);
        }

        Integer value;
        try {
            value = trayPreferences.getInt(key);
        } catch (Throwable e) {
            throw new ItemNotFoundException();
        }

        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public long getLong(@NonNull String key, long defaultValue) {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Long.parseLong(cachedVal);
        }

        Long value;
        try {
            value = trayPreferences.getLong(key, defaultValue);
        } catch (Throwable e) {
            e.printStackTrace();
            value = defaultValue;
        }

        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public long getLong(@NonNull String key) throws ItemNotFoundException {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return Long.parseLong(cachedVal);
        }

        Long value;
        try {
            value = trayPreferences.getLong(key);
        } catch (Throwable e) {
            throw new ItemNotFoundException();
        }

        preferencesCache.put(key, String.valueOf(value));
        return value;
    }

    public String getString(@NonNull String key) throws ItemNotFoundException {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return cachedVal;
        }

        String value;
        try {
            value = trayPreferences.getString(key);
        } catch (Throwable e) {
            throw new ItemNotFoundException();
        }

        if (value != null) {
            preferencesCache.put(key, value);
        }
        return value;
    }

    @Nullable
    public String getString(@NonNull String key, String defaultValue) {
        String cachedVal = preferencesCache.get(key);
        if (cachedVal != null) {
            return cachedVal;
        }

        String value;
        try {
            value = trayPreferences.getString(key, defaultValue);
        } catch (Throwable e) {
            e.printStackTrace();
            value = defaultValue;
        }

        if (value != null) {
            preferencesCache.put(key, value);
        }
        return value;
    }

    public void onTrayPreferenceChanged(Collection<TrayItem> items) {
        for (TrayItem trayItem : items) {
            if (trayItem.module() != null && trayItem.module().equals(trayPreferences.getName())) {
                String value = trayItem.value();
                String cachedVal = preferencesCache.get(trayItem.key());
                if (cachedVal != null) {
                    if (value == null) {
                        preferencesCache.remove(trayItem.key());
                    } else if (!value.equals(cachedVal)) {
                        preferencesCache.put(trayItem.key(), value);
                    }
                }
            }
        }

        if (this.preferenceChangeListener != null) {
            this.preferenceChangeListener.onTrayPreferenceChanged(items);
        }
    }

    public void registerOnTrayPreferenceChangeListener(@NonNull OnTrayPreferenceChangeListener listener) {
        this.preferenceChangeListener = listener;
    }

    public void unregisterOnTrayPreferenceChangeListener(@NonNull OnTrayPreferenceChangeListener listener) {
        this.preferenceChangeListener = null;
    }
}
