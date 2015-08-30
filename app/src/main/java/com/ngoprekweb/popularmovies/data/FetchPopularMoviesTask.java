package com.ngoprekweb.popularmovies.data;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ngoprekweb.popularmovies.GetMoviesCallback;
import com.ngoprekweb.popularmovies.R;

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

public class FetchPopularMoviesTask extends AsyncTask<String, Void, String> {
    private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();
    private GetMoviesCallback mCallback;
    private Context mContext;

    public FetchPopularMoviesTask(Context context, GetMoviesCallback callback) {
        mCallback = callback;
        mContext = context;
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
                    .appendQueryParameter(API_KEY, mContext.getString(R.string.tmdb_api_key))
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
        if(forecastJsonStr!=null) {
            try {
                mCallback.done(parseJSON(forecastJsonStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
