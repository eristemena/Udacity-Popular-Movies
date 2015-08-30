package com.ngoprekweb.popularmovies.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ngoprekweb.popularmovies.GetMoviesCallback;
import com.ngoprekweb.popularmovies.ImageAdapter;
import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.FetchPopularMoviesTask;
import com.ngoprekweb.popularmovies.data.Movie;
import com.ngoprekweb.popularmovies.data.MovieDbHelper;
import com.ngoprekweb.popularmovies.ui.activities.DetailActivity;
import com.ngoprekweb.popularmovies.ui.activities.MainActivity;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final String MOVIE_KEY = "movie_key";
    private GridView mGridView;
    private ArrayList<Movie> listOfMovies;
    SharedPreferences pref;
    FetchPopularMoviesTask mTask;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_KEY)) {
            // no saved instance found

            final MovieDbHelper dbHelper = MovieDbHelper.get(getActivity());
            listOfMovies = dbHelper.getAllMovies();

            if(isNetworkAvailable()) {
                mTask = new FetchPopularMoviesTask(getActivity(), new GetMoviesCallback() {
                    @Override
                    public void done(ArrayList<Movie> movies) {
                        dbHelper.bulkInsert(movies);
                        mGridView.setAdapter(new ImageAdapter(getActivity(), movies));
                    }
                });

                String sortBy = pref.getString(getString(R.string.pref_key_sort_by), getString(R.string.pref_default_sort_by));

                mTask.execute(sortBy);
            }
        } else {
            listOfMovies = savedInstanceState.getParcelableArrayList(MOVIE_KEY);
        }

        mGridView.setAdapter(new ImageAdapter(getActivity(), listOfMovies));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Movie movie = (Movie) parent.getItemAtPosition(position);
                intent.putExtra(MainActivity.EXTRA_MOVIE_ID, movie.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mTask.cancel(true);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(getString(R.string.pref_key_sort_by))) {
                final MovieDbHelper dbHelper = MovieDbHelper.get(getActivity());

                if(isNetworkAvailable()) {
                    mTask = new FetchPopularMoviesTask(getActivity(), new GetMoviesCallback() {
                        @Override
                        public void done(ArrayList<Movie> movies) {
                            dbHelper.bulkInsert(movies);
                            mGridView.setAdapter(new ImageAdapter(getActivity(), movies));
                        }
                    });

                    String sortBy = pref.getString(getString(R.string.pref_key_sort_by), getString(R.string.pref_default_sort_by));

                    mTask.execute(sortBy);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        pref.registerOnSharedPreferenceChangeListener(sharedPreferenceListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pref.unregisterOnSharedPreferenceChangeListener(sharedPreferenceListener);
    }

    @Override
    public void onPause() {
        super.onPause();


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


}
