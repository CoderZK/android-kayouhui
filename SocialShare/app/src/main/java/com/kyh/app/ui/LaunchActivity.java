package com.kyh.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.kyh.app.R;
import com.umeng.message.PushAgent;

public class LaunchActivity  extends AppCompatActivity{
    private RelativeLayout splashPage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(LaunchActivity.this).onAppStart();
        setContentView(R.layout.activity_launch);
        splashPage = findViewById(R.id.splash_page);
        autoFinish();
    }

    private void autoFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                //如果启动app的Intent中带有额外的参数，表明app是从点击通知栏的动作中启动的
                //将参数取出，传递到MainActivity中
                if(getIntent().getStringExtra("url") != null){
                    intent.putExtra("url",
                            getIntent().getStringExtra("url"));
                }
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }
}
