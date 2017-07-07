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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName(); //TAG for logging errors

    public static final String MOVIE_RESULTS = "MOVIE RESULTS"; //used for intent

    String apiKey = "313b7986e3fac321ab33d6d3546ac8ab"; //my unique api key

    private Movie[] mMovie; //creating a variable to store movie results - call it on onResponse when response is successful

    //use butterknife to declare all UI variables
    @BindView(R.id.votesEntry) EditText mVotesEntry;
    @BindView(R.id.ratingEntry) EditText mRatingEntry;
    @BindView(R.id.startDateEntry) EditText mStartDateEntry;
    @BindView(R.id.endDateEntry) EditText mEndDateEntry;
    @BindView(R.id.submitButton) Button mSubmitButton;
    @BindView(R.id.genreSpinner) Spinner mGenreSpinner;
    public String[] genreNames = {"", "28", "12", "16"};

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


        //when the button is clicked, do the api request and return the results in a new activity
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set what the user entered to variables used to build the api url
                String ratingAverage = mRatingEntry.getText().toString().trim();
                String votesCount = mVotesEntry.getText().toString().trim();
                String startDate = mStartDateEntry.getText().toString().trim();
                String endDate = mEndDateEntry.getText().toString().trim();
                //String genre = String.valueOf(mGenreSpinner.getSelectedItem());
                String genre = mGenreSpinner.getSelectedItem().toString();


                //build the api url
                String movieUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                        "&language=en-US&sort_by=popularity.desc&include_adult=false" +
                        "&include_video=false&page=1&vote_average.gte=" + ratingAverage +
                        "&vote_count.gte=" + votesCount + "&primary_release_date.gte=" + startDate +
                        "&primary_release_date.lte=" + endDate + "&with_genres=" + genre; //add + genrevariable

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
