package com.juliemenge.movienight.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

//alert dialog to display an error
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context) //set up the error display dialog
                .setTitle("Oops! Sorry!") //title of the display
                .setMessage("There was an error. Please try again!") //message in the middle
                .setPositiveButton("OK",null); //button to click on, null just closes the dialog

        AlertDialog dialog = builder.create(); //actually create the alert dialog
        return dialog;
    }
}
