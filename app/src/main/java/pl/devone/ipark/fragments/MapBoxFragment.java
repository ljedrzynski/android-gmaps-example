package pl.devone.ipark.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.v5.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.RouteProgress;
import com.mapbox.services.android.navigation.v5.listeners.AlertLevelChangeListener;
import com.mapbox.services.android.navigation.v5.listeners.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.listeners.OffRouteListener;
import com.mapbox.services.android.navigation.v5.listeners.ProgressChangeListener;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.models.Position;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.devone.ipark.MockLocationEngine;
import pl.devone.ipark.R;
import pl.devone.ipark.fragments.helpers.MapHelper;
import pl.devone.ipark.fragments.helpers.NavigationHelper;
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
    private LocationEngine mLocationEngine;
    private LocationEngineListener mLocationEngineListener;
    private MapBoxFragment.MapActionCallback mCallbacks;

    //Navigation related
    private MapboxNavigation mNavigation;
    private Position mDestination;
    private DirectionsRoute mRoute;
    private Polyline mRouteLine;
    private boolean mNavigating;
    private Marker destinationMarker;

    private Location mLastLocation;

    public static final int PERMISSION_REQUEST_LOCATION = 99;

    public MapBoxFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_token));

        mCallbacks = (MapBoxFragment.MapActionCallback) getParentFragment();

        //MOCK
        mLocationEngine = new MockLocationEngine();
//        mLocationEngine = LocationSource.getLocationEngine(getContext());
        ((MockLocationEngine) mLocationEngine).mockLocation(Position.fromCoordinates(21.115555, 52.2930));
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

                mMapBoxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (marker == destinationMarker) {
                            return false;
                        }
                        destinationMarker = marker;
                        mCallbacks.onMarkerClick(marker);
                        return true;
                    }
                });
                mMapBoxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        destinationMarker = null;
                        mCallbacks.onMapClick(point);
                    }
                });

                initLocationListener();
                mCallbacks.onMapReady();
            }
        });

        return mView;
    }

    private void initNavigationListeners() {
        mNavigation.addNavigationEventListener(new NavigationEventListener() {
            @Override
            public void onRunning(boolean running) {

            }
        });
        mNavigation.addProgressChangeListener(new ProgressChangeListener() {
            @Override
            public void onProgressChange(Location location, RouteProgress routeProgress) {
//                NavigationHelper.setRoute(mMapBoxMap, mRouteLine, routeProgress.getRoute());
            }
        });
        mNavigation.addAlertLevelChangeListener(new AlertLevelChangeListener() {
            @Override
            public void onAlertLevelChange(int alertLevel, RouteProgress routeProgress) {
                //TODO
            }
        });
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getParentFragment().getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.title_location_permission))
                    .setMessage(getString(R.string.info_location_permission))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    private void initLocationListener() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            mLastLocation = mLocationEngine.getLastLocation();

            if (mLastLocation != null) {
                MapHelper.moveCamera(mMapBoxMap, new LatLng(new LatLng(mLastLocation)), 16, 0, 45, 3000);
            }

            mLocationEngineListener = new LocationEngineListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
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
            mMapBoxMap.setMyLocationEnabled(true);
        }
    }

    public void markFreeParkingSpaces(List<ParkingSpace> parkingSpaces) {
        Set<LatLng> locations = new HashSet<>();
        mMapBoxMap.clear();

        for (ParkingSpace parkingSpace : parkingSpaces) {
            LatLng location = new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude());
            mMapBoxMap.addMarker(new MarkerOptions()
                    .position(location));
            locations.add(location);
        }
        locations.add(new LatLng(mLastLocation));

        MapHelper.moveCameraToBounds(mMapBoxMap, locations);
    }

    public void startNavigation() {
        if (mRoute == null) {
            throw new RuntimeException("Cannot start navigation without route!");
        }
        //TODO remove
        ((MockLocationEngine) mLocationEngine).setRoute(mRoute);
        MapBoxFragment.this.mNavigation.startNavigation(mRoute);
        mNavigating = true;
    }

    public void endNavigation() {
        mNavigation.endNavigation();
        mNavigating = false;
    }

    public void calculateRoute(Position destination, NavigationActionCallback readyCallback) {
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

    public void eraseRouteLine() {
        mMapBoxMap.removePolyline(mRouteLine);
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
        mCallbacks = null;
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

    }

    @Override
    public void onRunning(boolean running) {

    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {

    }

    @Override
    public void userOffRoute(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
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

        void onMapReady();

    }

    public interface NavigationActionCallback {

        void onRouteReady(DirectionsRoute route);

    }
}
