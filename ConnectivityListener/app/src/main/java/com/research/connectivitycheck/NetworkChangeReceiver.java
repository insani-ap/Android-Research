package com.research.connectivitycheck;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private AlertDialog alertDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (NetworkUtil.getConnectionType(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                alertDialog = new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle("Koneksi Terputus")
                        .setMessage("Koneksi anda Hilang, silahkan melakukan koneksi ulang")
                        .create();
                alertDialog.show();
            } else {
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        }
    }
}
