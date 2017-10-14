package com.vin.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vin.moviedb.R;
import com.vin.moviedb.prensenter.MovieReview;

import java.util.ArrayList;

/**
 * Created by vin on 14/3/17.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {

    private int mListItemCount;
    private ArrayList<MovieReview> mMovieReviewArrayList;

    public MovieReviewAdapter(ArrayList<MovieReview> movieReviewArrayList) {
        this.mListItemCount = movieReviewArrayList.size();
        this.mMovieReviewArrayList = movieReviewArrayList;
    }

    @Override
    public MovieReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_view_movie_review;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieReviewAdapter.ReviewViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapter.ReviewViewHolder holder, int position) {
        holder.bind(mMovieReviewArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mListItemCount;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView mListViewReviewContent;
        TextView mListViewReviewAuthor;

        Context mContext;

        ReviewViewHolder(View itemView, Context context) {
            super(itemView);
            mListViewReviewContent = (TextView) itemView.findViewById(R.id.detail_movie_review_content);
            mListViewReviewAuthor = (TextView) itemView.findViewById(R.id.detail_movie_review_author);
            mContext = context;
        }

        void bind(final MovieReview movieReview) {
            mListViewReviewContent.setText(movieReview.getContent());
            mListViewReviewAuthor.setText(
                    String.format(
                            mContext.getResources().getString(R.string.label_movie_review_author_hyphen),
                            movieReview.getAuthor()
                    )
            );
        }

    }
}
