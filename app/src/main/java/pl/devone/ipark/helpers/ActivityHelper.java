package pl.devone.ipark.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pl.devone.ipark.R;

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

    public static void reportError(int errorId, Context context) {
        String errorMsg = context.getString(errorId);
        Log.d("ERROR", errorMsg);
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_unexpected))
                .setMessage(errorMsg)
                .setCancelable(false)
                .setNeutralButton("OK", null)
                .show();
    }
}
