package pl.devone.ipark.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Strings;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import pl.devone.ipark.LoginActivity;
import pl.devone.ipark.R;
import pl.devone.ipark.authentication.callback.AuthTaskCallback;
import pl.devone.ipark.http.RestClient;
import pl.devone.ipark.utils.ActivityUtils;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public class AuthenticationManager {

    public static void signIn(final String email, final String password, final AuthTaskCallback callback, final Context context) {
        RestClient.post(getAbsoluteUrl(context, "/authenticate"), new RequestParams(new HashMap<String, String>() {{
            put("email", email);
            put("password", password);
        }}), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                try {
                    editor.putString(context.getString(R.string.authorization_token), response.getString(context.getString(R.string.authorization_token)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    ActivityUtils.reportError(context.getString(R.string.authorization_error), context);
                }
                editor.apply();
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    callback.onFailure();
                } else
                    callback.onError(context.getString(R.string.server_connection_error));
            }
        });
    }

    public static void signUp(final String nickName, final String email, final String password, final AuthTaskCallback callback, final Context context) {
        RestClient.post(getAbsoluteUrl(context, "/register"), new RequestParams(new HashMap<String, String>() {{
            put("nick", nickName);
            put("email", email);
            put("password", password);
        }}), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                callback.onFailure();
            }
        });
    }

    public static void signOut(Activity currentActivity) {
        Context context = currentActivity.getApplicationContext();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(context.getString(R.string.authorization_token));
        editor.apply();
        ActivityUtils.navigateActivity(currentActivity, LoginActivity.class, true);
    }

    public static boolean isAppAuthenticated(Context context) {
        return !Strings.isNullOrEmpty(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.authorization_token), ""));
    }

    private static String getAbsoluteUrl(Context context, String url) {
        return context.getString(R.string.api) + url;
    }
}
