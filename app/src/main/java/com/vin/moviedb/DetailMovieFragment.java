package com.vin.moviedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vin.moviedb.asynctask.FetchMovieReviewTask;
import com.vin.moviedb.asynctask.FetchMovieTrailerTask;
import com.vin.moviedb.callback.ShareIntentCallback;
import com.vin.moviedb.data.MovieContract;

/**
 * Created by vin on 26/2/17.
 */

public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> , ShareIntentCallback{

    // These indices are tied to DETAIL_COLUMNS.
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_ORIGINAL_TITLE = 3;
    public static final int COL_MOVIE_RELEASE_DATE = 4;
    public static final int COL_MOVIE_USER_RATING = 5;
    public static final int COL_MOVIE_POPULARITY = 6;
    public static final int COL_MOVIE_POSTER_PATH = 7;
    public static final int COL_MOVIE_BACKDROP_MOVIE_PATH = 8;
    public static final int COL_MOVIE_OVERVIEW = 9;

    static final String DETAIL_URI = "detail_uri";
    private static final String TAG_DETAIL_MOVIE_FRAGMENT = DetailMovieFragment.class.getSimpleName();
    private static final String MOVIE_SHARE_HASHTAG = " #MovieApp";
    private static final int DETAIL_LOADER = 0;
    private static final Uri LOAD_URI_POSTER = Uri.parse("https://image.tmdb.org/t/p/w185");

    private String mShareMovieString;
    private ShareActionProvider mShareActionProvider;
    private Uri mUri;

    private ImageView mPosterImageView;
    private TextView mRatingView;
    private TextView mTitleView;
    private TextView mReleaseDateView;
    private TextView mSynopsis;
    private ImageView mFavouriteView;
    private TextView mSlashView;
    private TextView mMaxRating;

    private RecyclerView mListViewMovieTrailers;
    private RecyclerView mListViewMovieReviews;

    private FetchMovieTrailerTask mFetchMovieTrailerTask;
    private FetchMovieReviewTask mFetchMovieReviewTask;

    public DetailMovieFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mTitleView = (TextView) rootView.findViewById(R.id.detail_movie_title_textview);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.detail_movie_poster_image);
        mRatingView = (TextView) rootView.findViewById(R.id.detail_movie_rating_textview);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.detail_movie_release_date_textview);
        mSynopsis = (TextView) rootView.findViewById(R.id.detail_movie_summary_textview);
        mFavouriteView = (ImageView) rootView.findViewById(R.id.detail_movie_favourite_imageview);
        mSlashView = (TextView) rootView.findViewById(R.id.detail_movie_rating_slash_textview);
        mMaxRating = (TextView) rootView.findViewById(R.id.detail_movie_rating_max_textview);

        mListViewMovieTrailers = (RecyclerView) rootView.findViewById(R.id.detail_movie_trailer_listview);
        mListViewMovieReviews = (RecyclerView) rootView.findViewById(R.id.detail_movie_review_listview);

        if (arguments != null) {
            mUri = arguments.getParcelable(DetailMovieFragment.DETAIL_URI);
            // get movie trailers
            mFetchMovieTrailerTask = new FetchMovieTrailerTask(this, getContext(), mListViewMovieTrailers);
            mFetchMovieTrailerTask.execute(MovieContract.getMovieIdFromUri(mUri));

            //get movie reviews
            mFetchMovieReviewTask = new FetchMovieReviewTask(getContext(), mListViewMovieReviews);
            mFetchMovieReviewTask.execute(MovieContract.getMovieIdFromUri(mUri));
        }


        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFetchMovieTrailerTask != null)
            mFetchMovieTrailerTask.cancel(true);
        if(mFetchMovieReviewTask != null){
            mFetchMovieReviewTask.cancel(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.movie_detail_fragment_menu, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareMovieString != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareMovieString + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG_DETAIL_MOVIE_FRAGMENT, "onCreateLoader");

        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final Cursor dataCursor = data;
        if (dataCursor != null && dataCursor.moveToFirst()) {

            final long movieId = dataCursor.getLong(COL_MOVIE_ID);
            final Uri favouriteUri = MovieContract.FavouriteEntry.buildUriUsingMovieId(movieId);

            mTitleView.setText(dataCursor.getString(COL_MOVIE_TITLE));

            Picasso.with(getContext())
                    .load(LOAD_URI_POSTER.buildUpon()
                            .appendPath(dataCursor.getString(COL_MOVIE_POSTER_PATH))
                            .build())
                    .into(mPosterImageView);
            mPosterImageView.setContentDescription(dataCursor.getString(COL_MOVIE_TITLE));

            mRatingView.setText(Double.toString(dataCursor.getDouble(COL_MOVIE_USER_RATING)));
            mSlashView.setText(getResources().getString(R.string.rating_separator));
            mMaxRating.setText(getResources().getString(R.string.rating_highest));
            mReleaseDateView.setText(Utility.getUiDateString(dataCursor.getString(COL_MOVIE_RELEASE_DATE)));
            mSynopsis.setText(dataCursor.getString(COL_MOVIE_OVERVIEW));

            // Check if the movie selected is favourite and set favourite star on UI.
            if (!mUri.toString().equals(MovieContract.FavouriteEntry.CONTENT_URI.toString())) {
                Cursor favCursor = getContext().getContentResolver().query(
                        favouriteUri,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                if(favCursor != null) {
                    if (favCursor.getCount() > 0) {
                        setmFavouriteView(true);
                    } else {
                        setmFavouriteView(false);
                    }
                    favCursor.close();
                }
            } else {
                setmFavouriteView(true);
            }

            // set onclick listener on favourite icon.
            mFavouriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavourite()) {
                        // change on UI
                        setmFavouriteView(false);
                        // delete favourite entry
                        int rowDeleteCount = getContext().getContentResolver().delete(
                                MovieContract.FavouriteEntry.CONTENT_URI,
                                MovieContract.FavouriteEntry.COLUMN_FAVOURITE_MOVIE_ID + " = ?",
                                new String[]{Long.toString(movieId)}
                        );
                        Log.d(TAG_DETAIL_MOVIE_FRAGMENT, "Number of Favourite rows Deleted: " + rowDeleteCount);
                    } else {
                        // change on UI
                        setmFavouriteView(true);
                        // add favourite entry
                        getContext().getContentResolver().insert(
                                MovieContract.FavouriteEntry.CONTENT_URI,
                                createFavouriteRow(dataCursor)
                        );
                        Log.d(TAG_DETAIL_MOVIE_FRAGMENT, "Favourite rows Inserted in database.");
                    }
                }
            });

        }
    }

    private void setmFavouriteView(boolean active) {
        /*
        * Drawables btn_favourite_on and btn_favourite_off credited to https://icons8.com/
        * */
        if (active) {
            mFavouriteView.setImageResource(R.drawable.btn_favourite_on);
        } else {
            mFavouriteView.setImageResource(R.drawable.btn_favourite_off);
        }
        mFavouriteView.setTag(active);
    }

    private boolean isFavourite() {
        return (boolean) mFavouriteView.getTag();
    }

    private ContentValues createFavouriteRow(Cursor cursor) {
        ContentValues favouriteContentValues = new ContentValues();

        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_MOVIE_ID,
                cursor.getLong(COL_MOVIE_ID));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_TITLE,
                cursor.getString(COL_MOVIE_TITLE));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_ORIGINAL_TITLE,
                cursor.getString(COL_MOVIE_ORIGINAL_TITLE));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_RELEASE_DATE,
                cursor.getString(COL_MOVIE_RELEASE_DATE));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_USER_RATING,
                cursor.getDouble(COL_MOVIE_USER_RATING));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_POPULARITY,
                cursor.getDouble(COL_MOVIE_POPULARITY));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_POSTER_PATH,
                cursor.getString(COL_MOVIE_POSTER_PATH));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_BACKDROP_POSTER_PATH,
                cursor.getString(COL_MOVIE_BACKDROP_MOVIE_PATH));
        favouriteContentValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_OVERVIEW,
                cursor.getString(COL_MOVIE_OVERVIEW));


        return favouriteContentValues;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    @Override
    public void setShareIntent(Uri shareUri) {
        // Prepare share intent.
        mShareMovieString = shareUri.toString();
        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }
}

