package com.juliemenge.movienight.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

public class OverviewDialogFragment extends DialogFragment {

    //dialog fragment to display a movie or tv show overview
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        //set up a bundle so the overview can be received
        Bundle bundle = getArguments();
        String overview = bundle.getString("overview");

        //create the dialog fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Overview") //displayed at the top of the dialog box
                .setMessage(overview) //overview of the movie is in the actual dialog box
                .setPositiveButton("OK", null); //pressing ok just closes the box

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
