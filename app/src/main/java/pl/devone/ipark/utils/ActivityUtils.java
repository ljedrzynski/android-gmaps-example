package pl.devone.ipark.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public class ActivityUtils {

    public static void navigateActivity(Activity currentActivity, Class nextActivityClass) {
        Intent intent = new Intent(currentActivity, nextActivityClass);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    public static void reportError(String error, Context context) {
        Log.d("ERROR", error);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(context);
        messageBox.setTitle("Application error");
        messageBox.setMessage(error);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }

}
