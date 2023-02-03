package com.research.reteiverdata.smsver;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.research.reteiverdata.R;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(new SMSReceiver(), intentFilter);

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Err", "Error");
            return;
        }
        Log.d("Info", tMgr.getLine1Number());

        Button example = findViewById(R.id.butOtp);
        example.setOnClickListener(view -> {
            example.setEnabled(false);
            new CountDownTimer(3750, 3750) {
                @Override
                public void onTick(long l) {}

                @Override
                public void onFinish() {
                    example.setEnabled(true);
                }
            }.start();

            change();
        });
    }

    private void change() {
        String[] data = {"9", "1", "2", "5", "7", "3"};
        TextView textView = findViewById(R.id.tvOTP);
        textView.setText("");
        Thread thread = new Thread(() -> {
            for (int i = 0; i <= 5; i++) {
                int finalI = i;
                runOnUiThread(() -> textView.setText(textView.getText().toString() + data[finalI]));
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
