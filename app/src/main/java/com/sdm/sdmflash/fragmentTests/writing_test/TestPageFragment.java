package com.sdm.sdmflash.fragmentTests.writing_test;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.text.Normalizer;

public class TestPageFragment extends Fragment implements Step {

    private TextView question;
    private EditText input;
    private WritingTestActivity activity;

    private int position;
    private Word currentWord;
    private boolean right = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_writing_step, container, false);

        question = v.findViewById(R.id.fragment_test_writing_question);
        input = v.findViewById(R.id.fragment_test_writing_answer_input);
        activity = (WritingTestActivity) getActivity();

        position = getArguments().getInt(StepperAdapter.CURRENT_STEP_POSITION_KEY, 0);
        currentWord = activity.getWords().get(position);

        question.setText(currentWord.getWord());

        //inicializace nápovědy
        v.findViewById(R.id.fragment_test_writing_hint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentWord.getDescription().equals("")){
                    if (question.getText() == currentWord.getWord()){
                        question.setText(currentWord.getDescription());
                    }else
                        question.setText(currentWord.getWord());
                }else{
                    if (question.getText() == getString(R.string.nothing)){
                        question.setText(currentWord.getWord());
                    }else
                        question.setText(R.string.nothing);
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            //kontrola správnosti vloženého slova
            @Override
            public void afterTextChanged(Editable s) {
                if (checkAnswer(input.getText().toString(), currentWord.getTranslation())){
                    //správné slovo
                    activity.setCorrectAnswer(position);
                    right = true;
                } else{
                    activity.setFalseAnswer(position);
                    right = false;
                }
            }
        });

        //zavření klávesnice po stisknutí DONE
        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    /**
     * převede vstupy na normalizovaný tvar (malá písmena, bez diakritiki) a porovná
     * @param s1
     * @param s2
     */
    public boolean checkAnswer(String s1, String s2){
        s1 = Normalizer.normalize(s1, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        s2 = Normalizer.normalize(s2, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        //spávný vstup
        if (s1.equals(s2)){
            return true;
        }else
            return false;
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected
        if (activity.isFinished()){
            input.setVisibility(View.INVISIBLE);
            if (!right){
                question.setTextColor(Color.RED);
            } else {
                question.setTextColor(Color.GREEN);
            }
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

}
