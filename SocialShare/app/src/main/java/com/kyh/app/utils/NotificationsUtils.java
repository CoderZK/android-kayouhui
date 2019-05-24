package com.kyh.app.utils;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kyh.app.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationsUtils {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context mContext) {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 24) {
            return mNotificationManager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps =
                    (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            String pkg = mContext.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                        == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                    | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }


    public static void showInNotificationBar(Context ctx, String title, String ticker, int notificationId, Intent intent) {
        NotificationManager notifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifyMgr == null) {
            return;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setContentTitle(title);
        builder.setContentText(ticker);
        builder.setSmallIcon(R.mipmap.kyh_app_icon);
        builder.setTicker(ticker);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);

        // this is the content near the right bottom side
        // builder.setContentInfo("content info");

        long[] vibrate = {0, 200, 250, 200};
        builder.setVibrate(vibrate);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        Log.i("PushActivity#onMessage", "666");
        // if MessageActivity is in the background, the system would bring it to
        // the front
        PendingIntent pendingIntent = PendingIntent.getActivity(
                ctx, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notifyMgr.notify(notificationId, notification);
        Log.i("PushActivity#onMessage", "7777");
    }
}
