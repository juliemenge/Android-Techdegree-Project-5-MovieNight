package com.juliemenge.movienight.UI;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.juliemenge.movienight.Data.Movie;
import com.juliemenge.movienight.R;
import com.juliemenge.movienight.adapters.MovieAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity {

    //property for array of movies when using parcelable
    private Movie[] mMovies;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this); //make butterknife do its thing

        Intent intent = getIntent(); //get the intent started in main activity
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.MOVIE_RESULTS);
        mMovies = Arrays.copyOf(parcelables, parcelables.length, Movie[].class);

        MovieAdapter adapter = new MovieAdapter(this, mMovies);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
    }
}
