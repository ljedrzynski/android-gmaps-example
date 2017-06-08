package pl.devone.ipark.services.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import pl.devone.ipark.R;
import pl.devone.ipark.activities.MainActivity;
import pl.devone.ipark.activities.helpers.CommonHelper;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.callbacks.AsyncTaskCallback;
import pl.devone.ipark.services.parkingspace.ParkingSpaceManager;
import pl.devone.ipark.services.parkingspace.callbacks.ParkingSpaceFetchCallback;


/**
 * Created by ljedrzynski on 03.06.2017.
 */

public class ActivityRecognitionHandler extends BroadcastReceiver {

    public static final String ACTIVITY_RECOGNITION_MSG = "pl.ipark.intent.action.MESSAGE_PROCESSED";
    public static final String ACTIVITY_DETECTED = "pl.ipark.intent.params.ACTIVITY_DETECTED";
    public static final String ACTIVITY_CONFIDENCE = "pl.ipark.intent.params.ACTIVITY_CONFIDENCE";

    private static final String FREE_PARK_SPACE_ACTION = "pl.ipark.intent.action.FREE_PARK_SPACE";
    private static final String NONE_ACTION = "pl.ipark.intent.action.NONE";
    private static final String OPEN_APP_ACTION = "pl.ipark.intent.action.OPEN_APP_ACTION";

    private static final String OCCUPIED_PARK_SPACE = "pl.ipark.intent.object.OCCUPIED_PARK_SPACE";

    private static boolean inVehicle;

    public static class NotificationActionHandler extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            switch (intent.getAction()) {
                case FREE_PARK_SPACE_ACTION: {
                    ParkingSpace parkingSpace = ((ParkingSpace) intent.getSerializableExtra(OCCUPIED_PARK_SPACE))
                            .setOccupied(false)
                            .setLastOccupierId(CommonHelper.getUser(context).getId())
                            .setCurrOccupierId(null);

                    AsyncTaskCallback callback = new AsyncTaskCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "Miejsce zostało udostępnione kolejnym użytkownikom! Dziękujemy!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure() {

                        }

                        @Override
                        public void onError(String error) {

                        }
                    };

                    ParkingSpaceManager.updateParkingSpace(context, parkingSpace, callback);
                }
                break;
                case OPEN_APP_ACTION: {
                    context.startActivity(new Intent(context, MainActivity.class));
                }
                break;
            }
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
        }

    }


    public ActivityRecognitionHandler() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        int confidence = intent.getIntExtra(ACTIVITY_CONFIDENCE, -1);

        if (confidence > 80) {
            int activity = intent.getIntExtra(ACTIVITY_DETECTED, -1);

            if (activity == DetectedActivity.IN_VEHICLE) {
                if (!inVehicle) {
                    showNotification(context);
                }

                inVehicle = true;

            } else if (activity == DetectedActivity.ON_FOOT
                    || activity == DetectedActivity.WALKING
                    || activity == DetectedActivity.RUNNING
                    || activity == DetectedActivity.ON_BICYCLE) {

                inVehicle = false;
            }
        }
    }

    private void showNotification(final Context context) {
        ParkingSpaceManager.getUserParkingSpaces(context, new ParkingSpaceFetchCallback() {
            @Override
            public void onSuccess(List<ParkingSpace> parkingSpaces) {
                if (parkingSpaces != null && parkingSpaces.size() > 0) {
                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Obecnie zajmujesz miejsce " + parkingSpaces.get(0).getAddressInfo() + "Czy chcesz je zwolnić?"))
                            .setContentText("TEST")
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);

                    Intent freeSpaceIntent = new Intent(context, NotificationActionHandler.class)
                            .setAction(FREE_PARK_SPACE_ACTION)
                            .putExtra(OCCUPIED_PARK_SPACE, parkingSpaces.get(0));

                    PendingIntent pendingFreeSpaceIntent = PendingIntent.getBroadcast(context, 1000, freeSpaceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.addAction(R.drawable.mapbox_marker_icon_default, "Zwolnij miejsce", pendingFreeSpaceIntent);

                    Intent openAppIntent = new Intent(context, NotificationActionHandler.class)
                            .setAction(OPEN_APP_ACTION);

                    PendingIntent pendingOpenAppIntent = PendingIntent.getBroadcast(context, 1001, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.addAction(R.drawable.common_full_open_on_phone, "Przejdź do aplikacji", pendingOpenAppIntent);

                    Intent noneIntent = new Intent(context, NotificationActionHandler.class)
                            .setAction(NONE_ACTION);

                    PendingIntent pendingNoneIntent = PendingIntent.getBroadcast(context, 1002, noneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.addAction(R.drawable.common_full_open_on_phone, "Jeszczę nie odjeżdzam", pendingNoneIntent);

                    notificationManager.notify(0, notificationBuilder.build());
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
