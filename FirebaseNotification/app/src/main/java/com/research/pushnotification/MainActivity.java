package com.research.pushnotification;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button subscribe;
    private Button unsubscribe;
    private RadioGroup radioGroup;
    private String thisToken;
    private String thisTopic;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscribe = findViewById(R.id.buttonSubscribe);
        unsubscribe = findViewById(R.id.buttonUnsubscribe);
        Button getToken = findViewById(R.id.buttonGetToken);
        Button sendPushNotif = findViewById(R.id.sendPushNotification);

        radioGroup = findViewById(R.id.rgMain);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Mohon tunggu...");

        subscribe.setOnClickListener(v -> subscribeToSomeTopic());
        unsubscribe.setOnClickListener(v -> unSubsribeToSomeTopic());
        getToken.setOnClickListener(v -> getDeviceToken());
        sendPushNotif.setOnClickListener(v -> {
            if (radioGroup.getCheckedRadioButtonId() != -1) {
                RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                sendPushNotification(Integer.parseInt(radioButton.getText().toString().replace("V", "")));
            }
        });

        unsubscribe.setEnabled(false);
    }

    private void unSubsribeToSomeTopic() {
        progressDialog.show();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(thisTopic).addOnCompleteListener(task -> {
            String message = "Unsubscribed from \"" + thisTopic + "\" Topic";
            if (!task.isSuccessful()) {
                message = "Failed to unsubscribe to \"" + thisTopic + "\"";
            }
            Log.d(TAG, message);

            unsubscribe.setEnabled(false);
            subscribe.setEnabled(true);

            showSnackbar(message);
            thisTopic = null;
            progressDialog.hide();
        });
    }

    private void subscribeToSomeTopic() {
        progressDialog.show();
        thisTopic = "exampleTopic";
        FirebaseMessaging.getInstance().subscribeToTopic(thisTopic)
                .addOnCompleteListener(task -> {
                    String message = "Subscribed to \"" + thisTopic + "\" Topic";
                    if (!task.isSuccessful()) {
                        message = "Failed to subscribe to \"" + thisTopic + "\"";
                    }
                    Log.d(TAG, message);

                    unsubscribe.setEnabled(true);
                    subscribe.setEnabled(false);

                    showSnackbar(message);
                    progressDialog.hide();
                });
    }

    private void getDeviceToken() {
        progressDialog.show();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        progressDialog.hide();
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    thisToken = task.getResult();
                    Log.d(TAG, "Your Token: " + thisToken);

                    showSnackbar("Token is OK");
                    progressDialog.hide();
                });
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.cLRoot), message, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }

    private void sendPushNotification(int version) {
        progressDialog.show();
        String url = "https://fcm.googleapis.com";
        switch (version) {
            case 1:
                progressDialog.hide();
                showSnackbar("Due to Restriction of V1, you can only receive notification when app run in background.");
                break;
            case 2:
                if (thisToken == null) {
                    progressDialog.hide();
                    showSnackbar("Get your token first.");
                    break;
                }
                RetrofitMain.sendPushNotifVersion2(new MainCallback() {
                    @Override
                    public void resultOk(Object o) {
                        progressDialog.hide();
                        showSnackbar("Sending Notification V2... Be patience");
                    }

                    @Override
                    public void resultFail(Throwable t) {
                        progressDialog.hide();
                        showSnackbar("Failed to send Notification");
                    }
                }, url, thisToken);
                break;
            case 3:
                if (thisTopic == null) {
                    progressDialog.hide();
                    showSnackbar("Subscribe to Topic first.");
                    break;
                }
                RetrofitMain.sendPushNotifVersion3(new MainCallback() {
                    @Override
                    public void resultOk(Object o) {
                        progressDialog.hide();
                        showSnackbar("Sending Notification V3... Be patience");
                    }

                    @Override
                    public void resultFail(Throwable t) {
                        progressDialog.hide();
                        showSnackbar("Failed to send Notification");
                    }
                }, url, thisTopic);
                break;
            case 4:
                progressDialog.hide();
                showSnackbar("Under maintenance.");
                break;
            case 5:
                if (thisToken == null) {
                    progressDialog.hide();
                    showSnackbar("Get your token first.");
                    break;
                }
                Thread thread = new Thread(() -> {
                    try {
                        RetrofitMain.sendPushNotifVersion5(new MainCallback() {
                            @Override
                            public void resultOk(Object o) {
                                runOnUiThread(() -> {
                                    progressDialog.hide();
                                    showSnackbar("Sending Notification V5... Be patience");
                                });
                            }

                            @Override
                            public void resultFail(Throwable t) {
                                runOnUiThread(() -> {
                                    progressDialog.hide();
                                    showSnackbar("Failed to send Notification");
                                });
                            }
                        }, url, thisToken);
                    } catch (IOException e) {
                        runOnUiThread(() -> {
                            progressDialog.hide();
                            showSnackbar("Something Error.");
                        });
                    }
                });
                thread.start();
                break;
            default:
                progressDialog.hide();
                showSnackbar("What?");
                break;
        }
    }
}