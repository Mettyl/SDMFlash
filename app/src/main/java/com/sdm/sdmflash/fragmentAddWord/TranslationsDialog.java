package com.sdm.sdmflash.fragmentAddWord;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

/**
 * Created by mety on 11.2.18.
 */

public class TranslationsDialog extends DialogFragment {

    public interface TranslationsDialogListener {
        void onClick(String word);
    }

    TranslationsDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            listener = (TranslationsDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] array = getArguments().getStringArray("translations");
        if (array.length == 0) {
            array = new String[1];
            array[0] = "No available translations";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Translations")
                .setItems(array, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        ListView view = ((AlertDialog) dialog).getListView();
                        if (!view.getAdapter().getItem(which).equals("No available translations"))
                            listener.onClick((String) view.getAdapter().getItem(which));
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

}
