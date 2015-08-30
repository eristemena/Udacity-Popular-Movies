package com.ngoprekweb.popularmovies.data;

import android.provider.BaseColumns;


public class MovieContract {
    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
    }
}
