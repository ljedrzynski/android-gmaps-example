package pl.devone.ipark.services.activity;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

import pl.devone.ipark.services.location.LocationProvider;
import timber.log.Timber;

/**
 * Created by ljedrzynski on 03.06.2017.
 */

public class ActivityRecognitionHandler extends Service implements LocationProvider.LocationServiceListener, ActivityRecognitionProvider.ActivityRecognitionListener {

    private LocationProvider mLocationProvider;
    private boolean mLocationProviderBound;

    private ActivityRecognitionProvider mActivityProvider;
    private boolean mActivityProviderBound;

    private static String FREE_PARK_SPACE_ACTION = "FREE_PARK_SPACE_ACTION";
    private static String OPEN_APP_ACTION = "OPEN_APP_ACTION";
    private static String NONE_ACTION = "NONE_ACTION";

    public static class ActionReceiver extends BroadcastReceiver {

        public ActionReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onReceive ", "");
        }
    }


    private ServiceConnection mLocationProviderConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LocationProvider.LocationBinder binder = (LocationProvider.LocationBinder) service;
            mLocationProvider = binder.getService();
            mLocationProvider.registerListener(ActivityRecognitionHandler.this);
            mLocationProviderBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLocationProviderBound = false;
        }
    };

    private ServiceConnection mActivityProviderConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ActivityRecognitionProvider.ActivityRecognitionBinder binder = (ActivityRecognitionProvider.ActivityRecognitionBinder) service;
            mActivityProvider = binder.getService();
            mActivityProvider.registerListener(ActivityRecognitionHandler.this);
            mActivityProviderBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mActivityProviderBound = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this.getApplicationContext(), LocationProvider.class);
        bindService(intent, mLocationProviderConnection, 0);

        intent = new Intent(this.getApplicationContext(), ActivityRecognitionProvider.class);
        bindService(intent, mActivityProviderConnection, 0);

        Timber.d(this.getClass().getSimpleName() + " has started");
    }


    @Override
    public void onActivityDetected(DetectedActivity activity) {
        switch (activity.getType()) {
            case DetectedActivity.IN_VEHICLE: {
                Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                break;
            }
            case DetectedActivity.ON_FOOT: {
                Log.e("ActivityRecogition", "On Foot: " + activity.getConfidence());
                break;
            }
            case DetectedActivity.RUNNING: {
                Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
                break;
            }
            case DetectedActivity.STILL: {
                Log.e("ActivityRecogition", "Still: " + activity.getConfidence() + " ");
                if (activity.getConfidence() >= 75) {
//                        ParkingSpaceManager.getUserParkingSpaces(getApplicationContext(), new ParkingSpaceFetchCallback() {
//                            @Override
//                            public void onSuccess(List<ParkingSpace> parkingSpaces) {
//
//                                if (parkingSpaces != null && parkingSpaces.size() == 1) {
//                                    NotificationManager mNotificationManager = (NotificationManager)
//                                            ActivityRecognitionHandler.this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//                                    NotificationCompat.Builder mBuilder =
//                                            (NotificationCompat.Builder) new NotificationCompat.Builder(ActivityRecognitionHandler.this)
//                                                    .setContentTitle("GCM Notification")
//                                                    .setStyle(new NotificationCompat.BigTextStyle()
//                                                            .bigText("Obecnie zajmujesz miejsce " + parkingSpaces.get(0).getLongitude() + "Czy chcesz je zwolnić?"))
//                                                    .setContentText("TEST")
//                                                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
////                                        .addAction(new android.support.v4.app.NotificationCompat
////                                                .Action(R.drawable.common_google_signin_btn_icon_dark_focused, "Zwolnij", contentIntent));
//
//                                    Intent freeSpaceIntent = new Intent(ActivityRecognitionHandler.this, ActionReceiver.class);
//                                    freeSpaceIntent.putExtra("occupying_place", parkingSpaces.get(0));
//                                    freeSpaceIntent.setAction(FREE_PARK_SPACE_ACTION);
//                                    PendingIntent pendingFreeSpaceIntent = PendingIntent.getBroadcast(ActivityRecognitionHandler.this, 1000, freeSpaceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                    mBuilder.addAction(R.drawable.mapbox_marker_icon_default, "Zwolnij miejsce", pendingFreeSpaceIntent);
//
//                                    mNotificationManager.notify(0, mBuilder.build());
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onFailure() {
//
//                            }
//                        });
                }
                break;
            }
            case DetectedActivity.TILTING: {
                Log.e("ActivityRecogition", "Tilting: " + activity.getConfidence());
                break;
            }
            case DetectedActivity.WALKING: {
                Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
                break;
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocationProviderBound) {
            unbindService(mLocationProviderConnection);
            mLocationProvider.removeListener(this);
            mLocationProviderBound = false;
        }

        if (mActivityProviderBound) {
            unbindService(mActivityProviderConnection);
            mActivityProvider.removeListener(this);
            mActivityProviderBound = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
