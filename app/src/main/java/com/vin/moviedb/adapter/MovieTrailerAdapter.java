package com.vin.moviedb.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vin.moviedb.R;
import com.vin.moviedb.prensenter.MovieTrailer;

import java.util.ArrayList;

/**
 * Created by vin on 14/3/17.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.TrailerViewHolder> {

    private int mListItemCount;
    private ArrayList<MovieTrailer> mMovieTrailerArrayList;

    public MovieTrailerAdapter(ArrayList<MovieTrailer> movieTrailerArrayList) {
        this.mListItemCount = movieTrailerArrayList.size();
        this.mMovieTrailerArrayList = movieTrailerArrayList;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_view_movie_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new TrailerViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(mMovieTrailerArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mListItemCount;
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder {

        TextView mListViewTextItem;
        Context mContext;

        TrailerViewHolder(View itemView, Context context) {
            super(itemView);
            mListViewTextItem = (TextView) itemView.findViewById(R.id.detail_movie_trailer_item);
            mContext = context;
        }

        void bind(final MovieTrailer movieTrailer) {
            mListViewTextItem.setText(movieTrailer.getName());
            mListViewTextItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"
                                + movieTrailer.getYoutubeKey()));
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + movieTrailer.getYoutubeKey()));
                        mContext.startActivity(intent);
                    }
                }
            });
        }

    }
}
