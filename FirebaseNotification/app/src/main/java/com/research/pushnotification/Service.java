package com.research.pushnotification;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class Service extends FirebaseMessagingService implements MainInterface {
    private static final String CHANNEL_STANDARD = "177013";
    private static final String CHANNEL_CUSTOM = "013177";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Awas, Setelah Notifikasi Channel di set di NotifManager,
        //dia tidak bisa dimodif/ditimpa isinya sampai di delete

        int version = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("version")));
        switch (version) {
            case 0: case 1:
                break;
            case 2: case 4: case 5:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        createStandardChannelNotification();
                triggerNotification(remoteMessage.getData());
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createModifableSoundChannelNotification();
                    triggerNotification(remoteMessage.getData());
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void createModifableSoundChannelNotification() {
        final Uri uriSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.harmonics);

        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        NotificationChannel channel = new NotificationChannel(CHANNEL_CUSTOM,"Notification Custom", NotificationManager.IMPORTANCE_HIGH);
        channel.setSound(uriSound, attributes);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void createStandardChannelNotification() {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_STANDARD, "Notification", importance);
        channel.setDescription("Channel for Notification");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void triggerNotification(Map<String, String> data) {
        Intent intent = new Intent(this, NotifActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("value", data.get("value"));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new NotificationCompat.Builder(this, data.get("sound") != null ? CHANNEL_CUSTOM : CHANNEL_STANDARD);

        builder.setSmallIcon(R.drawable.ic_message)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (data.get("image") != null) {
            Bitmap image = buildBitmap(data.get("image"));
            builder.setLargeIcon(image);
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null));
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Service.this);
            notificationManager.notify(Integer.parseInt(Objects.requireNonNull(data.get("tag"))), builder.build());
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Service.this);
        notificationManager.notify(Integer.parseInt(Objects.requireNonNull(data.get("tag"))), builder.build());
    }

    private Bitmap buildBitmap(String imageUrl) {
        Bitmap img;
        try (InputStream inputStream = (InputStream) new URL(imageUrl).getContent()) {
            img = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            img = null;
            Log.d("IMG", "Err. Fetch Image");
        }
        return img;
    }
}
