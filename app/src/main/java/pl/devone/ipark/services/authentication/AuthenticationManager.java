package pl.devone.ipark.services.authentication;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import pl.devone.ipark.activities.LoginActivity;
import pl.devone.ipark.R;
import pl.devone.ipark.services.callback.AsyncTaskCallback;
import pl.devone.ipark.services.http.RestClient;
import pl.devone.ipark.models.User;
import pl.devone.ipark.activities.helpers.ActivityHelper;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public class AuthenticationManager {

    private static Gson gson = new Gson();

    public static void signIn(final Context context, final User user, final AsyncTaskCallback callback) {
        try {
            RestClient.post(context, "/authenticate", gson.toJson(user), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (!response.has("auth_token")) {
                            throw new RuntimeException(context.getString(R.string.error_response_without_token));
                        }

                        RestClient.setAuthorizationHeader(response.getString("auth_token"));

                        PreferenceManager.getDefaultSharedPreferences(context)
                                .edit()
                                .putString("user", response.toString())
                                .apply();

                        callback.onSuccess();
                    } catch (Exception e) {
                        ActivityHelper.reportError(context, R.string.error_reported_info, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (statusCode == 401) {
                        callback.onFailure();
                    } else
                        callback.onError(context.getString(R.string.server_connection_error));
                }
            });
        } catch (Exception exc) {
            ActivityHelper.reportError(context, R.string.error_reported_info, exc);
        }

    }

    public static void signUp(final Context context, final User user, final AsyncTaskCallback callback) {
        try {
            RestClient.post(context, "/register", gson.toJson(user), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onSuccess();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    callback.onFailure();
                }
            });
        } catch (Exception exc) {
            ActivityHelper.reportError(context, R.string.error_reported_info, exc);
        }
    }

    public static void signOut(Activity currentActivity) {
        PreferenceManager.getDefaultSharedPreferences(currentActivity.getApplicationContext())
                .edit()
                .remove("user")
                .apply();
        ActivityHelper.navigateActivity(currentActivity, LoginActivity.class, true);
    }

    public static User getUserFromContext(Context context) {
        return gson.fromJson(PreferenceManager.getDefaultSharedPreferences(context)
                .getString("user", ""), User.class);
    }

    public static boolean isAppAuthenticated(Context context) {
        //TODO request
        User user = getUserFromContext(context);
        return user != null && !Strings.isNullOrEmpty(user.getAuthToken());
    }
}
