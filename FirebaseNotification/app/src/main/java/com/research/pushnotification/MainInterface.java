package com.research.pushnotification;

import java.util.Map;

public interface MainInterface {
    void createModifableSoundChannelNotification();
    void createStandardChannelNotification();
    void triggerNotification(Map<String, String> data);
}
