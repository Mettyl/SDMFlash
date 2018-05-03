package com.sdm.sdmflash;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.sdm.sdmflash.databases.structure.dictionaryDatabase.CzWord;
import com.sdm.sdmflash.databases.structure.dictionaryDatabase.DictionaryDatabase;
import com.sdm.sdmflash.databases.structure.dictionaryDatabase.EnCzJoin;
import com.sdm.sdmflash.databases.structure.dictionaryDatabase.EnWord;
import com.sdm.sdmflash.fragmentFlashcards.FlashcardsFragment;
import com.sdm.sdmflash.fragmentStatistics.StatisticsFragment;
import com.sdm.sdmflash.fragmentTests.TestsFragment;
import com.sdm.sdmflash.fragmentYourWords.YourWordsFragment;
import com.sdm.sdmflash.menu.HomeFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "debug";
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private Fragment fragmentToSet = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);


        drawerLayout = findViewById(R.id.drawer_layout);
        frameLayout = findViewById(R.id.content_frame);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float moveFactor = 0;
                moveFactor = (drawerView.getWidth() * slideOffset);

                frameLayout.setTranslationX(moveFactor);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Set your new fragment here
                if (fragmentToSet != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragmentToSet)
                            .addToBackStack(fragmentToSet.toString())
                            .commit();
                    fragmentToSet = null;
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, homeFragment, homeFragment.getTag()).commit();

        //Načte slovník do databáze

        // loadDictionary();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.nav_home:
                fragmentToSet = new HomeFragment();
                break;
            case R.id.nav_flashcards:
                fragmentToSet = new FlashcardsFragment();
                break;
            case R.id.nav_tests:
                fragmentToSet = new TestsFragment();
                break;
            case R.id.nav_stats:
                fragmentToSet = new StatisticsFragment();
                break;
            case R.id.nav_your_words:
                fragmentToSet = new YourWordsFragment();
                break;
        }
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public FrameLayout getFrameLayout() {
        return frameLayout;
    }
}
