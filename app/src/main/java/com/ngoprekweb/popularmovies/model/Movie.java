package com.ngoprekweb.popularmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String mId;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private String mRating;
    private String mThumbnail;

    public Movie(Cursor cursor) {
        setId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
        setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
        setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        setRating(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
        setThumbnail(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_THUMBNAIL)));
    }

    public String getId() {
        return mId;
    }

    public Movie() {
    }

    public Movie(Parcel in) {
        this.mId = in.readString();
        this.mTitle = in.readString();
        this.mOverview = in.readString();
        this.mReleaseDate = in.readString();
        this.mRating = in.readString();
        this.mThumbnail = in.readString();
    }

    public Movie setId(String id) {
        mId = id;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Movie setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getOverview() {
        return mOverview;
    }

    public Movie setOverview(String overview) {
        mOverview = overview;
        return this;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public Movie setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
        return this;
    }

    public String getRating() {
        return mRating;
    }

    public Movie setRating(String rating) {
        mRating = rating;
        return this;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public Movie setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mOverview);
        dest.writeString(this.mReleaseDate);
        dest.writeString(this.mRating);
        dest.writeString(this.mThumbnail);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
