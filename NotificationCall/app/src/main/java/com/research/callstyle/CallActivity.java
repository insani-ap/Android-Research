package com.research.callstyle;

import android.app.NotificationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.research.callstyle.databinding.ActivityCallBinding;

import java.util.Objects;

public class CallActivity extends AppCompatActivity {
    private ActivityCallBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(((NotificationManager) getSystemService(NOTIFICATION_SERVICE))).cancel(0);
        binding.response.setText(getIntent().getStringExtra("data"));
    }
}
