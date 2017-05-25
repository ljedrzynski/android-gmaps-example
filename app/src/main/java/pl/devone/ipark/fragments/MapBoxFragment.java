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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import com.mapbox.services.android.navigation.v5.MapboxNavigation;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;

import java.util.List;

import pl.devone.ipark.R;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.authentication.AuthenticationManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapBoxFragment extends Fragment {

    private View mView;
    private MapView mMapView;
    private MapboxMap mMapBoxMap;
    private LocationEngine mLocationEngine;
    private LocationEngineListener mLocationEngineListener;
    private MapBoxFragment.MapActionCallback mCallbacks;
    private MapboxNavigation mNavigation;

    Location mLastLocation;

    public static final int PERMISSION_REQUEST_LOCATION = 99;

    public MapBoxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_token));

        mNavigation = new MapboxNavigation(getContext(), Mapbox.getAccessToken());

        mCallbacks = (MapBoxFragment.MapActionCallback) getParentFragment();

        mLocationEngine = LocationSource.getLocationEngine(getContext());

        mLocationEngine.activate();
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
                mMapBoxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        moveCamera(marker.getPosition(), 16, 0, 45);
                        mCallbacks.onMarkerClick();
                        return true;
                    }
                });

                initLocationListener();
            }
        });

        return mView;
    }

    private void moveCamera(LatLng latLng, float zoom, float bearing, float tilt) {
        mMapBoxMap.easeCamera(CameraUpdateFactory.newCameraPosition(
                (new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build())));
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
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
    }

    private void initLocationListener() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();

        } else {
            mLastLocation = mLocationEngine.getLastLocation();

            if (mLastLocation != null) {
                mMapBoxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation), 16));
            }

            mLocationEngineListener = new LocationEngineListener() {
                @Override
                public void onConnected() {
                    // No action needed here.
                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        mLastLocation = location;
                        moveCamera(new LatLng(location), 16, 0, 45);
                    }
                }
            };
            mLocationEngine.addLocationEngineListener(mLocationEngineListener);
            mMapBoxMap.setMyLocationEnabled(true);
        }
    }

    public void markFreeParkingSpaces(List<ParkingSpace> parkingSpaces) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (ParkingSpace parkingSpace : parkingSpaces) {
            Marker marker = mMapBoxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude())));
            builder.include(marker.getPosition());
        }

        builder.include(new LatLng(mLastLocation));

        mMapBoxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200), 3000);
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        mMapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mCallbacks = null;
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

        void onMarkerClick();

    }
}
