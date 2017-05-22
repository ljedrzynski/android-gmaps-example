package pl.devone.ipark.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.devone.ipark.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        EntryViewFragment.EntryViewActionCallbacks {

    private View mView;
    private MapFragment mapFragment;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.fragment_map);

        setView(new EntryViewFragment());
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
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        return mView;
    }

    @Override
    public void onFindSpaceAction() {
        setView(new SearchViewFragment());
    }

    @Override
    public void onLeaveSpaceAction() {
    }
}
