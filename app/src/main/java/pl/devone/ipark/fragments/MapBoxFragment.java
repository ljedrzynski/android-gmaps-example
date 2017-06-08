package pl.devone.ipark.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.v5.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.NavigationConstants;
import com.mapbox.services.android.navigation.v5.RouteProgress;
import com.mapbox.services.android.navigation.v5.listeners.AlertLevelChangeListener;
import com.mapbox.services.android.navigation.v5.listeners.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.listeners.OffRouteListener;
import com.mapbox.services.android.navigation.v5.listeners.ProgressChangeListener;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.models.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.devone.ipark.MockLocationEngine;
import pl.devone.ipark.R;
import pl.devone.ipark.activities.helpers.PermissionHelper;
import pl.devone.ipark.fragments.helpers.MapHelper;
import pl.devone.ipark.fragments.helpers.NavigationHelper;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.location.LocationProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapBoxFragment extends Fragment implements NavigationEventListener, ProgressChangeListener, AlertLevelChangeListener, OffRouteListener,
        LocationProvider.LocationServiceListener {

    //Map related
    private View mView;
    private MapView mMapView;
    private MapboxMap mMapBoxMap;

    private MapBoxFragment.MapActionCallback mMapCallbacks;
    private MapBoxFragment.NavigationActionCallback mNavigationCallbacks;
    private MapBoxFragment.LocalPermissionResultCallback mLocPermissionResultCallbacks;

    //Navigation related
    private MapboxNavigation mNavigation;
    private Position mDestination;
    private DirectionsRoute mRoute;
    private Polyline mRouteLine;
    private boolean mNavigating;
    private Marker mDestinationMarker;
    private Location mLastLocation;


    private boolean mLocationServiceBound;
    private LocationProvider mLocationProvider;
    private Map<Marker, ParkingSpace> mParkingSpacesMap;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mLocationServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LocationProvider.LocationBinder binder = (LocationProvider.LocationBinder) service;

            mLocationProvider = binder.getService();
            mLocationProvider.registerListener(MapBoxFragment.this);

            mLastLocation = mLocationProvider.getLastLocation();

            setMapView();

            setNavigationEngine();

            mLocationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLocationServiceBound = false;
        }
    };

    public MapBoxFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLocPermissionResultCallbacks = (MapBoxFragment.LocalPermissionResultCallback) context;
        mMapCallbacks = (MapBoxFragment.MapActionCallback) getParentFragment();
        mNavigationCallbacks = (MapBoxFragment.NavigationActionCallback) getParentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_token));

        getContext().bindService(new Intent(getContext(), LocationProvider.class),
                mLocationServiceConnection,
                PermissionHelper.hasLocationPermission(getContext()) ? Context.BIND_AUTO_CREATE : 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_box, container, false);

        mMapView = (MapView) mView.findViewById(R.id.mapBoxView);
        mMapView.onCreate(savedInstanceState);

        mNavigation = new MapboxNavigation(getContext(), Mapbox.getAccessToken());

        if (!PermissionHelper.hasLocationPermission(getContext())) {
            PermissionHelper.requestLocationPermission(this);
        }

        return mView;
    }

    public void setMapView() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mMapBoxMap = mapboxMap;
                mMapBoxMap.getUiSettings().setCompassEnabled(true);
                mMapBoxMap.getTrackingSettings().setDismissAllTrackingOnGesture(false);

                mMapBoxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (marker == mDestinationMarker) {
                            return false;
                        }
                        mDestinationMarker = marker;
                        mMapCallbacks.onMarkerClick(marker);
                        return true;
                    }
                });

                mMapBoxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        mDestinationMarker = null;
                        mMapCallbacks.onMapClick(point);
                    }
                });

                mMapBoxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        mMapCallbacks.onMapLongClick(point);
                    }
                });

                mMapBoxMap.setMyLocationEnabled(true);
                mMapCallbacks.onMapReady();

                setCameraPositionDefault();
            }
        });

    }

    private void setNavigationEngine() {
        mNavigation.setLocationEngine(mLocationProvider.getLocationEngine());
        mNavigation.addProgressChangeListener(this);
        mNavigation.addAlertLevelChangeListener(this);
        mNavigation.addNavigationEventListener(this);
        mNavigation.addOffRouteListener(this);
    }

    public void setAvailableParkSpaces(List<ParkingSpace> parkingSpaces) {
        mParkingSpacesMap = new HashMap<>();
        mMapBoxMap.clear();

        for (ParkingSpace parkingSpace : parkingSpaces) {
            mParkingSpacesMap.put(mMapBoxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude()))), parkingSpace);
        }
    }

    public void setCameraPositionDefault() {
        if (mMapBoxMap == null) {
            throw new RuntimeException("MapBoxMap cannot be null!");
        }

        if (mParkingSpacesMap != null && mParkingSpacesMap.size() > 0) {
            Set<LatLng> latLngs = new HashSet<>();
            latLngs.add(new LatLng(mLastLocation));
            for (Marker marker : mParkingSpacesMap.keySet()) {
                latLngs.add(marker.getPosition());
            }
            MapHelper.cameraIncludePositions(mMapBoxMap, 300, 1000, latLngs);

        } else if (mLastLocation != null) {
            MapHelper.easeCamera(mMapBoxMap, new LatLng(new LatLng(mLastLocation)), 16, 0, 45, 2000);
        }
    }

    public void startNavigation() {
        if (mRoute == null) {
            throw new RuntimeException("Cannot start navigation without route!");
        }
        //TODO remove
        ((MockLocationEngine) mLocationProvider.getLocationEngine()).setRoute(mRoute);
        mMapBoxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
        mNavigation.startNavigation(mRoute);
        mNavigating = true;
    }

    public void endNavigation() {
        mMapBoxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        mNavigation.endNavigation();
        mNavigating = false;
    }


    public void clearMap() {
        mParkingSpacesMap = null;
        mMapBoxMap.clear();
    }

    public void setRoute(DirectionsRoute route) {
        mRoute = route;
        mRouteLine = NavigationHelper.drawRoute(mMapBoxMap, mRouteLine, route);
    }

    public void calculateRoute(Position destination, CalculateRouteCallback readyCallback) {
        mDestination = destination;
        NavigationHelper.calculateRoute(mMapBoxMap, mNavigation, destination, readyCallback);
    }

    public ParkingSpace getDestinationParkingSpace() {
        return mParkingSpacesMap.get(mDestinationMarker);
    }

    public void eraseRouteLine() {
        if (mRouteLine != null && mMapBoxMap.getPolylines().contains(mRouteLine)) {
            mMapBoxMap.removePolyline(mRouteLine);
        }
        mRouteLine = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mNavigating) {
            MapHelper.easeCamera(mMapBoxMap, new LatLng(location), 60, location.getBearing(), 60, 100);
        } else {
            MapHelper.easeCamera(mMapBoxMap, new LatLng(location), 16, mMapBoxMap.getCameraPosition().bearing, 45, 1000);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mNavigation.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNavigationCallbacks = null;
        mLocPermissionResultCallbacks = null;
        mMapCallbacks = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        mNavigation.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNavigation.removeAlertLevelChangeListener(this);
        mNavigation.removeNavigationEventListener(this);
        mNavigation.removeProgressChangeListener(this);
        mNavigation.removeOffRouteListener(this);

        if (mLocationServiceBound) {
            getContext().unbindService(mLocationServiceConnection);
            mLocationProvider.removeListener(this);
            mLocationServiceBound = false;
        }

        mNavigation.endNavigation();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onAlertLevelChange(int alertLevel, RouteProgress routeProgress) {
        switch (alertLevel) {
            case NavigationConstants.ARRIVE_ALERT_LEVEL:
                mNavigationCallbacks.onArrived();
                break;
        }
    }

    @Override
    public void onRunning(boolean running) {

    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {

    }

    @Override
    public void userOffRoute(Location location) {
        mNavigationCallbacks.onUserOffRoute(mDestinationMarker);
    }

    public boolean isRouteSet() {
        return mRoute != null;
    }

    public boolean isNavigating() {
        return mNavigating;
    }

    public MapboxMap getMap() {
        return mMapBoxMap;
    }

    public MapboxNavigation getNavigation() {
        return mNavigation;
    }

    public Position getDestination() {
        return mDestination;
    }

    public DirectionsRoute getRoute() {
        return mRoute;
    }

    public Polyline getRouteLine() {
        return mRouteLine;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.PERMISSION_REQUEST_LOCATION:
                boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    PermissionHelper.requestLocationPermission(this);
                    mLocPermissionResultCallbacks.onDenied();
                    return;
                }
                mLocPermissionResultCallbacks.onGranted();
                break;
            default:
                // Ignored
        }
    }

    public interface LocalPermissionResultCallback {

        void onGranted();

        void onDenied();
    }

    interface MapActionCallback {

        void onMarkerClick(Marker marker);

        void onMapClick(LatLng point);

        void onMapLongClick(LatLng point);

        void onMapReady();
    }

    interface NavigationActionCallback {

        void onArrived();

        void onUserOffRoute(Marker marker);
    }

    public interface CalculateRouteCallback {

        void onRouteReady(DirectionsRoute route);
    }
}
