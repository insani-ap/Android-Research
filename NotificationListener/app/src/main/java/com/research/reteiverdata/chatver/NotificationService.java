package com.research.reteiverdata.chatver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.research.reteiverdata.R;

public class NotificationService {
    private NotificationService() {}

    public static void showSomeNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel(context);
        createNotification(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannel(Context context) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("Notification Example", "Notification Example", importance);
        channel.setDescription("Channel for Notification Example");
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private static void createNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_MUTABLE);
        else
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new NotificationCompat.Builder(context, "Notification Example");

        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Example")
                .setContentText("Example Notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(9, builder.build());

        Intent intent1 = new Intent("Test");
        intent1.putExtra("value", "Ini OTPNYA");
        context.sendBroadcast(intent1);
    }
}