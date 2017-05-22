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
public class EntryViewFragment extends Fragment {

    private EntryViewActionCallbacks mCallbacks;

    public EntryViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbacks = (EntryViewActionCallbacks) getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_view, container, false);
        Button findSpaceButton = (Button) view.findViewById(R.id.find_space_button);
        findSpaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onFindSpaceAction();
            }
        });

        Button leaveSpaceButton = (Button) view.findViewById(R.id.leave_space_button);
        leaveSpaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onLeaveSpaceAction();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    interface EntryViewActionCallbacks {

        void onFindSpaceAction();

        void onLeaveSpaceAction();
    }
}
