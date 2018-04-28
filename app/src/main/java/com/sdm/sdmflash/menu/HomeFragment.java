package com.sdm.sdmflash.menu;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.camera.activities.CameraActivity;
import com.sdm.sdmflash.fragmentFlashcards.FlashcardsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.ocr_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CameraActivity.class));
            }
        });

        view.findViewById(R.id.add_word_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(container.getId(), new FlashcardsFragment())
                        .commit();
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_home);
        toolbar.setTitle("Home");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
    }

}
