package pl.devone.ipark.activities;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import pl.devone.ipark.activities.helpers.CommonHelper;
import pl.devone.ipark.activities.helpers.PermissionHelper;
import pl.devone.ipark.fragments.MainFragment;
import pl.devone.ipark.fragments.MapBoxFragment;
import pl.devone.ipark.fragments.NavigationDrawerFragment;
import pl.devone.ipark.R;
import pl.devone.ipark.services.activity.ActivityRecognitionHandler;
import pl.devone.ipark.services.activity.ActivityRecognitionProvider;
import pl.devone.ipark.services.authentication.AuthenticationProvider;
import pl.devone.ipark.services.http.RestClient;
import pl.devone.ipark.services.http.helpers.HttpHelper;
import pl.devone.ipark.services.location.LocationProvider;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        MapBoxFragment.LocalPermissionResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mApiClient;
    private Intent locationProvider;
    private Intent activityRecognitionHandler;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!onCreateCheck()) return;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        mTitle = getTitle();
    }

    private boolean onCreateCheck() {
        if (!HttpHelper.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
            CommonHelper.navigateLoginActivity(this.getApplicationContext());
            return false;
        }

        if (!isAppAuthenticated()) {
            CommonHelper.navigateLoginActivity(this);
            return false;
        } else {
            RestClient.setAuthorizationHeader(CommonHelper.getUser(this).getAuthToken());
        }

        if (PermissionHelper.hasLocationPermission(getApplicationContext())) {
            startBackgroundWork();
        } else {
            stopBackgroundWork();
        }

        return true;
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (isAppAuthenticated()) {
            switch (position) {
                case 0:
                    mTitle = getString(R.string.title_main);
                    replaceFragment(MainFragment.class);
                    break;

                case 1:
                    AuthenticationProvider.signOut(this);
                    return;

                case 2:
                    this.finish();
                    System.exit(0);
            }
        }
    }

    private boolean isAppAuthenticated() {
        return AuthenticationProvider.isAppAuthenticated(getApplicationContext());
    }

    private Fragment replaceFragment(Class cls) {
        Fragment fragment;
        try {
            fragment = (Fragment) cls.getConstructor().newInstance();

        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        return fragment;
    }


    private void startBackgroundWork() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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
