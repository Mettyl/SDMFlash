package com.sdm.sdmflash.menu;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.camera.activities.CameraActivity;
import com.sdm.sdmflash.fragmentAddWord.AddWordFragment;
import com.sdm.sdmflash.fragmentAddWord.AddWordFromText;
import com.sdm.sdmflash.fragmentStatistics.StatisticsFragment;
import com.sdm.sdmflash.fragmentTests.TestsFragment;

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

        Toolbar toolbar = view.findViewById(R.id.toolbar_home);
        toolbar.setTitle("Home");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        final DrawerLayout drawerLayout = ((MainActivity) activity).getDrawerLayout();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        view.findViewById(R.id.add_word_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CameraActivity.class));
            }
        });

        view.findViewById(R.id.daily_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(container.getId(), new TestsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.fragment_home_type_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddWordFromText.class));
            }
        });

        view.findViewById(R.id.fragment_home_graph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(container.getId(), new StatisticsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

}
