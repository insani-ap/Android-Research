package com.research.mqttpushnotification.V1;

import org.eclipse.paho.android.service.MqttAndroidClient;

public interface ViewCallback {
    void MQTTConnected(MqttAndroidClient mqttAndroidClient, String url);
    void MQTTSubscribed(MqttAndroidClient mqttAndroidClient, String topic);
    void messageRecevied(String topic, String messsage);
    void MQTTConnectionFailed(String cause, String where);

    void messageSend(String message);
}
