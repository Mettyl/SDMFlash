package com.sdm.sdmflash.fragmentTests;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sdm.sdmflash.fragmentFlashcards.FlashCardsActivity;
import com.sdm.sdmflash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestsFragment extends Fragment {


    public TestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tests, container, false);


        return view;
    }

}
