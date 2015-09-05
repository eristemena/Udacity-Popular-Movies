package com.ngoprekweb.popularmovies.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public static final String DETAIL_URI = "URI";
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL
    };

    private TextView mTitleTextView;
    private TextView mSynopsisTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private ImageView mPosterImageView;

    public DetailActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        }

        mTitleTextView = (TextView) rootView.findViewById(R.id.detailTextViewTitle);
        mSynopsisTextView = (TextView) rootView.findViewById(R.id.detailTextViewSynopsis);
        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.detailTextViewReleaseDate);
        mRatingTextView = (TextView) rootView.findViewById(R.id.detailTextViewRating);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.detailImageView);

        return rootView;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            Log.v(LOG_TAG, "Receive URI: " + mUri.toString());
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String title = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            mTitleTextView.setText(title);

            String overview = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            mSynopsisTextView.setText(overview);

            String thumbnail = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_THUMBNAIL));
            Picasso.with(getActivity()).load(thumbnail).into(mPosterImageView);

            String releaseDate = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            mReleaseDateTextView.setText(releaseDate);

            String rating = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            mRatingTextView.setText(rating);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
