package com.research.reteiverdata.chatver;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.research.reteiverdata.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(new ReceiverService(), new IntentFilter("Test"));

        Button clickMe = findViewById(R.id.clickMe);
        clickMe.setOnClickListener(view -> NotificationService.showSomeNotification(this));
    }
}