package com.sdm.sdmflash.fragmentStatistics;


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
import android.util.Log;
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
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.databases.structure.appDatabase.StudyChartEntry;
import com.sdm.sdmflash.databases.structure.appDatabase.TestChartEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private LineChart lineChart;

    public StatisticsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // nastaveni toolbaru
        Toolbar toolbar = view.findViewById(R.id.toolbar_statistics);
        toolbar.setTitle("Statistics");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        final DrawerLayout drawerLayout = ((MainActivity) activity).getDrawerLayout();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);


        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<TestChartEntry> list = AppDatabase.getInstance(getContext()).testChartDao().getFromWeek();
                Log.i("debug", "list " + list.size());
            }
        });


        toggle.syncState();
        List<TestChartEntry> testChartEntryList = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-4 10:15:25"), df.parse("20012-10-4 10:30:25")));
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-4 14:00:25"), df.parse("20012-10-4 15:04:25")));
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-5 10:15:25"), df.parse("20012-10-5 10:30:25")));
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-5 10:15:25"), df.parse("20012-10-5 10:30:25")));
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-5 10:15:25"), df.parse("20012-10-5 10:30:25")));
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-9 10:15:25"), df.parse("20012-10-9 10:30:25")));
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-9 10:15:25"), df.parse("20012-10-9 10:30:25")));
            testChartEntryList.add(new TestChartEntry(2, 1, 6, df.parse("20012-10-10 10:15:25"), df.parse("20012-10-10 10:30:25")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<StudyChartEntry> studyChartEntryList = new ArrayList<>();
        studyChartEntryList.add(new StudyChartEntry(new Date(1420200200000L), new Date(1420204200000L)));
        studyChartEntryList.add(new StudyChartEntry(new Date(1420300200000L), new Date(1420306200000L)));
        studyChartEntryList.add(new StudyChartEntry(new Date(1420403200000L), new Date(1420412200000L)));
        studyChartEntryList.add(new StudyChartEntry(new Date(1420501200000L), new Date(1420504200000L)));
        studyChartEntryList.add(new StudyChartEntry(new Date(1420600200000L), new Date(1420603200000L)));
        studyChartEntryList.add(new StudyChartEntry(new Date(1420600200000L), new Date(1420603200000L)));
        studyChartEntryList.add(new StudyChartEntry(new Date(1420600200000L), new Date(1420603200000L)));


        // nastaveni combined chartu
        lineChart = view.findViewById(R.id.statistics_chart);
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
                return (int) value + " min";
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


        int[] data = new int[7];
        int i = -1;
        int value = 0;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(testChartEntryList.get(0).getEndTest());

        for (int l = 0; l < testChartEntryList.size(); l++) {

            cal2.setTime(testChartEntryList.get(l).getEndTest());

            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {

                Log.i("debug", "same day " + l);

                value += (int) (((testChartEntryList.get(l).getEndTest().getTime() - testChartEntryList.get(l).getStartTest().getTime()) / 1000) / 60);

                Log.i("debug", " one value " + (int) (((testChartEntryList.get(l).getEndTest().getTime() - testChartEntryList.get(l).getStartTest().getTime()) / 1000) / 60));

            } else {

                i++;

                if (i > 6) {
                    Log.i("debug", "Unexpected");
                    break;
                }

                Log.i("debug", "final value " + value);
                Log.i("debug", "i: " + i);

                data[i] = value;
                value = 0;

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        Math.abs(cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR)) > 1) {

                    Log.i("debug", "First day " + cal1.get(Calendar.DAY_OF_YEAR));
                    Log.i("debug", "Second day " + cal2.get(Calendar.DAY_OF_YEAR));

                    int difference = Math.abs(cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR));
                    for (int d = 0; d < difference - 1; d++) {
                        i++;
                        data[i] = 0;
                    }
                }

                cal1.setTime(testChartEntryList.get(l).getEndTest());
                l--;

            }
            if (l == testChartEntryList.size() - 1) {
                data[data.length - 1] = value;
            }
        }


        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 7; index++) {

            if (index < testChartEntryList.size()) {
                entries.add(new Entry(index, data[index]));
            } else {
                entries.add(new Entry(index, 0));
            }
        }

        LineDataSet set1 = new LineDataSet(entries, "Study time");
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

        ArrayList<Entry> entries2 = new ArrayList<Entry>();

        for (int index = 0; index < 7; index++) {

            if (index < studyChartEntryList.size()) {
                entries2.add(new Entry(index, (int) (((studyChartEntryList.get(index).getEndStudy().getTime() - studyChartEntryList.get(index).getStartStudy().getTime()) / (1000 * 60)) % 60)));
            } else {
                entries2.add(new Entry(index, 0));
            }
        }

        LineDataSet set2 = new LineDataSet(entries2, "Test time");
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

        LineData lineData = new LineData();
        lineData.addDataSet(set1);
        lineData.addDataSet(set2);

        lineChart.setData(lineData);
        lineChart.invalidate();


        return view;
    }

}
