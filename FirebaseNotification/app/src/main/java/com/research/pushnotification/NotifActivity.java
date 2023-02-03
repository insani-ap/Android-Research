package com.research.pushnotification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotifActivity extends AppCompatActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent dataFromNotification = getIntent();
        if (dataFromNotification != null) {
            String value = dataFromNotification.getStringExtra("value");
            ((TextView) findViewById(R.id.tvNotif)).setText("Congratulations, Notification clicked by you.");
            ((TextView) findViewById(R.id.tvData)).setText("Here is the data: " + value);

        } else {
            ((TextView) findViewById(R.id.tvNotif)).setText("Congratulations, Notification clicked by you. Unfortunately there is no data");
        }
    }
}
