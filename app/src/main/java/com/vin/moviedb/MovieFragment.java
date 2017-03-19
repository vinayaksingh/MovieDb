package com.vin.moviedb;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vin.moviedb.callback.GridClickCallback;
import com.vin.moviedb.data.MovieContract;
import com.vin.moviedb.sync.MovieSyncAdapter;


/**

 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_MOVIE_ID = 1;
    private static final String TAG_MOVIE_FRAGMENT = MovieFragment.class.getName();
    private static final int MOVIE_LOADER = 0;
    private static final String SELECTOR_KEY = "grid_selector_key";
    GridView mGridView = null;
    MovieAdapter mMovieAdapter = null;
    private int mPosition = GridView.INVALID_POSITION;


    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Initialize loader to present grid value
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    public void onGridOrderChanged() {
        updateMovieGrid();
        // Restart loader to present grid value on change of movie sort order.
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovieGrid() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTOR_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTOR_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTOR_KEY);
        }
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTOR_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTOR_KEY);
        }
        //create adapter to designate movie grid view
        mMovieAdapter = new MovieAdapter(getContext(), null, 0);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                loadDetailMovie(cursor, position, true);
                mPosition = position;
            }
        });

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;
        Uri movieUri;
        if (Utility.getPreferredSortOrder(getContext())
                .equals(getContext().getResources().getString(R.string.pref_sort_popular))) {
            movieUri = MovieContract.PopularEntry.CONTENT_URI;
            sortOrder = MovieContract.PopularEntry.COLUMN_POPULAR_POPULARITY + " DESC";

        } else if (Utility.getPreferredSortOrder(getContext())
                .equals(getContext().getResources().getString(R.string.pref_sort_top))) {
            movieUri = MovieContract.TopRatedEntry.CONTENT_URI;
            sortOrder = MovieContract.TopRatedEntry.COLUMN_TOP_RATED_USER_RATING + " DESC";
        } else {
            movieUri = MovieContract.FavouriteEntry.CONTENT_URI;
            sortOrder = MovieContract.FavouriteEntry.COLUMN_FAVOURITE_USER_RATING + " DESC";
        }

        return new CursorLoader(getActivity(),
                movieUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG_MOVIE_FRAGMENT, "onLoadFinished:  " + data.getCount());
        mMovieAdapter.swapCursor(data);
        int dataCount = data.getCount();
        if (dataCount > 0) {
            if (mPosition != GridView.INVALID_POSITION && mPosition <= dataCount) {
                loadDetailMovie(data, mPosition, false);
                mGridView.smoothScrollToPosition(mPosition);
            } else {
                loadDetailMovie(data, GridView.INVALID_POSITION, false);
            }
        } else {
            loadDetailMovie(null, GridView.INVALID_POSITION, false);

        }
    }

    private void loadDetailMovie(final Cursor data, final int position, final boolean startDetailActivity) {

        final int LIST = 1;
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LIST)
                    if (data != null) {
                        if (position == GridView.INVALID_POSITION) {
                            data.moveToFirst();
                        } else {
                            data.moveToPosition(position);
                        }

                        if (Utility.getPreferredSortOrder(getContext())
                                .equals(getContext().getResources().getString(R.string.pref_sort_popular))) {
                            ((GridClickCallback) getActivity()).onGridItemSelected(MovieContract.
                                    PopularEntry.buildUriUsingMovieId(data.getLong(COL_MOVIE_ID)), startDetailActivity);

                        } else if (Utility.getPreferredSortOrder(getContext())
                                .equals(getContext().getResources().getString(R.string.pref_sort_top))) {
                            ((GridClickCallback) getActivity()).onGridItemSelected(MovieContract.
                                    TopRatedEntry.buildUriUsingMovieId(data.getLong(COL_MOVIE_ID)), startDetailActivity);

                        } else {
                            ((GridClickCallback) getActivity()).onGridItemSelected(MovieContract.
                                    FavouriteEntry.buildUriUsingMovieId(data.getLong(COL_MOVIE_ID)), startDetailActivity);
                        }
                    } else {
                        ((GridClickCallback) getActivity()).onGridItemSelected(null, false);
                    }
            }
        };
        handler.sendEmptyMessage(LIST);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


}
