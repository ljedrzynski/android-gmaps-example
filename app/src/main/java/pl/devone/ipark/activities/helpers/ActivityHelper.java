package pl.devone.ipark.activities.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pl.devone.ipark.R;
import pl.devone.ipark.models.User;
import pl.devone.ipark.services.authentication.AuthenticationManager;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public class ActivityHelper {

    public static void navigateActivity(Activity currentActivity, Class nextActivityClass, boolean finish) {
        if (finish) {
            currentActivity.finish();
        }
        currentActivity.startActivity(new Intent(currentActivity, nextActivityClass));
    }

    public static void reportError(Context context, int errorId, Exception exc) {
        String errorMsg = context.getString(errorId);
        Log.d("ERR", exc.getMessage() + ":" + exc.getStackTrace().toString());
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_unexpected))
                .setMessage(errorMsg)
                .setCancelable(false)
                .setNeutralButton("OK", null)
                .show();
    }

    public static void reportError(Context context, int errorId, String excMsg) {
        String errorMsg = context.getString(errorId);
        Log.d("ERR", excMsg);
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_unexpected))
                .setMessage(errorMsg)
                .setCancelable(false)
                .setNeutralButton("OK", null)
                .show();
    }

    public static User getUser(Context context) {
        return AuthenticationManager.getUserFromContext(context);
    }
}
