package com.research.mqttpushnotification.V1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.research.mqttpushnotification.MainActivity;
import com.research.mqttpushnotification.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ServiceMQTTImpl implements ServiceMQTT {
    private final ViewCallback viewCallback;
    private final Context context;
    private final MqttCallback mqttCallback;

    public static ServiceMQTTImpl getInstance(Context context, ViewCallback viewCallback) {
        return new ServiceMQTTImpl(viewCallback, context);
    }

    private ServiceMQTTImpl(ViewCallback viewCallback, Context context) {
        this.viewCallback = viewCallback;
        this.context = context;
        this.mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                viewCallback.MQTTConnectionFailed(cause.getMessage(), "Connection Lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                viewCallback.messageRecevied(topic, message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //Publish
            }
        };
    }

    @Override
    public void initializationMQTT(String username, String password, String server, String clientId) throws MqttException {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, server, clientId);
        mqttAndroidClient.setCallback(mqttCallback);

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                viewCallback.MQTTConnected(mqttAndroidClient, server);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                viewCallback.MQTTConnectionFailed(exception.getMessage(), "Connected");
            }
        });
    }

    @Override
    public void subscribeToSomeTopic(String topic, MqttAndroidClient mqttAndroidClient) throws MqttException {
        mqttAndroidClient.subscribe(topic, 1, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                viewCallback.MQTTSubscribed(mqttAndroidClient, topic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                viewCallback.MQTTConnectionFailed(exception.getMessage(), "Subscribed");
            }
        });
    }

    @Override
    public void publishSomeMessage(String topic, String message, MqttAndroidClient mqttAndroidClient) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        mqttMessage.setQos(1);
        mqttMessage.setRetained(false);
        mqttMessage.setId(1);

        mqttAndroidClient.publish(topic, mqttMessage, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                viewCallback.messageSend(message);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                viewCallback.MQTTConnectionFailed(exception.getMessage(), "Sending Message");
            }
        });
    }

    @Override
    public void doBuildNotification(String topic, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setChannelId("1");
        triggerNotification(message, topic);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setChannelId(String channelId) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelId, "Notification", importance);
        channel.setDescription("Channel for Notification");
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void triggerNotification(String body, String header) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("data", "exists");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new NotificationCompat.Builder(context, "1");

        builder.setSmallIcon(R.drawable.ic_article)
                .setContentTitle(header)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
