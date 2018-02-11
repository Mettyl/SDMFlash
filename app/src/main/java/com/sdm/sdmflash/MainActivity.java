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
import com.sdm.sdmflash.db.structure.CzWord;
import com.sdm.sdmflash.db.structure.DictionaryDatabase;
import com.sdm.sdmflash.db.structure.EnCzJoin;
import com.sdm.sdmflash.db.structure.EnWord;
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

        new DbTest().test(getApplicationContext());

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

    private void loadDictionary() {
        StringBuilder text = new StringBuilder();
        //K omezení počtu načtených slov při testování
        int i = 0;
        try {
            //Načte soubor z assets složky
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("en-cs.txt"), "UTF-8"));
            Scanner scanner = null;
            //Instance databáze
            DictionaryDatabase database = DictionaryDatabase.getInstance(getApplicationContext());
            String line;
            while ((line = br.readLine()) != null && i < 220000) {
                text.append(line);
                text.append('\n');

                //Rozdělí soubor podle tabulátoru
                scanner = new Scanner(line).useDelimiter("\\t");
                //Kontrola jestli jsou na řádku dvě slova (často nejsou)
                if (scanner.hasNext()) {
                    String enWord = scanner.next();
                    if (scanner.hasNext()) {
                        String czWord = scanner.next();
                        //Kontrola jestli dané slovo už v databázi není
                        int enId = database.enWordDao().findIdByWord(enWord);
                        if (enId == 0) {
                            //Pokud ne, přidá se nové slovo do databáze a vrátí se jeho id
                            database.enWordDao().insertAll(new EnWord(enWord));
                            enId = database.enWordDao().findIdByWord(enWord);
                        }
                        int czId = database.czWordDao().findIdByWord(czWord);
                        if (czId == 0) {
                            database.czWordDao().insertAll(new CzWord(czWord));
                            czId = database.czWordDao().findIdByWord(czWord);
                        }
                        //Vytvoření relací mezi slovami
                        final EnCzJoin join = new EnCzJoin(enId, czId);
                        database.enCzJoinDao().insert(join);
                    }
                }
                if (i % 10000 == 0) {
                    Log.i(TAG, "Načteno: " + i);
                }
                i++;
            }
            Log.i(TAG, "Načítaní dokončeno");

            scanner.close();
            br.close();
        } catch (
                IOException e) {
            e.printStackTrace();
            Log.i("debug", "Chyba při čtení ze souboru");
        }
    }
}
