package com.sdm.sdmflash.menu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sdm.sdmflash.study.flashcards.FlashCardsActivity;
import com.sdm.sdmflash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudyFragment extends Fragment {


    public StudyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        Button flashcardsButton = view.findViewById(R.id.flashcards_button);
        flashcardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FlashCardsActivity.class));
            }
        });

        return view;
    }

}
