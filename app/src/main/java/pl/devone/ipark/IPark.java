package pl.devone.ipark;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import pl.devone.ipark.activities.helpers.CommonHelper;
import pl.devone.ipark.activities.helpers.PermissionHelper;
import pl.devone.ipark.fragments.MapBoxFragment;
import pl.devone.ipark.services.activity.ActivityRecognitionHandler;
import pl.devone.ipark.services.activity.ActivityRecognitionProvider;
import pl.devone.ipark.services.authentication.AuthenticationManager;
import pl.devone.ipark.services.http.RestClient;
import pl.devone.ipark.services.http.helpers.HttpHelper;
import pl.devone.ipark.services.location.LocationProvider;

/**
 * Created by ljedrzynski on 03.06.2017.
 */

public class IPark extends Application implements MapBoxFragment.PermissionResultListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mApiClient;
    private Intent locationProvider;
    private Intent activityRecognitionHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        onCreateCheck();
    }

    private void onCreateCheck() {
        if (!HttpHelper.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
            CommonHelper.navigateLoginActivity(this.getApplicationContext());
        }

        if (!AuthenticationManager.isAppAuthenticated(getApplicationContext())) {
            CommonHelper.navigateLoginActivity(this);
        } else {
            RestClient.setAuthorizationHeader(CommonHelper.getUser(this).getAuthToken());
        }

        if (PermissionHelper.hasLocationPermission(getApplicationContext())) {
            startBackgroundWork();
        } else {
            stopBackgroundWork();
        }
    }

    private void startBackgroundWork() {
        if (mApiClient == null) {
            throw new RuntimeException("GoogleApiClient is null!");
        }
        mApiClient.connect();
        startService(locationProvider = new Intent(this, LocationProvider.class));
    }

    private void stopBackgroundWork() {
        if (mApiClient != null && mApiClient.isConnected()) {
            mApiClient.disconnect();
        }
        if (locationProvider != null) {
            stopService(locationProvider);
        }
        if (activityRecognitionHandler != null) {
            stopService(activityRecognitionHandler);
        }
    }

    @Override
    public void onGranted() {
        startBackgroundWork();
    }

    @Override
    public void onDenied() {
        stopBackgroundWork();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, new Intent(this, ActivityRecognitionProvider.class), PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);
        startService(activityRecognitionHandler = new Intent(this, ActivityRecognitionHandler.class));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
