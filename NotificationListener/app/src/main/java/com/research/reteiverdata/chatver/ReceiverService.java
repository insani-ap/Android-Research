package com.research.reteiverdata.chatver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.research.reteiverdata.R;

public class ReceiverService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TextView textView = ((Activity) context).findViewById(R.id.tvExample);
        textView.setText(intent.getStringExtra("value"));
    }
}
