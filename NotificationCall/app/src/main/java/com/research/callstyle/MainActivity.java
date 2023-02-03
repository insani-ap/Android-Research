package com.research.callstyle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ContentInfoCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.research.callstyle.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(((NotificationManager) getSystemService(NOTIFICATION_SERVICE))).cancel(0);

        binding.callSomeone.setOnClickListener(view -> {
            createNotification();
        });
    }

    private void createNotification() {
        createChannel();

        PendingIntent receiveCallPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, CallActivity.class).putExtra("data", "Answer"), PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent cancelCallPendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, CallActivity.class).putExtra("data", "Decline"), PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        Spannable spannableAnswer = new SpannableString("ANSWER");
        spannableAnswer.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.green)), 0, spannableAnswer.length(), 0);
        Spannable spannableDecline = new SpannableString("DECLINE");
        spannableDecline.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), 0, spannableDecline.length(), 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "CLR_ID")
                .setContentText("Someone")
                .setContentTitle("Incoming Voice Call")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .addAction(R.drawable.ic_baseline_call_24, spannableAnswer, receiveCallPendingIntent)
                .addAction(R.drawable.ic_baseline_call_end_24, spannableDecline, cancelCallPendingIntent)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anime_ringtone))
                .setColor(0x00000000)
                .setFullScreenIntent(receiveCallPendingIntent, true);

        Notification mCompat = notificationBuilder.build();
        mCompat.flags |= Notification.FLAG_INSISTENT;
        Objects.requireNonNull(((NotificationManager) getSystemService(NOTIFICATION_SERVICE))).notify(0, mCompat);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel("CLR_ID", "Call notifications", NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anime_ringtone),
                        new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setLegacyStreamType(AudioManager.STREAM_VOICE_CALL)
                                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                                .build());
                Objects.requireNonNull(getSystemService(NotificationManager.class)).createNotificationChannel(channel);
        }
    }
}