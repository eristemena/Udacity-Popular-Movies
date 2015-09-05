package com.ngoprekweb.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.model.Trailer;

import java.util.HashSet;
import java.util.Set;

public class Utility {
    public static final String PREF_FAVORED_MOVIES = "pref_favored_movies";
    public static final String SORTED_BY_POPULARITY = "popularity";
    public static final String SORTED_BY_HIGHEST_RATED = "vote_count";
    public static final String SORTED_BY_FAVORITE = "favorite";

    public static String getPreferredSortedBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_sort_by),
                context.getString(R.string.pref_default_sort_by));
    }

    public static String[] getFavorites(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = preferences.getStringSet(PREF_FAVORED_MOVIES, null);
        if(null!=set){
            return set.toArray(new String[set.size()]);
        }

        return new String[]{};
    }

    public static void addToFavorites(Context context, String movieId) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_FAVORED, 1);
        String selection = MovieContract.MovieEntry.COLUMN_ID+"=?";
        String[] selectionArgs = new String[]{movieId};

        context.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, values, selection, selectionArgs);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = preferences.getStringSet(PREF_FAVORED_MOVIES, null);
        if (null == set) {
            set = new HashSet<>();
        }
        set.add(movieId);
        preferences.edit().putStringSet(PREF_FAVORED_MOVIES, set).apply();
    }

    public static void removeFromFavorites(Context context, String movieId) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_FAVORED, 0);
        String selection = MovieContract.MovieEntry.COLUMN_ID+"=?";
        String[] selectionArgs = new String[]{movieId};

        context.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, values, selection, selectionArgs);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = preferences.getStringSet(PREF_FAVORED_MOVIES, null);
        if (null == set) {
            set = new HashSet<>();
        }
        set.remove(movieId);
        preferences.edit().putStringSet(PREF_FAVORED_MOVIES, set).apply();
    }

    public static void playTrailer(Context context, Trailer trailer) {
        if (trailer.getSite().equals(Trailer.SITE_YOUTUBE))
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey())));
        else
            Toast.makeText(context, "Unsupported video format", Toast.LENGTH_SHORT).show();
    }

}
