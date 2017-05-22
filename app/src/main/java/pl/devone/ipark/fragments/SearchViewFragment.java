package pl.devone.ipark.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.devone.ipark.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchViewFragment extends Fragment {

    SearchViewActionCallbacks mCallbacks;

    public SearchViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbacks = (SearchViewActionCallbacks) getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_view, container, false);

        Button searchContinueButton = (Button) view.findViewById(R.id.search_continue_button);
        searchContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onSearchContinueAction();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    interface SearchViewActionCallbacks {

        void onSearchContinueAction();
    }
}
