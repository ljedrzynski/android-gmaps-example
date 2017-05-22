package pl.devone.ipark.services.http.helpers;

import android.content.Context;

import pl.devone.ipark.R;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public class HttpHelper {

    public static String getApiAbsoluteUrl(Context context, String url) {
        return context.getString(R.string.api_url) + url;
    }
}
