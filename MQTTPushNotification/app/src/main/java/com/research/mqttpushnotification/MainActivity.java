package com.research.mqttpushnotification;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.research.mqttpushnotification.V1.ServiceMQTTImpl;
import com.research.mqttpushnotification.V1.ViewCallback;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity implements ViewCallback {
    private ServiceMQTTImpl serviceMQTTImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //V2
        //Intent intent = new Intent(this, ServiceMQTTBackground.class);
        //startService(intent);

        //V1
        String fromNotif = getIntent().getStringExtra("data");
        if (fromNotif == null) {
            serviceMQTTImpl = ServiceMQTTImpl.getInstance(this, this);
            try {
                serviceMQTTImpl.initializationMQTT("insani", "@Passwd123", "tcp://10.10.0.253:1883", "1");
            } catch (Exception e) {
                MQTTConnectionFailed(e.getMessage(), "Connecting");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void MQTTConnected(MqttAndroidClient mqttAndroidClient, String url) {
        Log.d("MQTT", "MQTT Connected to " + url);
        try {
            serviceMQTTImpl.subscribeToSomeTopic("MQTT Push Notification", mqttAndroidClient);
        } catch (Exception e) {
            MQTTConnectionFailed(e.getMessage(), "Subscribing");
        }
    }

    @Override
    public void MQTTSubscribed(MqttAndroidClient mqttAndroidClient, String topic) {
        Log.d("MQTT", "MQTT Subscribed to " + topic);
        Button sendMessage = findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(v -> {
            try {
                serviceMQTTImpl.publishSomeMessage(topic, "Sebuah Message Dikirim", mqttAndroidClient);
            } catch (MqttException e) {
                MQTTConnectionFailed(e.getMessage(), "Send Message");
            }
        });
    }

    @Override
    public void messageRecevied(String topic, String messsage) {
        Log.d("MQTT", "New message with Topic: " + topic + " | Message : " + messsage);
        serviceMQTTImpl.doBuildNotification(topic, messsage);
    }

    @Override
    public void MQTTConnectionFailed(String cause, String where) {
        Log.d("MQTT", "MQTT Error. Cause: " + cause + " | In: " + where);
    }

    @Override
    public void messageSend(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}