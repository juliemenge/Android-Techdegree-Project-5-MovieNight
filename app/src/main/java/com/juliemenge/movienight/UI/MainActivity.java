package com.juliemenge.movienight.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.juliemenge.movienight.Data.Movie;
import com.juliemenge.movienight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName(); //TAG for logging errors

    public static final String MOVIE_RESULTS = "MOVIE RESULTS"; //used for intent

    private Movie[] mMovie; //creating a variable to store movie results - call it on onResponse when response is successful

    //use butterknife to declare all UI variables
    //@BindView(R.id.votesEntry) EditText mVotesEntry;
    //@BindView(R.id.ratingEntry) EditText mRatingEntry;
    //@BindView(R.id.startDateEntry) EditText mStartDateEntry;
    //@BindView(R.id.endDateEntry) EditText mEndDateEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this); //make butterknife do its thing

        //variables to hold data about the api
        String apiKey = "313b7986e3fac321ab33d6d3546ac8ab";
        double voteAverage = 7.5;
        int voteCount = 1000;
        String primaryReleaseDateStart = "2014-01-01";
        String primaryReleaseDateEnd = "2015-06-30";

        //building the api url
        String movieUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                "&language=en-US&sort_by=popularity.desc&include_adult=false" +
                "&include_video=false&page=1&vote_average.gte=" + voteAverage +
                "&vote_count.gte=" + voteCount + "&primary_release_date.gte=" + primaryReleaseDateStart +
                "&primary_release_date.lte=" + primaryReleaseDateEnd;

        //asynchronous get recipe from OkHTTP to make the API get the data
        //first, check that the network is available
        if(isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(movieUrl)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //what to do when there is no response
                    runOnUiThread(new Runnable() { //added this - make sure it works
                        @Override
                        public void run() {

                        }
                    });
                    alertUserAboutError(); //if no response, let the user know
                }

                @Override
                //what to do when you receive a response back
                public void onResponse(Call call, Response response) throws IOException {
                    //do this when there is a successful response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }); //added this, make sure it works
                    try {
                        String jsonData = response.body().string(); //string to store all the json data
                        Log.v(TAG, jsonData); //Logging all the JSON data
                        if (response.isSuccessful()) {
                            mMovie = getMovieResults(jsonData); //pass the JSON data into the method that creates our movie model
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            }); //do I have to have an update display method?
                        } else { //if you receive a response and it was NOT successful
                            //let user know there was an error
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught:", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else { //if the network is NOT available, let the user know
            Toast.makeText(this, "Network unavailable!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        Log.v(TAG, "UI is running");
    }

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

    @OnClick(R.id.submitButton)
    public void startResultsActivity(View view) {
        //write intent to start results activity when you click on the button
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(MOVIE_RESULTS, mMovie); //not sure about this part - add run on UI thread stuff
        startActivity(intent);
    }
}
