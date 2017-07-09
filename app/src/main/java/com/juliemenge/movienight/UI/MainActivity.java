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
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.juliemenge.movienight.Data.Genre;
import com.juliemenge.movienight.Data.Movie;
import com.juliemenge.movienight.Data.TVShow;
import com.juliemenge.movienight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName(); //TAG for logging errors

    public static final String MOVIE_RESULTS = "MOVIE RESULTS"; //used for intent
    public static final String TV_RESULTS = "TV RESULTS"; //used for intent

    String apiKey = "313b7986e3fac321ab33d6d3546ac8ab"; //my unique api key

    private Movie[] mMovie; //creating a variable to store movie results - call it on onResponse when response is successful
    private TVShow[] mTVShows; //array to store tv results - call it in onResponse when response is successful

    //use butterknife to declare all UI variables
    @BindView(R.id.tvCheckBox) CheckBox mTVCheckBox;
    @BindView(R.id.votesEntry) EditText mVotesEntry;
    @BindView(R.id.ratingEntry) EditText mRatingEntry;
    @BindView(R.id.startDateEntry) EditText mStartDateEntry;
    @BindView(R.id.endDateEntry) EditText mEndDateEntry;
    @BindView(R.id.submitButton) Button mSubmitButton;
    @BindView(R.id.genreSpinner) Spinner mGenreSpinner;
    public String[] genreNames = {"", "Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama",
                                    "Family", "Fantasy", "History", "Horror", "Music", "Mystery", "Romance", "Science Fiction",
                                    "TV Movie", "Thriller", "War", "Western"};
    @BindView(R.id.sortSpinner) Spinner mSortSpinner;
    public String[] sortOptions = {"", "Popularity", "Release Date", "Revenue", "Average Vote", "Number of Votes"};


    //private Genre[] mGenre;
    //private List<Genre> genreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this); //make butterknife do its thing

        //populateGenres(); //make request to get list of genres

        //create the spinner for genres
        ArrayAdapter genreAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genreNames);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenreSpinner.setAdapter(genreAdapter);
        mGenreSpinner.setOnItemSelectedListener(this);

        //create the spinner for sort options
        final ArrayAdapter sortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);
        mSortSpinner.setOnItemSelectedListener(this);



        //when the button is clicked, do the api request and return the results in a new activity
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set what the user entered to variables used to build the api url
                String ratingAverage = mRatingEntry.getText().toString().trim();
                String votesCount = mVotesEntry.getText().toString().trim();
                String startDate = mStartDateEntry.getText().toString().trim();
                String endDate = mEndDateEntry.getText().toString().trim();

                //set genre id based on what was selected in spinner
                String genre;
                switch (mGenreSpinner.getSelectedItem().toString()) {
                    case "Action":  genre = "28";
                        break;
                    case "Adventure":  genre = "12";
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
                    case "Fantasy": genre="14";
                        break;
                    case "History": genre="36";
                        break;
                    case "Horror": genre="27";
                        break;
                    case "Music": genre="10402";
                        break;
                    case "Mystery": genre="9648";
                        break;
                    case "Romance": genre="10749";
                        break;
                    case "Science Fiction": genre="878";
                        break;
                    case "TV Movie": genre="10770";
                        break;
                    case "Thriller": genre="53";
                        break;
                    case "War": genre="10752";
                        break;
                    case "Western": genre="37";
                        break;
                    default: genre= ""; //do not include genre in request if none specified
                        break;
                }

                //set sort variable based on what was selected
                String sortBy;
                switch (mSortSpinner.getSelectedItem().toString()) {
                    case "Popularity": sortBy = "popularity.desc";
                        break;
                    case "Release Date": sortBy = "release_date.asc";
                        break;
                    case "Revenue": sortBy = "revenue.asc";
                        break;
                    case "Average Vote": sortBy = "vote_average.asc";
                        break;
                    case "Number of Votes": sortBy = "vote_count.asc";
                        break;
                    default: sortBy = ""; //do not include sort in request if none specified
                        break;
                }

                //check if tv show button is checked
                if(mTVCheckBox.isChecked()) {
                    //all the stuff to search for and return TV results
                    Toast.makeText(MainActivity.this, "Box is checked!", Toast.LENGTH_LONG).show();

                    String tvUrl = "https://api.themoviedb.org/3/discover/tv?api_key=" + apiKey +
                            "&language=en-US&sort_by=" + sortBy + "&air_date.gte=" + startDate +
                            "&air_date.lte=" + endDate + "&vote_average.gte=" + ratingAverage +
                            "&vote_count.gte=" + votesCount + "&with_genres=" + genre +
                            "&include_null_first_air_dates=false";

                    //start tv search block
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
                                        mTVShows = getTVResults(jsonData); //pass the json data into the method that creates the movies model
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateDisplay(); //need to check if i need this for refreshing?
                                                Log.v(TAG, "Time for TV results!");
                                                //start the intent to display the new data
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
                    //end tv network search block

                } else { //if tv box isn't checked, search for movies


                    //build the api url
                    String movieUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                            "&language=en-US&sort_by=" + sortBy + "100&include_adult=false" +
                            "&include_video=false&page=1&vote_average.gte=" + ratingAverage +
                            "&vote_count.gte=" + votesCount + "&primary_release_date.gte=" + startDate +
                            "&primary_release_date.lte=" + endDate + "&with_genres=" + genre; //add + genrevariable

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
                                                updateDisplay(); //need to check if i need this for refreshing?

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



    //do I need this for refreshing?
    private void updateDisplay() {
        Log.v(TAG, "UI is running");
    }

    //GENRES
    /* create a new genre class (name and id)
    use genre url to return a list of all the genres
    use that list to populate my spinner
    if that's too crazy, just populate the spinner with genres to try it out first
    */

    //method to create a movie using json data from the api
    private Movie[] getMovieResults(String jsonData) throws JSONException {
        JSONObject allData = new JSONObject(jsonData); //creating a json object to store everything possible from the api request

        //create a new json object of just the array of movie results
        JSONArray results = allData.getJSONArray("results");

        Movie[] movies = new Movie[results.length()]; //create an array of movies of the same length of however long the array of results is from the api request

        //loop through each item in the results array and assign it to an element of the movies array
        for(int i=0; i<results.length(); i++) {
            JSONObject jsonMovie = results.getJSONObject(i); //create a new json object at proper element of results
            Movie movie = new Movie(); //create a new movie object

            //set the values of a movie
            movie.setTitle(jsonMovie.getString("title"));
            //movie.setGenre(jsonMovie.getJSONArray("genres"));
            movie.setOverview(jsonMovie.getString("overview"));
            movie.setPopularity(jsonMovie.getDouble("popularity"));
            movie.setRating(jsonMovie.getDouble("vote_average"));
            movie.setReleaseDate(jsonMovie.getString("release_date"));
            //movie.setRevenue(jsonMovie.getInt("revenue"));
            movie.setVoteCount(jsonMovie.getInt("vote_count"));

            movies[i] = movie; //set the element of the movies array to the object we just populated
        }

        return movies; //return the array of movies
    }

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

    /*
    //get current available genres from tmdb api
    public void populateGenres() {
        String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=313b7986e3fac321ab33d6d3546ac8ab&language=en-US";

        if(isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(genreUrl)
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
                            mGenre = getGenreResults(jsonData); //pass the json data into the method that creates the movies model
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay(); //need to check if i need this for refreshing?

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

    private Genre[] getGenreResults(String jsonData) throws JSONException {
        genreList = new ArrayList<>();

        JSONObject allData = new JSONObject(jsonData); //creating a json object to store everything possible from the api request

        //create a new json object of just the array of genre results
        JSONArray results = allData.getJSONArray("genres");

        Genre[] genres = new Genre[results.length()]; //create an array of genres of the same length of however long the array of results is from the api request

        //loop through each item in the results array and assign it to an element of the movies array
        for(int i=0; i<results.length(); i++) {
            JSONObject jsonGenre = results.getJSONObject(i); //create a new json object at proper element of results
            Genre genre = new Genre(); //create a new genre object

            //set the values of a genre
            genre.setId(jsonGenre.getInt("id"));
            genre.setName(jsonGenre.getString("name"));

            genreList.add(genre);


            genres[i] = genre; //set the element of the genres array to the object we just populated
        }

        return genres; //return the array of genres
    }
    */

    //required methods for spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
