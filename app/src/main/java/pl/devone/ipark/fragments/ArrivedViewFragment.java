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
public class ArrivedViewFragment extends Fragment {

    Button mOccupyingButton;
    Button mOccupiedButton;

    ArrivedViewActionCallbacks mCallbacks;

    public ArrivedViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbacks = (ArrivedViewActionCallbacks) getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arrived_view, container, false);

        mOccupyingButton = (Button) view.findViewById(R.id.occupying_button);
        mOccupyingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onOccupying();
            }
        });

        mOccupiedButton = (Button) view.findViewById(R.id.occupied_button);
        mOccupiedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onOccupied();
            }
        });

        return view;
    }

    interface ArrivedViewActionCallbacks {

        void onOccupying();

        void onOccupied();
    }
}
