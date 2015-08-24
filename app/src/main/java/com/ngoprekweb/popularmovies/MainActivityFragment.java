package com.ngoprekweb.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ngoprekweb.popularmovies.model.Movie;
import com.ngoprekweb.popularmovies.model.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
                FetchPopularMoviesTask task = new FetchPopularMoviesTask(new GetMoviesCallback() {
                    @Override
                    public void done(ArrayList<Movie> movies) {
                        dbHelper.bulkInsert(movies);
                        mGridView.setAdapter(new ImageAdapter(getActivity(), movies));
                    }
                });

                String sortBy = pref.getString(getString(R.string.pref_key_sort_by), getString(R.string.pref_default_sort_by));

                task.execute(sortBy);
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

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(getString(R.string.pref_key_sort_by))) {
                final MovieDbHelper dbHelper = MovieDbHelper.get(getActivity());

                if(isNetworkAvailable()) {
                    FetchPopularMoviesTask task = new FetchPopularMoviesTask(new GetMoviesCallback() {
                        @Override
                        public void done(ArrayList<Movie> movies) {
                            dbHelper.bulkInsert(movies);
                            mGridView.setAdapter(new ImageAdapter(getActivity(), movies));
                        }
                    });

                    String sortBy = pref.getString(getString(R.string.pref_key_sort_by), getString(R.string.pref_default_sort_by));

                    task.execute(sortBy);
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

    public class FetchPopularMoviesTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();
        private GetMoviesCallback mCallback;

        public FetchPopularMoviesTask(GetMoviesCallback callback) {
            mCallback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, getString(R.string.tmdb_api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);

                return forecastJsonStr;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String forecastJsonStr) {
            try {
                mCallback.done(parseJSON(forecastJsonStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private ArrayList<Movie> parseJSON(String jsonString) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_ID = "id";
            final String TMDB_TITLE = "title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_RATING = "vote_average";
            final String TMDB_POSTER_PATH = "poster_path";

            JSONObject jobj = new JSONObject(jsonString);
            JSONArray jMovies = jobj.getJSONArray("results");

            ArrayList<Movie> movies = new ArrayList<Movie>();
            for (int i = 0; i < jMovies.length(); i++) {
                Movie movie = new Movie();
                JSONObject jMovie = jMovies.getJSONObject(i);

                movie.setId(jMovie.getString(TMDB_ID));
                movie.setTitle(jMovie.getString(TMDB_TITLE));
                movie.setOverview(jMovie.getString(TMDB_OVERVIEW));
                movie.setReleaseDate(jMovie.getString(TMDB_RELEASE_DATE));
                movie.setRating(jMovie.getString(TMDB_RATING));

                String thumbUrl = "http://image.tmdb.org/t/p/w185" + jMovie.getString(TMDB_POSTER_PATH);
                movie.setThumbnail(thumbUrl);

                movies.add(movie);
            }

            return movies;

        }


    }
}
