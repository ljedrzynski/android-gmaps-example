package pl.devone.ipark.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pl.devone.ipark.R;
import pl.devone.ipark.activities.RegisterActivity;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.callback.AsyncTaskCallback;
import pl.devone.ipark.services.parkingspace.ParkingSpaceManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        EntryViewFragment.EntryViewActionCallbacks {

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
        setView(new SearchViewFragment());
    }

    @Override
    public void onLeaveSpaceAction() {
        ParkingSpaceManager.createParkingSpace(getContext(), new ParkingSpace(mapFragment.mLastLocation), new AsyncTaskCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Zarjestrowane!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
