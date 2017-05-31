package pl.devone.ipark.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.style.layers.Property;
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
        ArrivedViewFragment.ArrivedViewActionCallbacks,
        MapBoxFragment.MapActionCallback,
        MapBoxFragment.NavigationActionCallback {

    private MapBoxFragment mMapFragment;
    private View mView;


    public MainFragment() {
    }

    private Fragment setView(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.view_container, fragment)
                .commit();
        return fragment;
    }

    private Fragment getCurrentView() {
        return getChildFragmentManager().findFragmentById(R.id.view_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = (MapBoxFragment)
                getChildFragmentManager().findFragmentById(R.id.fragment_map);

    }

    private void calculateRoute(Marker marker) {
        mMapFragment.calculateRoute(LocationHelper.positionFromLatLng(marker.getPosition()), new MapBoxFragment.CalculateRouteCallback() {
            @Override
            public void onRouteReady(DirectionsRoute route) {
                mMapFragment.setRoute(route);
                ((SearchViewFragment) getCurrentView()).setNavigationButtonVisibility(View.VISIBLE);
                MapHelper.moveCameraToBounds(mMapFragment.getMap(), 300, 2000, mMapFragment.getRouteLine().getPoints());
            }
        });
    }

    private void updateParkingSpaceStatus(ParkingSpace parkingSpace, AsyncTaskCallback callback) {
        ParkingSpaceManager.updateParkingSpace(getContext(), parkingSpace, callback);
    }

    @Override
    public void onFindSpaceAction() {
        //TODO set radius in settings
        ParkingSpaceManager.getParkingSpaces(getContext(), mMapFragment.getLastLocation(), 1, new ParkingSpaceFetchCallback() {
            @Override
            public void onSuccess(List<ParkingSpace> parkingSpaces) {
                mMapFragment.clearMap();
                if (parkingSpaces == null || parkingSpaces.size() == 0) {
                    Toast.makeText(getContext(), "Brak wolnych miejsc w okolicy! ", Toast.LENGTH_LONG).show();
                    return;
                }
                setView(new SearchViewFragment());
                mMapFragment.setAvailableParkSpaces(parkingSpaces);
                mMapFragment.setCameraPositionDefault();
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
    public void onOccupying() {
        ParkingSpace parkingSpace = mMapFragment.getDestinationParkingSpace();
        parkingSpace.setLastOccupierId(parkingSpace.getCurrOccupierId());
        parkingSpace.setCurrOccupierId(CommonHelper.getUser(getContext()).getId());
        parkingSpace.setOccupied(true);
        updateParkingSpaceStatus(parkingSpace, new AsyncTaskCallback() {
            @Override
            public void onSuccess() {
                CommonHelper.goBackground(getContext());
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
    public void onOccupied() {
        ParkingSpace parkingSpace = mMapFragment.getDestinationParkingSpace();
        parkingSpace.setLastOccupierId(parkingSpace.getCurrOccupierId());
        parkingSpace.setCurrOccupierId(null);
        parkingSpace.setOccupied(true);
        updateParkingSpaceStatus(parkingSpace, new AsyncTaskCallback() {
            @Override
            public void onSuccess() {
                setView(new SearchViewFragment());
            }

            @Override
            public void onFailure() {
                //TODO
            }

            @Override
            public void onError(String error) {
                //TODO
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
            //TODO
        }
        ((SearchViewFragment) getCurrentView()).setNavigationButtonVisibility(View.INVISIBLE);
        mMapFragment.startNavigation();
    }

    @Override
    public void onMapReady() {
        setView(new EntryViewFragment());
    }

    @Override
    public void onMapClick(LatLng point) {
        Fragment currentView = getCurrentView();
        if (currentView instanceof SearchViewFragment) {
            ((SearchViewFragment) currentView).setNavigationButtonVisibility(View.INVISIBLE);
        }
        if (currentView instanceof ArrivedViewFragment) {
            setView(new EntryViewFragment());
        }
        mMapFragment.eraseRouteLine();
        mMapFragment.setCameraPositionDefault();
    }

    @Override
    public void onMarkerClick(Marker marker) {
        mMapFragment.eraseRouteLine();

        if (LocationHelper.isLocationInRadius(new LatLng(mMapFragment.getLastLocation()), marker.getPosition(), 0.1)) {
            MapHelper.moveCameraToBounds(mMapFragment.getMap(), 300, 2000, new LatLng(mMapFragment.getLastLocation()), marker.getPosition());
            setView(new ArrivedViewFragment());
        } else {
            if (!(getCurrentView() instanceof SearchViewFragment)) {
                setView(new SearchViewFragment().setNavigationButtonVisibility(View.VISIBLE));
            }
            calculateRoute(marker);
        }
    }

    @Override
    public void onArrived() {
        mMapFragment.endNavigation();
        mMapFragment.eraseRouteLine();
        setView(new ArrivedViewFragment());
    }

    @Override
    public void onUserOffRoute(Marker marker) {
        calculateRoute(marker);
    }
}
