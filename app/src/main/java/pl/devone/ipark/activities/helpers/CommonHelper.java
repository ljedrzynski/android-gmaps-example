package pl.devone.ipark.activities.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pl.devone.ipark.R;
import pl.devone.ipark.activities.LoginActivity;
import pl.devone.ipark.models.User;
import pl.devone.ipark.services.authentication.AuthenticationProvider;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public class CommonHelper {

    public static void navigateActivity(Context context, Class nextActivityClass, boolean finish) {
        if (context instanceof Activity & finish) {
            ((Activity) context).finish();
        }
        context.startActivity(new Intent(context, nextActivityClass));
    }

    public static void navigateLoginActivity(Context context) {
        navigateActivity(context, LoginActivity.class, true);
    }

    public static void goBackground(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
        return AuthenticationProvider.getUserFromContext(context);
    }
}
