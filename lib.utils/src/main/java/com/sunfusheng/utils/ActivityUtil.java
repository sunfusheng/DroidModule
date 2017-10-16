package com.sunfusheng.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.Map;

@SuppressWarnings({"unchecked", "unused"})
public class ActivityUtil {

    public static void startActivity(Context context, Class<? extends Activity> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }

    public static void startActivityForResult(Context context, Class<? extends Activity> clazz, Bundle bundle, int requestCode) {
        if (!(context instanceof Activity)) {
            return;
        }

        Intent intent = new Intent(context, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static Activity getRunningActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getRotation(Activity activity) {
        if (activity == null) {
            return -1;
        }

        return activity.getWindowManager().getDefaultDisplay().getRotation();
    }

    public static int getOrientation(Activity activity) {
        if (activity == null) {
            return -1;
        }

        return activity.getResources().getConfiguration().orientation;
    }
}
