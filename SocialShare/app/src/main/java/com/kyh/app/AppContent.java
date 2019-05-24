package com.kyh.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kyh.app.config.SystemConfigSp;
import com.kyh.app.ui.MainActivity;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

public class AppContent extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //s : appkey   s2 pushSecret
        UMConfigure.init(this, "5badbec0b465f5519b0004ff"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "13b37745c5b0c56b15a13ea08063a229");

        PlatformConfig.setWeixin("wxcb65aa46d04ad49b", "e44bf1d172e7b6a4638e8ecc63bb80e1");
        PlatformConfig.setSinaWeibo("102135063", "47a31952aed883dc13cdccaf9b30df0d", "http://sns.whalecloud.com");
        PlatformConfig.setQQZone("101504727", "2e7928e5d1e2974eb06a35fa408e0950");

        //小米通道
        MiPushRegistar.register(this, "2882303761517876578", "5531787638578");
        //华为通道
        HuaWeiRegister.register(this);
        //魅族通道
        MeizuRegister.register(this, "1002184", "c234922886d34b058f937638856ae9f7");

        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setResourcePackageName("com.kyh.app");
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER); //声音
        mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);//呼吸灯
        mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SERVER);//振动

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                String content = msg.custom;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url", content);
                startActivity(intent);

            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.d("AppContent", "onSuccess: " + deviceToken);
                SystemConfigSp.instance().init(getApplicationContext());
                SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.deviceToken, deviceToken);

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.d("AppContent", "onFailure: " + s + "---s1:" + s1);
            }
        });
    }
}
