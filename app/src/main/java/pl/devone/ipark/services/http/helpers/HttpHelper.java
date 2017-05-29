package pl.devone.ipark.services.http.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

import pl.devone.ipark.R;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public class HttpHelper {

    public static String getApiAbsoluteUrl(Context context, String url) {
        return context.getString(R.string.api_url) + url;
    }

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
