package com.sdm.sdmflash.fragmentYourWords;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sdm.sdmflash.R;

/**
 * Created by mety on 3.2.18.
 */

public class WordInfoDialog extends DialogFragment {

    public interface WordInfoDialogListener {
        void onDialogNeutralClick(int id);

        void onDialogNegativeClick(int id);
    }

    WordInfoDialogListener dialogListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            dialogListener = (WordInfoDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement WordInfoDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.your_words_info_dialog_layout, null);

        TextView slovo = view.findViewById(R.id.your_words_info_dialog_slovo);
        TextView preklad = view.findViewById(R.id.your_words_info_dialog_preklad);
        TextView zdroj = view.findViewById(R.id.your_words_info_dialog_zdroj);
        TextView datumPridani = view.findViewById(R.id.your_words_info_dialog_datum_pridani);
        TextView datumZmeny = view.findViewById(R.id.your_words_info_dialog_datum_zmeny);

        slovo.setText(getArguments().getString("slovo"));
        preklad.setText(getArguments().getString("preklad"));
        zdroj.setText(getArguments().getString("zdroj"));
        datumPridani.setText(getArguments().getString("datum_pridani"));
        datumZmeny.setText(getArguments().getString("datum_zmeny"));

        final int ID = getArguments().getInt("ID");

        builder.setView(view)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onDialogNegativeClick(ID);
                    }
                })
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogListener.onDialogNeutralClick(ID);
                    }
                });

        return builder.create();
    }

}
