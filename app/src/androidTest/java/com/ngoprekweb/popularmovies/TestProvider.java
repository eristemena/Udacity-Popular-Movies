package com.ngoprekweb.popularmovies;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.ngoprekweb.popularmovies.data.MovieContract;
import com.ngoprekweb.popularmovies.data.MovieDbHelper;
import com.ngoprekweb.popularmovies.data.MovieProvider;

public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals("Error: the WeatherEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        String movieId = "94074";
        // content://com.ngoprekweb.popularmovies/movie/94074
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildMovieDetail(movieId));
        assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testBasicMovieQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = MovieDbHelper.get(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_ID, "123");
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, "TITLE");
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "OVERVIEW");
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "RATING");
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-05-01");
        values.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL, "http://image.com/image");

        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        assertTrue("Unable to insert MovieEntry into the database", movieRowId!= -1);

        db.close();

        // Test the basic content provider query
        Cursor weatherCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieDetail("123"),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        assertTrue("Empty cursor returned. ", weatherCursor.moveToFirst());

        assertTrue("Wrong title", weatherCursor.getString(weatherCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)).equals("123"));
    }

    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = MovieDbHelper.get(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }
}
