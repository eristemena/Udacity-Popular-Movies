package com.ngoprekweb.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static MovieDbHelper sMovieDbHelper;

    private static final int DATABASE_VERSION = 4;

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

    /**
     * Bulk insert (i just need to cache)
     *
     * @param movies array of movies
     */
    public void bulkInsert(ArrayList<Movie> movies) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);

        try {
            db.beginTransaction();
            for (int i = 0; i < movies.size(); i++) {
                Movie movie = movies.get(i);

                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getRating());
                values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                values.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL, movie.getThumbnail());

                db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
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
                MovieContract.MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL " +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);

        onCreate(db);
    }
}
