package com.ngoprekweb.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ngoprekweb.popularmovies.data.model.Movie;

import java.util.ArrayList;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static MovieDbHelper sMovieDbHelper;

    private static final int DATABASE_VERSION = 7;

    static final String DATABASE_NAME = "movie.db";

    public static MovieDbHelper get(Context context) {
        if (sMovieDbHelper == null) {
            sMovieDbHelper = new MovieDbHelper(context.getApplicationContext());
        }

        return sMovieDbHelper;
    }

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Movie getMovie(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                MovieContract.MovieEntry.COLUMN_ID + "=?",
                new String[]{id},
                null,
                null,
                null
        );

        Movie movie;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            movie = new Movie(cursor);
        } else {
            movie = null;
        }

        db.close();

        return movie;
    }

    public ArrayList<Movie> getAllMovies() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<Movie> movies = new ArrayList<Movie>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Movie movie = new Movie(cursor);

            movies.add(movie);
        }

        return movies;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME +
                " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_ID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_FAVORED + " INTEGER NOT NULL DEFAULT 0, " +
                "UNIQUE (" + MovieContract.MovieEntry.COLUMN_ID+ ") ON CONFLICT REPLACE" +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);

        onCreate(db);
    }
}
