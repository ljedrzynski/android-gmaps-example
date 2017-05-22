package pl.devone.ipark.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import pl.devone.ipark.R;
import pl.devone.ipark.activities.helpers.ActivityHelper;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.callbacks.AsyncTaskCallback;
import pl.devone.ipark.services.parkingspace.ParkingSpaceManager;
import pl.devone.ipark.services.parkingspace.callbacks.ParkingSpaceFetchCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        EntryViewFragment.EntryViewActionCallbacks,
        SearchViewFragment.SearchViewActionCallbacks {

    private View mView;
    private MapFragment mapFragment;

    public MainFragment() {
    }

    private void setView(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.view_container, fragment)
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setView(new EntryViewFragment());
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = (MapFragment)
                getChildFragmentManager().findFragmentById(R.id.fragment_map);

    }

    @Override
    public void onFindSpaceAction() {
        ParkingSpaceManager.getParkingSpaces(getContext(), mapFragment.mLastLocation, new ParkingSpaceFetchCallback() {
            @Override
            public void onSuccess(List<ParkingSpace> parkingSpaces) {
                mapFragment.markFreeParkingSpaces(parkingSpaces);
                setView(new SearchViewFragment());
                Toast.makeText(getContext(), "Znaleziono w okolicy " + parkingSpaces.size() + (parkingSpaces.size() > 1 ? "miejsc" : "miejsce"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onLeaveSpaceAction() {
        ParkingSpaceManager.createParkingSpace(getContext(), new ParkingSpace(mapFragment.mLastLocation, ActivityHelper.getUser(getContext())), new AsyncTaskCallback() {
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
        onFindSpaceAction();
    }
}
