package pl.devone.ipark.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;

import java.util.List;

import pl.devone.ipark.R;
import pl.devone.ipark.activities.helpers.CommonHelper;
import pl.devone.ipark.fragments.helpers.LocationHelper;
import pl.devone.ipark.fragments.helpers.MapHelper;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.callbacks.AsyncTaskCallback;
import pl.devone.ipark.services.parkingspace.ParkingSpaceManager;
import pl.devone.ipark.services.parkingspace.callbacks.ParkingSpaceFetchCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        EntryViewFragment.EntryViewActionCallbacks,
        SearchViewFragment.SearchViewActionCallbacks,
        MapBoxFragment.MapActionCallback {

    private MapBoxFragment mMapFragment;
    private EntryViewFragment mEntryViewFragment;
    private SearchViewFragment mSearchViewFragment;


    public MainFragment() {
    }

    private Fragment setView(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.view_container, fragment)
                .commit();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = (MapBoxFragment)
                getChildFragmentManager().findFragmentById(R.id.fragment_map);

    }

    @Override
    public void onFindSpaceAction() {
        ParkingSpaceManager.getParkingSpaces(getContext(), mMapFragment.getLastLocation(), new ParkingSpaceFetchCallback() {
            @Override
            public void onSuccess(List<ParkingSpace> parkingSpaces) {
                if (parkingSpaces == null || parkingSpaces.size() == 0) {
                    Toast.makeText(getContext(), "Brak wolnych miejsc w okolicy! ", Toast.LENGTH_LONG).show();
                    return;
                }
                mSearchViewFragment = (SearchViewFragment) setView(new SearchViewFragment());
                mMapFragment.markFreeParkingSpaces(parkingSpaces);
                Toast.makeText(getContext(), "Znaleziono " + parkingSpaces.size() + (parkingSpaces.size() > 1 ? " miejsc" : "miejsce"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onLeaveSpaceAction() {
        ParkingSpaceManager.createParkingSpace(getContext(), new ParkingSpace(mMapFragment.getLastLocation(), CommonHelper.getUser(getContext())), new AsyncTaskCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Zarejestrowane!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onSearchContinueAction() {
        if (mMapFragment.isNavigating()) {
            mMapFragment.endNavigation();
        }
        onFindSpaceAction();
    }

    @Override
    public void onNavigate() {
        if (!mMapFragment.isRouteSet()) {
        }
        mSearchViewFragment.setNavigationButtonVisibility(View.INVISIBLE);
        mMapFragment.startNavigation();
    }

    @Override
    public void onMapReady() {
        mEntryViewFragment = (EntryViewFragment) setView(new EntryViewFragment());
    }

    @Override
    public void onMapClick(LatLng point) {
        if (mSearchViewFragment != null) {
            mSearchViewFragment.setNavigationButtonVisibility(View.INVISIBLE);
        }
        mMapFragment.eraseRouteLine();
    }

    @Override
    public void onMarkerClick(Marker marker) {
        mMapFragment.calculateRoute(LocationHelper.latLngToPosition(marker.getPosition()), new MapBoxFragment.NavigationActionCallback() {
            @Override
            public void onRouteReady(DirectionsRoute route) {
                mMapFragment.setRoute(route);
                mSearchViewFragment.setNavigationButtonVisibility(View.VISIBLE);
                MapHelper.moveCameraToBounds(mMapFragment.getMap(), 300, 2000, mMapFragment.getRouteLine().getPoints());
            }
        });
    }
}
