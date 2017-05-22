package pl.devone.ipark.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.devone.ipark.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchViewFragment extends Fragment {


    public SearchViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_view, container, false);
    }

}
