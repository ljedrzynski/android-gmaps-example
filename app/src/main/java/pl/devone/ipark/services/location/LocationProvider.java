package pl.devone.ipark.services.location;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.commons.models.Position;

import java.util.HashSet;
import java.util.Set;

import pl.devone.ipark.MockLocationEngine;
import pl.devone.ipark.activities.helpers.PermissionHelper;

/**
 * Created by ljedrzynski on 02.06.2017.
 */

public class LocationProvider extends Service implements LocationEngineListener {

    private final IBinder mBinder = new LocationBinder();
    private LocationEngine mLocationEngine;
    private Location mLastLocation;
    private Set<LocationServiceListener> mLocationServiceListeners = new HashSet<>();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocationBinder extends Binder {
        public LocationProvider getService() {
            return LocationProvider.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!PermissionHelper.hasLocationPermission(getApplicationContext())) {
            this.stopSelf();
        }

        mLocationEngine = new MockLocationEngine();
//        mLocationEngine = LocationSource.getLocationEngine(getContext());52.290842, 21.115158   52.290842	21.115158
        mLocationEngine.setInterval(0);
        mLocationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        mLocationEngine.setFastestInterval(1000);
        mLocationEngine.addLocationEngineListener(new LocationEngineListener() {
            @Override
            public void onConnected() {
                mLastLocation = mLocationEngine.getLastLocation();
            }

            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    //TODO remove
                    if (location.getLongitude() == mLastLocation.getLongitude() && location.getLatitude() == mLastLocation.getLatitude()) {
                        mLocationEngine.deactivate();
                    }
                    mLastLocation = location;

                    for (LocationServiceListener mLocationServiceListener : mLocationServiceListeners) {
                        mLocationServiceListener.onLocationChanged(location);
                    }
                }
            }
        });
        mLocationEngine.activate();

        mLastLocation = ((MockLocationEngine) mLocationEngine).mockLocation(Position.fromCoordinates(21.115158, 52.290842));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerListener(LocationServiceListener locationServiceListener) {
        if (!mLocationServiceListeners.contains(locationServiceListener)) {
            mLocationServiceListeners.add(locationServiceListener);
        }
    }

    public void removeListener(LocationServiceListener locationServiceListener) {
        mLocationServiceListeners.remove(locationServiceListener);
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //TODO remove
            if (location.getLongitude() == mLastLocation.getLongitude() && location.getLatitude() == mLastLocation.getLatitude()) {
                mLocationEngine.deactivate();
            }
            mLastLocation = location;
            for (LocationServiceListener mLocationServiceListener : mLocationServiceListeners) {
                mLocationServiceListener.onLocationChanged(location);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public LocationEngine getLocationEngine() {
        return mLocationEngine;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public interface LocationServiceListener {

        void onLocationChanged(Location location);
    }
}
