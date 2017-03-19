package com.vin.moviedb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by vin on 5/3/17.
 */

public class MovieAdapter extends CursorAdapter {

    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_POSTER_PATH = 7;

    private static Uri LOAD_URI = Uri.parse("https://image.tmdb.org/t/p/w185");
    Context mContext;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_movie, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read title from cursor
        String movieTitle = cursor.getString(COL_MOVIE_TITLE);
        // set the description
        viewHolder.posterView.setContentDescription(movieTitle);
        // get poster path
        String posterPath = cursor.getString(COL_MOVIE_POSTER_PATH);
        // load via Picasso
        Picasso.with(mContext)
                .load(LOAD_URI.buildUpon()
                        .appendPath(posterPath)
                        .build())
                .into(viewHolder.posterView);
    }

    /**
     * Cache of the children views for a movie grid item.
     */
    public static class ViewHolder {
        public final ImageView posterView;


        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.posterImage);

        }
    }

}
