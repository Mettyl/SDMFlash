package com.sdm.sdmflash;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sdm.sdmflash.YourWordsFragment.YourWordsFragment;
import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.menu.AddWordFragment;
import com.sdm.sdmflash.menu.MainFragment;
import com.sdm.sdmflash.menu.StudyFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_wall, mainFragment, mainFragment.getTag()).commit();

        StringBuilder text = new StringBuilder();

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("en-cs.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                Scanner s = new Scanner(line).useDelimiter("\\t");
                if (s.hasNext()) {
                    Log.i(TAG, s.next() + " - " + s.next());

                }
                s.close();

            }
            br.close();
        } catch (
                IOException e) {
            e.printStackTrace();
            Log.i("debug", "error");

        }




        new DbTest().test(getApplicationContext());
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
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_home) {
            MainFragment mainFragment = new MainFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_wall, mainFragment, mainFragment.getTag()).commit();
        } else if (id == R.id.nav_study) {
            StudyFragment studyFragment = new StudyFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_wall, studyFragment, studyFragment.getTag()).commit();

        } else if (id == R.id.nav_games) {

        } else if (id == R.id.nav_add_word) {
            AddWordFragment addWordFragment = new AddWordFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_wall, addWordFragment, addWordFragment.getTag()).commit();

        } else if (id == R.id.nav_stats) {

        } else if (id == R.id.nav_your_words) {
            YourWordsFragment yourWordsFragment = new YourWordsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_wall, yourWordsFragment, yourWordsFragment.getTag()).commit();

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
