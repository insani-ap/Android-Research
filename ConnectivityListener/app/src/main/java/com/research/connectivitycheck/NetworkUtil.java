package com.research.connectivitycheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

public class NetworkUtil {
    public static int TYPE_NOT_CONNECTED = 0;
    public static int TYPE_MOBILE_DATA = 1;
    public static int TYPE_WIFI = 2;
    public static int TYPE_VPN = 3;

    public static int getConnectionType(Context context) {
        int res = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        res = TYPE_WIFI;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        res = TYPE_MOBILE_DATA;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        res = TYPE_VPN;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        res = TYPE_WIFI;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        res = TYPE_MOBILE_DATA;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                        res = TYPE_VPN;
                    }
                }
            }
        }

        return res;
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 nexsoft.co.id");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (Exception e) {
            Log.d("NetworkUtil", "" +  e.getMessage());
        }

        return false;
    }
}
