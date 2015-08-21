package com.ngoprekweb.popularmovies;

import com.ngoprekweb.popularmovies.model.Movie;

import java.util.ArrayList;


public abstract class GetMoviesCallback {
    public abstract void done(ArrayList<Movie> movies);
}
