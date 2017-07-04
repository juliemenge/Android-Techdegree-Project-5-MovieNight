package com.juliemenge.movienight.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

public class OverviewDialogFragment extends DialogFragment {

    //dialog fragment to display the movie's overview
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        //set up a bundle so the overview can be received
        Bundle bundle = getArguments();
        String overview = bundle.getString("overview");

        //create the dialog fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Overview of the movie")
                .setMessage(overview) //overview of the movie is in the actual dialog box
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
