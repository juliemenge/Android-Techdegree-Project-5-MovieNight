package com.juliemenge.movienight.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class TVShow implements Parcelable{

    private String mTitle; //TV show title
    private String mOverview; //TV show overview

    //required empty constructor for parcelable
    public TVShow() {

    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    //required methods for parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //all the things we want to pass into the parcel
        dest.writeString(mTitle);
        dest.writeString(mOverview);
    }

    //method to UNparcel the data
    private TVShow(Parcel in){
        mTitle = in.readString();
        mOverview = in.readString();
    }

    public static final Creator<TVShow> CREATOR = new Creator<TVShow>() {
        @Override
        public TVShow createFromParcel(Parcel source) {
            return new TVShow(source);
        }

        @Override
        public TVShow[] newArray(int size) {
            return new TVShow[size];
        }
    };

}
