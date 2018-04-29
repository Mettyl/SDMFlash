package com.sdm.sdmflash.fragmentTests.writing_test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdm.sdmflash.R;

import java.text.MessageFormat;

public class FragmentComplete extends Fragment {

    private int points;
    private String message;

    public void setPoints(int points) {
        this.points = points;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writing_test_complete, container, false);

        ((TextView)view.findViewById(R.id.fragment_writing_test_points)).setText(MessageFormat.format(getString(R.string.you_have_number_points), points));
        view.findViewById(R.id.fragment_test_writing_finish_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        if (message != null){
            ((TextView)view.findViewById(R.id.fragment_writing_test_points)).setText("");
            ((TextView)view.findViewById(R.id.fragment_writing_test_complete_message)).setText(message);
        }

        return view;
    }
}
