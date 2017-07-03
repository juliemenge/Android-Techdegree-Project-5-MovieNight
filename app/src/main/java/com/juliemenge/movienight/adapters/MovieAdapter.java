package com.juliemenge.movienight.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.juliemenge.movienight.R;

import com.juliemenge.movienight.Data.Movie;
import com.juliemenge.movienight.UI.MainActivity;
import com.juliemenge.movienight.UI.ResultsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Movie[] mMovies;
    public static final String TAG = MovieAdapter.class.getSimpleName(); //TAG for logging errors

    public MovieAdapter(ResultsActivity resultsActivity, Movie[] movies) {
        mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_results_item, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bindMovie(mMovies[position]);
    }

    @Override
    public int getItemCount() {
        return mMovies.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener { //implement so items in a list are clickable

        @BindView(R.id.movieTitleLabel) TextView mMovieTitleLabel;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this); //need this for clickable items

        }

        //method to map data to the view
        public void bindMovie(Movie movie) {
            mMovieTitleLabel.setText(movie.getTitle());
        }

        //click on a movie name in a list to display the overview
        @Override
        public void onClick(View v) {
            String overview = mMovies[getAdapterPosition()].getOverview(); //how to get the real overview? pass in a movie, 0 isn't right
            Log.v(TAG, overview);

        }


    }

}
