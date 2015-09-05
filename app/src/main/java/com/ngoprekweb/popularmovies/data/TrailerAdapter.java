package com.ngoprekweb.popularmovies.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ngoprekweb.popularmovies.R;
import com.ngoprekweb.popularmovies.data.model.Trailer;

import java.util.List;


public class TrailerAdapter extends ArrayAdapter<Trailer> {
    private int mResource;
    private Context mContext;

    public TrailerAdapter(Context context, int resource, List<Trailer> objects) {
        super(context, resource, objects);

        mResource = resource;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_trailer, parent, false);

            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        Trailer trailer = getItem(position);

        String name = trailer.getName();
        viewHolder.titleView.setText(name);

        viewHolder.playImageButton.setTag(trailer);
        viewHolder.playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trailer trailer = (Trailer) v.getTag();
                Utility.playTrailer(mContext, trailer);
            }
        });

        return convertView;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView titleView;
        public final ImageButton playImageButton;

        public ViewHolder(View view) {
            titleView = (TextView) view.findViewById(R.id.list_item_trailer_name_textview);
            playImageButton = (ImageButton) view.findViewById(R.id.list_item_trailer_imagebutton);
        }
    }
}
