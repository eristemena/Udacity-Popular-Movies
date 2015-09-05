package com.ngoprekweb.popularmovies.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.ngoprekweb.popularmovies.data.MovieContract;

public class Movie implements Parcelable {
    private String mId;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private double mVoteAverage;
    private long mVoteCount;
    private String mThumbnail;
    private boolean mIsFavored;

    public Movie(Cursor cursor) {
        setId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
        setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
        setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
        setVoteCount(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)));
        setThumbnail(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_THUMBNAIL)));
        setAsFavored(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORED)) != 0);
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
        this.mVoteAverage = in.readDouble();
        this.mThumbnail = in.readString();
        this.mIsFavored = in.readByte() != 0;
    }

    public Movie setId(String id) {
        mId = id;
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry.COLUMN_ID, this.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, this.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, this.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, this.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, this.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, this.getVoteCount());
        values.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL, this.getThumbnail());
        values.put(MovieContract.MovieEntry.COLUMN_FAVORED, this.isFavored() ? 1 : 0);

        return values;
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

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public Movie setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
        return this;
    }

    public long getVoteCount() {
        return mVoteCount;
    }

    public Movie setVoteCount(long voteCount) {
        mVoteCount = voteCount;
        return this;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public Movie setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
        return this;
    }

    public boolean isFavored() {
        return mIsFavored;
    }

    public Movie setAsFavored(boolean isFavored) {
        mIsFavored = isFavored;
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
        dest.writeDouble(this.mVoteAverage);
        dest.writeLong(this.mVoteCount);
        dest.writeString(this.mThumbnail);
        dest.writeByte(this.mIsFavored ? (byte) 1 : (byte) 0);
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
