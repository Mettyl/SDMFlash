package com.sdm.sdmflash.fragmentAddWord;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;

import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class AddWordFromText extends AppCompatActivity implements VerticalStepperForm {

    private VerticalStepperFormLayout verticalStepperForm;

    public AddWordFromText(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_word_from_text);

            String[] mySteps = {"Word", "Translation", "Additional options"};
            int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
            int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

            // Finding the view
            verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.add_word_from_text_vertical_stepper);

            // Setting up and initializing the form
            VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                    .primaryColor(colorPrimary)
                    .primaryDarkColor(colorPrimaryDark)
                    .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                    .init();

    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        switch (stepNumber) {
            case 0:
                view = createWordStep(inflater);
                break;
            case 1:
                view = createTranslationStep(inflater);
                break;
            case 2:
                view = createOptionsStep(inflater);
                break;
        }
        return view;
    }

    private View createWordStep(LayoutInflater inflater) {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_word_from_text_step_word, null, false);

        final AutoCompleteTextView autoCompleteTextView = layout.findViewById(R.id.add_word_from_text_autocomplete);

        List<AutoCompleteRow> words = new ArrayList<AutoCompleteRow>();
        words.add(new AutoCompleteRow("ahoj", Language.CZ));

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, R.layout.add_word_from_text_autocomplete_row, words);

        autoCompleteTextView.setAdapter(adapter);

        return layout;
    }

    private View createTranslationStep(LayoutInflater inflater) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_word_from_text_step_translation, null, false);

        return layout;
    }

    private View createOptionsStep(LayoutInflater inflater) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_word_from_text_step_options, null, false);
        return layout;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                verticalStepperForm.setStepAsCompleted(0);
                break;
            case 1:
                verticalStepperForm.setStepAsCompleted(1);
                break;
            case 2:
                verticalStepperForm.setStepAsCompleted(2);
                break;
        }
    }

    @Override
    public void sendData() {

    }
}
