package com.ngoprekweb.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ngoprekweb.popularmovies.data.MovieContract;
import com.ngoprekweb.popularmovies.data.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends CursorAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovies;

//    public MovieAdapter(Context context, ArrayList<Movie> movies) {
//        mContext = context;
//        mMovies = movies;
//    }


    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_THUMBNAIL));
        Picasso.with(context).load(poster).into(viewHolder.posterView);

//        String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
//        viewHolder.titleView.setText(title);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageView imageView;
//        if (convertView == null) {
//            // if it's not recycled, initialize some attributes
//            imageView = new ImageView(mContext);
//            imageView.setAdjustViewBounds(true);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        } else {
//            imageView = (ImageView) convertView;
//        }
//
//        Picasso.with(mContext).load(mMovies.get(position).getThumbnail()).into(imageView);
//
//        return imageView;
//    }

//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mMovies.get(position);
//    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView posterView;
//        public final TextView titleView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.list_item_movie_imageview);
//            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
        }
    }
}
