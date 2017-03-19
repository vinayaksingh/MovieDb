package com.vin.moviedb.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by vin on 4/3/17.
 */

public class MovieContentProvider extends ContentProvider {

    // Constants for Uri Matcher
    public final static int POPULAR = 101;
    public final static int POPULAR_WITH_MOVIE_ID = 102;
    public final static int TOP_RATED = 103;
    public final static int TOP_RATED_WITH_MOVIE_ID = 104;
    public final static int FAVOURITE = 105;
    public final static int FAVOURITE_WITH_MOVIE_ID = 106;

    private static final String TAG_MOVIE_CONTENT_PROVIDER = MovieContentProvider.class.getName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR, POPULAR);
        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR + "/#", POPULAR_WITH_MOVIE_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_TOP_RATED, TOP_RATED);
        uriMatcher.addURI(authority, MovieContract.PATH_TOP_RATED + "/#", TOP_RATED_WITH_MOVIE_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVOURITE, FAVOURITE);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVOURITE + "/#", FAVOURITE_WITH_MOVIE_ID);

        return uriMatcher;

    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POPULAR:
                return MovieContract.PopularEntry.CONTENT_TYPE;
            case POPULAR_WITH_MOVIE_ID:
                return MovieContract.PopularEntry.CONTENT_ITEM_TYPE;
            case TOP_RATED:
                return MovieContract.TopRatedEntry.CONTENT_TYPE;
            case TOP_RATED_WITH_MOVIE_ID:
                return MovieContract.TopRatedEntry.CONTENT_ITEM_TYPE;
            case FAVOURITE:
                return MovieContract.FavouriteEntry.CONTENT_TYPE;
            case FAVOURITE_WITH_MOVIE_ID:
                return MovieContract.FavouriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public boolean onCreate() {
        // create new object of database helper for actions on Db.
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case POPULAR: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POPULAR_WITH_MOVIE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularEntry.TABLE_NAME,
                        projection,
                        MovieContract.PopularEntry.COLUMN_POPULAR_MOVIE_ID + " = ?",
                        new String[]{Long.toString(MovieContract.getMovieIdFromUri(uri))},
                        null,
                        null,
                        null
                );
                break;
            }
            case TOP_RATED: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TOP_RATED_WITH_MOVIE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        MovieContract.TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID + " = ?",
                        new String[]{Long.toString(MovieContract.getMovieIdFromUri(uri))},
                        null,
                        null,
                        null
                );
                break;
            }
            case FAVOURITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVOURITE_WITH_MOVIE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        null,
                        MovieContract.FavouriteEntry.COLUMN_FAVOURITE_MOVIE_ID + " = ?",
                        new String[]{Long.toString(MovieContract.getMovieIdFromUri(uri))},
                        null,
                        null,
                        null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        /*
        * http://stackoverflow.com/questions/7915050/cursorloader-not-updating-after-data-change
        * */
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = 0;
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case POPULAR:
                rowId = mOpenHelper.getWritableDatabase().insert(
                        MovieContract.PopularEntry.TABLE_NAME,
                        null,
                        values
                );
                if (rowId < 1) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TOP_RATED:
                rowId = mOpenHelper.getWritableDatabase().insert(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        null,
                        values
                );
                if (rowId < 1) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case FAVOURITE:
                rowId = mOpenHelper.getWritableDatabase().insert(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        null,
                        values
                );
                if (rowId < 1) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {
            case POPULAR:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.PopularEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case TOP_RATED:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case FAVOURITE:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {
            case POPULAR_WITH_MOVIE_ID:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.PopularEntry.TABLE_NAME,
                        values,
                        MovieContract.PopularEntry.COLUMN_POPULAR_MOVIE_ID + " = ?",
                        new String[]{Long.toString(MovieContract.getMovieIdFromUri(uri))}
                );
                break;
            case TOP_RATED_WITH_MOVIE_ID:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        values,
                        MovieContract.TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID + " = ?",
                        new String[]{Long.toString(MovieContract.getMovieIdFromUri(uri))}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated == 1) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            throw new UnsupportedOperationException("Number of rows updated should always be 1 ");
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int rowCount = 0;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case POPULAR: {
                try {
                    db.beginTransaction();
                    for (int i = 0; i < values.length; i++) {
                        long movieId = values[i].getAsLong(MovieContract.PopularEntry.COLUMN_POPULAR_MOVIE_ID);
                        Cursor cursor = db.query(MovieContract.PopularEntry.TABLE_NAME,
                                new String[]{MovieContract.PopularEntry.COLUMN_POPULAR_MOVIE_ID},
                                MovieContract.PopularEntry.COLUMN_POPULAR_MOVIE_ID + " = ?",
                                new String[]{Long.toString(movieId)},
                                null,
                                null,
                                null
                        );
                        if (cursor.getCount() == 0) {
                            db.insert(MovieContract.PopularEntry.TABLE_NAME,
                                    null,
                                    values[i]);
                            rowCount++;
                        } else {
                            db.update(MovieContract.PopularEntry.TABLE_NAME,
                                    values[i],
                                    MovieContract.PopularEntry.COLUMN_POPULAR_MOVIE_ID + " = ?",
                                    new String[]{Long.toString(movieId)});
                            rowCount++;
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowCount;
            }

            case TOP_RATED: {
                try {
                    db.beginTransaction();
                    for (int i = 0; i < values.length; i++) {
                        long movieId = values[i].getAsLong(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID);
                        Cursor cursor = db.query(MovieContract.TopRatedEntry.TABLE_NAME,
                                new String[]{MovieContract.TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID},
                                MovieContract.TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID + " = ?",
                                new String[]{Long.toString(movieId)},
                                null,
                                null,
                                null
                        );
                        if (cursor.getCount() == 0) {
                            db.insert(MovieContract.TopRatedEntry.TABLE_NAME,
                                    null,
                                    values[i]);
                            rowCount++;
                        } else {
                            db.update(MovieContract.TopRatedEntry.TABLE_NAME,
                                    values[i],
                                    MovieContract.TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID + " = ?",
                                    new String[]{Long.toString(movieId)});
                            rowCount++;
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }

    }


    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
