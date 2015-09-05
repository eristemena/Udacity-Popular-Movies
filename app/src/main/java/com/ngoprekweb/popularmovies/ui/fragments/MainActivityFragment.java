package com.ngoprekweb.popularmovies.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ngoprekweb.popularmovies.MovieAdapter;
import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.FetchPopularMoviesTask;
import com.ngoprekweb.popularmovies.data.Movie;
import com.ngoprekweb.popularmovies.data.MovieContract;
import com.ngoprekweb.popularmovies.data.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final String MOVIE_KEY = "movie_key";
    private GridView mGridView;
    private ArrayList<Movie> listOfMovies;
    SharedPreferences pref;
    MovieAdapter mMovieAdapter;

    private static final int MOVIE_LOADER = 0;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * callback for when an item has been selected.
         */
        public void onItemSelected(Uri contentUri);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));

                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieDetail(movieId));
                }
            }
        });

        return rootView;
    }

    public void onLocationChanged( ) {
        updateWeather();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateWeather() {
        FetchPopularMoviesTask weatherTask = new FetchPopularMoviesTask(getActivity());
        String sortedBy = Utility.getPreferredSortedBy(getActivity());
        Log.v(LOG_TAG,"Sorted by === "+sortedBy);
        weatherTask.execute(sortedBy);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MOVIE_KEY, listOfMovies);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }
}
