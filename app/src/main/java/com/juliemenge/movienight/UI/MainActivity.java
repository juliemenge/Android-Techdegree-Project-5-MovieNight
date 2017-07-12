package com.juliemenge.movienight.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.juliemenge.movienight.Data.Movie;
import com.juliemenge.movienight.Data.TVShow;
import com.juliemenge.movienight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName(); //TAG for logging errors

    public static final String MOVIE_RESULTS = "MOVIE RESULTS"; //used for intent to display movies
    public static final String TV_RESULTS = "TV RESULTS"; //used for intent to display tv shows

    String apiKey = "313b7986e3fac321ab33d6d3546ac8ab"; //my unique tmdb api key

    private Movie[] mMovie; //array to store movie results - call it on onResponse when response is successful
    private TVShow[] mTVShows; //array to store tv results - call it in onResponse when response is successful

    //use butterknife to declare all UI variables
    @BindView(R.id.tvCheckBox) CheckBox mTVCheckBox;
    @BindView(R.id.votesEntry) EditText mVotesEntry;
    @BindView(R.id.ratingEntry) EditText mRatingEntry;
    @BindView(R.id.startDateEntry) EditText mStartDateEntry;
    @BindView(R.id.endDateEntry) EditText mEndDateEntry;
    @BindView(R.id.submitButton) Button mSubmitButton;
    @BindView(R.id.genreSpinner) Spinner mGenreSpinner; //genre drop down
    //genre options
    public String[] genreNames = {"", "Action (Movies only)", "Action & Adventure (TV only)", "Adventure (Movies only)",
                                    "Animation", "Comedy", "Crime", "Documentary", "Drama",
                                    "Family", "Fantasy (Movies only)", "History (Movies only)", "Horror (Movies only)",
                                    "Kids (TV only)", "Music (Movies only)", "Mystery", "News (TV only)", "Reality (TV only)",
                                    "Romance (Movies only)", "Science Fiction (Movies only)", "Sci-Fi & Fantasy (TV only)",
                                    "Soap (TV only)", "Talk (TV only)", "TV Movie (Movies only)", "Thriller (Movies only)",
                                    "War (Movies only)", "War & Politics (TV only)", "Western"};
    @BindView(R.id.sortSpinner) Spinner mSortSpinner; //sort by drop down
    //sort by options
    public String[] sortOptions = {"", "Popularity", "Release Date (Movies only)", "First Air Date (TV only)", "Revenue (Movies only)",
                                    "Average Vote", "Number of Votes (Movies only)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this); //make butterknife do its thing

        //create the spinner for genres
        ArrayAdapter genreAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genreNames);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenreSpinner.setAdapter(genreAdapter);
        mGenreSpinner.setOnItemSelectedListener(this);

        //create the spinner for sort by options
        final ArrayAdapter sortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);
        mSortSpinner.setOnItemSelectedListener(this);

        //when the submit button is clicked, submit the api request and display results in a new activity
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set what the user entered to variables and use them to build the api url
                String ratingAverage = mRatingEntry.getText().toString().trim();
                String votesCount = mVotesEntry.getText().toString().trim();
                String startDate = mStartDateEntry.getText().toString().trim();
                String endDate = mEndDateEntry.getText().toString().trim();

                //set genre id based on what was selected in spinner
                String genre;
                switch (mGenreSpinner.getSelectedItem().toString()) {
                    case "Action (Movies only)":  genre = "28";
                        break;
                    case "Action & Adventure (TV only)": genre="10759";
                        break;
                    case "Adventure (Movies only)":  genre = "12";
                        break;
                    case "Animation":  genre = "16";
                        break;
                    case "Comedy": genre="35";
                        break;
                    case "Crime": genre="80";
                        break;
                    case "Documentary": genre="99";
                        break;
                    case "Drama": genre="18";
                        break;
                    case "Family": genre="10751";
                        break;
                    case "Fantasy (Movies only)": genre="14";
                        break;
                    case "History": genre="36";
                        break;
                    case "Horror": genre="27";
                        break;
                    case "Kids": genre="10762";
                        break;
                    case "Music": genre="10402";
                        break;
                    case "Mystery": genre="9648";
                        break;
                    case "News (TV only)": genre="10763";
                        break;
                    case "Reality (TV only)": genre="10764";
                        break;
                    case "Romance (Movies only)": genre="10749";
                        break;
                    case "Science Fiction (Movies only)": genre="878";
                        break;
                    case "Sci-Fi & Fantasy (TV only)": genre="10765";
                        break;
                    case "Soap (TV only)": genre="10766";
                        break;
                    case "Talk (TV only)": genre="10767";
                        break;
                    case "TV Movie (Movies only)": genre="10770";
                        break;
                    case "Thriller (Movies only)": genre="53";
                        break;
                    case "War (Movies only)": genre="10752";
                        break;
                    case "War & Politics (TV only)": genre="10768";
                        break;
                    case "Western": genre="37";
                        break;
                    default: genre= ""; //do not include genre in request if none specified
                        break;
                }

                //set sort by id based on what was selected in spinner
                String sortBy;
                switch (mSortSpinner.getSelectedItem().toString()) {
                    case "Popularity": sortBy = "popularity.desc";
                        break;
                    case "Release Date (Movies only)": sortBy = "release_date.desc";
                        break;
                    case "First Air Date (TV only):": sortBy = "first_air_date.desc";
                        break;
                    case "Revenue (Movies only)": sortBy = "revenue.desc";
                        break;
                    case "Average Vote": sortBy = "vote_average.desc";
                        break;
                    case "Number of Votes (Movies only)": sortBy = "vote_count.desc";
                        break;
                    default: sortBy = ""; //do not include sort in request if none specified
                        break;
                }

                //check if tv show button is checked - if so, display tv results only
                if(mTVCheckBox.isChecked()) {
                    //tv show discover url
                    String tvUrl = "https://api.themoviedb.org/3/discover/tv?api_key=" + apiKey +
                            "&language=en-US&sort_by=" + sortBy + "&air_date.gte=" + startDate +
                            "&air_date.lte=" + endDate + "&vote_average.gte=" + ratingAverage +
                            "&vote_count.gte=" + votesCount + "&with_genres=" + genre +
                            "&include_null_first_air_dates=false";

                    //asynchronous get recipe from OkHTTP to make the API get the data about TV shows
                    //first, check that the network is available
                    if (isNetworkAvailable()) {

                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(tvUrl)
                                .build();

                        Call call = client.newCall(request);

                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                //what to do when there is no response
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                                alertUserAboutError(); //if no response, let the user know
                            }

                            @Override
                            //what to do when you receive a response back
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    final String jsonData = response.body().string(); //string to store all the json data
                                    if (response.isSuccessful()) { //if you receive a response and it is successful
                                        mTVShows = getTVResults(jsonData); //pass the json data into the method that creates the tv show model
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //start the intent to display the tv show results
                                                Intent intent = new Intent(MainActivity.this, TVResultsActivity.class);
                                                intent.putExtra(TV_RESULTS, mTVShows);
                                                startActivity(intent);

                                            }
                                        });
                                    } else { //if you receive a response and it is NOT successful
                                        alertUserAboutError(); //let user know there was an error
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG, "Exception caught: ", e);
                                } catch (JSONException e) {
                                    Log.e(TAG, "Exception caught: ", e);
                                }

                            }
                        });
                    } else { //if the network is NOT available, let the user know
                        Toast.makeText(MainActivity.this, "Network unavailable!", Toast.LENGTH_LONG).show();
                    }

                } else { //if tv box isn't checked, search for movies only


                    //build the api url for movies
                    String movieUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                            "&language=en-US&sort_by=" + sortBy + "100&include_adult=false" +
                            "&include_video=false&page=1&vote_average.gte=" + ratingAverage +
                            "&vote_count.gte=" + votesCount + "&primary_release_date.gte=" + startDate +
                            "&primary_release_date.lte=" + endDate + "&with_genres=" + genre;

                    //asynchronous get recipe from OkHTTP to make the API get the data about movies
                    //first, check that the network is available
                    if (isNetworkAvailable()) {

                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(movieUrl)
                                .build();

                        Call call = client.newCall(request);

                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                //what to do when there is no response
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                                alertUserAboutError(); //if no response, let the user know
                            }

                            @Override
                            //what to do when you receive a response back
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    final String jsonData = response.body().string(); //string to store all the json data
                                    if (response.isSuccessful()) { //if you receive a response and it is successful
                                        mMovie = getMovieResults(jsonData); //pass the json data into the method that creates the movies model
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //start the intent to display the new data
                                                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                                                intent.putExtra(MOVIE_RESULTS, mMovie);
                                                startActivity(intent);
                                            }
                                        });
                                    } else { //if you receive a response and it is NOT successful
                                        alertUserAboutError(); //let user know there was an error
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG, "Exception caught: ", e);
                                } catch (JSONException e) {
                                    Log.e(TAG, "Exception caught: ", e);
                                }

                            }
                        });
                    } else { //if the network is NOT available, let the user know
                        Toast.makeText(MainActivity.this, "Network unavailable!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    //create a movie using json data from the api
    private Movie[] getMovieResults(String jsonData) throws JSONException {
        JSONObject allData = new JSONObject(jsonData); //json object to store everything possible from the api request

        //new json object of just the array of movie results
        JSONArray results = allData.getJSONArray("results");

        Movie[] movies = new Movie[results.length()]; //array of movies of the same length of however long the array of results is from the api request

        //loop through each item in the results array and assign it to an element of the movies array
        for(int i=0; i<results.length(); i++) {
            JSONObject jsonMovie = results.getJSONObject(i); //new json object at proper element of results
            Movie movie = new Movie(); //create a new movie object

            //set the values of a movie
            movie.setTitle(jsonMovie.getString("title"));
            movie.setOverview(jsonMovie.getString("overview"));
            movie.setPopularity(jsonMovie.getDouble("popularity"));
            movie.setRating(jsonMovie.getDouble("vote_average"));
            movie.setReleaseDate(jsonMovie.getString("release_date"));
            movie.setVoteCount(jsonMovie.getInt("vote_count"));

            movies[i] = movie; //set the element of the movies array to the object we just populated
        }

        return movies; //return the array of movies
    }

    //create a tv show using json data from the api
    private TVShow[] getTVResults(String jsonData) throws JSONException {
        JSONObject allData = new JSONObject(jsonData); //json object to store everything possible that api requested
        JSONArray results = allData.getJSONArray("results"); //json object of just the stuff in the TV show results array

        TVShow[] tvShows = new TVShow[results.length()]; //array of TV shows of the same length however long the array of results is from the api request

        //loop through each item in the TV Show results array from the api and assign it to an element of my TV Shows array
        for(int i=0; i<results.length(); i++) {
            JSONObject jsonTVShow = results.getJSONObject(i); //create a new json object at proper element of the results
            TVShow tvShow = new TVShow(); //create a new TV Show object

            //set the values of the TV show
            tvShow.setTitle(jsonTVShow.getString("original_name"));
            tvShow.setOverview(jsonTVShow.getString("overview"));

            tvShows[i] = tvShow; //set the element of the tv shows array to the object we just populated
        }

        return tvShows; //return the array of TV Shows
    }


    //checking to see if the network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) { //network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    //display a dialog alert that there was an error
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    //required methods for spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
