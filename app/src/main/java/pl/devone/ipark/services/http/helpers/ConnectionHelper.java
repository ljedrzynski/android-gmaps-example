package pl.devone.ipark.services.http.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public class ConnectionHelper {


    public static boolean isServerReachable(String host) {
        try {
            return InetAddress.getByName(host).isReachable(3000);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}