package com.sdm.sdmflash.menu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.camera.CameraActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.ocr_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CameraActivity.class));
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_home);
        toolbar.setTitle("Home");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
    }

}
