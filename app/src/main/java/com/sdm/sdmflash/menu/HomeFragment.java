package com.sdm.sdmflash.menu;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;
import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.camera.activities.CameraActivity;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.databases.structure.appDatabase.StudyChartEntry;
import com.sdm.sdmflash.databases.structure.appDatabase.TestChartEntry;
import com.sdm.sdmflash.fragmentAddWord.AddWordFromText;
import com.sdm.sdmflash.fragmentStatistics.StatisticsFragment;
import com.sdm.sdmflash.fragmentTests.TestsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        // nastaveni combined chartu
        final LineChart lineChart = view.findViewById(R.id.statistics_usage_chart_home);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        // animate both axes with easing
        lineChart.animateXY(2000, 2000, Easing.EasingOption.EaseOutBack, Easing.EasingOption.Linear);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value + " min";
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        final String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int i = (day - 6) + (int) value;
                if (i < 0) {
                    return days[7 + i];
                } else {
                    return days[i];
                }
            }
        });

        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {

                List<TestChartEntry> testList = AppDatabase.getInstance(getContext()).testChartDao().getFromWeek(new Date().getTime() - 518400000);
                List<StudyChartEntry> studyList = AppDatabase.getInstance(getContext()).studyChartDao().getFromWeek(new Date().getTime() - 518400000);


                ArrayList<Entry> entriesTest = new ArrayList<Entry>();
                ArrayList<Entry> entriesStudy = new ArrayList<Entry>();

                float[] testPole = countTestMinutesInDays(testList);
                float[] studyPole = countStudyMinutesInDays(studyList);

                for (int index = 0; index < 7; index++) {
                    entriesTest.add(new Entry(index, testPole[index]));
                    entriesStudy.add(new Entry(index, studyPole[index]));
                }

                LineDataSet set1 = new LineDataSet(entriesStudy, "Study time");
                set1.setColor(Color.BLUE);
                set1.setLineWidth(2.5f);
                set1.setCircleColor(Color.BLUE);
                set1.setCircleRadius(5f);
                set1.setFillColor(Color.BLUE);
                set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                set1.setDrawValues(true);

                set1.setDrawFilled(true);
                if (Utils.getSDKInt() >= 18) {

                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient_blue);
                    set1.setFillDrawable(drawable);
                } else {
                    set1.setFillColor(Color.BLUE);
                }

                LineDataSet set2 = new LineDataSet(entriesTest, "Test time");
                set2.setColor(Color.RED);
                set2.setLineWidth(2.5f);
                set2.setCircleColor(Color.RED);
                set2.setCircleRadius(5f);
                set2.setFillColor(Color.RED);
                set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                set2.setDrawValues(true);

                set2.setDrawFilled(true);
                if (Utils.getSDKInt() >= 18) {

                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient_red);
                    set2.setFillDrawable(drawable);
                } else {
                    set2.setFillColor(Color.RED);
                }


                final LineData lineData = new LineData();
                lineData.addDataSet(set1);
                lineData.addDataSet(set2);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lineChart.setData(lineData);
                        lineChart.invalidate();

                    }
                });
            }
        });


        return view;
    }

    public float[] countTestMinutesInDays(List<TestChartEntry> testChartEntryList) {

        float[] data = new float[7];

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date());

        float value = 0;

        for (int l = 0; l < 7; l++) {

            for (int j = 0; j < testChartEntryList.size(); j++) {

                cal2.setTime(testChartEntryList.get(j).getEndTest());

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        (cal1.get(Calendar.DAY_OF_YEAR) - 6 + l) == cal2.get(Calendar.DAY_OF_YEAR)) {

                    value += ((testChartEntryList.get(j).getEndTest().getTime() - testChartEntryList.get(j).getStartTest().getTime()) / 1000.0f) / 60.0f;
                }
            }
            data[l] = value;
            value = 0;
        }
        return data;

    }

    public float[] countStudyMinutesInDays(List<StudyChartEntry> studyChartEntryList) {

        float[] data = new float[7];

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date());

        float value = 0;

        for (int l = 0; l < 7; l++) {

            for (int j = 0; j < studyChartEntryList.size(); j++) {

                cal2.setTime(studyChartEntryList.get(j).getEndStudy());

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        (cal1.get(Calendar.DAY_OF_YEAR) - 6 + l) == cal2.get(Calendar.DAY_OF_YEAR)) {

                    value += ((studyChartEntryList.get(j).getEndStudy().getTime() - studyChartEntryList.get(j).getStartStudy().getTime()) / 1000.0f) / 60.0f;
                }
            }
            data[l] = value;
            value = 0;
        }
        return data;

    }

}
