package pl.devone.ipark.activities.helpers;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.Set;

import pl.devone.ipark.R;
import pl.devone.ipark.fragments.MapBoxFragment;

/**
 * Created by ljedrzynski on 30.05.2017.
 */

public class PermissionHelper {
    public static final int PERMISSION_REQUEST_LOCATION = 99;

    public static void requestLocationPermission(final Fragment fragment) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(fragment.getContext())
                    .setTitle(fragment.getString(R.string.title_location_permission))
                    .setMessage(fragment.getString(R.string.info_location_permission))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();
        } else {
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    public static boolean hasLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
