package com.research.mqttpushnotification.V1;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public interface ServiceMQTT {
    void initializationMQTT(String username, String password, String server, String clientId) throws MqttException;
    void subscribeToSomeTopic(String topic, MqttAndroidClient mqttAndroidClient) throws MqttException;
    void publishSomeMessage(String topic, String message, MqttAndroidClient mqttAndroidClient) throws MqttException;
    void doBuildNotification(String topic, String message);
    void setChannelId(String channelId);
    void triggerNotification(String body, String header);
}
