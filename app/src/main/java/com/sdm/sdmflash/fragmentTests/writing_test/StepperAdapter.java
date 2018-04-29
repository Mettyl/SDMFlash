package com.sdm.sdmflash.fragmentTests.writing_test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

public class StepperAdapter extends AbstractFragmentStepAdapter {

    public static final String CURRENT_STEP_POSITION_KEY = "CURRENT_STEP_POSITION_KEY";
    private int wordsCount;

    public StepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    public void setWordsCount(int wordsCount) {
        this.wordsCount = wordsCount;
    }

    @Override
    public Step createStep(int position) {
        final TestPageFragment step = new TestPageFragment();
        Bundle b = new Bundle();
        b.putInt(CURRENT_STEP_POSITION_KEY, position);
        step.setArguments(b);
        return step;
    }

    @Override
    public int getCount() {
        return wordsCount;
    }
}
