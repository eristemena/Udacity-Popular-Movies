package com.ngoprekweb.popularmovies.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.model.Review;

import java.util.List;


public class ReviewAdapter extends ArrayAdapter<Review> {
    private int mResource;
    private Context mContext;

    public ReviewAdapter(Context context, int resource, List<Review> objects) {
        super(context, resource, objects);

        mResource = resource;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_review, parent, false);

            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        Review trailer = getItem(position);

        String author = trailer.getAuthor();
        viewHolder.authorView.setText(author);

        String content = trailer.getContent();
        viewHolder.contentView.setText(content);


        return convertView;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.list_item_review_author_textview);
            contentView = (TextView)view.findViewById(R.id.list_item_revivew_content_textview);
        }
    }
}
