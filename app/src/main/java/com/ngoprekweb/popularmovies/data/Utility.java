package com.ngoprekweb.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ngoprekweb.popularmovies.R;

public class Utility {
    public static String getPreferredSortedBy(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_sort_by),
                context.getString(R.string.pref_default_sort_by));
    }
}
