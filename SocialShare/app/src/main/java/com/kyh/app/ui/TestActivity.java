package com.kyh.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kyh.app.R;
import com.kyh.app.utils.NotificationsUtils;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipush);

        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("url", "21312");
        NotificationsUtils.showInNotificationBar(this, "1", "2", 3, intent1);
    }
}
