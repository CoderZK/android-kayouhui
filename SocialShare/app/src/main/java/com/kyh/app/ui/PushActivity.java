package com.kyh.app.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kyh.app.R;
import com.kyh.app.config.SystemConfigSp;
import com.umeng.message.UmengNotifyClickActivity;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PushActivity extends UmengNotifyClickActivity {
    private static String TAG = PushActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipush); //这里设置不同的页面，为了区分是友盟推送进来的，还是通道推送进来的
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        Log.i("PushActivity#onMessage","111");
        SystemConfigSp.instance().init(getApplicationContext());
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i("PushActivity#onMessage","2222");
        try {
            int noId = SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.notifycationId);
            AtomicInteger atomicInteger = new AtomicInteger(noId);
            int notificationId = atomicInteger.incrementAndGet();
            UMessage var3 = new UMessage(new JSONObject(body));
            String title = var3.title;
            String ticker = var3.ticker;
            String content = var3.custom;
            Intent intent1;
            Log.i("PushActivity#onMessage","333");
            if(isAppRunning(this, "com.kyh.app")) {
                Log.i("PushActivity#onMessage","444");
                intent1 = new Intent(this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("url", content);
            } else {
                Log.i("PushActivity#onMessage","555");
                intent1 = getPackageManager().
                        getLaunchIntentForPackage(PushActivity.this.getPackageName());
//                intent1.addCategory(Intent.CATEGORY_LAUNCHER);
                intent1.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent1.putExtra("url", content);
            }
            startActivity(intent1);
//            Log.i("PushActivity#onMessage","444");
//            NotificationsUtils.showInNotificationBar(this, title, ticker, notificationId, intent1);
//            Log.i("PushActivity#onMessage", "45454545");
//            SystemConfigSp.instance().setIntConfig(SystemConfigSp.SysCfgDimension.notifycationId, notificationId);
//            Log.i("PushActivity#onMessage", "5555");
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, body);
    }


    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
