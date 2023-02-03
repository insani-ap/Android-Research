package com.research.reteiverdata.smsver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.TextView;

import com.research.reteiverdata.R;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            String pesan = "000000";
            String pengirim = "000000";
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                pesan = smsMessage.getMessageBody();
                pengirim = smsMessage.getOriginatingAddress();
            }
            TextView otp = ((Activity) context).findViewById(R.id.tvOTP);
            otp.setText("Pengirim: " + pengirim);
        }
    }
}
