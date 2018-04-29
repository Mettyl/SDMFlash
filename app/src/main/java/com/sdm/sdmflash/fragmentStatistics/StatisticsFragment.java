package com.sdm.sdmflash.fragmentStatistics;


import android.graphics.Color;
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

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.appDatabase.TestChartEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private CombinedChart combinedChart;

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

        toggle.syncState();

        // nastaveni combined chartu
        combinedChart = view.findViewById(R.id.statistics_chart);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.getAxisLeft().setEnabled(false);
        combinedChart.setBackgroundColor(Color.WHITE);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightPerDragEnabled(false);
        combinedChart.setHighlightPerTapEnabled(false);
        combinedChart.setHighlightFullBarEnabled(false);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});

        List<TestChartEntry> testChartEntryList = new ArrayList<>();
        testChartEntryList.add(new TestChartEntry(2, 1, 6, new Date(1420200200000L), new Date(1420207200000L)));
        testChartEntryList.add(new TestChartEntry(4, 0, 3, new Date(1420300200000L), new Date(1420302200000L)));
        testChartEntryList.add(new TestChartEntry(1, 5, 2, new Date(1420403200000L), new Date(1420414200000L)));
        testChartEntryList.add(new TestChartEntry(3, 3, 8, new Date(1420501200000L), new Date(1420505200000L)));
        testChartEntryList.add(new TestChartEntry(6, 3, 0, new Date(1420600200000L), new Date(1420603200000L)));

        LineData lineData = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < testChartEntryList.size(); index++)
            entries.add(new Entry(index, (testChartEntryList.get(index).getEndTest().getTime() - testChartEntryList.get(index).getStartTest().getTime()) / 1000000));

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(Color.BLACK);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.BLACK);
        set.setCircleRadius(5f);
        set.setFillColor(Color.BLACK);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);


        lineData.addDataSet(set);


        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();
        List<BarEntry> entriesGroup3 = new ArrayList<>();

        for (int i = 0; i < testChartEntryList.size(); i++) {
            entriesGroup1.add(new BarEntry(i, testChartEntryList.get(i).getWordsDown()));
            entriesGroup2.add(new BarEntry(i, testChartEntryList.get(i).getWordsStayed()));
            entriesGroup3.add(new BarEntry(i, testChartEntryList.get(i).getWordsUp()));
        }

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Group 1");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Group 2");
        BarDataSet set3 = new BarDataSet(entriesGroup3, "Group 3");

        set1.setColor(Color.RED);
        set2.setColor(Color.YELLOW);
        set3.setColor(Color.GREEN);

        float groupSpace = 0.06f;
        float barSpace = 0.04f; // x2 dataset
        float barWidth = 0.30f; // x2 dataset
// (0.04 + 0.30) * 3 + 0.06 = 1.00 -> interval per "group"

        BarData barData = new BarData(set1, set2, set3);
        barData.setBarWidth(barWidth);
        barData.groupBars(0, groupSpace, barSpace);

        CombinedData data = new CombinedData();

        data.setData(barData);
        data.setData(lineData);

        combinedChart.setData(data);
        combinedChart.invalidate();


        return view;
    }

}
