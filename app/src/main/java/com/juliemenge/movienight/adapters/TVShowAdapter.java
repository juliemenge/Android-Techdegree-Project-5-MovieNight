package com.juliemenge.movienight.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juliemenge.movienight.Data.Movie;
import com.juliemenge.movienight.Data.TVShow;
import com.juliemenge.movienight.R;
import com.juliemenge.movienight.UI.OverviewDialogFragment;
import com.juliemenge.movienight.UI.ResultsActivity;
import com.juliemenge.movienight.UI.TVResultsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowAdapter extends RecyclerView.Adapter<TVShowAdapter.TVViewHolder> {

    private TVShow[] mTVShows;

    private TVResultsActivity mTVResultsActivity;

    public static final String TAG = TVShowAdapter.class.getSimpleName(); //TAG for logging errors

    public TVShowAdapter(TVResultsActivity tvResultsActivity, TVShow[] tvShows) {
        mTVShows = tvShows;
        mTVResultsActivity = tvResultsActivity;
    }

    @Override
    public TVShowAdapter.TVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_tv_results_item, parent, false);
        TVViewHolder viewHolder = new TVViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TVViewHolder holder, int position) {
        holder.bindTVShow(mTVShows[position]);
    }

    @Override
    public int getItemCount() {
        return mTVShows.length;
    }

    public class TVViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener { //implement so items in a list are clickable

        @BindView(R.id.tvTitleLabel)TextView mTVTitleLabel;

        public TVViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this); //need this for clickable items

        }

        //method to map data to the view
        public void bindTVShow(TVShow tvShow) {
            mTVTitleLabel.setText(tvShow.getTitle());
        }

        //click on a tv show name in a list to display the overview
        @Override
        public void onClick(View v) {

            String overview = mTVShows[getAdapterPosition()].getOverview(); //get the overview of the specific tv show in the list

            //create a bundle so the overview can be passed to the dialog fragment
            Bundle bundle = new Bundle();
            bundle.putString("overview", overview);

            //create and display the overview dialog
            OverviewDialogFragment dialog = new OverviewDialogFragment();
            dialog.setArguments(bundle);
            dialog.show(mTVResultsActivity.getFragmentManager(), "movie_dialog");

        }

    }


}
