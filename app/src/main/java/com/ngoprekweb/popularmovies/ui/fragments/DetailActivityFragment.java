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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.MovieContract;
import com.ngoprekweb.popularmovies.data.ReviewAdapter;
import com.ngoprekweb.popularmovies.data.TrailerAdapter;
import com.ngoprekweb.popularmovies.data.Utility;
import com.ngoprekweb.popularmovies.data.loader.ReviewLoader;
import com.ngoprekweb.popularmovies.data.loader.TrailerLoader;
import com.ngoprekweb.popularmovies.data.model.Review;
import com.ngoprekweb.popularmovies.data.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public static final String DETAIL_URI = "URI";
    private Uri mUri;
    private String mMovieId;

    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL,
            MovieContract.MovieEntry.COLUMN_FAVORED
    };

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private TextView mTitleTextView;
    private TextView mSynopsisTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private ImageView mPosterImageView;
    private Button mAddToFavoritesButton;

    private LinearLayout mTrailersLinearLayout;
    private LinearLayout mReviewsLinearLayout;

    public DetailActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        getLoaderManager().initLoader(TRAILER_LOADER, null, new LoaderManager.LoaderCallbacks<ArrayList<Trailer>>() {
            @Override
            public Loader<ArrayList<Trailer>> onCreateLoader(int id, Bundle args) {
                TrailerLoader loader = new TrailerLoader(getActivity());
                loader.setMovieId(mMovieId);

                return loader;
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> data) {
                if (null != data) {
                    mTrailerAdapter = new TrailerAdapter(getContext(), 0, data);

                    for (int i = 0; i < mTrailerAdapter.getCount(); i++) {
                        View view = mTrailerAdapter.getView(i, null, null);

                        mTrailersLinearLayout.addView(view);
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {

            }
        }).forceLoad();

        getLoaderManager().initLoader(REVIEW_LOADER, null, new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {
            @Override
            public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
                ReviewLoader loader = new ReviewLoader(getActivity());
                loader.setMovieId(mMovieId);

                return loader;
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
                if (null != data) {
                    mReviewAdapter = new ReviewAdapter(getContext(), 0, data);

                    for (int i = 0; i < mReviewAdapter.getCount(); i++) {
                        View view = mReviewAdapter.getView(i, null, null);

                        mReviewsLinearLayout.addView(view);
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Review>> loader) {

            }
        }).forceLoad();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            mMovieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
        }

        mTitleTextView = (TextView) rootView.findViewById(R.id.detailTextViewTitle);
        mSynopsisTextView = (TextView) rootView.findViewById(R.id.detailTextViewSynopsis);
        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.detailTextViewReleaseDate);
        mRatingTextView = (TextView) rootView.findViewById(R.id.detailTextViewRating);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.detailImageView);
        mAddToFavoritesButton = (Button) rootView.findViewById(R.id.detailButtonMarkAsFavorite);

        mTrailersLinearLayout = (LinearLayout) rootView.findViewById(R.id.trailers_linear_layout);
        mReviewsLinearLayout = (LinearLayout) rootView.findViewById(R.id.reviews_linear_layout);

        mAddToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavored = (boolean) v.getTag();
                if(isFavored){
                    Utility.removeFromFavorites(getActivity(), mMovieId);
                }else {
                    Utility.addToFavorites(getActivity(), mMovieId);
                }
            }
        });
        return rootView;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
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

            String rating = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            mRatingTextView.setText(rating);

            Log.v(LOG_TAG, "col index===" + data.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORED));

            boolean isFavored = data.getInt(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORED)) == 1;

            if (isFavored) {
                mAddToFavoritesButton.setText(getString(R.string.remove_from_favorites));
                mAddToFavoritesButton.setTag(true);
            } else {
                mAddToFavoritesButton.setText(getString(R.string.mark_as_favorite));
                mAddToFavoritesButton.setTag(false);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
