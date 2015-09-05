package com.ngoprekweb.popularmovies.data.loader;


import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.model.Trailer;

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

public class TrailerLoader extends AsyncTaskLoader<ArrayList<Trailer>> {
    private static final String LOG_TAG = TrailerLoader.class.getSimpleName();
    private String mMovieId;

    public TrailerLoader(Context context) {
        super(context);
    }

    public void setMovieId(String movieId){
        mMovieId = movieId;
    }

    @Override
    public ArrayList<Trailer> loadInBackground() {
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
            final String TMDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/"+mMovieId+"/videos?";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, getContext().getString(R.string.tmdb_api_key))
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

            return parseJSON(forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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

        return null;
    }

    private ArrayList<Trailer> parseJSON(String jsonString) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_NAME = "name";
        final String TMDB_SITE = "site";
        final String TMDB_KEY = "key";

        JSONObject jobj = new JSONObject(jsonString);
        JSONArray jMovies = jobj.getJSONArray("results");

        ArrayList<Trailer> trailers = new ArrayList<>();

        for (int i = 0; i < jMovies.length(); i++) {
            Trailer movie = new Trailer();
            JSONObject jMovie = jMovies.getJSONObject(i);

            movie.setName(jMovie.getString(TMDB_NAME));
            movie.setKey(jMovie.getString(TMDB_KEY));
            movie.setSite(jMovie.getString(TMDB_SITE));

            trailers.add(movie);
        }

        return trailers;

    }
}
