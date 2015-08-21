package com.ngoprekweb.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngoprekweb.popularmovies.model.Movie;
import com.ngoprekweb.popularmovies.model.MovieDbHelper;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        String extra = getActivity().getIntent().getStringExtra(MainActivity.EXTRA_MOVIE_ID);
        MovieDbHelper dbHelper = MovieDbHelper.get(getActivity());
        Movie movie = dbHelper.getMovie(extra);
        TextView tv = (TextView) rootView.findViewById(R.id.detailTextViewTitle);
        TextView tvSynopsis = (TextView) rootView.findViewById(R.id.detailTextViewSynopsis);
        TextView tvReleaseDate = (TextView) rootView.findViewById(R.id.detailTextViewReleaseDate);
        TextView tvRating = (TextView) rootView.findViewById(R.id.detailTextViewRating);
        ImageView iv = (ImageView) rootView.findViewById(R.id.detailImageView);

        if (null != movie) {
            tv.setText(movie.getTitle());
            Picasso.with(getActivity()).load(movie.getThumbnail()).into(iv);
            tvSynopsis.setText(movie.getOverview());
            tvReleaseDate.setText("Release data: "+movie.getReleaseDate());
            tvRating.setText("Average rating: "+movie.getRating());
        } else {
            tv.setText("NULL for " + extra);
        }

        return rootView;
    }
}
