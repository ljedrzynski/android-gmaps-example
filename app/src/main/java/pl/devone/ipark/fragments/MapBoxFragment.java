package pl.devone.ipark.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.models.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.devone.ipark.MockLocationEngine;
import pl.devone.ipark.R;
import pl.devone.ipark.fragments.helpers.MapHelper;
import pl.devone.ipark.fragments.helpers.NavigationHelper;
import pl.devone.ipark.fragments.helpers.PermissionHelper;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.authentication.AuthenticationManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapBoxFragment extends Fragment implements NavigationEventListener, ProgressChangeListener, AlertLevelChangeListener, OffRouteListener {

    //Map related
    private View mView;
    private MapView mMapView;
    private MapboxMap mMapBoxMap;
    private MockLocationEngine mLocationEngine;
    private LocationEngineListener mLocationEngineListener;
    private MapBoxFragment.MapActionCallback mMapCallbacks;
    private MapBoxFragment.NavigationActionCallback mNavigationCallbacks;

    //Navigation related
    private MapboxNavigation mNavigation;
    private Position mDestination;
    private DirectionsRoute mRoute;
    private Polyline mRouteLine;
    private boolean mNavigating;
    private Marker mDestinationMarker;
    private Location mLastLocation;

    private Map<Marker, ParkingSpace> mParkingSpacesMap;

    public MapBoxFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_token));

        mMapCallbacks = (MapBoxFragment.MapActionCallback) getParentFragment();
        mNavigationCallbacks = (MapBoxFragment.NavigationActionCallback) getParentFragment();

        //MOCK
        mLocationEngine = new MockLocationEngine();
//        mLocationEngine = LocationSource.getLocationEngine(getContext());52.290842, 21.115158   52.290842	21.115158

        mLocationEngine.mockLocation(Position.fromCoordinates(21.115158, 52.290842));
        mLocationEngine.setInterval(0);
        mLocationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        mLocationEngine.setFastestInterval(1000);
        mLocationEngine.activate();

        mNavigation = new MapboxNavigation(getContext(), Mapbox.getAccessToken());
        mNavigation.setLocationEngine(mLocationEngine);
        initNavigationListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_box, container, false);

        mMapView = (MapView) mView.findViewById(R.id.mapBoxView);
        mMapView.onCreate(savedInstanceState);
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

                initLocationListener();

                mMapCallbacks.onMapReady();
            }
        });

        return mView;
    }

    private void initNavigationListeners() {
        mNavigation.addProgressChangeListener(this);
        mNavigation.addAlertLevelChangeListener(this);
        mNavigation.addNavigationEventListener(this);
        mNavigation.addOffRouteListener(this);
    }

    private void initLocationListener() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.requestLocationPermission(this);
        } else {
            mMapBoxMap.setMyLocationEnabled(true);

            mLastLocation = mLocationEngine.getLastLocation();

            setCameraPositionDefault();

            mLocationEngineListener = new LocationEngineListener() {
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
                        if (mNavigating) {
                            MapHelper.moveCamera(mMapBoxMap, new LatLng(location), 60, location.getBearing(), 60, 100);
                        } else {
                            MapHelper.moveCamera(mMapBoxMap, new LatLng(location), 16, mMapBoxMap.getCameraPosition().bearing, 45, 1000);
                        }
                    }
                }
            };

            mLocationEngine.addLocationEngineListener(mLocationEngineListener);
        }
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

            MapHelper.moveCameraToBounds(mMapBoxMap, 300, 1000, latLngs);
        } else if (mLastLocation != null) {
            MapHelper.moveCamera(mMapBoxMap, new LatLng(new LatLng(mLastLocation)), 16, 0, 45, 2000);
        }
    }

    public void clearMap() {
        mParkingSpacesMap = null;
        mMapBoxMap.clear();
    }

    public void startNavigation() {
        if (mRoute == null) {
            throw new RuntimeException("Cannot start navigation without route!");
        }
        //TODO remove
        mLocationEngine.activate();
        mLocationEngine.setRoute(mRoute);
        mMapBoxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
        mNavigation.startNavigation(mRoute);
        mNavigating = true;
    }

    public void endNavigation() {
        mMapBoxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        mNavigation.endNavigation();
        mNavigating = false;
    }

    public void calculateRoute(Position destination, CalculateRouteCallback readyCallback) {
        mDestination = destination;
        NavigationHelper.calculateRoute(mMapBoxMap, mNavigation, destination, readyCallback);
    }

    public void setRoute(DirectionsRoute route) {
        mRoute = route;
        mRouteLine = NavigationHelper.drawRoute(mMapBoxMap, mRouteLine, route);
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
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        mNavigation.onStart();
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
    public void onStop() {
        super.onStop();

        mMapView.onStop();
        mMapView.onDestroy();

        mNavigation.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endNavigation();

        mMapCallbacks = null;

        mNavigation.removeAlertLevelChangeListener(this);
        mNavigation.removeNavigationEventListener(this);
        mNavigation.removeProgressChangeListener(this);
        mNavigation.removeOffRouteListener(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.PERMISSION_REQUEST_LOCATION:
                boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                mMapBoxMap.setMyLocationEnabled(granted);
                if (!granted) {
                    AuthenticationManager.signOut(getActivity());
                    Toast.makeText(getContext(), R.string.error_location_permission_denied, Toast.LENGTH_LONG).show();
                    return;
                }
                initLocationListener();
                break;
            default:
                // Ignored
        }
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
