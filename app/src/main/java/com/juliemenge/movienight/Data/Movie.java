package com.juliemenge.movienight.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String mTitle; //movie title
    private double mRating; //user rating on a scale of 1-10
    private int mVoteCount; //number of votes the movies has received
    private String mReleaseDate; //date the movie was released
    private int[] mGenres; //array of ids representing movie's genres
    private String mOverview; //brief summary of the movie's plot
    private double mPopularity; //how popular the movie is
    private int mRevenue; //total revenue the movie has brought in

    public Movie() {

    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(double rating) {
        mRating = rating;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(int voteCount) {
        mVoteCount = voteCount;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    /*
    //figure out genre ids
    public int[] getGenre() {
        return mGenres;
    }

    public void setGenre(int[] genres) {
        mGenres = genres;
    }
    */


    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(double popularity) {
        mPopularity = popularity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //add in all the values we want to pass into the parcel
        dest.writeString(mTitle);
        dest.writeString(mOverview);
    }

    //method to UNparcel the data
    private Movie(Parcel in){
        mTitle = in.readString();
        mOverview = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //see if revenue is still available in the api
    /*
    public int getRevenue() {
        return mRevenue;
    }

    public void setRevenue(int revenue) {
        mRevenue = revenue;
    }
    */

}
