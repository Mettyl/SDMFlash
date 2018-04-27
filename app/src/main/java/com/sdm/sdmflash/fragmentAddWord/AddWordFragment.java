package com.sdm.sdmflash.fragmentAddWord;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdm.sdmflash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddWordFragment extends Fragment implements CardAdapter.OnCardClickListener {


    public AddWordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_word, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_add_word);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        activity.setSupportActionBar(toolbar);

        toolbar.setTitle("Add word");


        RecyclerView recyclerView = view.findViewById(R.id.add_word_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new CardAdapter(getContext(), this));

        return view;
    }


    /**
     * Open activities acording to card click
     *
     * @param position card number
     */
    @Override
    public void onClick(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(getContext(), AddWordFromText.class);
                startActivity(intent);
                break;
        }
    }
}
