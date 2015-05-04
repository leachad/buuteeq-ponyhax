/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;

/**
 * Created by leachad on 5/4/2015.
 * A Custom class that has the Date and Time Picker utilities on one Dialog Box.
 */
public class RangeDialogFragment extends DialogFragment{


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_range_picker, null));

        dialogBuilder.setNegativeButton(R.string.confirmRange, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogBuilder.setPositiveButton(R.string.cancelRange, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO something with the date and time in here to store the information to shared prefs
            }
        });
       if (this.getTag().matches(RangePickerFragment.START_RANGE)) {
            dialogBuilder.setTitle(R.string.startOfRange);
        } else {
            dialogBuilder.setTitle(R.string.endOfRange);
        }

        return dialogBuilder.create();
    }
}
